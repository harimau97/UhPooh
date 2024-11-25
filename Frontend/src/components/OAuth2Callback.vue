<template>
    <div class="flex justify-center items-center min-h-screen">
      <div class="text-center">
        <h2 class="text-xl font-bold mb-4">로그인 처리 중...</h2>
        <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-gray-900 mx-auto"></div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { onMounted } from 'vue';
  import { useRouter } from 'vue-router';
  
  const router = useRouter();
  
  onMounted(() => {
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
      
      // 홈페이지로 리다이렉트
      location.replace('/');
    //   router.push('/');
    //   location.reload(); // 필요한 경우 페이지 새로고침
    } else {
      // 오류 발생 시 로그인 페이지로 리다이렉트
      router.push('/login?error=true');
    }
  });
  </script>