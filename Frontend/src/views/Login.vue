<template>
  <div class="relative min-h-screen flex items-center justify-center overflow-hidden bg-gradient-to-br from-gray-50 to-white">
    <!-- Animated Background Elements -->
    <div class="absolute inset-0 overflow-hidden">
      <div class="absolute inset-0 bg-[radial-gradient(circle_at_50%_50%,rgba(0,0,0,0.03)_1px,transparent_1px)] bg-[length:20px_20px]"></div>
      <div class="absolute top-0 -left-4 w-96 h-96 bg-sky-100 rounded-full mix-blend-multiply filter blur-3xl opacity-70 animate-blob"></div>
      <div class="absolute top-0 -right-4 w-96 h-96 bg-blue-50 rounded-full mix-blend-multiply filter blur-3xl opacity-70 animate-blob animation-delay-2000"></div>
      <div class="absolute bottom-0 left-20 w-96 h-96 bg-gray-100 rounded-full mix-blend-multiply filter blur-3xl opacity-70 animate-blob animation-delay-4000"></div>
    </div>

    <!-- Login Container -->
    <div class="relative w-full max-w-md mx-4 -mt-20">
      <!-- Login Card -->
      <div class="bg-white/80 backdrop-blur-lg rounded-md shadow-2xl border border-gray-100 animate-slide-up">
        <div class="py-12 px-8">
          <form>
            <!-- Email Input -->
            <div class="space-y-3">
              <label for="fname" class="block text-sm font-bold text-gray-700">
                아이디
              </label>
              <input
                type="text"
                name="userEmail"
                placeholder="이메일을 입력해주세요."
                required="required"
                v-model="userEmail"
                class="w-full px-4 py-4 bg-gray-50 border border-gray-200 rounded-sm text-gray-800 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-sky-100 focus:border-sky-300 transition-all duration-300"
              />
            </div>

            <!-- Password Input -->
            <div class="space-y-3 mt-8">
              <label for="lname" class="block text-sm font-bold text-gray-700">
                비밀번호
              </label>
              <input
                type="password"
                name="password"
                placeholder="비밀번호를 입력해주세요."
                required="required"
                v-model="password"
                class="w-full px-4 py-4 bg-gray-50 border border-gray-200 rounded-sm text-gray-800 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-sky-100 focus:border-sky-300 transition-all duration-300"
              />
            </div>

            <!-- Action Buttons -->
            <div class="space-y-4 mt-12">
              <button
                type="button"
                @click="login"
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
          </form>
        </div>
      </div>

      <!-- Additional Links -->
      <div class="mt-6 text-center space-y-2 animate-fade-in-delay">
        <a href="#" class="text-sm text-gray-600 hover:text-gray-800 transition-colors">비밀번호를 잊으셨나요?</a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
const router = useRouter();

import axios from "axios";
const userEmail = ref("");
const password = ref("");

const login = async () => {
  console.log(userEmail.value);
  console.log(password.value);
  await axios
    .post("http://localhost:8080/uhpooh/api/user/login", {
      userEmail: userEmail.value,
      password: password.value,
    })
    .then((response) => {
      console.log(response.data.user);
      localStorage.setItem("userId", response.data.user.userId);
      localStorage.setItem("userName", response.data.user.userName);
      localStorage.setItem("userAddress", response.data.user.userAddress);
      localStorage.setItem("pImage", response.data.user.pImage);
      localStorage.setItem("userToken", response.data.userToken);
      console.log(response.data);
      console.log(localStorage.getItem("userToken"));
      router.push("/").then(() => {
        location.reload();
      });
    })
    .catch((err) => {
      alert("아이디 또는 비밀번호를 잘못 입력했습니다.");
    });
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
  animation: fadeIn 0.8s ease-out;
}

.animate-fade-in-delay {
  animation: fadeIn 0.8s ease-out 0.2s both;
}

.animate-slide-up {
  animation: slideUp 0.8s ease-out 0.2s both;
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

