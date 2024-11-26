import { ref } from "vue";

const showModal = ref(false);
const modalMessage = ref("");
let onCloseCallback = null;

export function useModal() {
  const showModalMessage = (message, callback = null) => {
    modalMessage.value = message;
    showModal.value = true;
    onCloseCallback = callback;
  };

  const closeModal = () => {
    showModal.value = false;
    if (onCloseCallback) {
      onCloseCallback();
      onCloseCallback = null;
    }
  };

  return {
    showModal,
    modalMessage,
    showModalMessage,
    closeModal,
  };
}
