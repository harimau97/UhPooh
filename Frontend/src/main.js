import "./assets/main.css";

import { createApp } from "vue";
import App from "./App.vue";
import { createPinia } from "pinia";
import router from "./router";

import { useKakao } from "vue3-kakao-maps/@utils";

useKakao("e8e6b744bbb5ccb91dc2c9c077b06860");

const app = createApp(App);
const pinia = createPinia();

app.use(pinia); // Pinia 등록
app.use(router);

// axios.defaults.baseURL = "http://localhost:8080/uhpooh/api"; // 기본 API URL 설정
// axios.defaults.headers.common["Authorization"] = "Bearer your_token"; // 공통 헤더 설정

// Axios를 Vue 인스턴스에 주입
// app.config.globalProperties.$axios = axios;

app.mount("#app");
