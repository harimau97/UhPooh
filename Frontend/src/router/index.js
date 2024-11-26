import { createRouter, createWebHistory } from "vue-router";
import Home from "../views/Home.vue";
import {
  isAuthenticated,
  getUserInfo,
  userAuthenticated,
} from "../composables/userAuth";
import { useModal } from "@/composables/useModal";

const { showModalMessage } = useModal();

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
      path: "/mypools",
      name: "mypool",
      component: () => import("../views/myPools.vue"),
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
      path: "/edit", // 새로 추가한 내 정보 수정 라우트
      name: "edit",
      component: () => import("../views/EditProfile.vue"), // 새로 만든 컴포넌트
      meta: { requiresAuth: true }, // 인증된 사용자만 접근 가능
    },
    {
      path: "/admin",
      name: "admin",
      component: () => import("../views/Admin.vue"),
      meta: { requiresAuth: true, requiresAdmin: true },
    },
    {
      path: "/guide",
      name: "guide",
      component: () => import("../views/UserGuide.vue"),
      meta: { requiresAuth: false },
    },
    {
      path: "/faq",
      name: "faq",
      component: () => import("../views/FAQ.vue"),
      meta: { requiresAuth: false },
    },
    {
      path: "/terms",
      name: "terms",
      component: () => import("../views/Terms.vue"),
      meta: { requiresAuth: false },
    },
    {
      path: "/privacy",
      name: "privacy",
      component: () => import("../views/Privacy.vue"),
      meta: { requiresAuth: false },
    },
    {
      path: "/location-terms",
      name: "locationTerms",
      component: () => import("../views/LocationTerms.vue"),
      meta: { requiresAuth: false },
    },
    {
      path: "/:pathMatch(.*)*", // 404 페이지 추가
      name: "not-found",
      component: () => import("../views/NotFound.vue"), // 404 페이지 컴포넌트
    },
    {
      path: "/change-password",
      component: () => import("@/views/PasswordChange.vue"),
    },
    {
      path: "/oauth2/callback",
      component: () => import("../components/OAuth2Callback.vue"),
    },
    {
      path: "/payment/success",
      name: "payment-success",
      component: () => import("../views/PaymentSuccess.vue"),
      props: (route) => ({
        paymentKey: route.query.paymentKey,
        orderId: route.query.orderId,
        amount: parseInt(route.query.amount),
      }),
    },
    {
      path: "/payment/fail",
      name: "payment-fail",
      component: () => import("../views/PaymentFail.vue"),
      props: (route) => ({
        message: route.query.message,
        code: route.query.code,
      }),
    },
    {
      path: "/settings",
      name: "settings",
      component: () => import("../views/Settings.vue"),
      meta: { requiresAuth: true },
    },
  ],
  // 스크롤 동작 추가
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition;
    }
    return { top: 0 };
  },
});

// 전역 라우터 가드
router.beforeEach(async (to, from, next) => {
  // 비밀번호 변경 페이지에 대한 추가 검사
  if (to.path === "/change-password") {
    const provider = localStorage.getItem("provider");
    if (provider !== "local") {
      next("/mypage"); // 소셜 로그인 사용자는 마이페이지로 리다이렉트
      return;
    }
  }

  // 먼저 인증 상태를 체크
  await isAuthenticated();
  const isLoggedIn = userAuthenticated.value;

  // 이미 로그인한 사용자가 로그인/회원가입 페이지 접근 시 홈으로 리다이렉트
  if ((to.path === "/login" || to.path === "/signup") && isLoggedIn) {
    return next("/");
  }

  // 로그인이 필요한 페이지 체크
  if (to.matched.some((record) => record.meta.requiresAuth)) {
    if (!isLoggedIn) {
      showModalMessage("로그인이 필요한 서비스입니다.");
      return next({
        path: "/login",
        query: { redirect: to.fullPath },
      });
    }

    // Admin 페이지 접근 체크
    if (to.meta.requiresAdmin && localStorage.getItem("isAdmin") !== "1") {
      showModalMessage("관리자 권한이 필요합니다.");
      return next("/");
    }
  }

  await getUserInfo();
  next();
});

export default router;
