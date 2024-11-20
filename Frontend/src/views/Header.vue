<template>
  <header class="flex justify-between items-center p-5 text-white bg-blue-900">
    <RouterLink to="/" class="text-xl font-bold">어푸</RouterLink>
    <nav class="space-x-6">
      <RouterLink to="/around" class="hover:underline">주변 수영장</RouterLink>
      <a href="#our-pools" class="hover:underline">결제 내역</a>
      <a href="#pricing" class="hover:underline">서비스 소개</a>
    </nav>
    <div>
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
    </div>
  </header>
</template>

<script setup></script>

<style scoped></style>
