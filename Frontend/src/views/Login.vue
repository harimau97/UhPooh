<!-- <template>
  <head>
    <meta charset="UTF-8" />
    <title>Welcome</title>
  </head>
  <body id="loginBody">
    <h3>Login</h3>

    <div>
      <form>
        <label for="fname">아이디</label>
        <input
          type="text"
          name="userEmail"
          placeholder="이메일을 입력해주세요."
          required="required"
          v-model="userEmail"
        />

        <label for="lname">패스워드</label>
        <input
          type="password"
          name="password"
          placeholder="비밀 번호를 입력해주세요."
          required="required"
          v-model="password"
        />
      </form>
      <button
        type="button"
        class="w-[8rem] h-[3rem] inline-block px-8 py-3 bg-gradient-to-br from-blue-600 via-blue-500 to-blue-700 rounded-full text-white font-bold uppercase tracking-wide transition-all duration-400 shadow-lg hover:from-blue-700 hover:via-blue-600 hover:to-blue-800 border-2 border-blue-700"
      >
        <Button @click="login">로그인</Button>
      </button>
      <button
        class="w-[8rem] h-[3rem] inline-block px-8 py-3 bg-gradient-to-br from-blue-600 via-blue-500 to-blue-700 rounded-full text-white font-bold uppercase tracking-wide transition-all duration-400 shadow-lg hover:from-blue-700 hover:via-blue-600 hover:to-blue-800 border-2 border-blue-700"
      >
        <RouterLink to="/signup">회원가입</RouterLink>
      </button>
    </div>
  </body>
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
      alert("환영합니다. " + response.data.user.userName + "님");
      console.log(response.data);
      console.log(localStorage.getItem("userToken"));
      location.replace("/");
    })
    .catch((err) => {
      alert(err);
    });
};
</script>

<style scoped>
#loginBody {
  font-family: "Arial", sans-serif;
  background-color: #f9fcff;
  background-image: linear-gradient(147deg, #f9fcff 0%, #dee4ea 74%);
  background-repeat: repeat-x;
  height: 100%;
  padding: 20px;
  color: #333;
}

h3 {
  font-size: 36px;
  font-weight: bold;
  color: black;
  margin-bottom: 30px;
  text-align: center;
}

div {
  border-radius: 10px;
  background-color: #fff;
  padding: 40px;
  max-width: 500px;
  margin: 0 auto;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

label {
  font-size: 16px;
  font-weight: bold;
  color: #555;
  display: block;
}

input[type="text"],
input[type="password"] {
  width: 100%;
  padding: 15px;
  margin: 5px 0;
  border: 1px solid #ccc;
  border-radius: 5px;
  box-sizing: border-box;
  font-size: 16px;
  background-color: #f9f9f9;
  margin-bottom: 10px;
  transition: all 0.3s ease;
}

input[type="text"]:focus,
input[type="password"]:focus {
  border-color: #833ab4;
  box-shadow: 0 0 10px rgba(131, 58, 180, 0.2);
}

input[type="submit"] {
  width: 100%;
  padding: 15px;
  border: none;
  border-radius: 50px;
  background-image: linear-gradient(45deg, #fcb045, #fd1d1d, #833ab4);
  color: white;
  font-size: 18px;
  font-weight: bold;
  text-transform: uppercase;
  cursor: pointer;
  transition: background 0.4s ease;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  margin-top: 20px;
}

input[type="submit"]:hover {
  background-position: right center;
  background-color: #fd1d1d;
}
</style> -->
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
                class="px-4 py-4 w-full placeholder-gray-400 text-gray-800 bg-gray-50 rounded-sm border border-gray-200 transition-all duration-300 focus:outline-none focus:ring-2 focus:ring-sky-100 focus:border-sky-300"
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
                required="required"
                v-model="password"
                class="px-4 py-4 w-full placeholder-gray-400 text-gray-800 bg-gray-50 rounded-sm border border-gray-200 transition-all duration-300 focus:outline-none focus:ring-2 focus:ring-sky-100 focus:border-sky-300"
              />
            </div>

            <!-- Action Buttons -->
            <div class="mt-12 space-y-4">
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
      <div class="mt-6 space-y-2 text-center animate-fade-in-delay">
        <a
          href="#"
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
