<template>
  <header class="bg-white border-b border-gray-200">
    <nav class="container flex justify-between items-center px-4 py-4 mx-auto">
      <!-- Logo -->
      <RouterLink
        to="/"
        class="flex gap-2 items-center text-2xl font-bold text-blue-600"
      >
        <img src="../assets/logo_text.png" style="width: 130px; height: auto" />
        <span class="text-2xl">🏊‍♂️</span>
      </RouterLink>

      <!-- Navigation Links -->
      <div class="space-x-8">
        <RouterLink
          v-for="item in navigationLinks"
          :key="item.text"
          :to="item.path"
          class="text-gray-700 transition-colors hover:text-blue-600"
          @click="clearTargetAddress(item.path)"
        >
          {{ item.text }}
        </RouterLink>
        <RouterLink
          v-show="!isLoggined"
          to="/login"
          class="text-gray-700 transition-colors hover:text-blue-600"
        >
          로그인
        </RouterLink>
        <button
          v-show="isLoggined"
          @click="logout"
          class="text-gray-700 transition-colors hover:text-blue-600"
        >
          로그아웃
        </button>
      </div>
    </nav>
  </header>
</template>

<script setup>
import { RouterLink } from "vue-router";
import { onMounted, inject } from "vue";
import axios from "axios";

const isLoggined = inject("isLoggedIn");

console.log(isLoggined.value);
console.log(localStorage.getItem("userId"));

const navigationLinks = [
  { text: "수영장 찾기", path: "/around" },
  { text: "이용 가이드", path: "/guide" },
  { text: "마이페이지", path: "/mypage" },
];

const clearTargetAddress = (path) => {
  if (path === "/around") {
    localStorage.removeItem("targetAddress"), location.replace("/around");
  }
};

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
  // localStorage.removeItem("userToken");
  // localStorage.removeItem("userId");
  // localStorage.removeItem("userName");
  // localStorage.removeItem("userAddress");
  // localStorage.removeItem("pImage");
  // localStorage.removeItem("isAdmin");
  // localStorage.removeItem("userEmail");
  // localStorage.removeItem("tempKeyword");
  localStorage.clear();
  isLoggined.value = false; // 로그인 상태 변경

  location.replace("/"); // 메인 페이지로 이동;
};
</script>

<style scoped>
.router-link-active {
  color: rgb(37, 99, 235); /* text-blue-600 */
  font-weight: 500;
}
</style>
