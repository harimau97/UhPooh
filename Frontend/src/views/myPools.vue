<template>
  <div class="my-pools">
    <div class="header-section">
      <h1> My 수영장🏊‍♂️</h1>
      <p class="subtitle">내가 찜한 수영장과 작성한 리뷰를 한눈에 확인해보세요</p>
    </div>
    
    <!-- Liked Places Section -->
    <div class="section liked-places">
      <div class="section-header">
        <h2>❤️ 찜한 수영장</h2>
        <span class="count-badge">{{ likedPlaces.length }}개</span>
      </div>
      <div class="places-grid" v-if="likedPlaces.length > 0">
        <div v-for="place in likedPlaces" :key="place.id" class="place-card">
          <div class="image-container">
            <img :src="place.image" :alt="place.name" class="place-image">
            <div class="overlay">
              <button class="view-details">자세히 보기</button>
            </div>
          </div>
          <div class="place-info">
            <h3>{{ place.name }}</h3>
            <p class="address">📍 {{ place.address }}</p>
            <div class="tags">
              <span class="tag">{{ place.type }}</span>
              <span class="tag">{{ place.price }}</span>
            </div>
            <div class="rating">
              <div class="stars">
                <span v-for="n in Math.floor(place.rating)" :key="n" class="star">⭐</span>
              </div>
              <span class="rating-text">{{ place.rating }}/5.0</span>
            </div>
          </div>
        </div>
      </div>
      <div v-else class="empty-state">
        <img src="@/assets/empty-heart.svg" alt="빈 하트" class="empty-icon">
        <p>아직 찜한 수영장이 없어요</p>
        <router-link to="/around" class="browse-button">수영장 둘러보기</router-link>
      </div>
    </div>

    <!-- My Reviews Section -->
    <div class="section my-reviews">
      <div class="section-header">
        <h2>✏️ 내가 쓴 리뷰</h2>
        <span class="count-badge">{{ myReviews.length }}개</span>
      </div>
      <div class="reviews-list" v-if="myReviews.length > 0">
        <div v-for="review in myReviews" :key="review.id" class="review-card">
          <div class="review-header">
            <div class="place-details">
              <h3>{{ review.placeName }}</h3>
              <p class="review-date">{{ formatDate(review.createdAt) }}</p>
            </div>
            <div class="review-rating">
              <div class="stars">
                <span v-for="n in review.rating" :key="n" class="star">⭐</span>
              </div>
              <span class="rating-text">{{ review.rating }}/5.0</span>
            </div>
          </div>
          <p class="review-content">{{ review.content }}</p>
          <div class="review-footer">
            <button class="edit-button">수정하기</button>
            <button class="delete-button">삭제하기</button>
          </div>
        </div>
      </div>
      <div v-else class="empty-state ">
        <img src="@/assets/empty-review.svg" alt="빈 리뷰" class="empty-icon">
        <p>아직 작성한 리뷰가 없어요</p>
        <!-- <router-link to="/placeBoard" class="browse-button">리뷰 작성하러 가기</router-link> -->
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { ref, onMounted } from 'vue';

export default {
  name: 'MyPools',
  setup() {
    const likedPlaces = ref([]);
    const myReviews = ref([]);

    const fetchMyReviews = async () => {
      try {
        const userId = localStorage.getItem("userId");
        if (!userId) {
          console.error('User ID not found');
          return;
        }
        const response = await axios.get(`http://localhost:8080/uhpooh/api/user/${userId}/reviews`);
        if (response.data && Array.isArray(response.data)) {
          myReviews.value = response.data;
        } else {
          console.error('Invalid review data format');
          myReviews.value = [];
        }
      } catch (error) {
        console.error('Error fetching reviews:', error);
        myReviews.value = [];
      }
    };

    const fetchLikedPlaces = async () => {
      try {
        const userId = localStorage.getItem("userId");
        if (!userId) {
          console.error('User ID not found');
          return;
        }
        const response = await axios.get(`http://localhost:8080/uhpooh/api/user/${userId}/liked-places`);
        if (response.data && Array.isArray(response.data)) {
          likedPlaces.value = response.data;
        } else {
          console.error('Invalid liked places data format');
          likedPlaces.value = [];
        }
      } catch (error) {
        console.error('Error fetching liked places:', error);
        likedPlaces.value = [];
      }
    };

    const formatDate = (dateString) => {
      const date = new Date(dateString);
      return `${date.getFullYear()}년 ${date.getMonth() + 1}월 ${date.getDate()}일`;
    };

    onMounted(() => {
      fetchLikedPlaces();
      fetchMyReviews();
    });

    return {
      likedPlaces,
      myReviews,
      formatDate
    };
  }
};
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
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
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
  background: #f0f0f0;
  padding: 0.3rem 0.8rem;
  border-radius: 20px;
  margin-left: 1rem;
  font-size: 0.9rem;
  color: #666;
}

.places-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1.5rem;
}

.place-card {
  background: white;
  border-radius: 15px;
  overflow: hidden;
  transition: transform 0.2s, box-shadow 0.2s;
  border: 1px solid #eee;
}

.place-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 15px rgba(0, 0, 0, 0.1);
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
  flex-direction: column;
  gap: 1.2rem;
}

.review-card {
  background: white;
  border-radius: 15px;
  padding: 1.5rem;
  border: 1px solid #eee;
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

.edit-button, .delete-button {
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

@media (max-width: 768px) {
  .my-pools {
    padding: 1rem;
  }

  .places-grid {
    grid-template-columns: 1fr;
  }

  .section {
    padding: 1.5rem;
  }

  h1 {
    font-size: 2rem;
  }
}
</style>