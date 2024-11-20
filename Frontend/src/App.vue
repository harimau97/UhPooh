<script setup>
import Header from "./views/Header.vue";
import Chat from "./views/Chat.vue";
import {
  getUserInfo,
  isAuthenticated,
  userAuthenticated,
} from "./composables/userAuth.js";
import { onMounted, provide, ref, computed } from "vue";
import { RouterLink, RouterView, useRoute } from "vue-router";
import MainHeader from "./views/Header.vue";
import DefaultHeader from "./views/DefaultHeader.vue";

const userAddress = ref("");
const updateUserAddress = (address) => {
  userAddress.value = address; // 자식에서 전달받은 값으로 상태 업데이트
  console.log("Updated userAddress in parent:", userAddress.value);
};

const isLoggedIn = computed(() => userAuthenticated.value);
console.log(isLoggedIn.value);
provide("isLoggedIn", isLoggedIn);

onMounted(async () => {
  await isAuthenticated();
  if (userAuthenticated.value) {
    await getUserInfo();
  }
});

// 현재 라우트를 사용하여 메인 페이지 여부를 확인
const route = useRoute();
const isMainPage = computed(() => route.path === "/");
</script>

<template>
  <Chat />
  <MainHeader v-if="isMainPage" />
  <DefaultHeader v-else />
  <RouterView />
</template>

<style scoped></style>
