<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';

const route = useRoute();
const router = useRouter();
const loading = ref(true);
const orderId = ref('');
const amount = ref(0);

onMounted(async () => {
  try {
    // URL 쿼리 파라미터에서 결제 정보 추출
    const { paymentKey, orderId: orderIdParam, amount: amountParam } = route.query;
    orderId.value = orderIdParam;
    amount.value = amountParam;
    
    try {
      // 결제 승인 요청
      const response = await axios.post('http://localhost:8080/uhpooh/api/payments/confirm', null, {
        params: {
          paymentKey,
          orderId: orderIdParam,
          amount: Number(amountParam)
        }
      });
      console.log('결제가 성공적으로 완료되었습니다:', response.data);
    } catch (error) {
      console.warn('백엔드 결제 승인 요청 실패:', error);
    }
    
    // 3초 후 로딩 상태 해제
    setTimeout(() => {
      loading.value = false;
    }, 2000);

  } catch (error) {
    console.error('결제 승인 중 오류 발생:', error);
    alert('결제 승인 중 오류가 발생했습니다.');
    router.push('/');
  }
});
</script>

<template>
  <div class="min-h-screen bg-gray-100 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8 bg-white p-10 rounded-xl shadow-lg">
      <!-- 로딩 상태 -->
      <div v-if="loading" class="text-center">
        <div class="animate-spin rounded-full h-16 w-16 border-b-2 border-blue-500 mx-auto"></div>
        <h2 class="mt-6 text-xl font-semibold text-gray-900">결제 확인 중...</h2>
        <p class="mt-2 text-sm text-gray-600">잠시만 기다려주세요.</p>
      </div>

      <!-- 결제 완료 상태 -->
      <div v-else class="text-center">
        <div class="mx-auto flex items-center justify-center h-16 w-16 rounded-full bg-green-100">
          <svg class="h-8 w-8 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
          </svg>
        </div>
        <h2 class="mt-6 text-2xl font-bold text-gray-900">결제가 완료되었습니다!</h2>
        <div class="mt-4 space-y-2">
          <p class="text-gray-600">주문번호: {{ orderId }}</p>
          <p class="text-gray-600">결제금액: {{ Number(amount).toLocaleString() }}원</p>
        </div>
        <div class="mt-8 space-y-4">
          <router-link to="/mypage" class="inline-flex items-center px-6 py-3 border border-transparent text-base font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-colors duration-200">
            마이페이지로 이동
          </router-link>
          <div>
            <router-link to="/" class="inline-block mt-4 text-sm font-medium text-blue-600 hover:text-blue-500">
              홈으로 돌아가기
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>