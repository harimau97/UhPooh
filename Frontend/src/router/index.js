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
      meta: { requiresAuth: true }, // 인증 필요 추가
    },
    {
      path: "/edit",  // 새로 추가한 내 정보 수정 라우트
      name: "edit",
      component: () => import("../views/EditProfile.vue"),  // 새로 만든 컴포넌트
      meta: { requiresAuth: true },  // 인증된 사용자만 접근 가능
    },
    {
      path: "/admin",
      name: "admin",
      component: () => import("../views/Admin.vue"),
      meta: { requiresAuth: true, requiresAdmin: true },
    },
    {
      path: "/:pathMatch(.*)*",  // 404 페이지 추가
      name: "not-found",
      component: () => import("../views/NotFound.vue"),  // 404 페이지 컴포넌트
    },
    {
      path: '/change-password',
      component: () => import('@/views/PasswordChange.vue')
    },
    {
      path: '/oauth2/callback',
      component: () => import('../components/OAuth2Callback.vue')
    }
  ],
  // 스크롤 동작 추가
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition;
    }
    return { top: 0 };
  },
});

// 전역 라우터 가드 수정
router.beforeEach(async (to, from, next) => {
  // 로그인이 필요한 페이지 체크
  if (to.meta.requiresAuth) {
    await isAuthenticated();
    if (!userAuthenticated.value) {
      alert("로그인이 필요한 서비스입니다.");
      return next({ 
        path: '/login', 
        query: { redirect: to.fullPath } // 로그인 후 원래 가려던 페이지로 리다이렉트하기 위한 정보 저장
      });
    }

    // Admin 페이지 접근 체크
    if (to.meta.requiresAdmin && localStorage.getItem("isAdmin") !== "1") {
      alert("관리자 권한이 필요합니다.");
      return next("/");
    }
  }

  // 이미 로그인한 사용자가 로그인/회원가입 페이지 접근 시 홈으로 리다이렉트
  if ((to.name === 'login' || to.name === 'signup') && userAuthenticated.value) {
    return next('/');
  }

  await getUserInfo();
  next();
});

export default router;