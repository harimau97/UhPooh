import { createRouter, createWebHistory } from "vue-router";
import Home from "../views/Home.vue";
import { userAuthenticated } from "../composables/userAuth";

// 인증 확인 함수

const checkAuthentication = userAuthenticated.value;
console.log(checkAuthentication);

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
      meta: { requiresAuth: true }, // 인증 필요
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
  ],
});

// 전역 라우터 가드 설정
router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth && !checkAuthentication) {
    // 인증이 필요하고, 인증되지 않은 경우 로그인 페이지로 이동
    alert("잘못된 접근입니다.");
    next("/login");
  } else {
    next();
  }
});

export default router;
