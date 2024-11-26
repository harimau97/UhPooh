<template>
  <Transition name="modal">
    <div v-if="isOpen" class="fixed inset-0 z-50 flex items-center justify-center">
      <!-- Backdrop -->
      <div class="absolute inset-0 bg-black bg-opacity-50" @click="close"></div>
      
      <!-- Modal -->
      <div class="relative bg-white rounded-lg shadow-xl max-w-md w-full mx-4 z-10">
        <div class="p-6">
          <!-- Header -->
          <div class="flex justify-between items-center mb-4">
            <h3 class="text-xl font-semibold text-gray-900">고객센터 안내</h3>
            <button
              @click="close"
              class="text-gray-400 hover:text-gray-500 focus:outline-none"
            >
              <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
          
          <!-- Content -->
          <div class="space-y-4">
            <p class="text-gray-600">
              어푸어푸 고객센터로 연결해드리겠습니다.
            </p>
            <div class="flex items-center gap-2 text-gray-700">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
              </svg>
              <span class="font-medium">1234-5678</span>
            </div>
            <div class="text-sm text-gray-500">
              <p>평일 09:00 - 18:00</p>
              <p>주말 및 공휴일 휴무</p>
            </div>
          </div>
          
          <!-- Actions -->
          <div class="mt-6 flex justify-end gap-3">
            <button
              @click="close"
              class="px-4 py-2 text-gray-500 hover:text-gray-700 font-medium focus:outline-none"
            >
              닫기
            </button>
            <a
              href="tel:1234-5678"
              class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 font-medium focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
            >
              전화 걸기
            </a>
          </div>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script setup>
const props = defineProps({
  isOpen: {
    type: Boolean,
    required: true
  }
});

const emit = defineEmits(['close']);

const close = () => {
  emit('close');
};
</script>

<style scoped>
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
</style>
