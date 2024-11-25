<script setup>
import { ref, computed } from "vue";
import { onMounted } from "vue";
import axios from "axios";
import { isAuthenticated, getUserInfo } from "@/composables/userAuth";
import { inject } from "vue";
import { ThumbsUp } from "lucide-vue-next";

//로그인 상태 확인
const isLoggined = inject("isLoggedIn");

// 좋아요 버튼 상태
const checkLike = ref(false);

// 리뷰관련 변수

const likeCount = ref(0);
const isLikeClicked = ref(false);
const currentPlace = ref("");
const placeName = ref("");
const addressName = ref("");
const placeUrl = ref("");
const phone = ref("");
const placeId = ref(0); // 실제 장소 id
const tableId = ref(0); // 테이블 상의 장소 id
const title = ref("");
const content = ref("");
const currentUser = ref(null);
const isModalOpen = ref(false);
const nowEditing = ref(false);
const tempReview = ref({});
const tempReviewId = ref("");
const watchingDetails = ref(false);
const reviewMap = ref({});

//로드뷰 관련 변수
const isLoading = ref(false);
const hasSearched = ref(false);

// 모달 열고 닫기
const openModal = () => {
  isModalOpen.value = true;
};
const closeModal = () => {
  isModalOpen.value = false;
  title.value = "";
  content.value = "";
  nowEditing.value = false;
  watchingDetails.value = false;
};

// 사진 업로드
const uploadedImages = ref([]);
const fileInput = ref(null);
const handleFileUpload = (event, reviewId) => {
  const files = Array.from(event.target.files);
  const totalCurrentImages =
    uploadedImages.value.length + Object.keys(reviewMap.value).length;
  const remainingSlots = 5 - totalCurrentImages;

  if (remainingSlots <= 0) {
    alert("최대 5장까지만 업로드할 수 있습니다.");
    return;
  }

  const newFiles = files.slice(0, remainingSlots);

  newFiles.forEach((file) => {
    if (file.type.startsWith("image/")) {
      const reader = new FileReader();
      reader.onload = (e) => {
        uploadedImages.value.push({
          file: file,
          preview: e.target.result,
        });
      };
      reader.readAsDataURL(file);
    }
  });

  // Reset file input
  if (fileInput.value) {
    fileInput.value.value = "";
  }
};

const sendImageData = async (reviewId) => {
  try {
    console.log("Uploading images:", uploadedImages.value);
    const formData = new FormData();
    for (let i = 0; i < uploadedImages.value.length; i++) {
      formData.append("files", uploadedImages.value[i].file);
    }

    const response = await axios.post(
      "http://localhost:8080/uhpooh/api/file/review/" + reviewId,
      formData,
      {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      }
    );
    console.log("Images uploaded successfully:", response);
    // Clear uploaded images after successful upload
    uploadedImages.value = [];
    return response;
  } catch (error) {
    console.error("Error uploading images:", error);
    throw error; // Propagate the error so confirmEdit can handle it
  }
};

// 이미지 캐싱 처리
const imgName = ref("");
const imgPath = ref("");
const cacheImage = async (cat) => {
  imgPath.value =
    "http://localhost:8080/uhpooh/api/file/images/" + cat + "/" + imgName.value;
  const response = await axios.get(imgPath.value, { timeout: 5000 });
  console.log("이미지 캐싱");
};

const removeImage = (index) => {
  uploadedImages.value.splice(index, 1);
};

const deleteReviewImage = async (targetImageId) => {
  try {
    await axios.delete(
      `http://localhost:8080/uhpooh/api/file/review/image/${targetImageId}`
    );
    // Find and remove the image from reviewMap
    const imageKey = Object.keys(reviewMap.value).find(
      (key) => reviewMap.value[key].id === targetImageId
    );
    if (imageKey) {
      delete reviewMap.value[imageKey];
    }
    console.log("Image deleted successfully");
  } catch (error) {
    console.error("Failed to delete image:", error);
    alert("이미지 삭제에 실패했습니다.");
  }
};
// 이미지 캐싱 처리

// 장소 관련 처리
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

  tableId.value = result.data.data.placeId;
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
// 장소 관련 처리

// 리뷰 관리
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

    await sendImageData(response.data.data.reviewId);
    await alert("리뷰가 성공적으로 작성되었습니다.");

    closeModal();
  } catch (error) {
    console.error(error);
  }
  location.reload();
};

const deleteReview = async (rId) => {
  try {
    const response = await axios.delete(
      "http://localhost:8080/uhpooh/api/review/delete/" + rId
    );
    console.log(response);
    alert("리뷰가 성공적으로 삭제되었습니다.");
  } catch (error) {
    console.error(error);
  }
  location.reload();
};

const confirmEdit = async (rId) => {
  try {
    // First update the review text
    const response = await axios.put(
      "http://localhost:8080/uhpooh/api/review/edit/" + rId,
      { title: title.value, content: content.value }
    );
    console.log(response);

    // Wait for image upload to complete if there are new images
    if (uploadedImages.value.length > 0) {
      await sendImageData(rId);
    }

    // Only close and reload after everything is done
    closeModal();
    location.reload();
  } catch (error) {
    console.log(error);
    alert("리뷰 수정 중 오류가 발생했습니다.");
  }
};
const searchPlaceById = async () => {
  console.log("Searching place by ID:", placeId.value);
  try {
    const response = await axios.get(
      "http://localhost:8080/uhpooh/api/place/kakao/" + placeId.value
    );
    tableId.value = response.data.data.placeId;
    console.log("Received tableId from API:", tableId.value);
    await isLiked();
    await getLikeCount();
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

const openDetail = async (rId) => {
  watchingDetails.value = true;
  console.log("리뷰를 상세조회합니다.", rId);
  const response = await axios.get(
    "http://localhost:8080/uhpooh/api/review/detail/" + rId
  );
  console.log("Current user:", currentUser.value);
  console.log("Review user:", response.data.data.userId);
  const imageArray = await axios.get(
    "http://localhost:8080/uhpooh/api/review/reviewimages/" + rId
  );
  console.log(imageArray.data);

  // Clear previous images
  reviewMap.value = {};

  // Process each image
  for (const reviewImage of imageArray.data) {
    const rawPath = reviewImage.imageUrl;
    const rawImageId = reviewImage.imageId;
    console.log(rawPath);
    const rawFileName = rawPath.replace("/images/reviews/", "");
    imgName.value = rawFileName;
    console.log(rawFileName);
    await cacheImage("reviews");
    // Store the image path
    reviewMap.value[imgName.value] = {
      id: rawImageId,
      name: imgName.value,
      path: rawPath,
    };
    console.log(reviewMap.value);
  }

  tempReview.value = response.data.data;
  console.log(tempReview.value);
  title.value = tempReview.value.title;
  content.value = tempReview.value.content;
  nowEditing.value = true;
  tempReviewId.value = rId;
  openModal();
};

const editReview = async (rId) => {
  openModal();
};
// 리뷰 관리 //
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

// Form validation
const isFormValid = computed(() => {
  return title.value?.length > 0 && content.value?.length > 0;
});

// 글 작성

// 장소 정보

// 좋아요 버튼 처리

const isLiked = async () => {
  try {
    const checkResponse = await axios.get(
      "http://localhost:8080/uhpooh/api/like/checklike",
      {
        params: {
          placeId: tableId.value,
          userId: currentUser.value,
        },
      }
    );

    checkLike.value = checkResponse.data;

    await console.log("this is checkLike", checkResponse);
  } catch (error) {
    console.log(error);
  }
};

const addLike = async (placeId, userId) => {
  console.log(checkLike.data);
  if (!checkLike.data) {
    try {
      const response = await axios.post(
        "http://localhost:8080/uhpooh/api/like/addlike",
        {
          placeId: placeId,
          userId: userId,
        }
      );

      console.log(response);
      checkLike.value = !checkLike.value;
      likeCount.value = likeCount.value + 1;
    } catch (error) {
      console.error(error);
    }
  }
};

const deleteLike = async (tableId, userId) => {
  console.log("Deleting like with tableId:", tableId, "and", userId);
  try {
    const response = await axios.delete(
      "http://localhost:8080/uhpooh/api/like/deletelike",
      {
        data: {
          placeId: tableId,
          userId: userId,
        },
      }
    );
    console.log(response);
    checkLike.value = !checkLike.value;
    likeCount.value = likeCount.value - 1;
  } catch (error) {
    console.error(error);
  }
};

const getLikeCount = async () => {
  try {
    const response = await axios.get(
      "http://localhost:8080/uhpooh/api/like/getlikebyplaceid/" + tableId.value
    );
    likeCount.value = response.data.length;
  } catch (error) {
    console.error(error);
  }
};

// 좋아요 버튼 처리
</script>

<template>
  <div class="pt-10 min-h-screen bg-gradient-to-br from-blue-50 to-indigo-50">
    <div class="grid grid-cols-1 gap-6 mx-6 lg:grid-cols-2">
      <div>
        <!-- 장소 정보 -->
        <div
          class="p-8 mb-6 bg-white rounded-xl shadow-xl transition-all duration-300 transform hover:shadow-2xl"
        >
          <h1
            class="mb-4 text-4xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-indigo-600"
          >
            {{ placeName }}
          </h1>
          <div class="space-y-3">
            <p class="flex items-center text-gray-600">
              <i class="mr-2 text-indigo-500 fas fa-map-marker-alt"></i>
              {{ addressName }}
            </p>
            <p v-if="phone" class="flex items-center text-gray-600">
              <i class="mr-2 text-indigo-500 fas fa-phone"></i>
              {{ phone }}
            </p>
            <div class="flex items-center space-x-4">
              <a
                :href="placeUrl"
                target="_blank"
                class="inline-flex items-center px-4 py-2 text-white bg-indigo-600 rounded-lg transition-colors duration-200 hover:bg-indigo-700"
              >
                <i class="mr-2 fas fa-external-link-alt"></i>
                카카오맵에서 보기
              </a>
              <button
                @click="openModal"
                class="inline-flex items-center px-4 py-2 text-white bg-indigo-600 rounded-lg transition-colors duration-200 hover:bg-indigo-700"
              >
                <i class="mr-2 fas fa-plus"></i>
                리뷰 작성
              </button>
              <div>
                <!-- Unliked Button -->
                <button
                  v-if="!checkLike"
                  @click="addLike(tableId, currentUser)"
                  class="flex relative items-center px-4 py-2 space-x-2 text-gray-600 bg-gray-200 rounded-full shadow-lg transition-all duration-300 active:scale-95"
                >
                  <ThumbsUp class="w-5 h-5" />
                  <span>{{ likeCount }}</span>
                  <div
                    class="absolute inset-0 bg-gray-400 rounded-full opacity-0 transition-opacity duration-300 active:opacity-30"
                  ></div>
                </button>

                <!-- Liked Button -->
                <button
                  v-else
                  @click="deleteLike(tableId, currentUser)"
                  class="flex relative items-center px-4 py-2 space-x-2 text-white bg-red-600 rounded-full shadow-lg transition-all duration-300 active:scale-95"
                >
                  <ThumbsUp class="w-5 h-5" />
                  <span>{{ likeCount }}</span>
                  <div
                    class="absolute inset-0 bg-red-400 rounded-full opacity-0 transition-opacity duration-300 active:opacity-30"
                  ></div>
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- 로드뷰 컨테이너 -->
        <div class="overflow-hidden bg-white rounded-xl shadow-xl">
          <div ref="roadviewContainer" class="w-full h-[400px]"></div>
        </div>
      </div>

      <!-- 리뷰 섹션 -->
      <div class="bg-white rounded-xl shadow-xl p-6 h-[655px]">
        <h2 class="mb-6 text-2xl font-bold text-indigo-800">리뷰</h2>
        <div class="space-y-6 h-[550px] pr-2 overflow-y-scroll">
          <div
            v-for="review in reviews"
            @click="openDetail(review.reviewId)"
            class="p-3 h-12 bg-gray-50 rounded-xl shadow transition-shadow duration-300 hover:shadow-lg"
          >
            <div class="flex justify-between items-center mb-4">
              <div class="flex flex-1 items-center space-x-4 min-w-0">
                <h3
                  class="text-xl font-semibold text-gray-800 whitespace-nowrap"
                >
                  {{ review.title }}
                </h3>
                <p class="text-sm text-gray-500 whitespace-nowrap">
                  작성자: {{ review.userName }}
                </p>
                <p class="flex-1 text-gray-700 truncate">
                  {{ review.content }}
                </p>
              </div>
              <div>
                <p class="text-sm text-gray-500 whitespace-nowrap">
                  {{ review.regTime }}
                </p>
              </div>
              <div
                class="flex ml-4 space-x-2"
                v-show="review.userId == currentUser.value"
              ></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 리뷰 작성 모달 -->
    <div
      v-if="isModalOpen"
      class="flex fixed inset-0 z-50 justify-center items-center bg-black bg-opacity-50"
    >
      <div class="relative w-11/12 max-w-3xl bg-white rounded-xl shadow-2xl">
        <div
          class="flex justify-between items-center px-6 py-4 border-b border-gray-200"
        >
          <h2 class="text-2xl font-bold text-gray-800">
            {{ nowEditing ? "나의 리뷰" : "리뷰 작성" }}
          </h2>
          <button
            @click="closeModal"
            class="text-gray-500 transition-colors duration-200 hover:text-gray-700"
          >
            <i class="text-xl fas fa-times"></i>
          </button>
        </div>
        <div class="p-6">
          <div class="space-y-4">
            <div>
              <label class="block mb-2 text-sm font-semibold text-gray-700"
                >제목</label
              >
              <input
                v-model="title"
                type="text"
                class="px-4 py-2 w-full rounded-lg border border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                placeholder="제목을 입력하세요"
                required
              />
            </div>
            <div>
              <label class="block mb-2 text-sm font-semibold text-gray-700"
                >내용</label
              >
              <textarea
                v-model="content"
                rows="4"
                class="px-4 py-2 w-full rounded-lg border border-gray-300 resize-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                placeholder="내용을 입력하세요"
                required
              ></textarea>
            </div>
            <div>
              <label class="block mb-2 text-sm font-semibold text-gray-700">
                사진 첨부 (최대 5장)
              </label>
              <div class="space-y-4">
                <div class="flex overflow-x-scroll">
                  <!-- Review Images Display -->
                  <div
                    v-if="watchingDetails && Object.keys(reviewMap).length > 0"
                  >
                    <label
                      class="block mb-2 text-sm font-semibold text-gray-700"
                    >
                      첨부된 이미지
                    </label>
                    <div class="flex gap-2">
                      <div
                        v-for="(image, key) in reviewMap"
                        :key="key"
                        class="relative w-32 h-32 group"
                      >
                        <img
                          :src="
                            'http://localhost:8080/uhpooh/api/file' + image.path
                          "
                          class="object-cover w-full h-full rounded-lg"
                          alt="Review image"
                        />
                        <button
                          v-if="currentUser == tempReview.userId"
                          @click="deleteReviewImage(image.id)"
                          class="flex absolute top-1 right-1 justify-center items-center w-6 h-6 text-white bg-red-500 rounded-full opacity-0 transition-opacity duration-200 group-hover:opacity-100"
                        >
                          ×
                        </button>
                      </div>
                    </div>
                  </div>
                  <!-- Image Preview -->
                  <div
                    v-if="uploadedImages.length > 0"
                    class="flex gap-2 pt-7 mb-4 ml-2"
                  >
                    <div
                      v-for="(image, index) in uploadedImages"
                      :key="index"
                      class="relative w-32 h-32 group"
                    >
                      <img
                        :src="image.preview"
                        class="object-cover w-full h-full rounded-lg"
                      />
                      <button
                        v-if="
                          (!watchingDetails && !nowEditing) ||
                          (watchingDetails && currentUser == tempReview.userId)
                        "
                        @click="removeImage(index)"
                        class="flex absolute top-1 right-1 justify-center items-center w-6 h-6 text-white bg-red-500 rounded-full opacity-0 transition-opacity duration-200 group-hover:opacity-100"
                      >
                        ×
                      </button>
                    </div>
                  </div>
                </div>
                <!-- Upload Button -->
                <div
                  v-if="
                    uploadedImages.length < 5 &&
                    ((!watchingDetails && !nowEditing) ||
                      (watchingDetails && currentUser == tempReview.userId))
                  "
                  class="flex justify-center items-center p-4 rounded-lg border-2 border-gray-300 border-dashed transition-colors duration-200 hover:border-indigo-500"
                >
                  <input
                    type="file"
                    @change="handleFileUpload($event, tempReview.reviewId)"
                    accept="image/*"
                    multiple
                    class="hidden"
                    ref="fileInput"
                  />
                  <button
                    @click="$refs.fileInput.click()"
                    class="flex items-center space-x-2 text-gray-600 hover:text-indigo-600"
                  >
                    <svg
                      class="w-6 h-6"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M12 4v16m8-8H4"
                      ></path>
                    </svg>
                    <span>사진 추가하기</span>
                  </button>
                </div>
              </div>
            </div>
          </div>
          <div class="flex justify-end mt-6 space-x-3">
            <button
              v-show="!watchingDetails && !nowEditing"
              @click="addReview"
              :disabled="!isFormValid"
              class="px-4 py-2 text-white bg-indigo-600 rounded-lg transition-colors duration-200 hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              작성하기
            </button>

            <button
              v-show="watchingDetails && currentUser == tempReview.userId"
              @click="confirmEdit(tempReviewId)"
              :disabled="!isFormValid"
              class="px-3 py-1 text-sm text-indigo-600 whitespace-nowrap bg-indigo-100 rounded-lg transition-colors duration-200 hover:bg-indigo-200 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              수정
            </button>
            <button
              v-show="watchingDetails && currentUser == tempReview.userId"
              @click="deleteReview(tempReviewId)"
              class="px-3 py-1 text-sm text-red-600 whitespace-nowrap bg-red-100 rounded-lg transition-colors duration-200 hover:bg-red-200"
            >
              삭제
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 반응형 스타일 */
@media (max-width: 768px) {
  .modal-content {
    width: 90%;
    margin: 2rem auto;
  }
}

/* 애니메이션 */
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

/* 스크롤바 스타일링 */
::-webkit-scrollbar {
  width: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

/* 입력 필드 포커스 효과 */
input:focus,
textarea:focus {
  outline: none;
  box-shadow: 0 0 0 2px rgba(99, 102, 241, 0.2);
}

/* 버튼 호버 효과 */
button {
  transition: all 0.2s ease;
}

button:hover {
  transform: translateY(-1px);
}

/* 카드 호버 효과 */
.review-card {
  transition: all 0.3s ease;
}

.review-card:hover {
  transform: translateY(-2px);
}
</style>
