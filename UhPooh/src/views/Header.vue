<template>
  <header class="bg-blue-900 text-white p-5 flex justify-between items-center">
    <RouterLink to="/" class="text-xl font-bold">어푸</RouterLink>
    <nav class="space-x-6">
      <RouterLink to="/around" class="hover:underline">주변 수영장</RouterLink>
      <a href="#our-pools" class="hover:underline">Our Pools</a>
      <a href="#pricing" class="hover:underline">Pricing</a>
      <a href="#visit-us" class="hover:underline">Visit Us</a>
    </nav>
    <button class="bg-white text-blue-900 px-4 py-2 rounded-md">
      Book Your Swim
    </button>
  </header>
</template>

<script setup></script>

<style scoped></style>
