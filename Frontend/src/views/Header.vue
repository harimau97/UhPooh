<template>
  <!-- <header class="flex justify-between items-center p-5 text-white bg-blue-900">
    <RouterLink to="/" class="text-xl font-bold">어푸</RouterLink>
    <nav class="space-x-6">
      <RouterLink to="/around" class="hover:underline">주변 수영장</RouterLink>
      <a href="#our-pools" class="hover:underline">결제 내역</a>
      <a href="#pricing" class="hover:underline">서비스 소개</a>
    </nav>
    <div v-show="!isLoggined">
      <RouterLink
        class="px-4 py-2 mr-4 text-blue-900 bg-white rounded-md"
        to="/signup"
        >회원가입</RouterLink
      >
      <RouterLink
        class="px-4 py-2 text-blue-900 bg-white rounded-md"
        to="/login"
        >로그인</RouterLink
      >
    </div> -->
  <header class="absolute top-0 right-0 left-0 z-50">
    <nav class="container flex justify-between items-center px-4 py-6 mx-auto">
      <!-- Logo -->
      <RouterLink to="/" class="text-2xl font-bold text-white">
        <div class="text-2xl font-bold text-white">어푸어푸</div>
      </RouterLink>

      <!-- Navigation Links -->
      <div class="space-x-8">
        <RouterLink
          v-for="item in navigationLinks"
          :key="item.text"
          :to="item.path"
          class="text-white transition-colors hover:text-blue-300"
        >
          {{ item.text }}
        </RouterLink>
        <RouterLink
          v-show="!isLoggined"
          to="/login"
          class="text-white transition-colors hover:text-blue-300"
        >
          로그인
        </RouterLink>
        <button
          v-show="isLoggined"
          @click="logout"
          class="text-white transition-colors hover:text-blue-300"
        >
          로그아웃
        </button>
      </div>
    </nav>
  </header>
</template>

<script setup>
import { onMounted } from "vue";
import { inject } from "vue";

const isLoggined = inject("isLoggedIn");

console.log(isLoggined.value);

const logout = () => {
  localStorage.clear(); // localStorage 초기화
  isLoggined.value = false; // 로그인 상태 변경
  alert("로그아웃 완료");
  location.reload();
};

const navigationLinks = [
  { text: "수영장 찾기", path: "/around" },
  { text: "이용 가이드", path: "/guide" },
  { text: "파트너 등록", path: "/partner" },
];
</script>

<style scoped>
.router-link-active {
  color: rgb(147, 197, 253); /* text-blue-300 */
  font-weight: 500;
}
</style>
