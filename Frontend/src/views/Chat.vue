<template>
  <transition name="slide">
    <div
      v-show="openChat"
      class="flex fixed inset-0 z-50 justify-end items-end"
    >
      <div
        class="flex flex-col h-[600px] w-[400px] mr-4 mb-20 bg-white rounded-lg shadow-2xl overflow-hidden border border-indigo-100"
      >
        <!-- Chat header -->
        <div
          class="flex justify-between items-center px-4 py-3 bg-gradient-to-r from-indigo-500 to-purple-500"
        >
          <h3 class="text-lg font-semibold text-white">채팅</h3>
          <button
            @click="closeChat"
            class="p-1 text-white transition-colors hover:text-gray-200"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
              class="w-6 h-6"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M6 18L18 6M6 6l12 12"
              />
            </svg>
          </button>
        </div>

        <!-- Chat content -->
        <div class="flex-1 bg-white">
          <Chat
            :config="config"
            :setSbUserInfo="setSbUserInfo"
            :setUnreadMessageCount="setUnreadMessageCount"
            :onRef="handleChatRef"
          />
        </div>
      </div>
    </div>
  </transition>

  <!-- Chat button -->
  <button
    @click="openChat = !openChat"
    class="flex fixed bottom-[24px] right-20 z-50 justify-center items-center w-12 h-12 text-white bg-gradient-to-r from-indigo-500 to-purple-500 rounded-full shadow-lg transition-all duration-300 transform hover:from-indigo-600 hover:to-purple-600 hover:scale-105"
  >
    <svg
      xmlns="http://www.w3.org/2000/svg"
      class="w-7 h-7"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      stroke-width="2"
      stroke-linecap="round"
      stroke-linejoin="round"
    >
      <path
        d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"
      />
    </svg>
    <div
      v-if="messageCount > 0"
      class="absolute -top-1 -right-1 flex items-center justify-center min-w-[20px] h-5 px-1 bg-red-500 text-white text-xs font-bold rounded-full"
    >
      {{ messageCount }}
    </div>
  </button>
</template>

<script>
import { applyReactInVue, applyPureReactInVue } from "veaury";
import { onMounted, ref, watch } from "vue";
import ChatReactComponent from "../react_app/Chat.jsx";
import Header from "./Header.vue";
import { inject } from "vue";
const isLoggined = ref(false);
onMounted(async () => {
  isLoggined.value = await inject("isLoggedIn");
});

console.log("채팅 가능 여부", isLoggined.value);

const user_id = ref("");
const user_email = ref("");

user_id.value = localStorage.getItem("userName");
user_email.value = localStorage.getItem("userEmail");

console.log(localStorage.setItem("채팅 확인", user_email.value));
console.log("");

const config = {
  APP_ID: import.meta.env.VITE_SENDBIRD_APP_ID,
  USER_ID: user_id.value,
  NICKNAME: user_email.value,
  API_TOKEN: import.meta.env.VITE_SENDBIRD_API_TOKEN,
  LANG: "ko",
};

export default {
  data() {
    return {
      openChat: false, // 모달을 열고 닫기 위한 상태 변수
      chatRef: null,
    };
  },
  components: {
    Chat: applyPureReactInVue(ChatReactComponent),
  },
  computed: {
    classStyle() {
      switch (this.openChat) {
        case true:
          return "animate-slide-up";
        case false:
          return "animate-slide-down";
      }
    },
  },
  methods: {
    handleChatRef(ref) {
      this.chatRef = ref;
    },
    async closeChat() {
      if (this.chatRef && this.chatRef.disconnect) {
        await this.chatRef.disconnect();
      }
      this.openChat = false;
    },
  },
  watch: {
    openChat(newValue) {
      if (!newValue && this.chatRef && this.chatRef.disconnect) {
        this.chatRef.disconnect();
      }
    },
  },
  setup() {
    const userRef = ref(null);
    const messageCountRef = ref(null);
    return {
      config: config,
      setSbUserInfo: (user) => {
        userRef.value = user;
      },
      setUnreadMessageCount: (count) => {
        messageCountRef.value = count;
      },
      sbUserInfo: userRef,
      messageCount: messageCountRef,
    };
  },
};
</script>

<style scoped>
.slide-enter-active,
.slide-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.slide-enter-from,
.slide-leave-to {
  transform: translateX(100%);
  opacity: 0;
}

.slide-enter-to,
.slide-leave-from {
  transform: translateX(0);
  opacity: 1;
}

/* 전체 폰트 */
.sb-chat {
  font-family: "Arial", sans-serif !important;
  font-size: 14px !important;
}
</style>
