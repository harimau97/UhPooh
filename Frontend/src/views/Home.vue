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
  Heart, // 체력 향상
  Brain, // 스트레스 해소
  Scale, // 체중 관리
  UsersRound, // 함께하는 즐거움
} from "lucide-vue-next";
import router from "@/router";
import ContactModal from "../components/ContactModal.vue";

import 김수영Image from "../assets/김수영.png";
import 이미란Image from "../assets/이미란.png";
import 박지훈Image from "../assets/박지훈.png";

const isVideoLoaded = ref(false);
const searchInput = ref("");
const activeTestimonial = ref(0);
const showContactModal = ref(false);

const handleVideoLoad = () => {
  isVideoLoaded.value = true;
};

const handleSearch = () => {
  if (searchInput.value && searchInput.value.trim() !== "") {
    localStorage.setItem("tempKeyword", searchInput.value);
    router.push("/around");
  }
};

const handleAreaSearch = (areaName) => {
  localStorage.setItem("tempKeyword", areaName);
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
    icon: Clock,
    title: "빠른 예약",
    description:
      "원하는 수영장을 바로 예약할 수 있습니다. 번거로운 전화나 방문 없이 온라인으로 간편하게 예약하세요.",
    color: "bg-indigo-100",
  },
];

const testimonials = [
  {
    name: "김수영",
    role: "수영 강사",
    content:
      "수영장 찾기가 이렇게 쉬울 줄 몰랐어요. 특히 실시간 혼잡도 기능이 정말 유용합니다.",
    image: 김수영Image,
  },
  {
    name: "이미란",
    role: "수영 동호회장",
    content:
      "동호회 활동을 위한 수영장을 고를 때 항상 사용합니다. 리뷰가 특히 도움이 되요.",
    image: 이미란Image,
  },
  {
    name: "박지훈",
    role: "수영장 운영자",
    content:
      "파트너로 등록한 후 방문객이 30% 증가했습니다. 관리 시스템도 너무 편리해요.",
    image: 박지훈Image,
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

// Updated service links with router paths
const serviceLinks = [
  { name: "수영장 찾기", path: "/around" },
  { name: "이용 가이드", path: "/guide" },
  { name: "마이페이지", path: "/mypage" },
];

const supportLinks = [
  { name: "자주 묻는 질문", path: "/faq" },
  { name: "이용약관", path: "/terms" },
  { name: "개인정보처리방침", path: "/privacy" },
  { name: "위치기반서비스 이용약관", path: "/location-terms" },
];

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
          src="@/assets/swimming2.mp4"
          autoplay
          loop
          muted
          playsinline
          preload="auto"
          @loadeddata="handleVideoLoad"
          class="object-cover w-full h-full"
          disable-picture-in-picture="true"
        ></video>
        <div
          class="absolute inset-0 bg-gradient-to-b from-black/60 via-black/40 to-black/60 backdrop-blur-[2px]"
        ></div>
      </div>

      <!-- Hero Content -->
      <div class="container relative px-4 mx-auto h-full">
        <!-- Navigation -->
        <nav class="flex justify-between items-center py-6"></nav>

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
                  class="flex gap-2 items-center px-8 py-4 font-medium text-white bg-gradient-to-r from-blue-500 to-blue-600 rounded-lg shadow-md transition-all duration-300 group hover:shadow-lg hover:shadow-blue-500/25 hover:-translate-y-0.5"
                >
                  <Search
                    class="w-5 h-5 transition-transform group-hover:scale-110"
                  />
                  <span
                    class="relative after:absolute after:bottom-0 after:left-0 after:h-px after:w-0 after:bg-white after:transition-all after:duration-300 group-hover:after:w-full"
                  >
                    찾아보기
                  </span>
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
                    @click="handleAreaSearch(area.name)"
                    class="flex gap-1 items-center px-4 py-1.5 text-sm text-gray-600 bg-white rounded-full border border-gray-100 shadow-sm transition-all duration-200 hover:border-blue-200 hover:shadow-md hover:text-blue-600"
                  >
                    <MapPin class="w-4 h-4" />
                    <span>{{ area.name }}</span>
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

    <!-- Benefits Section -->
    <section
      class="overflow-hidden relative py-20 bg-gradient-to-r from-blue-600 to-blue-400"
    >
      <!-- Background Pattern -->
      <div class="absolute inset-0 opacity-10">
        <div
          class="absolute inset-0 bg-[radial-gradient(circle_at_50%_50%,rgba(255,255,255,0.1)_1px,transparent_1px)] bg-[length:20px_20px]"
        ></div>
      </div>

      <div class="container relative px-4 mx-auto">
        <div class="grid grid-cols-1 gap-8 items-center md:grid-cols-3">
          <!-- Text Content -->
          <div class="text-white md:col-span-1">
            <h2 class="mb-6 text-3xl font-bold md:text-4xl">
              건강한 수영 라이프를<br />시작해보세요
            </h2>
            <p class="mb-8 text-lg text-blue-100">
              수영은 전신 운동으로 체력 향상과 스트레스 해소에 도움을 줍니다.
              어푸어푸와 함께 즐거운 수영을 시작해보세요.
            </p>
            <RouterLink
              to="/guide"
              class="inline-flex items-center px-6 py-3 font-medium text-blue-600 bg-white rounded-lg transition-all duration-200 hover:bg-blue-50 hover:shadow-lg"
            >
              이용가이드 보기
              <ChevronRight class="ml-2 w-5 h-5" />
            </RouterLink>
          </div>

          <!-- Benefits Grid -->
          <div class="grid grid-cols-1 gap-6 md:col-span-2 sm:grid-cols-2">
            <div
              class="p-6 text-white rounded-xl backdrop-blur-sm transition-all duration-200 transform bg-white/10 hover:-translate-y-1 hover:shadow-lg"
            >
              <div
                class="flex justify-center items-center mb-4 w-12 h-12 rounded-full bg-white/20"
              >
                <Heart class="w-6 h-6 text-white" />
              </div>
              <div class="mb-2 text-2xl font-bold">체력 향상</div>
              <p class="text-blue-100">
                수영은 유산소 운동으로 심폐 기능을 강화하고 근력을 향상시킵니다
              </p>
            </div>
            <div
              class="p-6 text-white rounded-xl backdrop-blur-sm transition-all duration-200 transform bg-white/10 hover:-translate-y-1 hover:shadow-lg"
            >
              <div
                class="flex justify-center items-center mb-4 w-12 h-12 rounded-full bg-white/20"
              >
                <Brain class="w-6 h-6 text-white" />
              </div>
              <div class="mb-2 text-2xl font-bold">스트레스 해소</div>
              <p class="text-blue-100">
                물속에서의 운동은 마음의 안정과 스트레스 해소에 도움이 됩니다
              </p>
            </div>
            <div
              class="p-6 text-white rounded-xl backdrop-blur-sm transition-all duration-200 transform bg-white/10 hover:-translate-y-1 hover:shadow-lg"
            >
              <div
                class="flex justify-center items-center mb-4 w-12 h-12 rounded-full bg-white/20"
              >
                <Scale class="w-6 h-6 text-white" />
              </div>
              <div class="mb-2 text-2xl font-bold">체중 관리</div>
              <p class="text-blue-100">
                부상 위험이 적으면서도 효과적인 체중 관리가 가능합니다
              </p>
            </div>
            <div
              class="p-6 text-white rounded-xl backdrop-blur-sm transition-all duration-200 transform bg-white/10 hover:-translate-y-1 hover:shadow-lg"
            >
              <div
                class="flex justify-center items-center mb-4 w-12 h-12 rounded-full bg-white/20"
              >
                <UsersRound class="w-6 h-6 text-white" />
              </div>
              <div class="mb-2 text-2xl font-bold">함께하는 즐거움</div>
              <p class="text-blue-100">
                수영 커뮤니티에서 같은 취미를 가진 사람들과 교류할 수 있습니다
              </p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Testimonials Section -->
    <section class="relative py-12">
      <div class="container px-4 mx-auto">
        <div class="mb-8 text-center">
          <h2 class="mb-2 text-3xl font-bold text-gray-900">이용자 후기</h2>
          <p class="text-gray-600">
            어푸어푸와 함께한 분들의 이야기를 들어보세요
          </p>
        </div>

        <!-- Testimonials Carousel -->
        <div class="relative mx-auto max-w-4xl">
          <div class="relative h-[280px]">
            <transition-group name="slide">
              <div
                v-for="(testimonial, index) in testimonials"
                :key="testimonial.name"
                v-show="index === activeTestimonial"
                class="flex absolute inset-0 items-center"
              >
                <div class="w-full">
                  <div
                    class="flex flex-col justify-center items-center px-4 h-full text-center"
                  >
                    <img
                      :src="testimonial.image"
                      :alt="testimonial.name"
                      class="object-cover mb-4 w-16 h-16 rounded-full"
                    />
                    <p class="mb-6 max-w-2xl text-lg text-gray-600">
                      "{{ testimonial.content }}"
                    </p>
                    <div class="text-center">
                      <div class="font-semibold text-gray-900">
                        {{ testimonial.name }}
                      </div>
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
    <section
      class="py-24 text-white bg-gradient-to-r from-blue-600 to-cyan-600"
    >
      <div class="container px-4 mx-auto text-center">
        <h2 class="mb-6 text-4xl font-bold">지금 바로 시작하세요</h2>
        <p class="mb-8 text-xl text-blue-100">
          더 이상 고민하지 마세요. 지금 바로 당신 근처의 수영장을 찾아보세요.
        </p>
        <RouterLink
          to="/around"
          class="px-8 py-4 text-lg font-semibold text-blue-600 bg-white rounded-lg transition-colors hover:bg-blue-50"
        >
          수영장 찾기
        </RouterLink>
      </div>
    </section>
  </main>
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
            <li v-for="link in serviceLinks" :key="link.name">
              <RouterLink
                :to="link.path"
                class="transition-colors hover:text-white"
              >
                {{ link.name }}
              </RouterLink>
            </li>
          </ul>
        </div>

        <!-- Support Links -->
        <div>
          <h3 class="mb-4 text-lg font-semibold text-white">고객지원</h3>
          <ul class="space-y-2">
            <li v-for="link in supportLinks" :key="link.name">
              <RouterLink
                :to="link.path"
                class="transition-colors hover:text-white"
              >
                {{ link.name }}
              </RouterLink>
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
            <div class="text-sm text-gray-400">
              <p>평일 09:00 - 18:00</p>
              <p>주말 및 공휴일 휴무</p>
            </div>
            <button
              @click="showContactModal = true"
              class="px-4 py-2 w-full text-white bg-gray-800 rounded transition-colors hover:bg-gray-700 focus:outline-none focus:ring-2 focus:ring-gray-600 focus:ring-offset-2 focus:ring-offset-gray-900"
            >
              문의하기
            </button>
          </div>
        </div>
      </div>

      <div class="pt-8 mt-12 border-t border-gray-800">
        <div
          class="flex flex-col gap-4 justify-between items-center md:flex-row"
        >
          <p>&copy; 2024 어푸어푸. All rights reserved.</p>
          <address class="not-italic text-center md:text-right">
            서울특별시 강남구 테헤란로 212 멀티캠퍼스 501호<br />
            사업자등록번호: 123-45-67890
          </address>
        </div>
      </div>
    </div>
  </footer>

  <ContactModal :is-open="showContactModal" @close="showContactModal = false" />

  <div>
    <!-- AI Chatbot이 삽입될 div -->
    <div id="aichatbot"></div>
  </div>
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
