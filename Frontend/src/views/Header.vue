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
        <img
          src="../assets/logo_text.png"
          style="filter: invert(100%); width: 130px; height: auto"
        />
      </RouterLink>

      <!-- Navigation Links -->
      <div class="space-x-8">
        <RouterLink
          v-for="item in navigationLinks"
          :key="item.text"
          :to="item.path"
          class="text-white transition-colors hover:text-blue-300"
          @click="clearTargetAddress"
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
import axios from "axios";

const isLoggined = inject("isLoggedIn");

console.log(isLoggined.value);
console.log(localStorage.getItem("userId"));

const logout = () => {
  const uId = localStorage.getItem("userId");
  const tryLogout = async () => {
    try {
      console.log("로그아웃 시작");
      const response = await axios({
        method: "post", // 강제로 POST로 설정
        url: `http://localhost:8080/uhpooh/api/user/logout/${uId}`,
        headers: {
          "Content-Type": "application/json",
        },
        data: {}, // POST 요청에 필요한 데이터
      });
    } catch (error) {
      console.error(error);
    }
  };
  tryLogout();
  localStorage.removeItem("userToken");
  localStorage.removeItem("userId");
  localStorage.removeItem("userName");
  localStorage.removeItem("userAddress");
  localStorage.removeItem("pImage");
  isLoggined.value = false; // 로그인 상태 변경

  location.replace("/"); // 메인 페이지로 이동;
};

const navigationLinks = [
  { text: "수영장 찾기", path: "/around" },
  { text: "이용 가이드", path: "/guide" },
  { text: "파트너 등록", path: "/partner" },
];
</script>

const clearTargetAddress = () => { localStorage.removeItem("targetAddress"); };

<style scoped>
.router-link-active {
  color: rgb(147, 197, 253); /* text-blue-300 */
  font-weight: 500;
}
</style>
