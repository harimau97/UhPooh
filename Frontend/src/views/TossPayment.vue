<template>
  <div>
    <button @click="handlePayment">결제하기</button>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()

// 토스페이먼츠 객체 초기화
const tossPayments = window.TossPayments("test_ck_PBal2vxj81LajZzexNOwr5RQgOAN") // 여기에 클라이언트 키를 넣으세요

// 결제 완료 후 서버에 예약 정보 저장
const saveReservation = async (paymentData) => {
  try {
    const response = await axios.post('http://localhost:8080/uhpooh/api/reservations', {
      paymentKey: paymentData.paymentKey,
      orderId: paymentData.orderId,
      amount: paymentData.amount,
      placeId: localStorage.getItem('currentPlaceId'),
      date: localStorage.getItem('selectedDate'),
      time: localStorage.getItem('selectedTime'),
      userId: localStorage.getItem('userId')
    }, {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    return response.data
  } catch (error) {
    console.error('예약 저장 실패:', error)
    throw new Error('예약 정보 저장 중 오류가 발생했습니다.')
  }
}

// 결제 검증
const verifyPayment = async (paymentKey, orderId, amount) => {
  try {
    const response = await axios.post('http://localhost:8080/uhpooh/api/payments/verify', {
      paymentKey,
      orderId,
      amount
    }, {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    
    if (response.data.status === 'DONE') {
      return response.data
    } else {
      throw new Error('결제가 정상적으로 완료되지 않았습니다.')
    }
  } catch (error) {
    console.error('결제 검증 실패:', error)
    throw error
  }
}

// 결제 후 정리
const cleanupPaymentData = () => {
  localStorage.removeItem('currentPlaceId')
  localStorage.removeItem('selectedDate')
  localStorage.removeItem('selectedTime')
}

const handlePayment = async () => {
  try {
    // 주문 ID 생성 (예: 현재 timestamp)
    const orderId = `order_${Date.now()}`

    // 결제 요청
    await tossPayments.requestPayment('카드', {
      amount: 50000,                                  // 결제 금액
      orderId: orderId,                              // 주문 ID
      orderName: '테스트 상품',                      // 주문명
      customerName: '홍길동',                        // 구매자 이름
      successUrl: 'http://localhost:8080/uhpooh/api/payments/toss/success',   // 결제 성공 시 리다이렉트될 URL
      failUrl: 'http://localhost:8080/uhpooh/api/payments/toss/fail',         // 결제 실패 시 리다이렉트될 URL,
    })

  } catch (error) {
    console.error('결제 요청 실패:', error)
  }
}

export { saveReservation, verifyPayment, cleanupPaymentData }
</script>