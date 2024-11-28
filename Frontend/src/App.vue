<script setup>
import Header from "./views/Header.vue";
import Chat from "./views/Chat.vue";
import { useThemeStore } from "./stores/theme";

import {
  getUserInfo,
  isAuthenticated,
  userAuthenticated,
} from "./composables/userAuth.js";
import { onMounted, provide, ref, computed, watch } from "vue";
import { RouterLink, RouterView, useRoute } from "vue-router";
import MainHeader from "./views/Header.vue";
import DefaultHeader from "./views/DefaultHeader.vue";
import CustomModal from "@/components/CustomModal.vue";
import { useModal } from "@/composables/useModal";

const isLoggedIn = computed(() => userAuthenticated.value);
console.log("로그인 상태", isLoggedIn.value);
provide("isLoggedIn", isLoggedIn);

// Sendbird 설정
const applicationId = import.meta.env.VITE_SENDBIRD_APP_ID; // Sendbird 애플리케이션 ID
const apiToken = import.meta.env.VITE_SENDBIRD_API_TOKEN; // Sendbird API 토큰

// API URL
const url = `https://api-${applicationId}.sendbird.com/v3/users`;

// 요청 헤더
const headers = {
  "Content-Type": "application/json",
  "Api-Token": apiToken,
};

// 사용자 등록 함수
async function createChatUser() {
  // 요청 데이터
  const requestData = {
    user_id: localStorage.getItem("userName"), // 고유 사용자 ID
    nickname: localStorage.getItem("userEmail"), // 사용자 닉네임
    profile_url: "", // 프로필 이미지 URL (선택)
  };

  // 디버깅을 위한 로그
  console.log("SendBird 요청 데이터:", requestData);
  console.log("localStorage values:", {
    email: localStorage.getItem("userEmail"),
    name: localStorage.getItem("userName"),
  });

  try {
    const response = await fetch(url, {
      method: "POST",
      headers: headers,
      body: JSON.stringify(requestData),
    });

    const responseData = await response.json();
    console.log("SendBird API 응답:", responseData);

    if (!response.ok) {
      console.error("SendBird API 에러 상세:", responseData);
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    console.log("사용자 생성 성공:", responseData);
  } catch (error) {
    console.error("사용자 생성 실패:", error);
    // 에러 응답 내용도 출력
    if (error.response) {
      const errorData = await error.response.json();
      console.error("에러 상세 내용:", errorData);
    }
  }
}

// 테마 스토어 초기화
const themeStore = useThemeStore();

watch(userAuthenticated, async (newValue) => {
  if (newValue) {
    await createChatUser();
  }
});

onMounted(async () => {
  themeStore.applyTheme(themeStore.currentTheme);
  (function (w, d, s, ...args) {
    const f = d.getElementsByTagName(s)[0];
    const j = d.createElement(s);
    w.chatbotConfig = args;
    j.defer = true;
    j.type = "module";
    j.src = "https://aichatbot.sendbird.com/index.js";
    f.parentNode.insertBefore(j, f);
  })(
    window,
    document,
    "script",
    import.meta.env.VITE_SENDBIRD_APP_ID,
    import.meta.env.VITE_SENDBIRD_CHATBOT_KEY,
    {
      apiHost: "https://api-cf-ap-2.sendbird.com",
    }
  );
  await isAuthenticated();
  if (userAuthenticated.value) {
    await getUserInfo();
  }
  
 
});

// 현재 라우트를 사용하여 메인 페이지 여부를 확인
const route = useRoute();
const isMainPage = computed(() => route.path === "/");

const { showModal, modalMessage, closeModal } = useModal();
</script>

<template>
  <div>
    <CustomModal 
      :show="showModal"
      :message="modalMessage"
      @close="closeModal"
    />
    <MainHeader v-if="isMainPage" />
    <DefaultHeader v-else />
    <RouterView />
    <div v-show="isLoggedIn">
      <Chat />
      <ChatBot />
    </div>
    <div>
      <!-- AI Chatbot이 삽입될 div -->
      <div id="aichatbot"></div>
    </div>
  </div>
</template>

<style scoped>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}
</style>
