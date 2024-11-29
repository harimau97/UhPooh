// src/utils/api.js
export const API_BASE_URL = import.meta.env.VITE_API_URL;

// API 엔드포인트들을 객체로 관리
export const API_ENDPOINTS = {
  LOGIN: `${API_BASE_URL}/user/login`,
  SIGNUP: `${API_BASE_URL}/user/signup`,
  PAYMENTS: {
    RESERVATION: `${API_BASE_URL}/reservations`,
    VERIFY: `${API_BASE_URL}/payments/verify`,
    SUCCESS: `${API_BASE_URL}/payments/toss/success`,
    FAIL: `${API_BASE_URL}/payments/toss/fail`,
  },

  // ... 기타 엔드포인트들
};
