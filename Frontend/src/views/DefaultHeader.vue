<template>
  <header class="bg-white border-b border-gray-200">
    <nav class="container flex justify-between items-center px-4 py-4 mx-auto">
      <!-- Logo -->
      <RouterLink
        to="/"
        class="text-2xl font-bold text-blue-600 flex items-center gap-2"
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

const logout = async () => {
  const uId = localStorage.getItem("userId");
  try {
    console.log("로그아웃 시작");
    await axios({
      method: "post",
      url: `http://localhost:8080/uhpooh/api/user/logout/${uId}`,
      headers: {
        "Content-Type": "application/json",
      },
      data: {},
    });
    
    // Clear all localStorage items
    localStorage.clear();
    
    // Update login state
    isLoggined.value = false;
    
    // Force reload and redirect to home
    window.location.href = "/";
  } catch (error) {
    console.error("로그아웃 실패:", error);
    // Even if the API call fails, clear local state
    localStorage.clear();
    isLoggined.value = false;
    window.location.href = "/";
  }
};

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
</script>

<style scoped>
.router-link-active {
  color: rgb(37, 99, 235); /* text-blue-600 */
  font-weight: 500;
}
</style>
