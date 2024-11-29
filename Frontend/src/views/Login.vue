<template>
  <div
    class="flex overflow-hidden relative justify-center items-center min-h-screen bg-gradient-to-br from-gray-50 to-white"
  >
    <!-- Animated Background Elements -->
    <div class="overflow-hidden absolute inset-0">
      <div
        class="absolute inset-0 bg-[radial-gradient(circle_at_50%_50%,rgba(0,0,0,0.03)_1px,transparent_1px)] bg-[length:20px_20px]"
      ></div>
      <div
        class="absolute top-0 -left-4 w-96 h-96 bg-sky-100 rounded-full opacity-70 mix-blend-multiply filter blur-3xl animate-blob"
      ></div>
      <div
        class="absolute top-0 -right-4 w-96 h-96 bg-blue-50 rounded-full opacity-70 mix-blend-multiply filter blur-3xl animate-blob animation-delay-2000"
      ></div>
      <div
        class="absolute bottom-0 left-20 w-96 h-96 bg-gray-100 rounded-full opacity-70 mix-blend-multiply filter blur-3xl animate-blob animation-delay-4000"
      ></div>
    </div>

    <!-- Login Container -->
    <div class="relative mx-4 -mt-20 w-full max-w-md">
      <!-- Login Card -->
      <div
        class="rounded-md border border-gray-100 shadow-2xl backdrop-blur-lg bg-white/80 animate-slide-up"
      >
        <div class="px-8 py-12">
          <form @submit.prevent="handleSubmit">
            <!-- Email Input -->
            <div class="space-y-3">
              <label for="fname" class="block text-sm font-bold text-gray-700">
                아이디
              </label>
              <input
                type="text"
                name="userEmail"
                placeholder="이메일을 입력해주세요."
                v-model="userEmail"
                class="px-4 py-4 w-full text-sm placeholder-gray-400 text-gray-800 bg-gray-50 rounded-sm border border-gray-200 transition-all duration-300 focus:outline-none focus:ring-2 focus:ring-sky-100 focus:border-sky-300"
              />
            </div>

            <!-- Password Input -->
            <div class="mt-8 space-y-3">
              <label for="lname" class="block text-sm font-bold text-gray-700">
                비밀번호
              </label>
              <input
                type="password"
                name="password"
                placeholder="비밀번호를 입력해주세요."
                v-model="password"
                class="px-4 py-4 w-full text-sm placeholder-gray-400 text-gray-800 bg-gray-50 rounded-sm border border-gray-200 transition-all duration-300 focus:outline-none focus:ring-2 focus:ring-sky-100 focus:border-sky-300"
              />
              <!-- Error Message -->
              <p v-if="loginError" class="mt-2 text-sm text-red-500">
                아이디 또는 비밀번호를 잘못 입력했습니다.
              </p>
              <p v-if="validationError" class="mt-2 text-sm text-red-500">
                {{ validationError }}
              </p>
            </div>

            <!-- Action Buttons -->
            <div class="mt-12 space-y-4">
              <button
                type="submit"
                class="w-full bg-gradient-to-r from-sky-500 to-blue-500 text-white rounded-sm px-4 py-4 font-bold transform transition-all duration-300 hover:from-sky-600 hover:to-blue-600 hover:scale-[1.02] hover:shadow-lg focus:outline-none focus:ring-2 focus:ring-sky-300 active:scale-[0.98]"
              >
                로그인
              </button>

              <RouterLink
                to="/signup"
                class="block w-full px-4 py-4 text-center text-gray-600 bg-white border border-gray-200 rounded-sm font-bold transition-all duration-300 hover:bg-gray-50 hover:border-gray-300 hover:scale-[1.02] focus:outline-none focus:ring-2 focus:ring-gray-200"
              >
                회원가입
              </RouterLink>
            </div>

            <!-- Social Login Divider -->
            <div class="flex items-center mt-8">
              <div class="flex-1 border-t border-gray-200"></div>
              <div class="px-4 text-sm text-gray-500">소셜 로그인</div>
              <div class="flex-1 border-t border-gray-200"></div>
            </div>

            <!-- Social Login Buttons -->
            <div class="flex justify-center mt-6 space-x-4">
              <!-- 네이버 로그인 -->
              <button
                @click.prevent="handleNaverLogin"
                :disabled="isLoading"
                class="transition-transform transform hover:scale-110 focus:outline-none disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <img
                  class="w-12 h-12"
                  src="/src/assets/naver.png"
                  alt="Naver Login"
                />
              </button>

              <!-- 구글 로그인 -->
              <button
                @click.prevent="handleGoogleLogin"
                :disabled="isLoading"
                class="transition-transform transform hover:scale-110 focus:outline-none disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <img
                  class="w-12 h-12"
                  src="/src/assets/google.png"
                  alt="Google Login"
                />
              </button>
              <!-- 깃허브 로그인 -->
              <button
                @click.prevent="handleGithubLogin"
                :disabled="isLoading"
                class="transition-transform transform hover:scale-110 focus:outline-none disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <img
                  class="w-12 h-12"
                  src="/src/assets/github.png"
                  alt="Github Login"
                />
              </button>
            </div>
          </form>
        </div>
      </div>

      <!-- Additional Links -->
      <div class="mt-6 space-y-2 text-center animate-fade-in-delay">
        <a
          href="#"
          @click.prevent="showPasswordReset"
          class="text-sm text-gray-600 transition-colors hover:text-gray-800"
          >비밀번호를 잊으셨나요?</a
        >
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";

const router = useRouter();
const userEmail = ref("");
const password = ref("");
const loginError = ref(false);
const validationError = ref("");
const isLoading = ref(false);
const showPassword = ref(false);

const showPasswordReset = () => {
  alert("관리자에게 문의바랍니다.");
};

const handleSubmit = async () => {
  // Reset error states
  loginError.value = false;
  validationError.value = "";

  // Validation
  if (!userEmail.value || !password.value) {
    validationError.value = "아이디와 비밀번호를 모두 입력해주세요.";
    return;
  }

  // Proceed with login
  await login();
};

const login = async () => {
  await axios
    .post("http://localhost:8080/uhpooh/api/user/login", {
      userEmail: userEmail.value,
      password: password.value,
    })
    .then((response) => {
      console.log("유저정보 입니다.", response.data);
      localStorage.setItem("userId", response.data.user.userId);
      localStorage.setItem("userName", response.data.user.userName);
      localStorage.setItem("userAddress", response.data.user.userAddress);
      localStorage.setItem("pImage", response.data.user.pImage);
      localStorage.setItem("userToken", response.data.userToken);
      localStorage.setItem("provider", response.data.provider);
      console.log(response.data);
      console.log(localStorage.getItem("userToken"));
      setTimeout(() => {
       location.replace("/"); 
      }, 3000);
      
    })
    .catch((err) => {
      loginError.value = true;
    });
};

const handleNaverLogin = () => {
  window.location.href =
    "http://localhost:8080/uhpooh/oauth2/authorization/naver";
};

const handleGithubLogin = () => {
  console.log("GitHub Login Button Clicked");
  window.location.href =
    "http://localhost:8080/uhpooh/oauth2/authorization/github";
  console.log("GitHub Login Redirected");
};

const handleGoogleLogin = () => {
  window.location.href =
    "http://localhost:8080/uhpooh/oauth2/authorization/google";
};
</script>

<style scoped>
.animate-blob {
  animation: blob 7s infinite;
}

.animation-delay-2000 {
  animation-delay: 2s;
}

.animation-delay-4000 {
  animation-delay: 4s;
}

@keyframes blob {
  0% {
    transform: translate(0px, 0px) scale(1);
  }
  33% {
    transform: translate(30px, -50px) scale(1.1);
  }
  66% {
    transform: translate(-20px, 20px) scale(0.9);
  }
  100% {
    transform: translate(0px, 0px) scale(1);
  }
}

.animate-fade-in {
  animation: fadeIn 0.6s ease-out;
}

.animate-fade-in-delay {
  animation: fadeIn 0.6s ease-out 0.2s both;
}

.animate-slide-up {
  animation: slideUp 0.6s ease-out 0.2s both;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
