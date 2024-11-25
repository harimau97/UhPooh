<template>
  <div class="my-pools">
    <div class="header-section">
      <h1>My 수영장🏊‍♂️</h1>
      <p class="subtitle">
        내가 찜한 수영장과 작성한 리뷰를 한눈에 확인해보세요
      </p>
    </div>

    <!-- Liked Places Section -->
    <div v-if="isLoading">
      <!-- Show a loading indicator -->
      데이터를 불러오는 중입니다...
    </div>

    <div v-else class="section liked-places">
      <div class="section-header">
        <h2>❤️ 찜한 수영장</h2>
        <span class="count-badge">{{ likedPlacesLength }}개</span>
      </div>
      <div class="places-grid" v-if="likedPlacesLength > 0">
        <div
          v-for="place in likedPlaces"
          :key="place.placeId"
          class="place-card"
        >
          <div class="image-container">
            <img
              v-if="place.image"
              :src="place.image"
              :alt="place.placeName"
              class="place-image"
            />
            <img
              v-else
              src="/public/default-pool.jpg"
              alt="빈 수영장"
              class="place-image"
            />
            <div class="overlay">
              <button @click="goPlaceBoard(place.placeId)" class="view-details">
                자세히 보기
              </button>
            </div>
          </div>
          <div class="place-info">
            <h3>{{ place.placeName }}</h3>
            <!-- <p class="address">📍 {{ place.address }}</p> -->
            <!-- <div class="tags">
              <span class="tag">{{ place.type }}</span>
              <span class="tag">{{ place.price }}</span>
            </div> -->
            <div class="rating"></div>
          </div>
        </div>
      </div>
      <div v-else class="empty-state">
        <img src="@/assets/empty-heart.svg" alt="빈 하트" class="empty-icon" />
        <p>아직 찜한 수영장이 없어요</p>
        <router-link to="/around" class="browse-button"
          >수영장 둘러보기</router-link
        >
      </div>
    </div>

    <!-- My Reviews Section -->
    <div v-if="isLoadingReviews">
      <!-- Show a loading indicator -->
      데이터를 불러오는 중입니다...
    </div>
    <div v-else class="section my-reviews">
      <div class="section-header">
        <h2>✏️ 내가 쓴 리뷰</h2>
        <span class="count-badge">{{ myReviewsLength }}개</span>
      </div>
      <div class="reviews-list" v-if="myReviewsLength > 0">
        <div
          v-for="review in myReviews"
          :key="review.reviewId"
          class="review-card"
        >
          <div class="review-header">
            <div class="place-details">
              <h3>{{ review.title }}</h3>
              <p class="review-date">{{ formatDate(review.regTime) }}</p>
            </div>
            <div class="review-rating">
              <div class="stars">
                <!-- <span v-for="n in review.rating" :key="n" class="star">⭐</span> -->
              </div>
              <!-- <span class="rating-text">{{ review.rating }}/5.0</span> -->
            </div>
          </div>
          <p class="review-content">{{ review.content }}</p>
          <div class="review-footer">
            <button @click="goPlaceBoard(review.placeId)" class="edit-button">
              보러가기
            </button>
          </div>
        </div>
      </div>
      <div v-else class="empty-state">
        <img src="@/assets/empty-review.svg" alt="빈 리뷰" class="empty-icon" />
        <p>아직 작성한 리뷰가 없어요</p>
        <!-- <router-link to="/placeBoard" class="browse-button">리뷰 작성하러 가기</router-link> -->
      </div>
    </div>
  </div>
</template>

<script setup>
import axios from "axios";
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
const router = useRouter();

const likedPlacesId = ref([]);
const likedPlaces = ref([]);
const myReviews = ref([]);
const myReviewsLength = computed(() => myReviews.value.length);
const likedPlacesLength = computed(() => likedPlaces.value.length);
const isLoading = ref(true);
const isLoadingReviews = ref(true);
const imgPath = ref("");

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
        },
      }
    );

    if (response.data) {
      myReviews.value = response.data.data.items;
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
      console.log(likedPlacesId.value.length);
    } else {
      console.error("Invalid liked places data format");
      likedPlacesId.value = [];
    }
  } catch (error) {
    console.error("Error fetching liked places:", error);
    likedPlacesId.value = [];
  }

  // Clear existing places
  likedPlaces.value = [];
  
  // Create an array of promises for all place fetches
  const placePromises = likedPlacesId.value.map(async (placeId) => {
    try {
      const response = await axios.get(
        "http://localhost:8080/uhpooh/api/place/getplacebyplaceid/" + placeId
      );
      if (response && response.data) {
        const place = response.data;

        // Fetch image from Kakao Image Search API
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
            // Preload the image
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
    // Wait for all places and their images to be loaded
    const places = await Promise.all(placePromises);
    // Filter out any null values and update likedPlaces
    likedPlaces.value = places.filter(place => place !== null);
  } catch (error) {
    console.error("Error loading places:", error);
    likedPlaces.value = [];
  } finally {
    isLoading.value = false;
  }
  
  console.log("좋아요를 누른 장소들", likedPlaces.value);
  console.log(likedPlacesLength.value);
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

onMounted(async () => {
  await fetchMyReviews();
  await fetchLikedPlaces();
});
</script>

<style scoped>
.my-pools {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
  background-color: #f8f9fa;
  min-height: 100vh;
}

.header-section {
  text-align: center;
  margin-bottom: 3rem;
}

h1 {
  font-size: 2.5rem;
  color: #1a1a1a;
  margin-bottom: 0.5rem;
  font-weight: 700;
}

.subtitle {
  color: #666;
  font-size: 1.1rem;
}

.section {
  background: white;
  border-radius: 20px;
  padding: 2rem;
  margin-bottom: 2rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border: 3px solid #d1d5db;
}

.section-header {
  display: flex;
  align-items: center;
  margin-bottom: 1.5rem;
}

.section-header h2 {
  font-size: 1.5rem;
  color: #333;
  margin: 0;
}

.count-badge {
  background: linear-gradient(135deg, #63b8f1 0%, #4698e5 100%);
  padding: 0.4rem 1rem;
  border-radius: 20px;
  margin-left: 1rem;
  font-size: 0.9rem;
  color: white;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(229, 200, 70, 0.3);
  border: 2px solid rgba(255, 255, 255, 0.2);
  display: inline-flex;
  align-items: center;
  transition: all 0.3s ease;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  position: relative;
  overflow: hidden;
}

.count-badge::before {
  content: "";
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(255, 255, 255, 0.2),
    transparent
  );
  transition: 0.5s;
}

.count-badge:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(79, 70, 229, 0.4);
}

.count-badge:hover::before {
  left: 100%;
}

.places-grid {
  display: flex;
  gap: 1.5rem;
  overflow-x: auto;
  padding: 0.5rem;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: thin;
}

.place-card {
  flex: 0 0 280px;
  background: white;
  border-radius: 15px;
  overflow: hidden;
  transition: all 0.3s ease;
  border: 3px solid #d1d5db;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.place-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
  border-color: #9ca3af;
}

.image-container {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.place-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.image-container:hover .overlay {
  opacity: 1;
}

.image-container:hover .place-image {
  transform: scale(1.1);
}

.view-details {
  background: white;
  color: #333;
  border: none;
  padding: 0.8rem 1.5rem;
  border-radius: 25px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.3s;
}

.view-details:hover {
  background: #f0f0f0;
}

.place-info {
  padding: 1.2rem;
}

.place-info h3 {
  margin: 0 0 0.5rem 0;
  font-size: 1.2rem;
  color: #333;
}

.address {
  color: #666;
  font-size: 0.9rem;
  margin-bottom: 0.8rem;
}

.tags {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 0.8rem;
}

.tag {
  background: #f0f0f0;
  padding: 0.3rem 0.8rem;
  border-radius: 15px;
  font-size: 0.8rem;
  color: #666;
}

.rating {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.stars {
  display: flex;
  gap: 2px;
}

.star {
  font-size: 0.9rem;
}

.rating-text {
  color: #666;
  font-size: 0.9rem;
}

.reviews-list {
  display: flex;
  gap: 1.5rem;
  overflow-x: auto;
  padding: 0.5rem;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: thin;
}

.review-card {
  flex: 0 0 350px;
  background: white;
  border-radius: 15px;
  padding: 1.5rem;
  border: 3px solid #d1d5db;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.review-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
  border-color: #9ca3af;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
}

.place-details h3 {
  margin: 0;
  font-size: 1.2rem;
  color: #333;
}

.review-date {
  color: #888;
  font-size: 0.9rem;
  margin: 0.3rem 0 0 0;
}

.review-content {
  color: #444;
  line-height: 1.6;
  margin: 1rem 0;
}

.review-footer {
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
}

.edit-button,
.delete-button {
  padding: 0.5rem 1rem;
  border-radius: 8px;
  border: none;
  font-size: 0.9rem;
  cursor: pointer;
  transition: background 0.3s;
}

.edit-button {
  background: #e9ecef;
  color: #495057;
}

.delete-button {
  background: #fff0f0;
  color: #dc3545;
}

.edit-button:hover {
  background: #dee2e6;
}

.delete-button:hover {
  background: #ffe3e3;
}

.empty-state {
  text-align: center;
  padding: 3rem 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border: 3px dashed #d1d5db;
  border-radius: 15px;
  background: #f9fafb;
}

.empty-icon {
  width: 120px;
  height: 120px;
  margin-bottom: 1.5rem;
  opacity: 0.6;
}

.empty-state p {
  color: #666;
  margin-bottom: 1rem;
}

.browse-button {
  display: inline-block;
  background: #007bff;
  color: white;
  padding: 0.8rem 1.5rem;
  border-radius: 25px;
  text-decoration: none;
  font-weight: 600;
  transition: background 0.3s;
}

.browse-button:hover {
  background: #0056b3;
}

.places-grid::-webkit-scrollbar,
.reviews-list::-webkit-scrollbar {
  height: 8px;
}

.places-grid::-webkit-scrollbar-track,
.reviews-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.places-grid::-webkit-scrollbar-thumb,
.reviews-list::-webkit-scrollbar-thumb {
  background: #888;
  border-radius: 4px;
}

.places-grid::-webkit-scrollbar-thumb:hover,
.reviews-list::-webkit-scrollbar-thumb:hover {
  background: #666;
}

@media (max-width: 768px) {
  .my-pools {
    padding: 1rem;
  }

  .section {
    padding: 1.5rem;
  }

  h1 {
    font-size: 2rem;
  }
}
</style>
