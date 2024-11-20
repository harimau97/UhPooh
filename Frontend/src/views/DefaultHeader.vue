<template>
  <header class="bg-white border-b border-gray-200">
    <nav class="container mx-auto px-4 py-4 flex justify-between items-center">
      <!-- Logo -->
      <RouterLink to="/" class="text-blue-600 text-2xl font-bold">
        <div class="text-blue-600 text-2xl font-bold">어푸어푸</div>
      </RouterLink>

      <!-- Navigation Links -->
      <div class="space-x-8">
        <RouterLink 
          v-for="item in navigationLinks" 
          :key="item.text"
          :to="item.path"
          class="text-gray-700 hover:text-blue-600 transition-colors"
        >
          {{ item.text }}
        </RouterLink>
        <RouterLink 
          to="/login"
          class="text-gray-700 hover:text-blue-600 transition-colors"
        >
          로그인
        </RouterLink>
      </div>
    </nav>
  </header>
</template>

<script setup>
import { RouterLink } from 'vue-router'

const navigationLinks = [
  { text: '수영장 찾기', path: '/search' },
  { text: '이용 가이드', path: '/guide' },
  { text: '파트너 등록', path: '/partner' }
]
</script>

<style scoped>
.router-link-active {
  color: rgb(37, 99, 235); /* text-blue-600 */
  font-weight: 500;
}
</style>
