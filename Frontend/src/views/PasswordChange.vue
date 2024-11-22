<script setup>
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import { Lock, ArrowLeft } from "lucide-vue-next";
import axios from "axios";

const router = useRouter();
const currentStep = ref('verify'); // 'verify' or 'change'
const isLoading = ref(false);
const showModal = ref(false); // 모달 표시 상태 추가

// 비밀번호 입력 상태
const currentPassword = ref("");
const newPassword = ref("");
const confirmPassword = ref("");

// 에러 상태
const errors = ref({
 currentPassword: "",
 newPassword: "",
 confirmPassword: "",
});

// 새 비밀번호 유효성 검사
const isValidNewPassword = computed(() => {
 const hasLetter = /[a-zA-Z]/.test(newPassword.value);
 const hasNumber = /[0-9]/.test(newPassword.value);
 const hasSpecialChar = /[!@#$%^&*]/.test(newPassword.value);
 return newPassword.value.length >= 8 && hasLetter && hasNumber && hasSpecialChar;
});

// 비밀번호 확인 유효성 검사
const isValidConfirmPassword = computed(() => {
 return newPassword.value === confirmPassword.value;
});

const verifyCurrentPassword = async () => {
 if (!currentPassword.value) {
   errors.value.currentPassword = "현재 비밀번호를 입력해주세요";
   return;
 }

 isLoading.value = true;

 const requestData = {
   userId: parseInt(localStorage.getItem("userId")), // userId를 숫자로 변환
   password: currentPassword.value
 };

 console.log('Request data:', requestData); // 요청 데이터 확인

 try {
   const response = await axios.post("http://localhost:8080/uhpooh/api/user/verify-password", requestData);

   if (response.data.success) {
     currentStep.value = 'change';
     errors.value.currentPassword = "";
   } else {
     errors.value.currentPassword = "비밀번호가 일치하지 않습니다";
   }
 } catch (err) {
   console.error("비밀번호 확인 실패:", err);
   errors.value.currentPassword = "비밀번호가 일치하지 않습니다";
 } finally {
   isLoading.value = false;
 }
};

const handlePasswordChange = async () => {
 // 유효성 검사
 if (!isValidNewPassword.value) {
   errors.value.newPassword = "비밀번호는 영문, 숫자, 특수문자를 포함하여 8자 이상이어야 합니다";
   return;
 }

 if (!isValidConfirmPassword.value) {
   errors.value.confirmPassword = "비밀번호가 일치하지 않습니다";
   return;
 }

 isLoading.value = true;
 const userId = localStorage.getItem("userId");

 // 요청 데이터 객체 생성
 const requestData = {
   currentPassword: currentPassword.value,
   newPassword: newPassword.value,
   confirmPassword: confirmPassword.value
 };

 console.log('Password change request data:', requestData);

 try {
   const response = await axios.patch(  
     `http://localhost:8080/uhpooh/api/user/password/${userId}?requestUserId=${userId}`,
     requestData,
     {
       headers: {
         'Content-Type': 'application/json'
       }
     }
   );

   console.log('Password change response:', response.data);
   showModal.value = true; // 성공 시 모달 표시
 } catch (error) {
   console.error("비밀번호 변경 실패:", error);
   console.error("Error response:", error.response?.data);
   alert("비밀번호 변경에 실패했습니다. 다시 시도해주세요.");
 } finally {
   isLoading.value = false;
 }
};

const closeModal = () => {
 showModal.value = false;
 router.push("/edit");
};

const goBack = () => {
 router.push("/edit");
};
</script>

<template>
    <div class="p-4 mx-auto max-w-md space-y-6">
      <div class="flex items-center justify-between mb-6">
        <button
          @click="goBack"
          class="text-gray-600 hover:text-gray-900 flex items-center"
        >
          <ArrowLeft class="w-5 h-5 mr-2" />
          돌아가기
        </button>
        <h1 class="text-2xl font-bold text-center text-gray-900">비밀번호 변경</h1>
        <div class="w-20"></div>
      </div>
   
      <div class="bg-white rounded-2xl shadow-sm p-6">
        <!-- 현재 비밀번호 확인 단계 -->
        <form v-if="currentStep === 'verify'" @submit.prevent="verifyCurrentPassword" class="space-y-6">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">
              현재 비밀번호를 입력해주세요
            </label>
            <div class="relative">
              <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <Lock class="h-5 w-5 text-gray-400" />
              </div>
              <input
                v-model="currentPassword"
                type="password"
                class="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                :class="{ 'border-red-300': errors.currentPassword }"
              />
            </div>
            <p v-if="errors.currentPassword" class="mt-1 text-sm text-red-600">
              {{ errors.currentPassword }}
            </p>
          </div>
   
          <button
            type="submit"
            class="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            :disabled="isLoading"
          >
            {{ isLoading ? "확인 중..." : "다음" }}
          </button>
        </form>
   
        <!-- 새 비밀번호 입력 단계 -->
        <form v-else @submit.prevent="handlePasswordChange" class="space-y-6">
          <!-- 새 비밀번호 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">
              새 비밀번호
            </label>
            <div class="relative">
              <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <Lock class="h-5 w-5 text-gray-400" />
              </div>
              <input
                v-model="newPassword"
                type="password"
                class="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                :class="{ 'border-red-300': errors.newPassword }"
              />
            </div>
            <p v-if="errors.newPassword" class="mt-1 text-sm text-red-600">
              {{ errors.newPassword }}
            </p>
            <p class="mt-1 text-sm text-gray-500">
              비밀번호는 영문, 숫자, 특수문자(!@#$%^&*)를 포함하여 8자 이상이어야 합니다.
            </p>
          </div>
   
          <!-- 새 비밀번호 확인 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">
              새 비밀번호 확인
            </label>
            <div class="relative">
              <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <Lock class="h-5 w-5 text-gray-400" />
              </div>
              <input
                v-model="confirmPassword"
                type="password"
                class="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                :class="{ 'border-red-300': errors.confirmPassword }"
              />
            </div>
            <p v-if="errors.confirmPassword" class="mt-1 text-sm text-red-600">
              {{ errors.confirmPassword }}
            </p>
          </div>
   
          <button
            type="submit"
            class="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            :disabled="isLoading"
          >
            {{ isLoading ? "변경 중..." : "비밀번호 변경" }}
          </button>
        </form>
      </div>
   
      <!-- 성공 모달 -->
      <div v-if="showModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
        <div class="bg-white rounded-lg p-6 max-w-sm w-full mx-4">
          <h3 class="text-lg font-medium text-gray-900 mb-4">알림</h3>
          <p class="text-gray-600 mb-6">비밀번호가 성공적으로 변경되었습니다.</p>
          <div class="flex justify-end">
            <button
              @click="closeModal"
              class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
            >
              확인
            </button>
          </div>
        </div>
      </div>
    </div>
   </template>