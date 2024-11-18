import "./assets/main.css";

import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";

import { useKakao } from "vue3-kakao-maps/@utils";

useKakao("e8e6b744bbb5ccb91dc2c9c077b06860");

const app = createApp(App);

app.use(router);

app.mount("#app");
