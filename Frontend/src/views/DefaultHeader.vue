<template>
  <header class="bg-white border-b border-gray-200">
    <nav class="container flex justify-between items-center px-4 py-4 mx-auto">
      <!-- Logo -->
      <RouterLink to="/" class="text-2xl font-bold text-blue-600">
        <div class="text-2xl font-bold text-blue-600">어푸어푸</div>
      </RouterLink>

      <!-- Navigation Links -->
      <div class="space-x-8">
        <RouterLink
          v-for="item in navigationLinks"
          :key="item.text"
          :to="item.path"
          class="text-gray-700 transition-colors hover:text-blue-600"
        >
          {{ item.text }}
        </RouterLink>
        <RouterLink
          to="/login"
          class="text-gray-700 transition-colors hover:text-blue-600"
        >
          로그인
        </RouterLink>
      </div>
    </nav>
  </header>
</template>

<script setup>
import { RouterLink } from "vue-router";

const navigationLinks = [
  { text: "수영장 찾기", path: "/search" },
  { text: "이용 가이드", path: "/guide" },
  { text: "파트너 등록", path: "/partner" },
];
</script>

<style scoped>
.router-link-active {
  color: rgb(37, 99, 235); /* text-blue-600 */
  font-weight: 500;
}
</style>
