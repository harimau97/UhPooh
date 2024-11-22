import { createRouter, createWebHistory } from "vue-router";
import Home from "../views/Home.vue";
import {
  isAuthenticated,
  getUserInfo,
  userAuthenticated,
} from "../composables/userAuth";

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
      meta: { requiresAuth: false },
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
    {
      path: "/admin",
      name: "admin",
      component: () => import("../views/Admin.vue"),
      meta: { requiresAuth: true, requiresAdmin: true },
    },
  ],
  // 스크롤 동작 추가
  scrollBehavior(to, from, savedPosition) {
    // savedPosition은 브라우저의 뒤로/앞으로 버튼을 사용할 때만 존재
    if (savedPosition) {
      return savedPosition;
    }
    // 기본적으로는 페이지 최상단으로 스크롤
    return { top: 0 };
  },
});

// 전역 라우터 가드 설정
router.beforeEach(async (to, from, next) => {
  if (to.meta.requiresAuth) {
    await isAuthenticated();
    if (!userAuthenticated.value) {
      alert("잘못된 접근입니다.");
      return next("/login");
    }

    // Admin 페이지 접근 체크
    if (to.meta.requiresAdmin && localStorage.getItem("isAdmin") !== "1") {
      alert("관리자 권한이 필요합니다.");
      return next("/");
    }
  } else {
    await getUserInfo();
  }
  next();
});

export default router;
