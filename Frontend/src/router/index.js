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
      path: '/mypage',
      name: 'MyPage',
      component: () => import('@/views/MyPage.vue')
    }
  ],
  // 페이지 이동시 스크롤 동작 정의
  scrollBehavior(to, from, savedPosition) {
    // 브라우저의 뒤로/앞으로 버튼을 사용할 경우
    if (savedPosition) {
      return savedPosition;
    }
    
    // 해시 링크(#)로 이동할 경우
    if (to.hash) {
      return {
        el: to.hash,
        behavior: 'smooth'
      };
    }
    
    // 기본적으로 페이지 최상단으로 스크롤
    return { 
      top: 0,
      behavior: 'smooth'
    };
  }
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