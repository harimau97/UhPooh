<template>
  <div class="max-w-4xl mx-auto px-4 py-8">
    <h1 class="text-3xl font-bold mb-8 text-center">자주 묻는 질문</h1>
    
    <div class="space-y-6">
      <section v-for="(category, index) in faqCategories" :key="index" class="bg-white p-6 rounded-lg shadow-md">
        <h2 class="text-2xl font-semibold mb-6 text-blue-600">{{ category.title }}</h2>
        <div class="space-y-4">
          <div v-for="(item, itemIndex) in category.items" :key="itemIndex" class="border-b border-gray-200 pb-4">
            <h3 class="text-lg font-medium text-gray-800 mb-2">Q. {{ item.question }}</h3>
            <p class="text-gray-700 leading-relaxed">A. {{ item.answer }}</p>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
const faqCategories = [
  {
    title: "서비스 이용",
    items: [
      {
        question: "어푸어푸는 어떤 서비스인가요?",
        answer: "어푸어푸는 수영을 사랑하는 모든 분들을 위한 커뮤니티 플랫폼입니다. 주변 수영장 찾기, 리뷰 공유, 커뮤니티 활동 등 다양한 기능을 제공합니다."
      },
      {
        question: "서비스 이용은 무료인가요?",
        answer: "네, 어푸어푸의 기본적인 서비스 이용은 모두 무료입니다. 회원가입 후 모든 기능을 자유롭게 이용하실 수 있습니다."
      },
      {
        question: "회원가입은 어떻게 하나요?",
        answer: "상단 메뉴의 '회원가입' 버튼을 클릭하여 간단한 정보 입력 후 가입하실 수 있습니다. 소셜 로그인도 지원됩니다."
      }
    ]
  },
  {
    title: "수영장 찾기",
    items: [
      {
        question: "내 주변 수영장은 어떻게 찾나요?",
        answer: "메인 화면이나 상단 메뉴의 '수영장 찾기'를 클릭하면 현재 위치 기반으로 주변 수영장을 찾을 수 있습니다."
      },
      {
        question: "수영장 정보는 실시간으로 업데이트되나요?",
        answer: "네, 수영장의 기본 정보와 이용자 리뷰는 실시간으로 업데이트됩니다. 운영시간이나 요금 등의 변동 사항은 각 수영장의 공지를 참고해주세요."
      }
    ]
  },
  {
    title: "리뷰 및 커뮤니티",
    items: [
      {
        question: "수영장 리뷰는 어떻게 작성하나요?",
        answer: "방문한 수영장의 상세 페이지에서 '리뷰 작성' 버튼을 클릭하여 리뷰를 작성할 수 있습니다. 로그인이 필요한 서비스입니다."
      },
      {
        question: "부적절한 리뷰를 발견했어요.",
        answer: "각 리뷰의 '신고하기' 버튼을 통해 부적절한 내용을 신고할 수 있습니다. 운영진 검토 후 적절한 조치가 이루어집니다."
      }
    ]
  },
  {
    title: "계정 관리",
    items: [
      {
        question: "비밀번호를 잊어버렸어요.",
        answer: "로그인 페이지의 '비밀번호 찾기'를 통해 가입 시 등록한 이메일로 비밀번호 재설정 링크를 받으실 수 있습니다."
      },
      {
        question: "회원 탈퇴는 어떻게 하나요?",
        answer: "마이페이지 > 설정 > 회원 탈퇴에서 진행하실 수 있습니다. 탈퇴 시 모든 데이터는 삭제되며 복구가 불가능합니다."
      }
    ]
  }
];
</script>
