<script setup>
import { ref, computed } from "vue";
import { onMounted } from "vue";
import axios from "axios";
import { isAuthenticated, getUserInfo } from "@/composables/userAuth";
import { inject } from "vue";
import { ThumbsUp } from "lucide-vue-next";
import { useModal } from "@/composables/useModal";

const { showModalMessage } = useModal();

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
    showModalMessage("최대 5장까지만 업로드할 수 있습니다.");
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
    showModalMessage("이미지 삭제에 실패했습니다.");
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
          showModalMessage("해당 위치에서 이용 가능한 로드뷰가 없습니다.");
        }
      });
    } else {
      showModalMessage("오류가 발생했습니다.");
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
    await showModalMessage("리뷰가 성공적으로 작성되었습니다.");
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
    showModalMessage("리뷰가 성공적으로 삭제되었습니다.");
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
    showModalMessage("리뷰 수정 중 오류가 발생했습니다.");
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

// 새로운 예약 관련 변수들
const selectedDate = ref(new Date());
const selectedTime = ref(null);
const timeSlots = computed(() => {
  const slots = [];
  for (let i = 6; i <= 22; i++) {
    slots.push({
      value: i,
      label: `${i}:00`,
    });
  }
  return slots;
});

// 날짜가 오늘부터 1년 이내인지 확인
const isDateValid = (date) => {
  const today = new Date();
  const oneYearLater = new Date();
  oneYearLater.setFullYear(today.getFullYear() + 1);
  return date >= today && date <= oneYearLater;
};

// 결제 정보 준비
const getBookingInfo = computed(() => {
  if (!selectedDate.value || !selectedTime.value) return null;
  return {
    placeName: placeName.value,
    date:
      selectedDate.value instanceof Date
        ? selectedDate.value.toLocaleDateString()
        : selectedDate.value,
    time: `${selectedTime.value}:00`,
    price: 10000,
    totalPrice: 10000, // price와 동일한 값 추가
  };
});

const isPaymentEnabled = computed(() => {
  return selectedDate.value && selectedTime.value;
});

// 결제 처리 함수
const handlePayment = async () => {
  try {
    const bookingInfo = getBookingInfo.value;
    if (!bookingInfo) {
      showModalMessage("날짜와 시간을 선택해주세요.");
      return;
    }

    const userInfo = getUserInfo();
    if (!userInfo) {
      showModalMessage("로그인이 필요합니다.");
      return;
    }

    // 결제 요청 데이터 준비
    const paymentData = {
      amount: bookingInfo.totalPrice,
      orderId: `ORDER_${Date.now()}`,
      orderName: `${bookingInfo.placeName} 예약`,
      customerName: userInfo.name,
      successUrl: `${window.location.origin}/payment/success`,
      failUrl: `${window.location.origin}/payment/fail`,
    };

    try {
      // 백엔드에 결제 요청
      const response = await axios.post(
        "http://localhost:8080/uhpooh/api/payments/request",
        paymentData
      );
      console.log("백엔드 응답:", response.data);
    } catch (error) {
      console.warn("백엔드 요청 실패, 토스페이먼츠 SDK로 직접 진행:", error);
    }

    // 토스페이먼츠 SDK 초기화 및 결제창 호출
    const tossPayments = window.TossPayments(
      "test_ck_PBal2vxj81LajZzexNOwr5RQgOAN"
    );
    await tossPayments.requestPayment("카드", paymentData);
  } catch (error) {
    console.error("결제 초기화 중 오류 발생:", error);
    showModalMessage("결제 처리 중 오류가 발생했습니다.");
  }
};
</script>

<template>
  <div
    class="min-h-screen bg-gradient-to-br from-blue-50 via-indigo-50 to-purple-50"
  >
    <div class="container px-4 py-12 mx-auto">
      <div class="grid grid-cols-12 gap-8">
        <!-- 왼쪽 섹션 (8/12) -->
        <div class="col-span-8 space-y-8">
          <!-- 장소 정보 카드 -->
          <div
            class="p-8 bg-white bg-opacity-90 rounded-2xl shadow-xl backdrop-blur-lg transition-all duration-300 transform hover:shadow-2xl"
          >
            <h1
              class="mb-6 text-4xl font-extrabold text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-purple-600"
            >
              {{ placeName }}
            </h1>
            <div class="space-y-4">
              <p class="flex items-center font-medium text-gray-700">
                <i
                  class="mr-3 text-lg text-indigo-500 fas fa-map-marker-alt"
                ></i>
                {{ addressName }}
              </p>
              <p
                v-if="phone"
                class="flex items-center font-medium text-gray-700"
              >
                <i class="mr-3 text-lg text-indigo-500 fas fa-phone"></i>
                {{ phone }}
              </p>
              <!-- 액션 버튼 그룹 -->
              <div class="flex flex-wrap gap-4 items-center mt-6">
                <a
                  :href="placeUrl"
                  target="_blank"
                  class="flex items-center px-6 py-3 text-white bg-gradient-to-r from-indigo-600 to-purple-600 rounded-xl shadow-lg transition-all duration-300 hover:scale-105 hover:shadow-xl"
                >
                  <i class="mr-2 fas fa-external-link-alt"></i>
                  카카오맵에서 보기
                </a>
                <button
                  @click="openModal"
                  class="flex items-center px-6 py-3 text-white bg-gradient-to-r from-blue-600 to-indigo-600 rounded-xl shadow-lg transition-all duration-300 hover:scale-105 hover:shadow-xl"
                >
                  <i class="mr-2 fas fa-plus"></i>
                  리뷰 작성
                </button>
                <!-- 좋아요 버튼 -->
                <button
                  v-if="!checkLike"
                  @click="addLike(tableId, currentUser)"
                  class="flex items-center px-6 py-3 text-gray-700 bg-gray-100 rounded-xl shadow-lg transition-all duration-300 hover:bg-gray-200 hover:scale-105"
                >
                  <ThumbsUp class="mr-2 w-5 h-5 text-gray-600" />
                  <span class="font-medium">{{ likeCount }}</span>
                </button>
                <button
                  v-else
                  @click="deleteLike(tableId, currentUser)"
                  class="flex items-center px-6 py-3 text-white bg-gradient-to-r from-pink-500 to-red-500 rounded-xl shadow-lg transition-all duration-300 hover:scale-105 hover:shadow-xl"
                >
                  <ThumbsUp class="mr-2 w-5 h-5" />
                  <span class="font-medium">{{ likeCount }}</span>
                </button>
              </div>
            </div>
          </div>

          <!-- 로드뷰 컨테이너 -->
          <div
            class="overflow-hidden bg-white rounded-2xl shadow-xl transition-all duration-300 transform hover:shadow-2xl roadview-wrapper"
          >
            <div
              ref="roadviewContainer"
              class="w-full h-[400px] roadview-container"
            ></div>
          </div>

          <!-- 리뷰 섹션 -->
          <div
            class="p-8 bg-white rounded-2xl shadow-xl transition-all duration-300 transform hover:shadow-2xl"
          >
            <h2
              class="mb-6 text-3xl font-bold text-transparent text-gray-800 bg-clip-text bg-gradient-to-r from-blue-600 to-purple-600"
            >
              리뷰
            </h2>
            <div class="space-y-4 max-h-[600px] overflow-y-auto pr-4">
              <div
                v-for="review in reviews"
                @click="openDetail(review.reviewId)"
                class="p-6 bg-gradient-to-r from-gray-50 to-gray-100 rounded-xl border border-gray-100 transition-all duration-300 cursor-pointer hover:shadow-md hover:scale-102"
              >
                <div class="flex justify-between items-center mb-3">
                  <h3 class="text-xl font-semibold text-gray-800">
                    {{ review.title }}
                  </h3>
                  <p class="text-sm text-gray-500">{{ review.regTime }}</p>
                </div>
                <p class="mb-2 text-sm font-medium text-indigo-600">
                  작성자: {{ review.userName }}
                </p>
                <p class="text-gray-600 line-clamp-3">{{ review.content }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 오른쪽 섹션 (4/12) - 예약 시스템 -->
        <div class="col-span-4 space-y-8">
          <div
            class="sticky top-8 p-8 bg-white rounded-2xl shadow-xl transition-all duration-300 transform hover:shadow-2xl"
          >
            <h2
              class="mb-6 text-2xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-purple-600"
            >
              예약하기
            </h2>

            <!-- 달력 -->
            <div class="mb-6">
              <label class="block mb-2 text-sm font-medium text-gray-700"
                >날짜 선택</label
              >
              <input
                type="date"
                v-model="selectedDate"
                :min="new Date().toISOString().split('T')[0]"
                :max="
                  new Date(new Date().setFullYear(new Date().getFullYear() + 1))
                    .toISOString()
                    .split('T')[0]
                "
                class="px-4 py-2 w-full rounded-xl border-2 border-gray-200 transition-colors duration-200 focus:border-indigo-500 hover:border-gray-300"
              />
            </div>

            <!-- 시간 선택 -->
            <div class="mb-6">
              <label class="block mb-2 text-sm font-medium text-gray-700"
                >시간 선택</label
              >
              <select
                v-model="selectedTime"
                @change="getBookingInfo"
                class="px-4 py-2 w-full rounded-xl border-2 border-gray-200 transition-colors duration-200 focus:border-indigo-500 hover:border-gray-300"
              >
                <option value="" disabled selected>시간을 선택하세요</option>
                <option
                  v-for="slot in timeSlots"
                  :key="slot.value"
                  :value="slot.value"
                >
                  {{ slot.label }}
                </option>
              </select>
            </div>

            <!-- 예약 정보 -->
            <div
              v-if="getBookingInfo"
              class="p-6 mb-6 bg-gradient-to-br from-gray-50 to-gray-100 rounded-xl border border-gray-200"
            >
              <h3 class="mb-4 text-lg font-semibold text-gray-800">
                예약 정보
              </h3>
              <div class="space-y-3 text-sm">
                <p class="flex justify-between">
                  <span class="font-medium text-gray-600">장소:</span>
                  <span class="text-gray-800">{{
                    getBookingInfo.placeName
                  }}</span>
                </p>
                <p class="flex justify-between">
                  <span class="font-medium text-gray-600">날짜:</span>
                  <span class="text-gray-800">{{ getBookingInfo.date }}</span>
                </p>
                <p class="flex justify-between">
                  <span class="font-medium text-gray-600">시간:</span>
                  <span class="text-gray-800">{{ getBookingInfo.time }}</span>
                </p>
                <p class="flex justify-between">
                  <span class="font-medium text-gray-600">가격:</span>
                  <span class="text-gray-800"
                    >{{ getBookingInfo.price.toLocaleString() }}원</span
                  >
                </p>
              </div>
            </div>

            <!-- 결제 버튼 -->
            <button
              @click="handlePayment"
              :disabled="!isPaymentEnabled"
              class="px-6 py-3 w-full text-white bg-gradient-to-r from-indigo-600 to-purple-600 rounded-xl shadow-lg transition-all duration-300 hover:scale-105 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:scale-100 disabled:hover:shadow-lg"
            >
              결제하기
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 리뷰 모달 -->
    <div
      v-if="isModalOpen"
      class="flex fixed inset-0 z-50 justify-center items-center bg-black bg-opacity-60 backdrop-blur-sm"
    >
      <div
        class="relative w-11/12 max-w-4xl bg-white rounded-2xl shadow-2xl transition-all duration-300 transform"
      >
        <div class="p-8">
          <div class="space-y-6">
            <!-- 제목 입력 -->
            <div>
              <label class="block text-lg font-semibold text-gray-700"
                >제목</label
              >
              <input
                v-model="title"
                type="text"
                class="px-5 py-3 w-full text-gray-700 bg-gray-50 rounded-xl border-2 border-gray-200 transition-all duration-200 focus:border-indigo-500 focus:ring-2 focus:ring-indigo-200 hover:border-gray-300"
                placeholder="제목을 입력하세요"
                required
              />
            </div>

            <!-- 내용 입력 -->
            <div>
              <label class="block text-lg font-semibold text-gray-700"
                >내용</label
              >
              <textarea
                v-model="content"
                rows="4"
                class="px-5 py-3 w-full text-gray-700 bg-gray-50 rounded-xl border-2 border-gray-200 transition-all duration-200 resize-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-200 hover:border-gray-300"
                placeholder="내용을 입력하세요"
                required
              ></textarea>
            </div>

            <!-- 이미지 업로드 섹션 -->
            <div>
              <label class="block mb-4 text-lg font-semibold text-gray-700">
                사진 첨부 (최대 5장)
              </label>
              <!-- 기존 이미지 표시 -->
              <div
                v-if="watchingDetails && Object.keys(reviewMap).length > 0"
                class="mb-6"
              >
                <div class="flex flex-wrap gap-4">
                  <div
                    v-for="(image, key) in reviewMap"
                    :key="key"
                    class="relative group"
                  >
                    <img
                      :src="
                        'http://localhost:8080/uhpooh/api/file' + image.path
                      "
                      class="object-cover w-32 h-32 rounded-xl shadow-md transition-transform duration-300 group-hover:scale-105"
                      alt="Review image"
                    />
                    <button
                      v-if="currentUser == tempReview.userId"
                      @click="deleteReviewImage(image.id)"
                      class="flex absolute top-2 right-2 justify-center items-center w-8 h-8 text-white bg-red-500 rounded-full opacity-0 transition-opacity duration-200 group-hover:opacity-100 hover:bg-red-600"
                    >
                      ×
                    </button>
                  </div>
                </div>
              </div>

              <!-- 새 이미지 미리보기 -->
              <div
                v-if="uploadedImages.length > 0"
                class="flex flex-wrap gap-4 mb-6"
              >
                <div
                  v-for="(image, index) in uploadedImages"
                  :key="index"
                  class="relative group"
                >
                  <img
                    :src="image.preview"
                    class="object-cover w-32 h-32 rounded-xl shadow-md transition-transform duration-300 group-hover:scale-105"
                  />
                  <button
                    v-if="
                      (!watchingDetails && !nowEditing) ||
                      (watchingDetails && currentUser == tempReview.userId)
                    "
                    @click="removeImage(index)"
                    class="flex absolute top-2 right-2 justify-center items-center w-8 h-8 text-white bg-red-500 rounded-full opacity-0 transition-opacity duration-200 group-hover:opacity-100 hover:bg-red-600"
                  >
                    ×
                  </button>
                </div>
              </div>

              <!-- 업로드 버튼 -->
              <div
                v-if="
                  uploadedImages.length < 5 &&
                  ((!watchingDetails && !nowEditing) ||
                    (watchingDetails && currentUser == tempReview.userId))
                "
                class="flex justify-center items-center p-8 rounded-2xl border-2 border-gray-300 border-dashed transition-all duration-300 hover:border-indigo-500 hover:bg-indigo-50/30 group"
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
                  class="flex flex-col items-center space-y-4 text-gray-500 transition-all duration-300 group-hover:text-indigo-600"
                >
                  <div
                    class="flex justify-center items-center w-16 h-16 bg-gray-100 rounded-full transition-all duration-300 group-hover:bg-indigo-100"
                  >
                    <svg
                      class="w-8 h-8"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"
                      ></path>
                    </svg>
                  </div>
                  <div class="text-center">
                    <p class="text-lg font-semibold">사진 추가하기</p>
                    <p class="text-sm text-gray-400">또는 드래그하여 업로드</p>
                  </div>
                </button>
              </div>

              <!-- 하단 버튼 그룹 -->
              <div class="flex gap-4 justify-end items-center mt-8">
                <!-- 작성하기 버튼 -->
                <button
                  v-show="!watchingDetails && !nowEditing"
                  @click="addReview"
                  :disabled="!isFormValid"
                  class="flex items-center px-8 py-4 space-x-2 text-white bg-gradient-to-r from-indigo-600 to-purple-600 rounded-xl shadow-lg transition-all duration-300 hover:shadow-xl hover:scale-105 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:scale-100 disabled:hover:shadow-lg"
                >
                  <i class="fas fa-paper-plane"></i>
                  <span>작성하기</span>
                </button>

                <!-- 수정 버튼 -->
                <button
                  v-show="watchingDetails && currentUser == tempReview.userId"
                  @click="confirmEdit(tempReviewId)"
                  :disabled="!isFormValid"
                  class="flex items-center px-8 py-4 space-x-2 text-white bg-gradient-to-r from-blue-500 to-indigo-500 rounded-xl shadow-lg transition-all duration-300 hover:shadow-xl hover:scale-105 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:scale-100"
                >
                  <i class="fas fa-edit"></i>
                  <span>수정하기</span>
                </button>

                <!-- 삭제 버튼 -->
                <button
                  v-show="watchingDetails && currentUser == tempReview.userId"
                  @click="deleteReview(tempReviewId)"
                  class="flex items-center px-8 py-4 space-x-2 text-white bg-gradient-to-r from-red-500 to-pink-500 rounded-xl shadow-lg transition-all duration-300 hover:shadow-xl hover:scale-105"
                >
                  <i class="fas fa-trash-alt"></i>
                  <span>삭제하기</span>
                </button>
              </div>
            </div>
          </div>
          <!-- 닫기 버튼 -->
          <button
            @click="closeModal"
            class="flex absolute top-4 right-4 justify-center items-center w-10 h-10 text-gray-400 rounded-full transition-all duration-300 hover:text-gray-600 hover:bg-gray-100"
          >
            <i class="text-xl fas fa-times"></i>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 스크롤바 스타일링 */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 8px;
}

::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 8px;
}

::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

/* 모달 트랜지션 */
.modal-enter-active,
.modal-leave-active {
  transition: all 0.3s ease-out;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
  transform: scale(0.95);
}

/* 입력 필드 포커스 효과 */
input:focus,
textarea:focus {
  outline: none;
  border-color: #6366f1;
  box-shadow: 0 0 0 2px rgba(99, 102, 241, 0.2);
}

/* 호버 효과 */
.hover-scale {
  transition: transform 0.2s ease;
}

.hover-scale:hover {
  transform: scale(1.02);
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .container {
    padding: 1rem;
  }

  .modal-content {
    width: 95%;
    margin: 1rem;
  }

  .button-group {
    flex-direction: column;
    gap: 1rem;
  }

  .button-group button {
    width: 100%;
  }
}
</style>
