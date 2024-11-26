<template>
  <div class="min-h-screen bg-sky-50">
    <!-- Header Section -->
    <div class="relative bg-gradient-to-r from-cyan-600 to-blue-400 px-8 pt-16 pb-24 text-white overflow-hidden">
      <div class="max-w-6xl mx-auto text-center relative z-10">
        <h1 class="text-4xl md:text-5xl font-bold mb-4">
          My 수영장 <span class="text-cyan-600">🏊‍♂️</span>
        </h1>
        <p class="text-lg text-blue-50 mb-8">
          내가 찜한 수영장과 작성한 리뷰를 한눈에 확인해보세요
        </p>
        
        <!-- Stats Cards -->
        <div class="flex justify-center gap-8 mt-8">
          <div class="px-8 py-4 rounded-2xl bg-white/10 backdrop-blur border border-white/20">
            <span class="block text-3xl font-bold mb-2">{{ likedPlacesLength }}</span>
            <span class="text-sm text-blue-50">찜한 수영장</span>
          </div>
          <div class="px-8 py-4 rounded-2xl bg-white/10 backdrop-blur border border-white/20">
            <span class="block text-3xl font-bold mb-2">{{ myReviewsLength }}</span>
            <span class="text-sm text-blue-50">작성한 리뷰</span>
          </div>
        </div>
      </div>
      
      <!-- Wave Effect -->
      <div class="absolute bottom-0 left-0 w-full">
        <svg class="w-full h-auto" viewBox="0 0 1440 120" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M0,64L48,80C96,96,192,128,288,128C384,128,480,96,576,90.7C672,85,768,107,864,122.7C960,139,1056,149,1152,133.3C1248,117,1344,75,1392,53.3L1440,32L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z" 
                fill="#f0f7ff"/>
        </svg>
      </div>
    </div>

    <!-- Main Content -->
    <div class="max-w-6xl mx-auto px-4 -mt-16 relative z-20">
      <!-- Liked Places Section -->
      <section class="bg-white rounded-3xl shadow-lg p-8 mb-8 relative">
        <div class="flex justify-between items-center mb-8">
          <div class="flex items-center gap-4">
            <h2 class="text-2xl font-bold text-cyan-600">❤️ 찜한 수영장</h2>
            <span class="px-4 py-1 bg-gradient-to-r from-cyan-600 to-blue-400 text-white rounded-full text-sm font-medium">
              {{ likedPlacesLength }}개
            </span>
          </div>
        </div>

        <!-- Loading State -->
        <div v-if="isLoading" class="flex flex-col items-center justify-center py-12">
          <div class="w-12 h-12 border-4 border-cyan-200 border-t-cyan-600 rounded-full animate-spin"></div>
          <p class="mt-4 text-gray-500">데이터를 불러오는 중입니다...</p>
        </div>

        <div v-else-if="likedPlacesLength > 0" class="relative group">
          <button 
            @click="prevLikedPage" 
            class="absolute left-0 top-1/2 -translate-y-1/2 -translate-x-4 z-10 p-2 rounded-full bg-white shadow-lg hover:bg-gray-50 opacity-0 group-hover:opacity-100 transition-opacity"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-gray-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
            </svg>
          </button>

          <button 
            @click="nextLikedPage"
            class="absolute right-0 top-1/2 -translate-y-1/2 translate-x-4 z-10 p-2 rounded-full bg-white shadow-lg hover:bg-gray-50 opacity-0 group-hover:opacity-100 transition-opacity"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-gray-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
            </svg>
          </button>

          <div class="overflow-hidden px-4">
            <div 
              class="flex gap-6 transition-transform duration-500 ease-in-out"
              :style="{ transform: `translateX(-${currentLikedPage * (300 + 24)}px)` }"
            >
              <div v-for="place in likedPlaces" 
                   :key="place.placeId"
                   @click="goPlaceBoard(place.placeId)"
                   class="w-[300px] flex-shrink-0 group rounded-2xl overflow-hidden shadow-md hover:shadow-xl transition-all duration-300 hover:-translate-y-2 cursor-pointer bg-white"
              >
                <div class="relative h-48 overflow-hidden">
                  <img
                    :src="place.image || '/public/default-pool.jpg'"
                    :alt="place.placeName"
                    class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300"
                  />
                  <div class="absolute inset-0 bg-gradient-to-t from-black/50 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300 flex items-end justify-center pb-6">
                    <span class="text-white font-medium px-6 py-2 rounded-full bg-white/20 backdrop-blur-sm">
                      자세히 보기
                    </span>
                  </div>
                </div>
                <div class="p-6">
                  <h3 class="text-lg font-semibold text-gray-800 mb-2">{{ place.placeName }}</h3>
                  <span class="text-sm text-cyan-600">📍 상세정보 보기</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Empty State -->
        <div v-else class="text-center py-16 border-2 border-dashed border-gray-200 rounded-2xl">
          <img src="@/assets/empty-heart.svg" alt="빈 하트" class="w-32 h-32 mx-auto mb-6 opacity-60" />
          <p class="text-gray-600 mb-4">아직 찜한 수영장이 없어요</p>
          <router-link 
            to="/around" 
            class="px-8 py-3 bg-gradient-to-r from-cyan-600 to-blue-400 text-white rounded-full hover:shadow-lg transition-shadow"
          >
            수영장 둘러보기
          </router-link>
        </div>
      </section>

      <!-- Reviews Section -->
      <section class="bg-white rounded-3xl shadow-lg p-8 mb-8">
        <div class="flex justify-between items-center mb-8">
          <div class="flex items-center gap-4">
            <h2 class="text-2xl font-bold text-cyan-600">✏️ 내가 쓴 리뷰</h2>
            <span class="px-4 py-1 bg-gradient-to-r from-cyan-600 to-blue-400 text-white rounded-full text-sm font-medium">
              {{ myReviewsLength }}개
            </span>
          </div>
        </div>

        <!-- Loading State -->
        <div v-if="isLoadingReviews" class="flex flex-col items-center justify-center py-12">
          <div class="w-12 h-12 border-4 border-cyan-200 border-t-cyan-600 rounded-full animate-spin"></div>
          <p class="mt-4 text-gray-500">리뷰를 불러오는 중입니다...</p>
        </div>

        <!-- Reviews Grid -->
        <div v-else-if="myReviewsLength > 0">
          <div class="flex justify-between items-center mb-6">
            <div class="flex-1"></div>
            <div class="flex gap-2 items-center">
              <button 
                v-if="currentPage > 1"
                @click="currentPage--"
                class="px-4 py-2 rounded-full bg-gray-100 text-gray-600 hover:bg-gray-200"
              >
                이전
              </button>
              <button 
                v-for="page in displayedPages" 
                :key="page"
                @click="currentPage = page"
                :class="[
                  'px-4 py-2 rounded-full transition-all duration-200',
                  currentPage === page 
                    ? 'bg-cyan-600 text-white transform scale-110' 
                    : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                ]"
              >
                {{ page }}
              </button>
              <button 
                v-if="currentPage < totalPages"
                @click="currentPage++"
                class="px-4 py-2 rounded-full bg-gray-100 text-gray-600 hover:bg-gray-200"
              >
                다음
              </button>
            </div>
          </div>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div v-for="review in paginatedReviews" 
                 :key="review.reviewId"
                 class="p-6 rounded-2xl border border-gray-100 shadow-md hover:shadow-lg transition-shadow">
              <div class="flex flex-col gap-2 mb-4 pb-4 border-b border-gray-100">
                <div class="flex justify-between items-start">
                  <h3 class="text-lg font-semibold text-gray-800">{{ review.title }}</h3>
                  <span class="text-sm text-cyan-600">{{ formatDate(review.regTime) }}</span>
                </div>
                <span class="text-sm text-cyan-600">🏊‍♂️ {{ review.placeName }}</span>
              </div>
              <p class="text-gray-600 mb-6 line-clamp-3">{{ review.content }}</p>
              <button 
                @click="goPlaceBoard(review.placeId)"
                class="px-6 py-2 bg-cyan-600 text-white rounded-full hover:bg-cyan-700 transition-colors"
              >
                수영장 보기
              </button>
            </div>
          </div>
        </div>

        <!-- Empty State -->
        <div v-else class="text-center py-16 border-2 border-dashed border-gray-200 rounded-2xl">
          <img src="@/assets/empty-review.svg" alt="빈 리뷰" class="w-32 h-32 mx-auto mb-6 opacity-60" />
          <p class="text-gray-600 mb-2">아직 작성한 리뷰가 없어요</p>
          <p class="text-sm text-gray-400">수영장을 방문하고 리뷰를 남겨보세요!</p>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import axios from "axios";
import { ref, onMounted, computed, watch } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const likedPlacesId = ref([]);
const likedPlaces = ref([]);
const myReviews = ref([]);
const myReviewsLength = computed(() => myReviews.value.length);
const likedPlacesLength = computed(() => likedPlaces.value.length);
const isLoading = ref(true);
const isLoadingReviews = ref(true);

const currentPage = ref(1);
const itemsPerPage = 4;
const currentLikedPage = ref(0);
const cardWidth = 300;
const cardGap = 24;

const maxLikedScroll = computed(() => {
  if (!likedPlaces.value.length) return 0;
  const totalWidth = likedPlaces.value.length * (cardWidth + cardGap) - cardGap;
  const visibleWidth = (cardWidth + cardGap) * 3;
  return Math.max(0, totalWidth - visibleWidth);
});

const totalPages = computed(() => Math.ceil(myReviews.value.length / itemsPerPage));

const displayedPages = computed(() => {
  const total = totalPages.value;
  if (total <= 3) return Array.from({ length: total }, (_, i) => i + 1);
  
  let start = currentPage.value - 1;
  if (start < 1) start = 1;
  if (start > total - 2) start = total - 2;
  
  return Array.from({ length: 3 }, (_, i) => start + i);
});

const paginatedReviews = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return myReviews.value.slice(start, end);
});

const prevLikedPage = () => {
  if (currentLikedPage.value > 0) {
    currentLikedPage.value--;
  }
};

const nextLikedPage = () => {
  const nextScroll = (currentLikedPage.value + 1) * (cardWidth + cardGap);
  if (nextScroll < maxLikedScroll.value) {
    currentLikedPage.value++;
  } else if (maxLikedScroll.value > currentLikedPage.value * (cardWidth + cardGap)) {
    // 마지막 스크롤 위치로 이동
    currentLikedPage.value = Math.ceil(maxLikedScroll.value / (cardWidth + cardGap));
  }
};

const formatDate = (dateString) => {
  const date = new Date(dateString);
  return `${date.getFullYear()}년 ${date.getMonth() + 1}월 ${date.getDate()}일`;
};

const goPlaceBoard = async (placeId) => {
  const response = await axios.get(
    "http://localhost:8080/uhpooh/api/place/getplacebyplaceid/" + placeId
  );
  localStorage.setItem("currentPlace", response.data.placeName);
  router.push("/placeBoard");
};

const fetchMyReviews = async () => {
  try {
    const userId = localStorage.getItem("userId");
    if (!userId) {
      console.error("User ID not found");
      return;
    }
    const response = await axios.get(
      `http://localhost:8080/uhpooh/api/review/search/writer`,
      {
        params: {
          userId: userId,
          size: 100000  // 매우 큰 숫자로 설정하여 모든 리뷰를 가져옴
        },
      }
    );

    if (response.data) {
      // Fetch place names for each review
      const reviewsWithPlaceNames = await Promise.all(
        response.data.data.items.map(async (review) => {
          try {
            const placeResponse = await axios.get(
              `http://localhost:8080/uhpooh/api/place/getplacebyplaceid/${review.placeId}`
            );
            return {
              ...review,
              placeName: placeResponse.data.placeName
            };
          } catch (error) {
            console.error("Error fetching place name:", error);
            return {
              ...review,
              placeName: "알 수 없는 수영장"
            };
          }
        })
      );
      myReviews.value = reviewsWithPlaceNames;
      console.log("작성한 리뷰들", myReviews.value);
    } else {
      console.error("Invalid review data format");
      myReviews.value = [];
    }
  } catch (error) {
    console.error("Error fetching reviews:", error);
    myReviews.value = [];
  } finally {
    isLoadingReviews.value = false;
  }
};

const fetchLikedPlaces = async () => {
  try {
    const userId = localStorage.getItem("userId");
    if (!userId) {
      console.error("User ID not found");
      return;
    }
    const response = await axios.get(
      "http://localhost:8080/uhpooh/api/place/getplaceidbyuserid/" + userId
    );
    if (response.data && Array.isArray(response.data)) {
      likedPlacesId.value = response.data;
    } else {
      console.error("Invalid liked places data format");
      likedPlacesId.value = [];
    }
  } catch (error) {
    console.error("Error fetching liked places:", error);
    likedPlacesId.value = [];
  }

  likedPlaces.value = [];
  
  const placePromises = likedPlacesId.value.map(async (placeId) => {
    try {
      const response = await axios.get(
        "http://localhost:8080/uhpooh/api/place/getplacebyplaceid/" + placeId
      );
      if (response && response.data) {
        const place = response.data;

        try {
          const imageResponse = await axios.get(
            `https://dapi.kakao.com/v2/search/image?query=${encodeURIComponent(
              place.placeName
            )}`,
            {
              headers: {
                Authorization: "KakaoAK 51f3e2722310a3c53f4698c4a1d57f30",
              },
            }
          );

          if (
            imageResponse.data.documents &&
            imageResponse.data.documents.length > 0
          ) {
            place.image = imageResponse.data.documents[0].image_url;
            await new Promise((resolve, reject) => {
              const img = new Image();
              img.onload = resolve;
              img.onerror = () => {
                place.image = "/public/default-pool.jpg";
                resolve();
              };
              img.src = place.image;
            });
          } else {
            place.image = "/public/default-pool.jpg";
          }
        } catch (imageError) {
          console.error("Error fetching image for place:", imageError);
          place.image = "/public/default-pool.jpg";
        }
        return place;
      }
    } catch (error) {
      console.error("Error fetching place", placeId, ":", error);
      return null;
    }
  });

  try {
    const places = await Promise.all(placePromises);
    likedPlaces.value = places.filter(place => place !== null);
  } catch (error) {
    console.error("Error loading places:", error);
    likedPlaces.value = [];
  } finally {
    isLoading.value = false;
  }
};

onMounted(async () => {
  await Promise.all([fetchMyReviews(), fetchLikedPlaces()]);
});

watch(() => myReviews.value.length, () => {
  if (currentPage.value > totalPages.value) {
    currentPage.value = totalPages.value;
  }
});
</script>