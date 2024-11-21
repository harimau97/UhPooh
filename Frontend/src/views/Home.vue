<script setup>
import { ref, onMounted } from "vue";
import {
  Search,
  MapPin,
  ChevronRight,
  Star,
  Clock,
  Users,
  Phone,
} from "lucide-vue-next";
import router from "@/router";

const isVideoLoaded = ref(false);
const searchInput = ref("");
const activeTestimonial = ref(0);

const handleVideoLoad = () => {
  isVideoLoaded.value = true;
};

const handleSearch = () => {
  // Implement search functionality
  console.log("Searching:", searchInput.value);
  router.push("/around");
};

const popularAreas = [
  { name: "강남구", count: 24 },
  { name: "송파구", count: 18 },
  { name: "마포구", count: 15 },
  { name: "서초구", count: 21 },
];

const features = [
  {
    icon: MapPin,
    title: "내 주변 수영장",
    description:
      "현재 위치를 기반으로 가까운 수영장을 쉽고 빠르게 찾아보세요. 상세한 위치 정보와 길 안내를 제공합니다.",
    color: "bg-blue-100",
  },
  {
    icon: Star,
    title: "수영장 정보 비교",
    description:
      "수영장 이용 요금, 운영 시간, 시설 정보를 한눈에 비교해보세요. 실시간 리뷰로 더욱 똑똑한 선택이 가능합니다.",
    color: "bg-cyan-100",
  },
  {
    icon: Users,
    title: "실시간 혼잡도",
    description:
      "실시간으로 수영장의 혼잡도를 확인하고 여유로운 시간대를 선택하세요. 더 쾌적한 수영을 즐기실 수 있습니다.",
    color: "bg-sky-100",
  },
];

const testimonials = [
  {
    name: "김수영",
    role: "수영 강사",
    content:
      "수영장 찾기가 이렇게 쉬울 줄 몰랐어요. 특히 실시간 혼잡도 기능이 정말 유용합니다.",
    image: "/api/placeholder/64/64",
  },
  {
    name: "박지훈",
    role: "수영 동호회장",
    content:
      "동호회 활동을 위한 수영장을 고를 때 항상 사용합니다. 리뷰가 특히 도움이 되요.",
    image: "/api/placeholder/64/64",
  },
  {
    name: "이미란",
    role: "수영장 운영자",
    content:
      "파트너로 등록한 후 방문객이 30% 증가했습니다. 관리 시스템도 너무 편리해요.",
    image: "/api/placeholder/64/64",
  },
];

const stats = [
  { label: "등록된 수영장", value: "2,500+" },
  { label: "월간 이용자", value: "50,000+" },
  { label: "사용자 리뷰", value: "15,000+" },
];

const socialLinks = [
  { name: "페이스북", url: "#", icon: "facebook" },
  { name: "트위터", url: "#", icon: "twitter" },
  { name: "인스타그램", url: "#", icon: "instagram" },
];

const navigationLinks = ["수영장 찾기", "이용 가이드", "마이페이지"];
const serviceLinks = ["수영장 찾기", "이용 가이드", "마이페이지"];
const supportLinks = [
  "자주 묻는 질문",
  "이용약관",
  "개인정보처리방침",
  "위치기반서비스 이용약관",
];

const getServicePath = (link) => {
  switch(link) {
    case '수영장 찾기':
      return '/around';
    case '마이페이지':
      return '/mypage';
    default:
      return '#';
  }
};

const changeTargetAddress = async () => {
  await localStorage.setItem("targetAddress", searchInput.value);
  console.log("targetAddress:", localStorage.getItem("targetAddress"));
};

onMounted(() => {
  setInterval(() => {
    activeTestimonial.value =
      (activeTestimonial.value + 1) % testimonials.length;
  }, 5000);
});
</script>

<template>
 <main class="min-h-screen">
    <!-- Hero Section with Navigation -->
    <section class="relative h-screen">
      <!-- Video Background -->
      <div class="object-cover overflow-hidden absolute inset-0 w-full h-full">
        <video
          src="@/assets/swimming.mp4"
          autoplay
          loop
          muted
          playsinline
          preload="auto"
          @loadeddata="handleVideoLoad"
          class="object-cover w-full h-full"
        ></video>
        <div
          class="absolute inset-0 bg-gradient-to-b from-black/60 via-black/40 to-black/60 backdrop-blur-[2px]"
        ></div>
      </div>

      <!-- Hero Content -->
      <div class="container relative px-4 mx-auto h-full">
        <!-- Navigation -->
          <div class="flex gap-6">
            
          </div>

        <!-- Main Content -->
        <div class="mt-32 max-w-3xl">
          <h1
            class="mb-6 text-6xl font-bold leading-tight text-white animate-fade-in"
          >
            당신 근처의 <br />
            <span class="text-blue-400">모든 수영장</span>을<br />
            찾아보세요
          </h1>

          <p class="mb-12 text-xl text-gray-200 animate-fade-in-delay">
            실시간 정보와 이용자 리뷰로<br />
            나에게 딱 맞는 수영장을 쉽게 찾을 수 있습니다.
          </p>

          <!-- Search Box -->
          <div class="relative animate-slide-up">
            <div
              class="bg-white/95 backdrop-blur-md p-6 rounded-md shadow-[0_8px_30px_rgb(0,0,0,0.12)] border border-white/20"
            >
              <!-- Main Search Input -->
              <div class="flex gap-4 items-center">
                <div class="relative flex-1 group">
                  <div
                    class="absolute inset-0 bg-gradient-to-r from-gray-50 to-blue-50 rounded-md opacity-0 transition-opacity duration-300 group-hover:opacity-100"
                  ></div>
                  <div
                    class="flex relative gap-3 items-center px-5 py-4 bg-gray-50 rounded-xl"
                  >
                    <MapPin class="w-6 h-6 text-blue-500" />
                    <input
                      v-model="searchInput"
                      type="text"
                      placeholder="지역명 또는 수영장명을 입력하세요"
                      class="w-full text-lg bg-transparent transition-colors outline-none placeholder:text-gray-400 focus:placeholder:text-gray-300"
                      @input="changeTargetAddress"
                      @keydown.enter="handleSearch"
                    />
                  </div>
                </div>
                <RouterLink
                  to="/around"
                  @click="handleSearch"
                  class="flex gap-2 items-center px-8 py-4 font-medium text-white bg-gradient-to-r from-blue-600 to-blue-500 rounded-md shadow-lg transition-all duration-200 transform hover:from-blue-700 hover:to-blue-600 shadow-blue-500/20 hover:-translate-y-0.5 hover:shadow-xl"
                >
                  <Search class="w-5 h-5" />
                  <span>찾아보기</span>
                </RouterLink>
              </div>

              <!-- Popular Areas -->
              <div class="flex gap-4 items-center px-1 mt-5">
                <span class="text-sm font-medium text-gray-500"
                  >인기 지역:</span
                >
                <div class="flex flex-wrap gap-2">
                  <button
                    v-for="area in popularAreas"
                    :key="area.name"
                    class="flex gap-1 items-center px-4 py-1.5 text-sm text-gray-600 bg-white rounded-full border border-gray-100 shadow-sm transition-all duration-200 hover:border-blue-200 hover:shadow-md hover:text-blue-600"
                  >
                    <MapPin class="w-4 h-4" />
                    <span>{{ area.name }}</span>
                    <span class="text-xs text-gray-400"
                      >({{ area.count }})</span
                    >
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section class="py-24 bg-gradient-to-b from-gray-50 to-white">
      <div class="container px-4 mx-auto">
        <div class="mb-16 text-center">
          <h2 class="mb-4 text-4xl font-bold">서비스 특징</h2>
          <p class="text-xl text-gray-600">
            더 쉽고 스마트한 수영장 찾기 서비스
          </p>
        </div>

        <div class="grid grid-cols-1 gap-8 md:grid-cols-3">
          <div
            v-for="feature in features"
            :key="feature.title"
            class="p-8 bg-white rounded-2xl shadow-lg transition-all duration-300 hover:shadow-xl hover:-translate-y-1"
          >
            <div
              :class="[
                feature.color,
                'w-16 h-16 rounded-xl flex items-center justify-center mb-6',
              ]"
            >
              <component :is="feature.icon" class="w-8 h-8 text-blue-600" />
            </div>
            <h3 class="mb-4 text-xl font-semibold">{{ feature.title }}</h3>
            <p class="leading-relaxed text-gray-600">
              {{ feature.description }}
            </p>
          </div>
        </div>
      </div>
    </section>

    <!-- Stats Section -->
    <section class="overflow-hidden relative py-16 text-white bg-gradient-to-r from-blue-600 to-blue-700">
      <!-- Background Pattern -->
      <div class="absolute inset-0 opacity-10">
        <div
          class="absolute inset-0 bg-[radial-gradient(circle_at_50%_50%,rgba(255,255,255,0.1)_1px,transparent_1px)] bg-[length:20px_20px]"
        ></div>
      </div>

      <div class="container relative px-4 mx-auto">
        <div class="grid grid-cols-1 gap-8 text-center md:grid-cols-3">
          <div
            v-for="stat in stats"
            :key="stat.label"
            class="p-6 rounded-lg backdrop-blur-sm transition-transform transform bg-white/10 hover:scale-105"
          >
            <div class="mb-2 text-4xl font-bold">{{ stat.value }}</div>
            <div class="text-blue-200">{{ stat.label }}</div>
          </div>
        </div>
      </div>
    </section>

    <!-- Testimonials Section -->
    <section class="py-24 bg-white">
      <div class="container px-4 mx-auto">
        <div class="mb-16 text-center">
          <h2 class="mb-4 text-4xl font-bold">이용자 후기</h2>
          <p class="text-xl text-gray-600">
            실제 사용자들의 생생한 이야기를 들어보세요
          </p>
        </div>

        <div class="mx-auto max-w-4xl">
          <div class="relative h-[300px]">
            <transition-group name="slide">
              <div
                v-for="(testimonial, index) in testimonials"
                :key="testimonial.name"
                v-show="index === activeTestimonial"
                class="flex absolute inset-0 items-center"
              >
                <div class="w-full">
                  <div class="relative p-8 bg-gray-50 rounded-2xl">
                    <div class="absolute -top-6 left-1/2 -translate-x-1/2">
                      <img
                        :src="testimonial.image"
                        :alt="testimonial.name"
                        class="w-16 h-16 rounded-full border-4 border-white shadow-lg"
                      />
                    </div>
                    <div class="pt-8">
                      <p class="mb-6 text-xl text-gray-600">
                        {{ testimonial.content }}
                      </p>
                      <div class="font-semibold">{{ testimonial.name }}</div>
                      <div class="text-sm text-gray-500">
                        {{ testimonial.role }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </transition-group>
          </div>

          <div class="flex gap-2 justify-center mt-8">
            <button
              v-for="(_, index) in testimonials"
              :key="index"
              @click="activeTestimonial = index"
              :class="[
                'w-3 h-3 rounded-full transition-colors',
                index === activeTestimonial ? 'bg-blue-600' : 'bg-gray-300',
              ]"
            ></button>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA Section -->
    <section class="py-24 text-white bg-gradient-to-r from-blue-600 to-cyan-600">
      <div class="container px-4 mx-auto text-center">
        <h2 class="mb-6 text-4xl font-bold">지금 바로 시작하세요</h2>
        <p class="mb-8 text-xl text-blue-100">
          더 이상 고민하지 마세요. 지금 바로 당신 근처의 수영장을 찾아보세요.
        </p>
        <RouterLink
          to="/around"
          class="inline-block px-8 py-4 text-lg font-semibold text-blue-600 bg-white rounded-lg transition-colors hover:bg-blue-50"
        >
          수영장 찾기
        </RouterLink>
      </div>
    </section>

    <!-- Footer -->
    <footer class="text-gray-400 bg-gray-900">
      <div class="container px-4 py-12 mx-auto">
        <div class="grid grid-cols-1 gap-8 md:grid-cols-4">
          <!-- Company Info -->
          <div>
            <div class="mb-4 text-2xl font-bold text-white">어푸어푸</div>
            <p class="mb-4">더 쉽고 스마트한<br />수영장 찾기 서비스</p>
            <div class="flex gap-4">
              <a
                v-for="link in socialLinks"
                :key="link.name"
                :href="link.url"
                class="text-gray-400 transition-colors hover:text-white"
              >
                <component :is="link.icon" class="w-6 h-6" />
              </a>
            </div>
          </div>
          <!-- Service Links -->
<div>
  <h3 class="mb-4 text-lg font-semibold text-white">서비스</h3>
  <ul class="space-y-2">
    <li v-for="link in serviceLinks" :key="link">
      <RouterLink 
        :to="getServicePath(link)"
        class="transition-colors hover:text-white"
      >
        {{ link }}
      </RouterLink>
    </li>
  </ul>
</div>

          <!-- Support Links -->
          <div>
            <h3 class="mb-4 text-lg font-semibold text-white">고객지원</h3>
            <ul class="space-y-2">
              <li v-for="link in supportLinks" :key="link">
                <a href="#" class="transition-colors hover:text-white">{{ link }}</a>
              </li>
            </ul>
          </div>

          <!-- Customer Service -->
          <div>
            <h3 class="mb-4 text-lg font-semibold text-white">고객센터</h3>
            <div class="space-y-4">
              <div class="flex gap-2 items-center">
                <Phone class="w-5 h-5" />
                <span class="text-white">1234-5678</span>
              </div>
              <div>
                <p>평일 09:00 - 18:00</p>
                <p>주말 및 공휴일 휴무</p>
              </div>
              <button
                class="px-4 py-2 w-full text-white bg-gray-800 rounded transition-colors hover:bg-gray-700"
              >
                문의하기
              </button>
            </div>
          </div>
        </div>

        <div class="pt-8 mt-12 border-t border-gray-800">
          <div class="flex flex-col gap-4 justify-between items-center md:flex-row">
            <p>&copy; 2024 어푸어푸. All rights reserved.</p>
            <address class="not-italic text-center md:text-right">
              서울특별시 강남구 테헤란로 212 어푸어푸빌딩 501호<br />
              사업자등록번호: 123-45-67890
            </address>
          </div>
        </div>
      </div>
    </footer>
  </main>
</template>

<style scoped>
.slide-enter-active,
.slide-leave-active {
  transition: all 0.5s ease;
}

.slide-enter-from {
  opacity: 0;
  transform: translateX(50px);
}

.slide-leave-to {
  opacity: 0;
  transform: translateX(-50px);
}

.animate-fade-in {
  animation: fadeIn 1s ease-out;
}

.animate-fade-in-delay {
  animation: fadeIn 1s ease-out 0.3s both;
}

.animate-slide-up {
  animation: slideUp 1s ease-out 0.6s both;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 반응형 디자인을 위한 미디어 쿼리 */
@media (max-width: 768px) {
  .hero-content {
    padding: 0 1rem;
  }

  .hero-title {
    font-size: 2.5rem;
  }

  .search-box {
    flex-direction: column;
  }

  .popular-areas {
    flex-wrap: wrap;
  }
}
</style>