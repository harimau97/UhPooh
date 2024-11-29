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

    <!-- Signup Container -->
    <div class="relative mx-4 -mt-24 w-full max-w-lg">
      <div
        class="rounded-md border border-gray-100 shadow-2xl backdrop-blur-lg bg-white/80"
      >
        <div class="p-8">
          <form class="space-y-6">
            <!-- Email Input -->
            <div class="space-y-2">
              <label
                for="userEmail"
                class="block text-sm font-bold text-gray-700"
              >
                아이디
              </label>
              <input
                type="email"
                id="userEmail"
                name="userEmail"
                v-model="userEmail"
                @input="checkEmailValidity"
                placeholder="이메일을 입력해주세요"
                required="required"
                class="w-full px-4 py-3 bg-gray-50 border rounded-sm text-gray-800 text-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-sky-100 focus:border-sky-500 transition-colors duration-300"
                :class="
                  userEmail &&
                  (!validEmail || emailExists
                    ? 'border-red-300'
                    : 'border-gray-300')
                "
              />
              <p v-if="userEmail && !validEmail" class="text-xs text-red-500">
                이메일 형식만 가능합니다.
              </p>
              <p
                v-if="userEmail && validEmail && emailExists"
                class="text-xs text-red-500"
              >
                이미 사용중인 이메일입니다.
              </p>
              <p
                v-if="userEmail && validEmail && !emailExists && emailChecked"
                class="text-xs text-blue-500"
              >
                사용 가능한 이메일입니다.
              </p>
            </div>

            <!-- Password Input -->
            <div class="space-y-2">
              <label
                for="password"
                class="block text-sm font-bold text-gray-700"
              >
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
                class="w-full px-4 py-3 bg-gray-50 border rounded-sm text-gray-800 text-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-sky-100 focus:border-sky-500 transition-colors duration-300"
                :class="
                  password && !validPassword
                    ? 'border-red-300'
                    : 'border-gray-300'
                "
              />
              <p v-if="password && !validPassword" class="text-xs text-red-500">
                비밀번호는 영문, 숫자, 특수문자를 포함하여 8자 이상이어야
                합니다.
              </p>
            </div>

            <!-- Confirm Password Input -->
            <div class="space-y-2">
              <label
                for="confirmPassword"
                class="block text-sm font-bold text-gray-700"
              >
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
                class="w-full px-4 py-3 bg-gray-50 border rounded-sm text-gray-800 text-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-sky-100 focus:border-sky-500 transition-colors duration-300"
                :class="
                  confirmPassword && !validConfirmPassword
                    ? 'border-red-300'
                    : 'border-gray-300'
                "
              />
              <p
                v-if="confirmPassword && !validConfirmPassword"
                class="text-xs text-red-500"
              >
                비밀번호가 일치하지 않습니다.
              </p>
            </div>

            <!-- Nickname Input -->
            <div class="space-y-2">
              <label
                for="userName"
                class="block text-sm font-bold text-gray-700"
              >
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
                class="w-full px-4 py-3 bg-gray-50 border rounded-sm text-gray-800 text-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-sky-100 focus:border-sky-500 transition-colors duration-300"
                :class="
                  userName && !validName ? 'border-red-300' : 'border-gray-300'
                "
              />
              <p v-if="userName && !validName" class="text-xs text-red-500">
                닉네임은 2~20자의 한글, 영문, 숫자, 특수문자(_,-)만 사용
                가능합니다.
              </p>
            </div>

            <!-- Address Inputs -->
            <div class="space-y-2">
              <label class="block text-sm font-bold text-gray-700">
                주소
              </label>
              <div class="flex w-full space-x-2">
                <input
                  type="text"
                  v-model="postcode"
                  readonly
                  placeholder="우편번호"
                  class="flex-1 px-4 py-3 bg-gray-50 border rounded-sm text-gray-800 text-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-sky-100 focus:border-sky-500 transition-colors duration-300"
                />
                <button
                  type="button"
                  @click="openAddressSearch"
                  class="px-4 py-3 bg-gray-500 hover:bg-gray-600 text-white rounded-sm focus:outline-none focus:ring-1 focus:ring-gray-300 transition-colors duration-300 text-sm"
                >
                  주소 검색
                </button>
              </div>
              <input
                type="text"
                v-model="address"
                readonly
                placeholder="주소"
                class="w-full mt-2 px-4 py-3 bg-gray-50 border rounded-sm text-gray-800 text-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-sky-100 focus:border-sky-500 transition-colors duration-300"
              />
              <input
                type="text"
                v-model="detailAddress"
                @input="checkForm"
                placeholder="상세주소를 입력해주세요"
                class="w-full mt-2 px-4 py-3 bg-gray-50 border rounded-sm text-gray-800 text-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-sky-100 focus:border-sky-500 transition-colors duration-300"
              />
            </div>

            <!-- Submit Button -->
            <button
              type="submit"
              @click="signUp"
              class="w-full py-3 px-4 rounded-sm font-bold text-white transition-colors duration-300 bg-gradient-to-r from-sky-500 to-blue-500 hover:from-sky-600 hover:to-blue-600 focus:outline-none focus:ring-2 focus:ring-sky-300 mt-8 text-sm"
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
import { ref, watch, onMounted } from "vue";
import { useModal } from "@/composables/useModal";

const { showModalMessage } = useModal();

// 라우터 및 기본 상태 설정
const router = useRouter();
const emailExists = ref(false);
const emailChecked = ref(false);

// 입력 필드 상태
const userName = ref("");
const userEmail = ref("");
const password = ref("");
const confirmPassword = ref("");
const postcode = ref("");
const address = ref("");
const detailAddress = ref("");

// 유효성 검사 상태
const validName = ref(false);
const validEmail = ref(false);
const validPassword = ref(false);
const validConfirmPassword = ref(false);
const validAddress = ref(false);

// 닉네임 및 이메일 정규식
const nicknameRegex = /^[a-zA-Z0-9가-힣_-]{2,20}$/;
const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.(com|net|org|kr|co)$/;

// Daum 우편번호 스크립트 로드
const loadDaumPostcode = () => {
  const script = document.createElement("script");
  script.src = "//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js";
  document.head.appendChild(script);
};

onMounted(() => {
  loadDaumPostcode();
});

// 주소 검색 팝업 열기
const openAddressSearch = () => {
  new window.daum.Postcode({
    oncomplete: (data) => {
      postcode.value = data.zonecode;
      address.value = data.address;
      detailAddress.value = "";
      checkForm();
    },
  }).open();
};

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
    await axios.get(
      `http://localhost:8080/uhpooh/api/user/check/email/${email}`
    );
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
watch(
  [userName, password, confirmPassword, detailAddress],
  () => {
    checkForm();
  },
  { deep: true }
);

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
  validPassword.value =
    password.value.length >= 8 && hasLetter && hasNumber && hasSpecialChar;

  // 비밀번호 확인 검사
  validConfirmPassword.value = password.value === confirmPassword.value;

  // 주소 검사
  validAddress.value = postcode.value && address.value && detailAddress.value;
};

// 회원가입 처리
const signUp = async (event) => {
  event.preventDefault();

  // 모든 필드의 유효성 검사
  if (
    !userName.value ||
    !userEmail.value ||
    !password.value ||
    !confirmPassword.value ||
    !postcode.value ||
    !address.value ||
    !detailAddress.value ||
    !validName.value ||
    !validEmail.value ||
    !validPassword.value ||
    !validConfirmPassword.value ||
    !validAddress.value ||
    emailExists.value
  ) {
    showModalMessage("입력하신 정보를 다시 확인해주세요.");
    return;
  }

  const fullAddress = `(${postcode.value}) ${address.value} ${detailAddress.value}`;

  // 회원가입 요청
  try {
    const response = await axios.post(
      "http://localhost:8080/uhpooh/api/user/signup",
      {
        userName: userName.value,
        userEmail: userEmail.value,
        password: password.value,
        userAddress: fullAddress,
      }
    );

    showModalMessage("회원 가입이 완료되었습니다.");
    router.push("/login");
  } catch (error) {
    console.error(error);
    showModalMessage("회원가입 처리 중 오류가 발생했습니다.");
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
