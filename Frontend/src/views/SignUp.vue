<template>
  <div class="relative min-h-screen flex flex-col items-center justify-center overflow-hidden bg-gradient-to-br from-gray-50 to-white">
    <!-- Title -->
    <h1 class="text-sm font-bold text-gray-700 mb-4">회원가입</h1>
    
    <!-- Signup Container -->
    <div class="relative w-full max-w-xl mx-4">
      <div class="bg-white/80 backdrop-blur-lg rounded-md shadow-2xl border border-gray-100">
        <div class="p-8">
          <form class="space-y-6">
            <!-- Nickname Input -->
            <div class="space-y-1">
              <label for="userName" class="block text-sm font-bold text-gray-700">
                닉네임
              </label>
              <input
                type="text"
                id="userName"
                name="userName"
                v-model="userName"
                @input="checkForm()"
                placeholder="닉네임을 입력해주세요"
                required="required"
                maxlength="20"
                class="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-sm text-gray-800 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-sky-100 focus:border-sky-300 transition-all duration-300"
                :class="userName && !validName ? 'border-red-300' : ''"
              />
              <p v-if="userName && !validName" class="text-sm text-red-500 mt-1">
                닉네임은 2~20자의 한글, 영문, 숫자, 특수문자(_,-)만 사용 가능합니다.
              </p>
            </div>

            <!-- Email Input -->
            <div class="space-y-1">
              <label for="userEmail" class="block text-sm font-bold text-gray-700">
                이메일
              </label>
              <input
                type="email"
                id="userEmail"
                name="userEmail"
                v-model="userEmail"
                @input="checkEmailValidity"
                placeholder="이메일을 입력해주세요"
                required="required"
                class="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-sm text-gray-800 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-sky-100 focus:border-sky-300 transition-all duration-300"
                :class="userEmail && ((!validEmail || emailExists) ? 'border-red-300' : '')"
              />
              <p v-if="userEmail && !validEmail" class="text-sm text-red-500 mt-1">
                이메일 형식만 가능합니다.
              </p>
              <p v-if="userEmail && validEmail && emailExists" class="text-sm text-red-500 mt-1">
                이미 사용중인 이메일입니다.
              </p>
              <p v-if="userEmail && validEmail && !emailExists && emailChecked" class="text-sm text-blue-500 mt-1">
                사용 가능한 이메일입니다.
              </p>
            </div>

            <!-- Password Input -->
            <div class="space-y-1">
              <label for="password" class="block text-sm font-bold text-gray-700">
                비밀번호
              </label>
              <input
                type="password"
                id="password"
                name="password"
                v-model="password"
                @input="checkForm"
                placeholder="비밀번호를 입력해주세요"
                required="required"
                minlength="8"
                class="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-sm text-gray-800 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-sky-100 focus:border-sky-300 transition-all duration-300"
                :class="password && !validPassword ? 'border-red-300' : ''"
              />
              <p v-if="password && !validPassword" class="text-sm text-red-500 mt-1">
                비밀번호는 영문, 숫자, 특수문자를 포함하여 8자 이상이어야 합니다.
              </p>
            </div>

            <!-- Confirm Password Input -->
            <div class="space-y-1">
              <label for="confirmPassword" class="block text-sm font-bold text-gray-700">
                비밀번호 확인
              </label>
              <input
                type="password"
                id="confirmPassword"
                name="confirmPassword"
                v-model="confirmPassword"
                @input="checkForm"
                placeholder="비밀번호를 다시 입력해주세요"
                required="required"
                class="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-sm text-gray-800 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-sky-100 focus:border-sky-300 transition-all duration-300"
                :class="confirmPassword && !validConfirmPassword ? 'border-red-300' : ''"
              />
              <p v-if="confirmPassword && !validConfirmPassword" class="text-sm text-red-500 mt-1">
                비밀번호가 일치하지 않습니다.
              </p>
            </div>

            <!-- Address Input -->
            <div class="space-y-1">
              <label for="userAddress" class="block text-sm font-bold text-gray-700">
                기본 주소
              </label>
              <input
                type="text"
                id="userAddress"
                name="userAddress"
                v-model="userAddress"
                @input="checkForm()"
                placeholder="기본으로 사용할 주소를 입력해주세요"
                required="required"
                minlength="10"
                maxlength="100"
                class="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-sm text-gray-800 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-sky-100 focus:border-sky-300 transition-all duration-300"
                :class="userAddress && !validAddress ? 'border-red-300' : ''"
              />
              <p v-if="userAddress && !validAddress" class="text-sm text-red-500 mt-1">
                주소는 10글자 이상 입력해주세요.
              </p>
            </div>

            <!-- Submit Button -->
            <button
              type="submit"
              @click="signUp"
              class="w-full py-4 px-4 rounded-sm font-bold text-white transition-all duration-300 bg-gradient-to-r from-sky-500 to-blue-500 hover:from-sky-600 hover:to-blue-600 focus:outline-none focus:ring-2 focus:ring-sky-300 mt-8"
            >
              회원 가입
            </button>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import axios from "axios";
import { useRouter } from "vue-router";
import { ref, watch } from "vue";

// 라우터 및 기본 상태 설정
const router = useRouter();
const emailExists = ref(false);
const emailChecked = ref(false);

// 입력 필드 상태
const userName = ref("");
const userEmail = ref("");
const password = ref("");
const confirmPassword = ref("");
const userAddress = ref("");

// 유효성 검사 상태
const validName = ref(false);
const validEmail = ref(false);
const validPassword = ref(false);
const validConfirmPassword = ref(false);
const validAddress = ref(false);

// 닉네임 및 이메일 정규식
const nicknameRegex = /^[a-zA-Z0-9가-힣_-]{2,20}$/;
const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.(com|net|org|kr|co)$/;

// 디바운스 함수 구현
const debounce = (fn, delay) => {
  let timeoutId;
  return (...args) => {
    clearTimeout(timeoutId);
    timeoutId = setTimeout(() => fn(...args), delay);
  };
};

// 이메일 중복 확인 (디바운스 적용)
const checkEmailDuplicate = debounce(async (email) => {
  if (!email || !validEmail.value) return;
  
  try {
    await axios.get(`http://localhost:8080/uhpooh/api/user/check/email/${email}`);
    emailExists.value = false;
    emailChecked.value = true;
  } catch (error) {
    emailExists.value = true;
    emailChecked.value = true;
  }
}, 500);

// 이메일 유효성 검사 및 중복 확인
const checkEmailValidity = () => {
  validEmail.value = emailRegex.test(userEmail.value);
  emailChecked.value = false;
  if (validEmail.value) {
    checkEmailDuplicate(userEmail.value);
  }
};

// 입력값 변경 감지
watch([userName, password, confirmPassword, userAddress], () => {
  checkForm();
}, { deep: true });

// 비밀번호 확인 감지
watch([password, confirmPassword], () => {
  if (confirmPassword.value) {
    validConfirmPassword.value = confirmPassword.value === password.value;
  }
  checkForm();
});

// 폼 유효성 검사
const checkForm = () => {
  // 닉네임 검사 (2~20자, 완성된 한글/영문/숫자/_/- 만 허용)
  const hasIncompleteKorean = /[ㄱ-ㅎㅏ-ㅣ]/.test(userName.value);
  validName.value = nicknameRegex.test(userName.value) && !hasIncompleteKorean;

  // 비밀번호 검사 (영문, 숫자, 특수문자 포함 8자 이상)
  const hasLetter = /[a-zA-Z]/.test(password.value);
  const hasNumber = /[0-9]/.test(password.value);
  const hasSpecialChar = /[!@#$%^&*]/.test(password.value);
  validPassword.value = password.value.length >= 8 && hasLetter && hasNumber && hasSpecialChar;

  // 비밀번호 확인 검사
  validConfirmPassword.value = password.value === confirmPassword.value;

  // 주소 검사 (10자 이상)
  validAddress.value = userAddress.value.length >= 10;
};

// 회원가입 처리
const signUp = async (event) => {
  event.preventDefault();
  
  // 모든 필드의 유효성 검사
  if (!userName.value || !userEmail.value || !password.value || !confirmPassword.value || !userAddress.value ||
      !validName.value || !validEmail.value || !validPassword.value || !validConfirmPassword.value || !validAddress.value || emailExists.value) {
    alert("입력하신 정보를 다시 확인해주세요.");
    return;
  }

  // 회원가입 요청
  try {
    const response = await axios.post("http://localhost:8080/uhpooh/api/user/signup", {
      userName: userName.value,
      userEmail: userEmail.value,
      password: password.value,
      userAddress: userAddress.value,
    });
    
    alert("회원 가입이 완료되었습니다.");
    router.push("/");
  } catch (error) {
    console.error(error);
    alert("입력하신 정보를 다시 확인해주세요.");
  }
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
</style>