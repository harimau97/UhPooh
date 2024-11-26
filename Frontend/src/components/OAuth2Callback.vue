<template>
  <div class="flex justify-center items-center min-h-screen">
    <div class="text-center">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-gray-900 mx-auto"></div>
      <p class="mt-4 text-gray-600">로그인 처리 중...</p>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { isAuthenticated, userAuthenticated } from '../composables/userAuth';

const router = useRouter();

onMounted(async () => {
  const params = new URLSearchParams(window.location.search);
  const token = params.get('token');
  const userId = params.get('userId');
  const email = params.get('email');
  const name = params.get('name');

  if (token && userId) {
    // 로컬 스토리지에 사용자 정보 저장
    localStorage.setItem('userToken', token);
    localStorage.setItem('userId', userId);
    localStorage.setItem('userEmail', email);
    localStorage.setItem('userName', name);

    // 인증 상태 업데이트
    await isAuthenticated();

    // 메인 페이지로 리다이렉트
    router.push('/');
  } else {
    // 토큰이나 userId가 없는 경우 로그인 페이지로 리다이렉트
    console.error('No token or userId found in URL parameters');
    router.push('/login');
  }
});
</script>
