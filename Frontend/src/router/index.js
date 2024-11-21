import { createRouter, createWebHistory } from "vue-router";
import Home from "../views/Home.vue";
import {
  isAuthenticated,
  getUserInfo,
  userAuthenticated,
} from "../composables/userAuth";

// 인증 확인 함수

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "home",
      component: Home,
    },
    {
      path: "/around",
      name: "around",
      component: () => import("../views/AroundMe.vue"),
      meta: { requiresAuth: false }, // 인증 필요
    },
    {
      path: "/login",
      name: "login",
      component: () => import("../views/Login.vue"),
    },
    {
      path: "/signup",
      name: "signup",
      component: () => import("../views/SignUp.vue"),
    },
    {
      path: "/placeBoard",
      name: "placeBoard",
      component: () => import("../views/placeBoard.vue"),
    },
    {
      path: "/mypage",
      name: "mypage",
      component: () => import("../views/MyPage.vue"),
    },
  ],
});

// 전역 라우터 가드 설정
router.beforeEach(async (to, from, next) => {
  if (to.meta.requiresAuth) {
    await isAuthenticated();
    if (!userAuthenticated.value) {
      alert("잘못된 접근입니다.");
      return next("/login");
    }
  } else {
    await getUserInfo();
  }
  next();
});

export default router;
