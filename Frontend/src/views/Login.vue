<template>
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
      alert("환영합니다." + userEmail.value + "님");
      console.log(response.data);
      router.push("/");
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
</style>
