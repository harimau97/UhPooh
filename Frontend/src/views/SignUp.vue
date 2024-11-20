<template>
  <div>
    <div class="container">
      <h1>회원 가입</h1>

      <form action="#">
        <div class="form-group">
          <label for="memberId">이름</label>
          <input
            type="text"
            class="form-control"
            id="userName"
            name="userName"
            placeholder="이름을 입력하세요"
            title="3글자 이상 입력하세요."
            minlength="3"
            maxlength="10"
            required="required"
            v-model="userName"
            @input="checkForm()"
          />
          <br />
        </div>
        <div class="form-group">
          <label for="email">이메일</label>
          <input
            type="email"
            id="userEmail"
            name="userEmail"
            class="form-control"
            pattern="[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.(com|net|org|kr|co)"
            title="이메일 형식이 맞지 않습니다."
            placeholder="이메일을 입력하세요"
            required="required"
            v-model="userEmail"
            @input="checkForm"
          />
        </div>
        <div class="mt-4">
          <button
            type="button"
            id="checkIdBtn"
            @click="checkId"
            :disabled="!validEmail"
            :class="[
              'inline-block px-8 py-3 rounded-full text-white font-bold uppercase tracking-wide transition-all duration-400 shadow-lg border-2',
              validEmail
                ? 'bg-gradient-to-br from-blue-600 via-blue-500 to-blue-700 hover:from-blue-700 hover:via-blue-600 hover:to-blue-800'
                : 'bg-gray-400 border-gray-400 cursor-not-allowed',
            ]"
          >
            이메일 중복 확인
          </button>
        </div>
        <div class="form-group">
          <label for="password">비밀번호</label>
          <input
            type="password"
            id="password"
            name="password"
            class="form-control"
            placeholder="비밀번호를 입력하세요"
            title="숫자,영어 소문자, 대문자, 특수문자(~!@#$%)가 하나 이상 필요합니다."
            minlength="8"
            required="required"
            v-model="password"
            @input="checkForm"
          />
        </div>
        <div class="form-group">
          <label for="memberId">기본 주소</label>
          <input
            type="text"
            class="form-control"
            id="userAddress"
            name="userAddress"
            placeholder="기본으로 사용할 주소를 입력하세요"
            minlength="10"
            maxlength="100"
            required="required"
            v-model="userAddress"
            @input="checkForm()"
          />
          <br />
        </div>

        <div class="form-group">
          <button
            type="submit"
            @click="signUp"
            :disabled="!activeSignUP"
            :class="[
              'inline-block px-8 py-3 rounded-full text-white font-bold uppercase tracking-wide transition-all duration-400 shadow-lg border-2',
              activeSignUP
                ? 'bg-gradient-to-br from-blue-600 via-blue-500 to-blue-700 hover:from-blue-700 hover:via-blue-600 hover:to-blue-800'
                : 'bg-gray-400 border-gray-400 cursor-not-allowed',
            ]"
          >
            회원 가입
          </button>
        </div>
      </form>
      <ul>
        <li class="flex">
          <svg
            v-show="validName"
            xmlns="http://www.w3.org/2000/svg"
            width="20"
            height="20"
            viewBox="0 0 100 100"
          >
            <circle
              cx="50"
              cy="50"
              r="40"
              stroke="green"
              stroke-width="15"
              fill="none"
            />
          </svg>
          <svg
            v-show="!validName"
            xmlns="http://www.w3.org/2000/svg"
            width="20"
            height="20"
            viewBox="0 0 100 100"
          >
            <line
              x1="20"
              y1="20"
              x2="80"
              y2="80"
              stroke="red"
              stroke-width="15"
            />
            <line
              x1="20"
              y1="80"
              x2="80"
              y2="20"
              stroke="red"
              stroke-width="15"
            /></svg
          >이름은 3글자 이상 10글자 이하인가요?
        </li>
        <li class="flex">
          <svg
            v-show="validEmail"
            xmlns="http://www.w3.org/2000/svg"
            width="20"
            height="20"
            viewBox="0 0 100 100"
          >
            <circle
              cx="50"
              cy="50"
              r="40"
              stroke="green"
              stroke-width="15"
              fill="none"
            /></svg
          ><svg
            v-show="!validEmail"
            xmlns="http://www.w3.org/2000/svg"
            width="20"
            height="20"
            viewBox="0 0 100 100"
          >
            <line
              x1="20"
              y1="20"
              x2="80"
              y2="80"
              stroke="red"
              stroke-width="15"
            />
            <line
              x1="20"
              y1="80"
              x2="80"
              y2="20"
              stroke="red"
              stroke-width="15"
            /></svg
          >이메일 형식을 충족하셨나요?
        </li>

        <li class="flex pr-2">
          <svg
            v-show="validPassword"
            xmlns="http://www.w3.org/2000/svg"
            width="20"
            height="20"
            viewBox="0 0 100 100"
          >
            <circle
              cx="50"
              cy="50"
              r="40"
              stroke="green"
              stroke-width="15"
              fill="none"
            />
          </svg>
          <svg
            v-show="!validPassword"
            xmlns="http://www.w3.org/2000/svg"
            width="20"
            height="20"
            viewBox="0 0 100 100"
          >
            <line
              x1="20"
              y1="20"
              x2="80"
              y2="80"
              stroke="red"
              stroke-width="15"
            />
            <line
              x1="20"
              y1="80"
              x2="80"
              y2="20"
              stroke="red"
              stroke-width="15"
            />
          </svg>
          비밀번호는 숫자, 영어 소문자 및 대문자, 특수문자(!@#$%^&*)가 하나 이상
          필요합니다.
        </li>
        <li class="flex pr-2">
          <svg
            v-show="validAddress"
            xmlns="http://www.w3.org/2000/svg"
            width="20"
            height="20"
            viewBox="0 0 100 100"
          >
            <circle
              cx="50"
              cy="50"
              r="40"
              stroke="green"
              stroke-width="15"
              fill="none"
            />
          </svg>
          <svg
            v-show="!validAddress"
            xmlns="http://www.w3.org/2000/svg"
            width="20"
            height="20"
            viewBox="0 0 100 100"
          >
            <line
              x1="20"
              y1="20"
              x2="80"
              y2="80"
              stroke="red"
              stroke-width="15"
            />
            <line
              x1="20"
              y1="80"
              x2="80"
              y2="20"
              stroke="red"
              stroke-width="15"
            />
          </svg>
          주소를 입력하셨나요?
        </li>
      </ul>
    </div>

    <footer class="flex justify-center">
      <p>&copy; 2024 Your Website. Made with ❤️</p>
    </footer>
  </div>
</template>

<script setup>
import axios from "axios";
import { useRouter } from "vue-router";
import { ref } from "vue";
const isChecked = ref(false);
const router = useRouter();
const userEmail = ref("");
const userName = ref("");
const password = ref("");
const userAddress = ref("");
const validName = ref(false);
const validEmail = ref(false);
const validPassword = ref(false);
const validAddress = ref(false);
const activeSignUP = ref(false);
const checkForm = () => {
  const hasUppercase = /[A-Z]/.test(password.value);
  const hasLowercase = /[a-z]/.test(password.value);
  const hasNumber = /[0-9]/.test(password.value);
  const hasSpecialChar = /[!@#$%^&*]/.test(password.value);
  const hasEmailFormat =
    /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.(com|net|org|kr|co)$/.test(
      userEmail.value
    );
  const hasAddress = userAddress.value.length > 9;

  // Name validation (3-10 characters)
  validName.value = userName.value.length >= 2 && userName.value.length <= 10;

  // Email validation
  validEmail.value = hasEmailFormat;

  // Password validation
  validPassword.value =
    hasUppercase &&
    hasLowercase &&
    hasNumber &&
    hasSpecialChar &&
    password.value.length >= 8;

  validAddress.value = hasAddress;

  activeSignUP.value =
    validName.value &&
    validEmail.value &&
    validPassword.value &&
    validAddress.value &&
    isChecked.value;

  console.log(activeSignUP.value);
};

const checkId = () => {
  var email = userEmail.value;
  console.log(isChecked.value);
  console.log(email);
  if (email === "") {
    alert("이메일을 입력해주세요.");
    return;
  }

  const fetchData = async () => {
    try {
      const response = await axios.get(
        "http://localhost:8080/uhpooh/api/user/check/email/" + email
      );
      console.log(response.data);
      isChecked.value = true;
      alert(response.data.message);
    } catch (error) {
      console.error(error);
      alert("이미 사용중인 이메일입니다.");
      userEmail.value = "";
    }
  };
  fetchData();
};

const signUp = () => {
  console.log("회원가입 시작");
  event.preventDefault();
  var uName = userName.value;
  var uEmail = userEmail.value;
  var uPassword = password.value;
  var uAddress = userAddress.value;
  if (isChecked.value === true) {
    if (uName !== "" && uEmail !== "" && password !== "") {
      const sendData = async () => {
        await axios
          .post("http://localhost:8080/uhpooh/api/user/signup", {
            userName: uName,
            userEmail: uEmail,
            password: uPassword,
            userAddress: uAddress,
          })
          .then((response) => {
            alert("회원 가입 완료");
          })
          .catch((err) => {
            alert(err);
          });
      };
      sendData();
      router.push("/");
    }
  }
};
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* 메인 컨테이너 */
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

h1 {
  font-size: 48px;
  font-weight: 700;
  color: #333;
  margin-bottom: 20px;
}

p {
  font-size: 18px;
  line-height: 1.6;
  margin-bottom: 20px;
}

/* 폼 스타일 */
.form-group {
  margin-bottom: 10px;
  text-align: left;
}

.form-group label {
  display: block;
  margin-top: 5px;
  margin-bottom: 10px;
  font-size: 16px;
  color: #333;
}

.form-group input {
  width: 100%;
  padding: 10px;
  font-size: 16px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

/* 링크 스타일 */
a {
  color: #833ab4;
  text-decoration: none;
  font-weight: 600;
}

a:hover {
  color: #fd1d1d;
}

/* 푸터 */
footer {
  margin-top: 50px;
  color: #777;
  font-size: 14px;
}

footer a {
  color: #833ab4;
  text-decoration: none;
}

footer a:hover {
  color: #fd1d1d;
}
</style>
