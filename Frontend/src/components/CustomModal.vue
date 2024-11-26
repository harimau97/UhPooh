<template>
  <Transition name="modal">
    <div v-if="show" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3 class="modal-title">UhPooh</h3>
        </div>
        <div class="modal-body">
          <p>{{ message }}</p>
        </div>
        <div class="modal-footer">
          <button class="modal-button" @click="closeModal">확인</button>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script setup>
const props = defineProps({
  show: {
    type: Boolean,
    required: true,
  },
  message: {
    type: String,
    required: true,
  },
});

const emit = defineEmits(['close']);

const closeModal = () => {
  emit('close');
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 20px;
  border-radius: 12px;
  width: 90%;
  max-width: 400px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.modal-header {
  margin-bottom: 15px;
  text-align: center;
}

.modal-title {
  margin: 0;
  color: #2563eb;
  font-size: 1.5rem;
  font-weight: 600;
}

.modal-body {
  margin-bottom: 20px;
  text-align: center;
  color: #374151;
}

.modal-footer {
  display: flex;
  justify-content: center;
}

.modal-button {
  background-color: #2563eb;
  color: white;
  border: none;
  padding: 8px 24px;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: background-color 0.2s;
}

.modal-button:hover {
  background-color: #1d4ed8;
}

/* 트랜지션 애니메이션 */
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
</style>
