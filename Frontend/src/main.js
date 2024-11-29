import "./assets/main.css";

import { createApp } from "vue";
import App from "./App.vue";
import { createPinia } from "pinia";
import router from "./router";

import { useKakao } from "vue3-kakao-maps/@utils";

useKakao(import.meta.env.VITE_KAKAO_MAP_API_KEY);

const app = createApp(App);
const pinia = createPinia();

app.use(pinia); // Pinia 등록
app.use(router);

app.mount("#app");
