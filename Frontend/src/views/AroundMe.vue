<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";
import { isAuthenticated, getUserInfo } from "@/composables/userAuth";
import { inject } from "vue";
import { useModal } from "@/composables/useModal";

const { showModalMessage } = useModal();

const userAddress = ref(localStorage.getItem("targetAddress"));
const keyword = ref(userAddress.value);
const mapContainer = ref(null);
const isLoading = ref(false);
const hasSearched = ref(false);
const router = useRouter();
const showModal = ref(false);
const modalMessage = ref("");

let map;
userAddress.value = localStorage.getItem("userAddress");
var iwContent =
  '<div style="display:flex; justify-content:center; padding:10px; color:#333; white-space:normal; max-width:200px;"></div>';
var infowindow = new kakao.maps.InfoWindow({
  content: iwContent,
  removable: true,
});

const closeModal = () => {
  showModal.value = false;
};

onMounted(() => {
  const mapOptions = {
    center: new kakao.maps.LatLng(37.5665, 126.978),
    level: 3,
  };
  map = new kakao.maps.Map(mapContainer.value, mapOptions);
  if (localStorage.getItem("targetAddress") !== null) {
    searchPlaces();
  }
  if (localStorage.getItem("tempKeyword") !== null) {
    console.log(localStorage.getItem("tempKeyword"));
    keyword.value = localStorage.getItem("tempKeyword");
    searchPlaces();
    localStorage.removeItem("tempKeyword");
  }
  localStorage.removeItem("targetAddress");
});

const handleClick = async (currentPlace) => {
  await localStorage.removeItem("currentPlace");
  await localStorage.setItem("currentPlace", currentPlace);
  callBoard();
};

function searchPlaces() {
  if (!keyword.value || keyword.value.trim() === "") {
    showModalMessage("검색어를 입력해주세요.");
    return;
  }

  isLoading.value = true;
  const ps = new kakao.maps.services.Places();
  const bounds = new kakao.maps.LatLngBounds();

  ps.keywordSearch(keyword.value + "+수영장", (data, status) => {
    isLoading.value = false;
    hasSearched.value = true;

    if (status === kakao.maps.services.Status.OK) {
      const resultDiv = document.getElementById("results");
      resultDiv.innerHTML = "";

      // 모든 마커의 위치를 저장할 배열
      const positions = [];

      data.forEach((place) => {
        if (place.phone !== "") {
          console.log(place.id);
          const position = new kakao.maps.LatLng(place.y, place.x);
          positions.push(position);

          const marker = new kakao.maps.Marker({
            position: position,
          });
          marker.setMap(map);
          const itemEl = document.createElement("div");
          itemEl.id = "result-item";
          itemEl.className = "result-card";

          itemEl.innerHTML = `
            <div class="flex items-start space-x-4">
              <div class="flex-shrink-0">
                <div class="flex justify-center items-center w-12 h-12 bg-blue-100 rounded-full">
                  <svg class="w-6 h-6 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"/>
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"/>
                  </svg>
                </div>
              </div>
              <div class="flex-1">
                <h3 class="text-lg font-semibold text-gray-900">${place.place_name}</h3>
                <p class="mt-1 text-sm text-gray-500">${place.road_address_name}</p>
                <p class="mt-1 text-sm text-gray-500">📞 ${place.phone}</p>
                <p class="mt-1 text-sm text-gray-500"></p>
              </div>
            </div>
          `;
          resultDiv.appendChild(itemEl);

          kakao.maps.event.addListener(marker, "click", function () {
            const pos = marker.getPosition();
            map.panTo(pos);
            infowindow.open(map, marker);
            infowindow.setContent(`
              <div class="map-info-window">
                <div class="info-content">
                  <a class="place-name" target="_blank" href="${place.place_url}">
                    ${place.place_name}
                  </a>
                  <p class="place-phone">
                    <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z"/>
                    </svg>
                    ${place.phone}
                  </p>
                </div>
                <div class="info-arrow"></div>
              </div>
            `);
          });
          itemEl.addEventListener("click", () => handleClick(place.place_name));
        }
      });
      resultDiv.scrollTop = 0;

      // 모든 마커가 보이도록 지도 범위 재설정
      if (positions.length > 0) {
        positions.forEach((position) => bounds.extend(position));
        map.setBounds(bounds);

        // 약간의 지연 후에 지도 리사이즈 실행
        setTimeout(() => {
          map.relayout();
          map.setBounds(bounds);
        }, 100);
      }
    } else {
      showModalMessage("검색 결과가 없습니다.");
      hasSearched.value = false;
    }
  });
}

async function callBoard() {
  await localStorage.setItem("tempKeyword", keyword.value);
  router.push("/placeBoard");
}

function handleKeyPress(event) {
  if (event.key === "Enter") {
    searchPlaces();
  }
}
</script>

<template>
  <div
    class="flex flex-col h-screen bg-gradient-to-b from-blue-50 to-white overflow-x-hidden"
  >
    <div class="flex flex-col flex-1 p-4 overflow-hidden">
      <!-- Search Container -->
      <div class="relative mx-auto mb-4 w-full max-w-3xl">
        <input
          class="px-6 w-full h-12 text-lg placeholder-gray-400 text-gray-700 rounded-full border-2 border-gray-200 shadow-sm transition duration-200 ease-in-out focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-200"
          type="text"
          id="search-input"
          placeholder="수영장 주변 지하철역을 검색해주세요"
          v-model="keyword"
          @keypress="handleKeyPress"
          @input="keyword = $event.target.value"
        />
        <button
          class="absolute right-4 top-1/2 p-2 text-gray-400 transition duration-200 ease-in-out transform -translate-y-1/2 hover:text-blue-500"
          @click="searchPlaces"
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
              d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
            />
          </svg>
        </button>
      </div>

      <!-- Loading Indicator -->
      <div
        v-if="isLoading"
        class="absolute top-1/2 left-1/2 z-50 transform -translate-x-1/2 -translate-y-1/2"
      >
        <div
          class="w-8 h-8 rounded-full border-b-2 border-blue-500 animate-spin"
        ></div>
      </div>

      <!-- Content Container -->
      <div
        :class="[
          'flex-1 transition-all duration-500 ease-in-out overflow-hidden',
          hasSearched ? 'content-container-searched' : 'content-container',
        ]"
      >
        <!-- Map Container -->
        <div
          :class="[
            'map-container transition-all duration-500 ease-in-out',
            hasSearched ? 'map-container-searched' : '',
          ]"
        >
          <div class="w-full h-full" id="map" ref="mapContainer"></div>
        </div>

        <!-- Results Container -->
        <div v-show="hasSearched" id="results" class="results-container"></div>
      </div>
    </div>
  </div>
</template>

<style>
.content-container {
  width: 100%;
  height: 100%;
  position: relative;
  overflow: hidden;
}

.content-container-searched {
  width: 100%;
  height: 80vh;
  display: flex;
  gap: 1rem;
  position: relative;
  overflow: hidden;
}

.map-container {
  width: 100%;
  height: 80vh;
  background: white;
  border-radius: 1rem;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: all 0.5s ease;
  flex-shrink: 1;
  position: relative;
}

.map-container-searched {
  width: 60%;
}

.results-container {
  width: 40%;
  height: 80vh;
  background: white;
  border-radius: 1rem;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
  overflow-x: hidden;
  opacity: 0;
  animation: slideIn 0.5s ease forwards;
}

@keyframes slideIn {
  from {
    transform: translateX(20px);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

.result-card {
  padding: 1rem;
  border-bottom: 1px solid #f1f1f1;
  cursor: pointer;
  transition: all 0.2s ease;
}

.result-card:last-child {
  border-bottom: none;
}

.result-card:hover {
  background-color: #f8f9fa;
  transform: scale(1.01);
}

.map-info-window {
  background-color: white;
  border-radius: 0.5rem;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  padding: 1rem;
  position: relative;
  min-width: 200px;
}

.info-content {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.place-name {
  color: #2563eb;
  font-weight: 600;
  font-size: 1.125rem;
  display: block;
  text-decoration: none;
}

.place-name:hover {
  color: #1d4ed8;
}

.place-phone {
  color: #4b5563;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
}

.info-arrow {
  position: absolute;
  bottom: -0.5rem;
  left: 50%;
  transform: translateX(-50%) rotate(45deg);
  width: 1rem;
  height: 1rem;
  background-color: white;
}

#results::-webkit-scrollbar {
  width: 0.5rem;
}

#results::-webkit-scrollbar-track {
  background-color: #f3f4f6;
  border-radius: 9999px;
}

#results::-webkit-scrollbar-thumb {
  background-color: #d1d5db;
  border-radius: 9999px;
}

#results::-webkit-scrollbar-thumb:hover {
  background-color: #9ca3af;
}

@media (max-width: 768px) {
  .content-container-searched {
    flex-direction: column;
    height: calc(100vh - 80px);
  }

  .map-container-searched {
    width: 100%;
    height: 100%;
    flex: 0 0 50%;
  }

  .results-container {
    width: 100%;
    height: 50%;
  }
}
</style>
