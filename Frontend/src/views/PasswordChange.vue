<script setup>
import { ref, computed, watch } from "vue";
import { useRouter } from "vue-router";
import { Lock, ArrowLeft } from "lucide-vue-next";
import axios from "axios";

const router = useRouter();
const currentStep = ref('verify');
const isLoading = ref(false);
const showModal = ref(false);

const currentPassword = ref("");
const newPassword = ref("");
const confirmPassword = ref("");

// Password validation states
const validPassword = ref(false);
const validConfirmPassword = ref(false);

const errors = ref({
  currentPassword: "",
  newPassword: "",
  confirmPassword: "",
});

// Watch for password changes
watch([newPassword, confirmPassword], () => {
  if (confirmPassword.value) {
    validConfirmPassword.value = confirmPassword.value === newPassword.value;
  }
  checkPassword();
});

// Password validation check
const checkPassword = () => {
  const hasLetter = /[a-zA-Z]/.test(newPassword.value);
  const hasNumber = /[0-9]/.test(newPassword.value);
  const hasSpecialChar = /[!@#$%^&*]/.test(newPassword.value);
  validPassword.value = newPassword.value.length >= 8 && hasLetter && hasNumber && hasSpecialChar;
};

const verifyCurrentPassword = async () => {
  if (!currentPassword.value) {
    errors.value.currentPassword = "현재 비밀번호를 입력해주세요";
    return;
  }

  isLoading.value = true;

  const requestData = {
    userId: parseInt(localStorage.getItem("userId")),
    password: currentPassword.value
  };

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
  // Validation check
  if (!validPassword.value || !validConfirmPassword.value) {
    if (!validPassword.value) {
      errors.value.newPassword = "비밀번호는 영문, 숫자, 특수문자를 포함하여 8자 이상이어야 합니다";
    }
    if (!validConfirmPassword.value) {
      errors.value.confirmPassword = "비밀번호가 일치하지 않습니다";
    }
    return;
  }

  isLoading.value = true;
  const userId = localStorage.getItem("userId");

  const requestData = {
    currentPassword: currentPassword.value,
    newPassword: newPassword.value,
    confirmPassword: confirmPassword.value
  };

  try {
    const response = await axios.put(
      `http://localhost:8080/uhpooh/api/user/password/${userId}?requestUserId=${userId}`,
      requestData,
      {
        headers: {
          'Content-Type': 'application/json'
        }
      }
    );

    showModal.value = true;
  } catch (error) {
    console.error("비밀번호 변경 실패:", error);
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
      <!-- Current password verification step -->
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

      <!-- New password input step -->
      <form v-else @submit.prevent="handlePasswordChange" class="space-y-6">
        <div class="space-y-2">
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
              class="block w-full pl-10 pr-3 py-2 border rounded-md shadow-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              :class="newPassword && !validPassword ? 'border-red-300' : 'border-gray-300'"
            />
          </div>
          <p v-if="newPassword && !validPassword" class="text-xs text-red-500">
            비밀번호는 영문, 숫자, 특수문자를 포함하여 8자 이상이어야 합니다.
          </p>
        </div>

        <div class="space-y-2">
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
              class="block w-full pl-10 pr-3 py-2 border rounded-md shadow-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              :class="confirmPassword && !validConfirmPassword ? 'border-red-300' : 'border-gray-300'"
            />
          </div>
          <p v-if="confirmPassword && !validConfirmPassword" class="text-xs text-red-500">
            비밀번호가 일치하지 않습니다.
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

    <!-- Success modal -->
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