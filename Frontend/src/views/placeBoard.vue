<script setup>
import { ref } from "vue";
import { onMounted } from "vue";
import axios from "axios";
import { isAuthenticated, getUserInfo } from "@/composables/userAuth";
import { inject } from "vue";

//로그인 상태 확인
const isLoggined = inject("isLoggedIn");

// 좋아요 버튼 상태

// 리뷰관련 변수
const isLiked = ref(false);
const likeCount = ref(0);
const currentPlace = ref("");
const placeName = ref("");
const addressName = ref("");
const placeUrl = ref("");
const phone = ref("");
const placeId = ref("");
const tableId = ref("");
const title = ref("");
const content = ref("");
const currentUser = ref(null);
const isModalOpen = ref(false);
const nowEditing = ref(false);
const tempReviewId = ref("");
//

//로드뷰 관련 변수
const categoryGroupName = ref("");
const mapContainer = ref(null);
const isLoading = ref(false);
const hasSearched = ref(false);
let map;
//
// 모달 열고 닫기
const openModal = () => {
  isModalOpen.value = true;
};
const closeModal = () => {
  isModalOpen.value = false;
  title.value = "";
  content.value = "";
  nowEditing.value = false;
};

// 사진 업로드
const uploadedImages = ref([]);
const handleFileUpload = (event) => {
  const files = event.target.files;
  for (let i = 0; i < files.length; i++) {
    const reader = new FileReader();
    reader.onload = (e) => {
      uploadedImages.value.push(e.target.result);
    };
    reader.readAsDataURL(files[i]);
  }
};

const roadviewContainer = ref(null);
let roadview;
let roadviewClient;

const addPlace = async () => {
  console.log({
    kakaoPlaceId: placeId.value,
    placeName: placeName.value,
  });
  try {
    const response = await axios.post(
      "http://localhost:8080/uhpooh/api/place/",
      {
        kakaoPlaceId: placeId.value,
        placeName: placeName.value,
      }
    );
  } catch (error) {
    console.log(error);
  }
  await searchPlaceById();
  const result = await axios.get(
    "http://localhost:8080/uhpooh/api/place/kakao/" + placeId.value
  );
  likeCount.value = result.data.data.likeCount;
  console.log(result.data.data);
};

async function searchPlaces() {
  isLoading.value = true;
  const ps = new kakao.maps.services.Places();
  const bounds = new kakao.maps.LatLngBounds();

  await ps.keywordSearch(currentPlace.value + "+수영장", (data, status) => {
    isLoading.value = false;
    hasSearched.value = true;
    console.log(data[0]);
    if (status === kakao.maps.services.Status.OK) {
      placeName.value = data[0].place_name;
      addressName.value = data[0].address_name;
      placeUrl.value = data[0].place_url;
      phone.value = data[0].phone;
      placeId.value = data[0].id;
      addPlace();

      // searchPlaceById();
      const position = new kakao.maps.LatLng(data[0].y, data[0].x);

      roadviewClient.getNearestPanoId(position, 50, (panoId) => {
        if (panoId) {
          roadview.setPanoId(panoId, position);

          kakao.maps.event.addListener(roadview, "init", () => {
            // 마커 생성
            const rMarker = new kakao.maps.Marker({
              position: position, // 마커 위치 좌표
              map: roadview, // 로드뷰 객체
            });

            // 뷰포인트 계산 및 설정
            const projection = roadview.getProjection();
            const viewpoint = projection.viewpointFromCoords(
              position, // 마커의 좌표
              0 // 고도
            );
            viewpoint.zoom = -1;
            roadview.setViewpoint(viewpoint);

            // 마커 위에 인포윈도우 표시
            const rLabel = new kakao.maps.InfoWindow({
              position: position,
              content: `
    <div style="
      max-width: 200px;
      max-height: 100px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      padding: 10px;
      border-radius: 10px;
      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
      font-size: 14px;
      color: #333;
    ">
      <strong style="font-size: 16px; color: #000;">${placeName.value}</strong><br />
      <span style="font-size: 12px; color: #666;">${addressName.value}</span>
    </div>
  `,
            });
            rLabel.open(roadview, rMarker);
          });
        } else {
          alert("해당 위치에서 이용 가능한 로드뷰가 없습니다.");
        }
      });
    } else {
      alert("오류가 발생했습니다.");
      hasSearched.value = false;
    }
  });
}

const addReview = async () => {
  console.log("Adding review with tableId:", tableId.value);
  console.log("Current placeId:", placeId.value);
  console.log({
    userId: localStorage.getItem("userId"),
    placeId: tableId.value,
    title: title.value,
    content: content.value,
    images: uploadedImages.value,
  });
  try {
    const response = await axios.post(
      "http://localhost:8080/uhpooh/api/review/write",
      {
        userId: localStorage.getItem("userId"),
        placeId: tableId.value,
        title: title.value,
        content: content.value,
      }
    );
    console.log(response);
    alert("리뷰가 성공적으로 작성되었습니다.");

    closeModal();
  } catch (error) {
    console.error(error);
  }
  location.reload();
};

const confirmEdit = async (rId) => {
  try {
    const response = await axios.put(
      "http://localhost:8080/uhpooh/api/review/edit/" + rId,
      { title: title.value, content: content.value }
    );
    console.log(response);
    closeModal();
  } catch (error) {
    console.log(error);
  }
  location.reload();
};
const searchPlaceById = async () => {
  console.log("Searching place by ID:", placeId.value);
  try {
    const response = await axios.get(
      "http://localhost:8080/uhpooh/api/place/kakao/" + placeId.value
    );
    tableId.value = response.data.data.placeId;
    console.log("Received tableId from API:", tableId.value);
  } catch (error) {
    console.error("Error in searchPlaceById:", error);
  }
  await reviewList();
};

const reviews = ref([]);
const reviewList = async () => {
  console.log(tableId.value);
  try {
    const response = await axios.get(
      "http://localhost:8080/uhpooh/api/review/place/" + tableId.value
    );
    console.log(response.data.data.items);
    reviews.value = response.data.data.items;
    localStorage.setItem("tableId", tableId.value);
  } catch (error) {
    console.error(error);
  }
};

const editReview = async (rId) => {
  console.log(rId);
  const response = await axios.get(
    "http://localhost:8080/uhpooh/api/review/detail/" + rId
  );
  const tempReview = response.data.data;
  console.log(tempReview);
  title.value = tempReview.title;
  content.value = tempReview.content;
  nowEditing.value = true;
  tempReviewId.value = rId;
  openModal();
};

onMounted(async () => {
  // Get the current place from localStorage
  currentPlace.value = localStorage.getItem("currentPlace");
  currentUser.value = localStorage.getItem("userId");
  console.log("Current place:", currentPlace.value);
  console.log("Current user:", currentUser.value);

  roadviewClient = new kakao.maps.RoadviewClient();
  roadview = new kakao.maps.Roadview(roadviewContainer.value);
  // Only search if we have a valid place name
  if (currentPlace.value) {
    await searchPlaces();

    // 로드뷰 초기화
  } else {
    console.warn("No place name found in localStorage");
  }
});

// 글 작성

// 장소 정보

// 좋아요 버튼 클릭 핸들러
const toggleLike = () => {
  const response = axios.put(
    "http://localhost:8080/uhpooh/api/place/like/" + tableId.value
  );
};
</script>

<template>
  <div class="p-6 min-h-screen bg-gray-50">
    <!-- 장소 정보 -->
    <div class="p-6 mb-6 bg-white rounded-lg shadow-lg">
      <h1 class="mb-4 text-3xl font-bold">{{ placeName }}</h1>

      <!-- 로드뷰 컨테이너 추가 -->
      <div class="overflow-hidden mb-6 rounded-lg shadow-lg">
        <div
          ref="roadviewContainer"
          class="w-full h-[400px] transition-all duration-300 hover:shadow-xl"
        ></div>
      </div>

      <div class="grid grid-cols-1 gap-4 md:grid-cols-2">
        <div class="space-y-2">
          <div class="flex items-center space-x-2">
            <i class="text-red-500 fas fa-map-marker-alt"></i>
            <p class="text-gray-600">{{ addressName }}</p>
          </div>
          <div class="flex items-center space-x-2">
            <i class="text-blue-500 fas fa-phone"></i>
            <p class="text-gray-600">{{ phone }}</p>
          </div>
          <div class="flex items-center space-x-2">
            <i class="text-green-500 fas fa-info-circle"></i>
            <a
              :href="placeUrl"
              target="_blank"
              class="text-blue-500 transition-colors hover:text-blue-700"
            >
              상세정보 보기
            </a>
          </div>
        </div>
      </div>
    </div>

    <!-- 좋아요 버튼 -->
    <div class="flex items-center mb-6">
      <button
        @click="toggleLike"
        :class="[
          'px-4 py-2 rounded-lg font-medium transition-all duration-300',
          isLiked
            ? 'bg-red-500 text-white hover:bg-red-600'
            : 'bg-gray-200 text-gray-600 hover:bg-gray-300',
        ]"
      >
        {{ isLiked ? "♥ 좋아요 취소" : "♡ 좋아요" }}
      </button>
      <span class="ml-4 text-gray-600">{{ likeCount }}명이 좋아합니다</span>
    </div>

    <!-- 후기 작성 -->
    <div
      v-if="isModalOpen"
      class="flex fixed inset-0 z-50 justify-center items-center bg-black bg-opacity-50"
    >
      <div class="relative w-11/12 max-w-3xl bg-white rounded-lg shadow-lg">
        <!-- 헤더 -->
        <div
          class="flex justify-between items-center px-6 py-4 border-b border-gray-200"
        >
          <h2 class="text-xl font-bold text-gray-700"></h2>
          <button
            @click="closeModal"
            class="text-gray-500 hover:text-gray-800 focus:outline-none"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
              class="w-6 h-6"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M6 18L18 6M6 6l12 12"
              />
            </svg>
          </button>
        </div>

        <!-- 내용 -->
        <div class="px-6 py-4 space-y-6">
          <!-- 사진 업로드 -->
          <div>
            <h3 class="mb-4 text-lg font-semibold text-gray-700">
              사진 업로드
            </h3>
            <input
              type="file"
              accept="image/*"
              multiple
              @change="handleFileUpload"
              class="p-2 w-full rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
            <div class="grid grid-cols-3 gap-4 mt-4">
              <img
                v-for="(image, index) in uploadedImages"
                :key="index"
                :src="image"
                alt="Uploaded"
                class="object-cover w-full h-32 rounded-lg shadow"
              />
            </div>
          </div>

          <!-- 후기 작성 -->
          <div>
            <h3 class="mb-4 text-lg font-semibold text-gray-700">후기 작성</h3>
            <textarea
              :disabled="!isLoggined"
              v-model="title"
              rows="1"
              placeholder="제목을 작성해주세요."
              required="required"
              class="p-4 w-full rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
            ></textarea>
            <textarea
              :disabled="!isLoggined"
              v-model="content"
              rows="5"
              placeholder="이 장소에 대한 후기를 작성해주세요."
              required="required"
              class="p-4 mt-2 w-full rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
            ></textarea>
          </div>
        </div>

        <!-- 푸터 -->
        <div class="flex justify-end px-6 py-4 border-t border-gray-200">
          <button
            v-show="!nowEditing"
            @click="addReview"
            class="px-4 py-2 text-white bg-blue-500 rounded-lg shadow hover:bg-blue-600"
          >
            작성 완료
          </button>
          <button
            v-show="nowEditing"
            @click="confirmEdit(tempReviewId)"
            class="px-4 py-2 text-white bg-blue-500 rounded-lg shadow hover:bg-blue-600"
          >
            수정 완료
          </button>
          <button
            @click="closeModal"
            class="px-4 py-2 ml-2 text-gray-700 bg-gray-200 rounded-lg shadow hover:bg-gray-300"
          >
            취소
          </button>
        </div>
      </div>
    </div>
  </div>
  <button
    @click="openModal"
    class="fixed right-6 bottom-6 z-50 px-4 py-2 text-white bg-blue-500 rounded-lg hover:bg-blue-600"
  >
    후기 작성
  </button>
  <!-- 리뷰 목록 -->
  <div class="p-6 mb-6 bg-white rounded-lg shadow-lg">
    <h2 class="mb-4 text-2xl font-bold text-gray-700">리뷰 목록</h2>
    <div v-if="reviews.length === 0" class="text-gray-500">
      작성된 리뷰가 없습니다. 첫 번째 리뷰를 작성해보세요!
    </div>
    <div v-else class="space-y-4">
      <div v-for="review in reviews" class="p-4 rounded-lg border shadow">
        <div class="flex justify-between items-center mb-2">
          <h3 class="text-lg font-semibold text-gray-800">
            {{ review.title }}
          </h3>
        </div>
        <p class="text-gray-600">{{ review.content }}</p>

        <div class="grid grid-cols-3 gap-2 mt-2"></div>
        <button
          v-show="review.userId == currentUser"
          @click="editReview(review.reviewId)"
        >
          리뷰 수정
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 반응형 스타일 */
@media (max-width: 768px) {
  img {
    height: auto !important;
  }
}
.roadview-container {
  position: relative;
  border-radius: 0.5rem;
  overflow: hidden;
}

.roadview-container::after {
  content: "";
  display: block;
  padding-bottom: 56.25%;
}

@media (max-width: 768px) {
  .roadview-container::after {
    padding-bottom: 75%;
  }
}

/* 기존 스타일 유지 */
/* 모달 애니메이션 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}
@keyframes fadeOut {
  from {
    opacity: 1;
    transform: scale(1);
  }
  to {
    opacity: 0;
    transform: scale(0.95);
  }
}

.fade-enter-active,
.fade-leave-active {
  animation: fadeIn 0.3s ease-in-out, fadeOut 0.3s ease-in-out;
}
</style>
