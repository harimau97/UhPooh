<script setup>
import { useRouter } from 'vue-router';
import { Home } from 'lucide-vue-next';

const router = useRouter();

const goHome = () => {
  router.push('/');
};
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-100">
    <div class="text-center px-4 py-8">
      <!-- 404 이미지/텍스트 -->
      <div class="mb-8">
        <h1 class="text-9xl font-bold text-blue-600">404</h1>
        <div class="mt-4 text-gray-600 text-xl">
          페이지를 찾을 수 없습니다
        </div>
      </div>

      <!-- 설명 텍스트 -->
      <p class="mb-8 text-gray-500">
        요청하신 페이지가 삭제되었거나 잘못된 경로입니다.<br />
        다시 한 번 확인해주세요.
      </p>

      <!-- 홈으로 돌아가기 버튼 -->
      <button
        @click="goHome"
        class="inline-flex items-center px-6 py-3 text-base font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-colors duration-200"
      >
        <Home class="w-5 h-5 mr-2" />
        홈으로 돌아가기
      </button>

      <!-- 추가 링크들 -->
      <div class="mt-8 space-x-4 text-sm">
        <a
          href="/around"
          class="text-blue-600 hover:text-blue-800 hover:underline"
        >
          주변 수영장 찾기
        </a>
        <span class="text-gray-300">|</span>
        <a
          href="/placeBoard"
          class="text-blue-600 hover:text-blue-800 hover:underline"
        >
          수영장 목록
        </a>
        <span class="text-gray-300">|</span>
        <a
          href="/mypage"
          class="text-blue-600 hover:text-blue-800 hover:underline"
        >
          마이페이지
        </a>
      </div>

      <!-- 문의 안내 -->
      <div class="mt-12 text-sm text-gray-500">
        문제가 지속되면 관리자에게 문의해주세요.<br />
        <span class="font-medium">support@uhpooh.com</span>
      </div>
    </div>
  </div>
</template>