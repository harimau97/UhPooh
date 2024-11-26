<script setup>
import { ref, watch, onMounted } from "vue";
import { useRouter } from "vue-router";
import {
  User,
  Mail,
  MapPin,
  Save,
  ArrowLeft,
  Lock,
  Upload,
  Trash2,
} from "lucide-vue-next";
import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/uhpooh/api",
  timeout: 5000,
});

const router = useRouter();
const showSuccessMessage = ref(false);
const isLoading = ref(false);
const userId = ref("");
const fileInput = ref(null);
const errorMessage = ref("");
const showEmailCheckModal = ref(false);
const emailCheckMessage = ref("");
const emailCheckSuccess = ref(false);

const userForm = ref({
  name: localStorage.getItem("userName") || "",
  email: localStorage.getItem("userEmail") || "",
  profileImageUrl: localStorage.getItem("userProfileImage") || "",
  address: "",
  postcode: "",
  detailAddress: "",
});
//이미지 표시를 위한 이미지 실제 파일명
const imgName = ref("");
const imgPath = ref("");
const imageTrue = ref(null);

// 유효성 검사 상태
const validName = ref(true);
const validEmail = ref(true);
const emailExists = ref(false);
const emailChecked = ref(true);
const originalEmail = ref("");

//주소파싱
const fullAddress = ref("");
fullAddress.value = localStorage.getItem("userAddress");
const postcodeMatch = ref("");
const postcode = ref(""); // "06220"
postcodeMatch.value = fullAddress.value.match(/\((\d{5})\)/);
postcode.value = postcodeMatch.value ? postcodeMatch.value[1] : ""; // "06220"
const address = ref("");
address.value = fullAddress.value.replace(/\(\d{5}\)\s*/, "").trim(); // "서울 강남구 테헤란로 212 역삼동 멀티캠"

// 닉네임 및 이메일 정규식
const nicknameRegex = /^[a-zA-Z0-9가-힣_-]{2,20}$/;
const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.(com|net|org|kr|co)$/;

// 소셜 로그인 사용자 여부
const isLocalProvider = ref(localStorage.getItem("provider") === "local");

// 소셜 로그인 알림 모달 상태
const showSocialLoginModal = ref(false);

// 초기 데이터 로드
const loadUserData = async () => {
  try {
    isLoading.value = true;

    const currentUserId = localStorage.getItem("userId");
    if (!currentUserId) {
      router.push("/login");
      return;
    }

    const response = await api.get(
      `/user/${userId.value}?requestUserId=${userId.value}`
    );

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const data = await response.data;

    if (data) {
      const userData = data;
      const storedAddress = localStorage.getItem("userAddress");

      userForm.value = {
        name: localStorage.getItem("userName") || userData.userName || "",
        email: localStorage.getItem("userEmail") || userData.userEmail || "",
        profileImageUrl:
          localStorage.getItem("userProfileImage") || userData.pImage || "",
        address: "",
        postcode: "",
        detailAddress: "",
      };

      originalEmail.value = userForm.value.email;

      // 주소 처리
      if (storedAddress) {
        try {
          parseAddress(storedAddress);
        } catch (error) {
          userForm.value.address = storedAddress;
        }
      } else if (userData.userAddress) {
        try {
          parseAddress(userData.userAddress);
        } catch (error) {
          userForm.value.address = userData.userAddress;
        }
      }
    }
  } catch (error) {
    console.error("사용자 데이터 로딩 실패:", error);
    userForm.value = {
      name: localStorage.getItem("userName") || "",
      email: localStorage.getItem("userEmail") || "",
      profileImageUrl: localStorage.getItem("userProfileImage") || "",
      address: "",
      postcode: "",
      detailAddress: "",
    };

    const storedAddress = localStorage.getItem("userAddress");
    if (storedAddress) {
      try {
        parseAddress(storedAddress);
      } catch (error) {
        userForm.value.address = storedAddress;
      }
    }

    originalEmail.value = userForm.value.email;
  } finally {
    isLoading.value = false;
  }
};

// 주소 파싱
const parseAddress = (fullAddress) => {
  const postcodeMatch = fullAddress.value.match(/\((\d{5})\)/);
  if (postcodeMatch) {
    userForm.value.postcode = postcodeMatch[1];
    const remainingAddress = fullAddress.value
      .replace(/\(\d{5}\)\s*/, "")
      .trim();
    const lastSpaceIndex = remainingAddress.lastIndexOf(" ");
    if (lastSpaceIndex !== -1) {
      userForm.value.address = remainingAddress.substring(0, lastSpaceIndex);
      userForm.value.detailAddress = remainingAddress.substring(
        lastSpaceIndex + 1
      );
    } else {
      userForm.value.address = remainingAddress;
    }
  } else {
    userForm.value.address = fullAddress;
  }
};

// 이메일 중복 확인 함수 수정
const checkEmailDuplicate = async () => {
  if (!isLocalProvider.value) return;

  if (!validEmail.value) {
    alert("올바른 이메일 형식이 아닙니다.");
    return;
  }

  if (userForm.value.email === originalEmail.value) {
    emailExists.value = false;
    emailChecked.value = true;
    emailCheckMessage.value = "현재 사용중인 이메일입니다.";
    emailCheckSuccess.value = true;
    showEmailCheckModal.value = true;
    return;
  }

  try {
    isLoading.value = true;
    const response = await api.get(
      `/user/check/email/${encodeURIComponent(userForm.value.email)}`
    );

    emailExists.value = false;
    emailChecked.value = true;
    emailCheckMessage.value = "사용 가능한 이메일입니다.";
    emailCheckSuccess.value = true;
    showEmailCheckModal.value = true;
  } catch (error) {
    emailExists.value = true;
    emailChecked.value = true;
    emailCheckMessage.value = "이미 사용중인 이메일입니다.";
    emailCheckSuccess.value = false;
    showEmailCheckModal.value = true;
  } finally {
    isLoading.value = false;
  }
};

// 이메일 체크 모달 닫기
const closeEmailCheckModal = () => {
  showEmailCheckModal.value = false;
};

// 이미지 업로드 처리
const handleImageUpload = async (event) => {
  const file = event.target.files[0];
  if (!file) return;

  // 파일 크기 체크 (5MB)
  if (file.size > 5 * 1024 * 1024) {
    alert("파일 크기는 5MB를 초과할 수 없습니다.");
    return;
  }

  // 파일 형식 체크
  const allowedTypes = ["image/jpeg", "image/png", "image/gif"];
  if (!allowedTypes.includes(file.type)) {
    alert("JPG, PNG, GIF 형식의 이미지만 업로드 가능합니다.");
    return;
  }

  const formData = new FormData();
  formData.append("file", file);

  try {
    isLoading.value = true;
    const response = await api.post(`/file/profile/${userId.value}`, formData);

    if (response.data && response.data.data) {
      userForm.value.profileImageUrl = response.data.data.imageUrl;
      imgName.value = response.data.data.filename;
      localStorage.setItem("userProfileImage", response.data.data.imageUrl);
      localStorage.setItem("pImage", response.data.data.imageUrl);
      location.reload();
    }
  } catch (error) {
    console.error("프로필 이미지 업로드 실패:", error);
  } finally {
    isLoading.value = false;
  }
};

// 이미지 삭제 처리
const handleImageDelete = async () => {
  try {
    isLoading.value = true;
    await api.delete(`/file/profile/user/${userId.value}`);

    userForm.value.profileImageUrl = "";
    localStorage.removeItem("userProfileImage");
    localStorage.removeItem("pImage");
    location.reload();
  } catch (error) {
    console.error("프로필 이미지 삭제 실패:", error);
  } finally {
    isLoading.value = false;
  }
};

// 이미지 캐싱 처리
const cacheImage = async (cat) => {
  imgPath.value =
    "http://localhost:8080/uhpooh/api/file/images/" + cat + "/" + imgName.value;
  const response = await api.get(imgPath.value);
};

// Daum 우편번호 스크립트 로드
onMounted(async () => {
  userId.value = localStorage.getItem("userId");
  imageTrue.value = null;
  imageTrue.value = localStorage.getItem("pImage");
  console.log(imageTrue.value);
  imgName.value = imageTrue.value.replace("/images/profiles/", "");
  console.log(imgName.value);
  if (imgName.value !== null) {
    await cacheImage("profiles");
  }
  const script = document.createElement("script");
  script.src = "//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js";
  document.head.appendChild(script);
  loadUserData();
});

// 주소 검색 팝업 열기
const openAddressSearch = () => {
  new window.daum.Postcode({
    oncomplete: (data) => {
      userForm.value.postcode = data.zonecode;
      userForm.value.address = data.address;
      userForm.value.detailAddress = "";
    },
  }).open();
};

// 이메일 유효성 검사
const checkEmailValidity = () => {
  validEmail.value = emailRegex.test(userForm.value.email);
  if (userForm.value.email !== originalEmail.value) {
    emailChecked.value = false;
  }
};

// 닉네임 유효성 검사
const checkNameValidity = () => {
  const hasIncompleteKorean = /[ㄱ-ㅎㅏ-ㅣ]/.test(userForm.value.name);
  validName.value =
    nicknameRegex.test(userForm.value.name) && !hasIncompleteKorean;
};

// 입력값 변경 감지
watch(() => userForm.value.name, checkNameValidity);
watch(() => userForm.value.email, checkEmailValidity);

// handleSubmit 함수 수정
const handleSubmit = async (e) => {
  e.preventDefault();

  if (!validName.value || !validEmail.value) {
    alert("입력한 정보를 다시 확인해주세요.");
    return;
  }

  if (userForm.value.email !== originalEmail.value && !emailChecked.value) {
    alert("이메일 중복 확인이 필요합니다.");
    return;
  }

  if (emailExists.value) {
    alert("이미 사용중인 이메일입니다.");
    return;
  }

  isLoading.value = true;

  const fullAddress =
    userForm.value.postcode && userForm.value.address
      ? `(${userForm.value.postcode}) ${userForm.value.address} ${userForm.value.detailAddress}`
      : userForm.value.address;

  try {
    const response = await axios({
      method: "PUT",
      url: `http://localhost:8080/uhpooh/api/user/${userId.value}?requestUserId=${userId.value}`,
      data: {
        userEmail: userForm.value.email,
        userName: userForm.value.name,
        userAddress: fullAddress,
        pImage: imageTrue.value || null,
      },
      headers: {
        "Content-Type": "application/json",
        Accept: "application/json",
      },
      withCredentials: true,
    });

    if (response.data) {
      localStorage.setItem("userName", userForm.value.name);
      localStorage.setItem("userEmail", userForm.value.email);
      localStorage.setItem("userAddress", fullAddress);
      if (imageTrue.value) {
        localStorage.setItem("pImage", imageTrue.value);
      }

      showSuccessMessage.value = true;
      console.log("정보 수정 성공:", response.data);
    }
  } catch (error) {
    console.error("정보 수정 실패:", error);
    alert("정보 수정에 실패했습니다. 다시 시도해주세요.");
  } finally {
    isLoading.value = false;
  }
};

// 성공 모달 확인 버튼 핸들러
const handleSuccessConfirm = () => {
  showSuccessMessage.value = false;
  router.push("/mypage");
};

const goBack = () => {
  router.push("/mypage");
};
</script>

<template>
  <div class="p-4 mx-auto space-y-6 max-w-3xl">
    <!-- 에러 메시지 -->
    <div
      v-if="errorMessage"
      class="relative px-4 py-3 mb-4 text-red-700 bg-red-100 rounded border border-red-400"
    >
      {{ errorMessage }}
    </div>

    <!-- 로딩 인디케이터 -->
    <div
      v-if="isLoading"
      class="flex fixed inset-0 z-50 justify-center items-center bg-gray-500 bg-opacity-75"
    >
      <div
        class="w-32 h-32 rounded-full border-b-2 border-white animate-spin"
      ></div>
    </div>

    <!-- 소셜 로그인 알림 모달 -->
    <div
      v-if="showSocialLoginModal"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50"
    >
      <div class="p-6 bg-white rounded-lg shadow-xl">
        <h3 class="mb-4 text-lg font-semibold">알림</h3>
        <p class="mb-6">소셜 로그인 사용자는 비밀번호를 변경할 수 없습니다.</p>
        <div class="flex justify-end">
          <button
            @click="showSocialLoginModal = false"
            class="px-4 py-2 text-white bg-blue-600 rounded hover:bg-blue-700"
          >
            확인
          </button>
        </div>
      </div>
    </div>

    <!-- 헤더 -->
    <div class="flex justify-between items-center">
      <button
        @click="goBack"
        class="flex items-center text-gray-600 hover:text-gray-900"
      >
        <ArrowLeft class="mr-2 w-5 h-5" />
        돌아가기
      </button>
      <h1 class="text-2xl font-bold text-center text-gray-900">내 정보 수정</h1>
      <div class="w-20"></div>
    </div>

    <!-- 메인 폼 -->
    <div class="p-6 bg-white rounded-2xl shadow-sm">
      <form @submit="handleSubmit" class="space-y-6">
        <!-- 프로필 이미지 섹션 -->
        <div class="space-y-4">
          <h2 class="mb-4 text-lg font-semibold text-gray-900">
            프로필 이미지
          </h2>
          <div class="flex flex-col items-center space-y-4">
            <div class="relative w-32 h-32">
              <img
                v-if="imageTrue !== 'null' && imageTrue !== ''"
                :src="imgPath"
                alt="프로필 이미지"
                class="object-cover w-full h-full rounded-full"
              />
              <div
                v-else
                class="flex justify-center items-center w-full h-full bg-gray-200 rounded-full"
              >
                <User class="w-16 h-16 text-gray-400" />
              </div>
            </div>
            <div class="flex space-x-2">
              <input
                ref="fileInput"
                type="file"
                accept="image/*"
                class="hidden"
                @change="handleImageUpload"
              />
              <button
                type="button"
                @click="() => fileInput.click()"
                class="flex items-center px-4 py-2 text-white bg-blue-600 rounded-md hover:bg-blue-700"
              >
                <Upload class="mr-2 w-4 h-4" />
                이미지 업로드
              </button>
              <button
                v-if="imageTrue !== 'null' && imageTrue !== ''"
                type="button"
                @click="handleImageDelete"
                class="flex items-center px-4 py-2 text-white bg-red-600 rounded-md hover:bg-red-700"
              >
                <Trash2 class="mr-2 w-4 h-4" />
                이미지 삭제
              </button>
            </div>
          </div>
        </div>

        <!-- 기본 정보 섹션 -->
        <div class="pt-6 space-y-4 border-t">
          <h2 class="mb-4 text-lg font-semibold text-gray-900">기본 정보</h2>

          <!-- 닉네임 입력 -->
          <div>
            <label class="block mb-1 text-sm font-medium text-gray-700">
              닉네임
            </label>
            <div class="relative">
              <div
                class="flex absolute inset-y-0 left-0 items-center pl-3 pointer-events-none"
              >
                <User class="w-5 h-5 text-gray-400" />
              </div>
              <input
                v-model="userForm.name"
                type="text"
                class="block py-2 pr-3 pl-10 w-full rounded-md border border-gray-300 shadow-sm focus:ring-blue-500 focus:border-blue-500"
                :class="{ 'border-red-500': !validName && userForm.name }"
              />
            </div>
            <p
              v-if="userForm.name && !validName"
              class="mt-1 text-sm text-red-600"
            >
              닉네임은 2~20자의 한글, 영문, 숫자, 특수문자(_,-)만 사용
              가능합니다.
            </p>
          </div>

          <!-- 이메일 입력 -->
          <div>
            <label class="block mb-1 text-sm font-medium text-gray-700">
              이메일
            </label>
            <div class="flex gap-2 relative">
              <input
                v-model="userForm.email"
                type="email"
                :disabled="!isLocalProvider"
                class="flex-1 px-3 py-2 border rounded-md focus:ring-blue-500 focus:border-blue-500"
                :class="[
                  !validEmail ? 'border-red-300' : 'border-gray-300',
                  !isLocalProvider ? 'bg-gray-100' : '',
                  userForm.email === originalEmail.value ? 'pr-10' : '',
                ]"
              />
              <div
                v-if="userForm.email === originalEmail.value"
                class="absolute right-[4.5rem] top-1/2 transform -translate-y-1/2 flex items-center"
              >
                <svg
                  class="w-5 h-5 text-red-500"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M6 18L18 6M6 6l12 12"
                  />
                </svg>
              </div>
              <button
                v-if="isLocalProvider"
                type="button"
                @click="checkEmailDuplicate"
                class="px-4 py-2 text-white bg-blue-600 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
              >
                중복확인
              </button>
            </div>
            <p v-if="!validEmail" class="mt-1 text-sm text-red-600">
              올바른 이메일 형식이 아닙니다.
            </p>
            <p
              v-if="userForm.email === originalEmail.value"
              class="mt-1 text-sm text-blue-600"
            >
              현재 사용중인 이메일입니다.
            </p>
          </div>

          <!-- 주소 입력 -->
          <div class="space-y-2">
            <label class="block text-sm font-medium text-gray-700">
              주소
            </label>
            <div class="flex space-x-2 w-full">
              <input
                v-model="userForm.postcode"
                type="text"
                readonly
                :placeholder="postcode"
                class="flex-1 px-4 py-2 text-sm placeholder-gray-400 text-gray-800 bg-gray-50 rounded-md border focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              />
              <button
                type="button"
                @click="openAddressSearch"
                class="px-4 py-2 text-sm text-white bg-gray-500 rounded-md transition-colors duration-300 hover:bg-gray-600 focus:outline-none focus:ring-1 focus:ring-gray-300"
              >
                주소 검색
              </button>
            </div>
            <input
              v-model="userForm.address"
              type="text"
              readonly
              :placeholder="address"
              class="px-4 py-2 w-full text-sm placeholder-gray-400 text-gray-800 bg-gray-50 rounded-md border focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            />
            <input
              v-model="userForm.detailAddress"
              type="text"
              placeholder="상세주소를 입력해주세요"
              class="px-4 py-2 w-full text-sm placeholder-gray-400 text-gray-800 bg-white rounded-md border focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            />
          </div>

          <!-- 비밀번호 변경 섹션 -->
          <div class="pt-6 space-y-4 border-t">
            <h2 class="mb-4 text-lg font-semibold text-gray-900">
              비밀번호 변경
            </h2>
            <button
              type="button"
              @click="() => router.push('/change-password')"
              class="flex items-center px-4 py-2 text-sm font-medium text-gray-700 bg-white rounded-md border border-gray-300 shadow-sm hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            >
              <Lock class="mr-2 w-5 h-5" />
              비밀번호 변경하기
            </button>
          </div>

          <!-- 저장 버튼 -->
          <div class="flex justify-end pt-6">
            <button
              type="submit"
              class="flex justify-center items-center px-4 py-2 text-white bg-blue-600 rounded-md border border-transparent shadow-sm hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
              :disabled="isLoading"
            >
              <Save class="mr-2 w-5 h-5" />
              {{ isLoading ? "저장 중..." : "변경사항 저장" }}
            </button>
          </div>
        </div>
      </form>
    </div>

    <!-- 이메일 중복 체크 모달 -->
    <div
      v-if="showEmailCheckModal"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50"
    >
      <div class="p-6 bg-white rounded-lg shadow-xl">
        <div class="flex justify-center items-center mb-4">
          <div
            :class="[
              'rounded-full p-2',
              emailCheckSuccess ? 'bg-green-100' : 'bg-red-100',
            ]"
          >
            <svg
              v-if="emailCheckSuccess"
              class="w-6 h-6 text-green-600"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M5 13l4 4L19 7"
              />
            </svg>
            <svg
              v-else
              class="w-6 h-6 text-red-600"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M6 18L18 6M6 6l12 12"
              />
            </svg>
          </div>
        </div>
        <p
          class="mb-4 text-center"
          :class="[emailCheckSuccess ? 'text-green-600' : 'text-red-600']"
        >
          {{ emailCheckMessage }}
        </p>
        <div class="flex justify-center">
          <button
            @click="closeEmailCheckModal"
            class="px-4 py-2 text-white bg-blue-600 rounded hover:bg-blue-700"
          >
            확인
          </button>
        </div>
      </div>
    </div>

    <!-- 성공 모달 -->
    <div
      v-if="showSuccessMessage"
      class="flex fixed inset-0 z-50 justify-center items-center bg-gray-500 bg-opacity-75"
    >
      <div class="p-6 mx-auto space-y-4 max-w-sm bg-white rounded-lg">
        <div class="flex justify-center items-center mb-4 text-blue-500">
          <!-- 체크마크 아이콘 -->
          <svg
            class="w-12 h-12"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M5 13l4 4L19 7"
            ></path>
          </svg>
        </div>
        <h3 class="text-lg font-medium text-center text-gray-900">
          회원 정보를 성공적으로 변경했습니다.
        </h3>
        <div class="flex justify-center mt-4">
          <button
            @click="handleSuccessConfirm"
            class="inline-flex justify-center px-4 py-2 text-sm font-medium text-white bg-blue-400 rounded-md border border-transparent hover:bg-blue-800 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500"
          >
            확인
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.disabled-button {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
