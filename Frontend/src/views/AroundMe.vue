<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";
import { isAuthenticated, getUserInfo } from "@/composables/userAuth";
import { inject } from "vue";

// const isLoggined = inject("isLoggedIn");
// isAuthenticated();
// getUserInfo();

const userAddress = ref(localStorage.getItem("targetAddress"));
console.log(userAddress.value);
const keyword = ref(userAddress.value);

const mapContainer = ref(null);

let map;
userAddress.value = localStorage.getItem("userAddress");
var iwContent =
  '<div style="display:flex; justify-content:center; padding:10px;  color:#333; white-space:normal; max-width:200px;"></div>';
var infowindow = new kakao.maps.InfoWindow({
  content: iwContent,
  removable: true,
});

onMounted(() => {
  const mapOptions = {
    center: new kakao.maps.LatLng(37.5665, 126.978), // 서울 중심 좌표
    level: 3,
  };
  map = new kakao.maps.Map(mapContainer.value, mapOptions);
  if (localStorage.getItem("targetAddress") !== null) {
    searchPlaces();
  }
  localStorage.removeItem("targetAddress");
});
function searchPlaces() {
  const ps = new kakao.maps.services.Places();
  const bounds = new kakao.maps.LatLngBounds();

  ps.keywordSearch(keyword.value + "+수영장", (data, status) => {
    if (status === kakao.maps.services.Status.OK) {
      const resultDiv = document.getElementById("results");
      resultDiv.innerHTML = "";
      data.forEach((place) => {
        if (place.phone !== "") {
          console.log(place.id);

          const marker = new kakao.maps.Marker({
            position: new kakao.maps.LatLng(place.y, place.x),
          });
          marker.setMap(map);
          const itemEl = document.createElement("div");
          itemEl.id = "result-item";
          itemEl.style = "width: 90%;";
          itemEl.onclick = () => window.open(place.place_url, "_blank");
          itemEl.innerHTML =
            "<strong>" +
            place.place_name +
            "</strong><p>" +
            place.road_address_name +
            "</p>";
          resultDiv.appendChild(itemEl);

          kakao.maps.event.addListener(marker, "click", function () {
            const pos = marker.getPosition();
            map.panTo(pos);
            infowindow.open(map, marker);
            infowindow.setContent(`
              <div style="
                  font-size:14px; 
                  display: flex; 
                  flex-direction: column; 
                  align-items: center;
                  background-color: white; 
                  padding: 10px; 
                  border-radius: 8px; 
                  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.2);
                  max-width: 200px;
                  text-align: center;
                  position: relative;
                  z-index: 0;
              ">
                <a 
                  style="color: #007BFF; font-weight: bold; text-decoration: none; margin-bottom: 5px;" 
                  target="_blank" 
                  href="${place.place_url}"
                >
                  ${place.place_name}
                </a>
                <p style="margin: 0; color: #333; font-size: 12px;"><svg aria-hidden="true" focusable="false" class="icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512" width="1em" height="1em"><!-- Font Awesome Free 5.15.1 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free (Icons: CC BY 4.0, Fonts: SIL OFL 1.1, Code: MIT License) --><path d="M493.4 24.6l-104-24c-11.3-2.6-22.9 3.3-27.5 13.9l-48 112c-4.2 9.8-1.4 21.3 6.9 28l60.6 49.6c-36 76.7-98.9 140.5-177.2 177.2l-49.6-60.6c-6.8-8.3-18.2-11.1-28-6.9l-112 48C3.9 366.5-2 378.1.6 389.4l24 104C27.1 504.2 36.7 512 48 512c256.1 0 464-207.5 464-464 0-11.2-7.7-20.9-18.6-23.4z"/></svg>
	              </svg>${place.phone}</p>
                <!-- 아래 삼각형 화살표 -->
                <div style="
                    width: 0; 
                    height: 0; 
                    border-left: 10px solid transparent;
                    border-right: 10px solid transparent;
                    border-top: 10px solid white;
                    position: absolute;
                    bottom: -10px;
                    left: 50%;
                    transform: translateX(-50%);
                    box-shadow: 0px 2px 4px rgba(0, 0, 0, 0.1);
                "></div>
              </div>
            `);
          });

          bounds.extend(marker.getPosition());
        }
      });
      resultDiv.scrollTop = 0;
      map.setBounds(bounds);
    } else {
      alert("검색 결과가 없습니다.");
    }
  });
}
</script>

<template>
  <body
    class="flex flex-col items-center p-0 m-0 min-h-screen font-sans text-gray-800 bg-gray-50"
  >
    <!-- Search Container -->
    <div class="flex items-center mb-5 w-[90%] mt-6" id="search-container">
      <input
        class="p-4 w-full text-lg text-gray-600 text-center rounded-full border border-gray-200 shadow-lg bg-white outline-none transition-all duration-200 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 placeholder-gray-400"
        type="text"
        id="search-input"
        placeholder="수영장 주변 지하철역을 검색해주세요."
        v-model="keyword"
        @change="searchPlaces"
      />
    </div>

    <!-- Map Container -->
    <div
      class="w-[90%] h-[400px] rounded-lg shadow-lg bg-gray-100 border border-gray-200 overflow-hidden mb-6"
      id="map"
      ref="mapContainer"
    ></div>

    <!-- Results Container -->
    <div
      id="results"
      class="w-[90%] max-h-[400px] bg-white rounded-lg shadow-lg border border-gray-200 overflow-y-scroll overflow-x-hidden flex flex-col gap-3 p-4"
    ></div>
  </body>
</template>

<style>
#results {
  scroll-behavior: smooth;
}

#result-item {
  background-color: #f9f9f9;
  border: solid #e0e0e0 1px;
  border-radius: 12px;
  padding: 16px;
  transition: transform 0.2s, box-shadow 0.2s;
  cursor: pointer;
}

#result-item:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.1);
}

#result-item strong {
  color: #2d3748;
  font-size: 18px;
  font-weight: 700;
  display: block;
}

#result-item p {
  color: #718096;
  font-size: 14px;
  margin-top: 5px;
}
</style>
