<script setup>
import { ref } from "vue";
import { onMounted } from "vue";
import axios from "axios";
import { isAuthenticated, getUserInfo } from "@/composables/userAuth";
import { inject } from "vue";

// 좋아요 버튼 상태
const isLiked = ref(false);
const likeCount = ref(123);
const currentPlace = ref("");
const placeName = ref("");
const addressName = ref("");
const placeUrl = ref("");
const phone = ref("");
const categoryGroupName = ref("");
const mapContainer = ref(null);
const isLoading = ref(false);
const hasSearched = ref(false);
let map;
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

function searchPlaces() {
  isLoading.value = true;
  const ps = new kakao.maps.services.Places();
  const bounds = new kakao.maps.LatLngBounds();

  ps.keywordSearch(currentPlace.value + "+수영장", (data, status) => {
    isLoading.value = false;
    hasSearched.value = true;
    console.log(data[0]);
    if (status === kakao.maps.services.Status.OK) {
      const marker = new kakao.maps.Marker({
        position: new kakao.maps.LatLng(data[0].y, data[0].x),
      });
      marker.setMap(map);

      bounds.extend(marker.getPosition());

      // map.setBounds(bounds);
      placeName.value = data[0].place_name;
      addressName.value = data[0].address_name;
      placeUrl.value = data[0].place_url;
      phone.value = data[0].phone;
    } else {
      alert("오류가 발생했습니다.");
      hasSearched.value = false;
    }
  });
}

onMounted(async () => {
  // Get the current place from localStorage
  currentPlace.value = localStorage.getItem("currentPlace");

  // Only search if we have a valid place name
  if (currentPlace.value) {
    searchPlaces();
  } else {
    console.warn("No place name found in localStorage");
  }
});

// 글 작성

// 장소 정보

// 좋아요 버튼 클릭 핸들러
const toggleLike = () => {
  isLiked.value = !isLiked.value;
  likeCount.value += isLiked.value ? 1 : -1;
};
</script>

<template>
  <div class="p-6 min-h-screen bg-gray-50">
    <!-- 장소 정보 -->
    <div class="p-6 mb-6 bg-white rounded-lg shadow-lg">
      <h1 class="mb-4 text-3xl font-bold">{{ placeName }}</h1>
      <p class="mb-2 text-gray-600">주소:{{ addressName }}</p>
      <p class="mb-2 text-gray-600">전화번호:{{ phone }}</p>
      <p class="mb-2 text-gray-600">상세정보:{{ placeUrl }}</p>
      <p class="text-gray-700"></p>
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

    <!-- 사진 업로드 -->
    <div class="p-6 mb-6 bg-white rounded-lg shadow-lg">
      <h2 class="mb-4 text-2xl font-bold">사진 업로드</h2>
      <input
        type="file"
        accept="image/*"
        multiple
        @change="handleFileUpload"
        class="mb-4"
      />
      <div class="grid grid-cols-3 gap-4">
        <img
          v-for="(image, index) in uploadedImages"
          :key="index"
          :src="image"
          alt="Uploaded"
          class="object-cover w-full h-32 rounded-lg shadow"
        />
      </div>
    </div>

    <!-- 글 작성 -->
    <div class="p-6 mb-6 bg-white rounded-lg shadow-lg">
      <h2 class="mb-4 text-2xl font-bold">후기 작성</h2>
      <textarea
        v-model="content"
        rows="5"
        placeholder="이 장소에 대한 후기를 작성해주세요."
        class="p-4 w-full rounded-lg border border-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-500"
      ></textarea>
      <button
        @click="alert(`후기 내용: ${content}`)"
        class="px-6 py-2 mt-4 text-white bg-blue-500 rounded-lg hover:bg-blue-600"
      >
        작성 완료
      </button>
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
</style>
