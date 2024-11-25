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
              <label for="email" class="block text-sm font-bold text-gray-700">이메일</label>
              <input
                id="email"
                type="email"
                v-model="userEmail"
                :disabled="isLoading"
                placeholder="이메일을 입력해주세요."
                class="px-4 py-4 w-full text-sm placeholder-gray-400 text-gray-800 bg-gray-50 rounded-sm border border-gray-200 transition-all duration-300 focus:outline-none focus:ring-2 focus:ring-sky-100 focus:border-sky-300 disabled:opacity-50"
                required
              />
            </div>

            <!-- Password Input -->
            <div class="mt-8 space-y-3">
              <label for="password" class="block text-sm font-bold text-gray-700">비밀번호</label>
              <div class="relative">
                <input
                  id="password"
                  :type="showPassword ? 'text' : 'password'"
                  v-model="password"
                  :disabled="isLoading"
                  placeholder="비밀번호를 입력해주세요."
                  class="px-4 py-4 w-full text-sm placeholder-gray-400 text-gray-800 bg-gray-50 rounded-sm border border-gray-200 transition-all duration-300 focus:outline-none focus:ring-2 focus:ring-sky-100 focus:border-sky-300 disabled:opacity-50"
                  required
                />
                <button
                  type="button"
                  @click="showPassword = !showPassword"
                  class="absolute right-4 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600"
                >
                  <span class="sr-only">{{ showPassword ? "비밀번호 숨기기" : "비밀번호 표시" }}</span>
                  <i
                    :class="showPassword ? 'eye-slash-icon' : 'eye-icon'"
                    class="w-5 h-5"
                  />
                </button>
              </div>
              <!-- Error Message -->
              <p v-if="loginError" class="text-sm text-red-500 mt-2">
                {{ loginError }}
              </p>
            </div>

            <!-- Login Button -->
            <div class="mt-12 space-y-4">
              <button
                type="submit"
                :disabled="isLoading"
                class="w-full bg-gradient-to-r from-sky-500 to-blue-500 text-white rounded-sm px-4 py-4 font-bold transform transition-all duration-300 hover:from-sky-600 hover:to-blue-600 hover:scale-[1.02] hover:shadow-lg focus:outline-none focus:ring-2 focus:ring-sky-300 active:scale-[0.98] disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <span v-if="isLoading">
                  <span class="inline-block animate-spin mr-2">⌛</span>
                  로그인 중...
                </span>
                <span v-else>로그인</span>
              </button>

              <!-- Sign Up Link -->
              <RouterLink
                to="/signup"
                class="block w-full px-4 py-4 text-center text-gray-600 bg-white border border-gray-200 rounded-sm font-bold transition-all duration-300 hover:bg-gray-50 hover:border-gray-300 hover:scale-[1.02] focus:outline-none focus:ring-2 focus:ring-gray-200"
              >
                회원가입
              </RouterLink>
            </div>

            <!-- Social Login Divider -->
            <div class="mt-8 flex items-center">
              <div class="flex-1 border-t border-gray-200"></div>
              <div class="px-4 text-sm text-gray-500">소셜 로그인</div>
              <div class="flex-1 border-t border-gray-200"></div>
            </div>

            <!-- Social Login Buttons -->
            <div class="mt-6 flex justify-center space-x-4">
              <!-- 네이버 로그인 -->
              <button
                @click.prevent="handleNaverLogin"
                :disabled="isLoading"
                class="transform transition-transform hover:scale-110 focus:outline-none disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <img
                  class="w-12 h-12"
                  src="/src/assets/naver.png"
                  alt="Naver Login"
                />
              </button>

              <!-- 카카오 로그인 -->
              <button
                @click.prevent="handleKakaoLogin"
                :disabled="isLoading"
                class="transform transition-transform hover:scale-110 focus:outline-none disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <img
                  class="w-12 h-12"
                  src="/src/assets/kakao.png"
                  alt="Kakao Login"
                />
              </button>

              <!-- 구글 로그인 -->
              <button
                @click.prevent="handleGoogleLogin"
                :disabled="isLoading"
                class="transform transition-transform hover:scale-110 focus:outline-none disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <img
                  class="w-12 h-12"
                  src="/src/assets/google.png"
                  alt="Google Login"
                />
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";

// 환경변수
const API_URL = import.meta.env.VITE_API_URL;
const NAVER_CLIENT_ID = import.meta.env.VITE_NAVER_CLIENT_ID;
const KAKAO_CLIENT_ID = import.meta.env.VITE_KAKAO_CLIENT_ID;
const KAKAO_REDIRECT_URI = import.meta.env.VITE_KAKAO_REDIRECT_URI;
const GOOGLE_CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID;
const GOOGLE_REDIRECT_URI = import.meta.env.VITE_GOOGLE_REDIRECT_URI;

const router = useRouter();
const userEmail = ref("");
const password = ref("");
const loginError = ref("");
const isLoading = ref(false);
const showPassword = ref(false);

const validateForm = () => {
  const emailRegex = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
  
  if (!userEmail.value) {
    loginError.value = "이메일을 입력해주세요.";
    return false;
  }
  if (!emailRegex.test(userEmail.value)) {
    loginError.value = "유효한 이메일 주소를 입력해주세요.";
    return false;
  }
  if (!password.value) {
    loginError.value = "비밀번호를 입력해주세요.";
    return false;
  }
  return true;
};

const handleSubmit = async () => {
  if (!validateForm()) return;

  isLoading.value = true;
  loginError.value = "";

  try {
    const response = await axios.post(`${API_URL}/uhpooh/api/user/login`, {
      userEmail: userEmail.value,
      password: password.value,
    });

    const userData = response.data.user;
    const userDataToStore = {
      userId: userData.userId,
      userName: userData.userName,
      userAddress: userData.userAddress,
      pImage: userData.pImage,
    };

    Object.entries(userDataToStore).forEach(([key, value]) => {
      localStorage.setItem(key, value);
    });
    
    // 토큰은 쿠키나 세션 스토리지에 저장
    document.cookie = `userToken=${response.data.userToken}; path=/;`;

    await router.push("/");
    location.reload();
  } catch (error) {
    console.error("Login error:", error);
    loginError.value =
      error.response?.data?.error ||
      "로그인에 실패했습니다. 잠시 후 다시 시도해주세요.";
  } finally {
    isLoading.value = false;
  }
};

const generateRandomState = () => {
  return Array.from(crypto.getRandomValues(new Uint8Array(32)))
    .map(byte => byte.toString(16).padStart(2, '0'))
    .join('');
};

const handleSocialLogin = (authUrl, clientId, redirectUri) => {
  const STATE = generateRandomState();
  
  localStorage.setItem("oauth2State", STATE);
  
  const params = new URLSearchParams({
    client_id: clientId,
    redirect_uri: redirectUri,
    response_type: "code",
    state: STATE,
  });

  window.location.href = `${authUrl}?${params.toString()}`;
};

const handleNaverLogin = () => {
  const NAVER_AUTH_URL = "https://nid.naver.com/oauth2.0/authorize";
  
  handleSocialLogin(
    NAVER_AUTH_URL, 
    NAVER_CLIENT_ID, 
    `${API_URL}/uhpooh/login/oauth2/code/naver`
  );
};

const handleKakaoLogin = () => {
  const KAKAO_AUTH_URL = "https://kauth.kakao.com/oauth/authorize";
  
  handleSocialLogin(KAKAO_AUTH_URL, KAKAO_CLIENT_ID, KAKAO_REDIRECT_URI);
};

const handleGoogleLogin = () => {
  const GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";
  
  const params = new URLSearchParams({
    client_id: GOOGLE_CLIENT_ID,
    redirect_uri: GOOGLE_REDIRECT_URI,
    response_type: "code",
    scope: "email profile",
    state: generateRandomState(),
  });

  window.location.href = `${GOOGLE_AUTH_URL}?${params.toString()}`;
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

.animate-slide-up {
  animation: slideUp 0.6s ease-out 0.2s both;
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