<template>
  <transition name="slide">
    <div
      v-show="openChat"
      :class="[
        'fixed inset-0 flex items-center justify-center z-50',
        classStyle,
      ]"
    >
      <div
        class="absolute right-5 bottom-5 p-2 w-80 max-w-screen-md h-1/2 bg-blue-500 bg-opacity-60 rounded-lg shadow-lg"
      >
        <button
          @click="openChat = false"
          class="flex absolute top-1 right-1 z-50 justify-center items-center w-6 h-6 text-gray-300 bg-indigo-800 rounded-full shadow-md transition-colors duration-300 hover:text-gray-100 hover:bg-indigo-600 hover:invert"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
            class="w-5 h-5"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M6 18L18 6M6 6l12 12"
            />
          </svg>
        </button>
        <Chat
          :config="config"
          :setSbUserInfo="setSbUserInfo"
          :setUnreadMessageCount="setUnreadMessageCount"
        />
      </div>
    </div>
  </transition>
  <button
    v-show="isLoggined"
    @click="openChat = !openChat"
    class="fixed right-1 bottom-4 z-50 px-1 py-1 ml-4 text-white rounded-full hover:animate-bounce bg-slate-500"
  >
    <svg
      width="40px"
      height="40px"
      viewBox="0 0 20 20"
      xmlns="http://www.w3.org/2000/svg"
    >
      <path
        d="M11 12H9v-.148c0-.876.306-1.499 1-1.852.385-.195 1-.568 1-1a1.001 1.001 0 00-2 0H7c0-1.654 1.346-3 3-3s3 1 3 3-2 2.165-2 3zm-2 3h2v-2H9v2z"
        fill="#FFFFFF"
      />
      <path
        d="M10 4a6 6 0 100 12 6 6 0 000-12zm-8 6a8 8 0 1116 0 8 8 0 01-16 0z"
        fill="#FFFFFF"
      />
    </svg>
  </button>
</template>

<script>
import { applyReactInVue, applyPureReactInVue } from "veaury";
import { ref } from "vue";
import ChatReactComponent from "../react_app/Chat.jsx";
import Header from "./Header.vue";
import { inject, watch } from "vue";

const isLoggined = inject("isLoggedIn");
watch(isLoggined, (newVal) => {
  console.log("isLoggedIn 상태 변경:", newVal);
});

const config = {
  APP_ID: "7D7B93DC-9263-4488-9841-B1C8F158E8B4",
  USER_ID: "Bob_1",
  NICKNAME: "Bob",
};

export default {
  data() {
    return {
      openChat: false, // 모달을 열고 닫기 위한 상태 변수
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
.message-count {
  position: relative;
}
.nickname {
  color: #006d77; /* 녹청색 */
} /* App.vue 또는 전역 CSS 파일에 추가 */

/* 전체 채팅 UI 크기 */
.sb-chat {
  width: 700px !important;
  height: 800px !important;
}

/* 메시지 버블 스타일 */
.sb-message {
  background-color: #e0f7fa !important;
  color: #006d77 !important;
  border-radius: 8px;
  padding: 10px;
}

/* 내가 보낸 메시지와 상대방 메시지 색상 구분 */
.sb-message--incoming {
  background-color: #e0f7fa !important;
}
.sb-message--outgoing {
  background-color: #a7ffeb !important;
}

/* 전체 폰트 */
.sb-chat {
  font-family: "Arial", sans-serif !important;
  font-size: 14px !important;
}
</style>
