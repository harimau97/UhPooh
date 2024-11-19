<template>
  <div>
    <div class="container">
      <h1>회원 가입</h1>

      <form>
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
            required="required"
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
            placeholder="이메일을 입력하세요"
            required="required"
          />
        </div>
        <button
          type="button"
          id="checkIdBtn"
          @click="checkId"
          class="inline-block px-6 py-3 font-bold tracking-wide text-white uppercase bg-gradient-to-r from-yellow-500 via-red-500 to-purple-800 rounded-full shadow-md transition ease-in-out hover:shadow-lg duration-400"
        >
          이메일 중복 확인
        </button>

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
          />
        </div>

        <div class="form-group">
          <button
            type="submit"
            @click="signUp"
            class="inline-block px-6 py-3 font-bold tracking-wide text-white uppercase bg-gradient-to-r from-yellow-500 via-red-500 to-purple-800 rounded-full shadow-md transition ease-in-out hover:shadow-lg duration-400"
          >
            회원 가입
          </button>
        </div>
      </form>
    </div>

    <footer>
      <p>&copy; 2024 Your Website. Made with ❤️</p>
    </footer>
  </div>
</template>

<script setup>
import axios from "axios";
import { useRoute, useRouter } from "vue-router";
import { ref } from "vue";
const isChecked = ref(false);
const router = useRouter();

const checkId = () => {
  var emailField = document.getElementById("userEmail");
  var email = emailField.value;
  isChecked.value = !isChecked.value;
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
      alert(response.data.message);
    } catch (error) {
      console.error(error);
      alert("이미 사용중인 이메일입니다.");
      emailField.value = "";
    }
  };
  fetchData();
};

const signUp = () => {
  event.preventDefault();
  var userName = document.getElementById("userName").value;
  var userEmail = document.getElementById("userEmail").value;
  var password = document.getElementById("password").value;
  if (isChecked.value === true) {
    if (userName !== "" && userEmail !== "" && password !== "") {
      const sendData = async () => {
        await axios
          .post("http://localhost:8080/uhpooh/api/user/signup", {
            userName: userName,
            userEmail: userEmail,
            password: password,
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
  } else if (isChecked.value === false) {
    alert("이메일 중복 확인을 진행해주세요.");
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
  margin-bottom: 15px;
  text-align: left;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
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

/* 버튼 스타일 */
.btn {
  display: inline-block;
  padding: 12px 30px;
  background-color: #833ab4;
  background-image: linear-gradient(45deg, #fcb045, #fd1d1d, #833ab4);
  border-radius: 50px;
  color: white;
  text-decoration: none;
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 1.5px;
  transition: background 0.4s ease;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.btn:hover {
  background-position: right center;
  background-color: #fd1d1d;
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
