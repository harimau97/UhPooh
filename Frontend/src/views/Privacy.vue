<template>
  <div class="max-w-4xl mx-auto px-4 py-8">
    <h1 class="text-3xl font-bold mb-8 text-center">개인정보처리방침</h1>
    
    <div class="space-y-8">
      <section v-for="(section, index) in privacy" :key="index" class="bg-white p-6 rounded-lg shadow-md">
        <h2 class="text-2xl font-semibold mb-4 text-blue-600">{{ section.title }}</h2>
        <div class="space-y-4">
          <div v-for="(content, contentIndex) in section.contents" :key="contentIndex">
            <h3 v-if="content.subtitle" class="text-lg font-medium text-gray-800 mb-2">
              {{ content.subtitle }}
            </h3>
            <p class="text-gray-700 leading-relaxed" v-html="content.text"></p>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
const privacy = [
  {
    title: "1. 개인정보의 수집 및 이용 목적",
    contents: [
      {
        text: "어푸어푸는 다음의 목적을 위하여 개인정보를 처리합니다. 처리하고 있는 개인정보는 다음의 목적 이외의 용도로는 이용되지 않으며, 이용 목적이 변경되는 경우에는 별도의 동의를 받는 등 필요한 조치를 이행할 예정입니다.",
      },
      {
        subtitle: "회원 가입 및 관리",
        text: "회원 가입의사 확인, 회원제 서비스 제공에 따른 본인 식별·인증, 회원자격 유지·관리, 제한적 본인확인제 시행에 따른 본인확인, 서비스 부정이용 방지, 만14세 미만 아동의 개인정보 처리 시 법정대리인의 동의여부 확인, 각종 고지·통지, 고충처리 목적으로 개인정보를 처리합니다."
      },
      {
        subtitle: "서비스 제공",
        text: "수영장 위치 정보 제공, 리뷰 작성 및 조회, 콘텐츠 제공, 맞춤서비스 제공, 서비스 이용에 대한 통계 등을 목적으로 개인정보를 처리합니다."
      }
    ]
  },
  {
    title: "2. 수집하는 개인정보 항목",
    contents: [
      {
        text: "어푸어푸는 회원가입, 상담, 서비스 신청 등을 위해 아래와 같은 개인정보를 수집하고 있습니다.",
      },
      {
        subtitle: "필수항목",
        text: "이메일 주소, 비밀번호, 닉네임, 휴대폰 번호"
      },
      {
        subtitle: "선택항목",
        text: "프로필 이미지, 관심 지역"
      },
      {
        subtitle: "자동 수집 항목",
        text: "IP 주소, 쿠키, 서비스 이용 기록, 접속 로그"
      }
    ]
  },
  {
    title: "3. 개인정보의 보유 및 이용기간",
    contents: [
      {
        text: "어푸어푸는 법령에 따른 개인정보 보유·이용기간 또는 정보주체로부터 개인정보를 수집 시에 동의받은 개인정보 보유·이용기간 내에서 개인정보를 처리·보유합니다."
      },
      {
        subtitle: "회원 정보",
        text: "- 탈퇴 시까지 (단, 관계 법령에 따라 필요한 경우 해당 법령에서 정한 기간까지)<br>- 부정이용 방지를 위한 정보: 탈퇴 후 1년"
      },
      {
        subtitle: "서비스 이용 기록",
        text: "- 서비스 이용 기록: 3년<br>- 결제 및 취소 기록: 5년"
      }
    ]
  },
  {
    title: "4. 개인정보의 파기절차 및 방법",
    contents: [
      {
        text: "어푸어푸는 원칙적으로 개인정보 처리목적이 달성된 경우에는 지체없이 해당 개인정보를 파기합니다. 파기의 절차, 기한 및 방법은 다음과 같습니다."
      },
      {
        subtitle: "파기절차",
        text: "이용자가 입력한 정보는 목적 달성 후 별도의 DB에 옮겨져 내부 방침 및 기타 관련 법령에 따라 일정기간 저장된 후 혹은 즉시 파기됩니다."
      },
      {
        subtitle: "파기방법",
        text: "전자적 파일 형태의 정보는 기록을 재생할 수 없는 기술적 방법을 사용하여 삭제하며, 종이에 출력된 개인정보는 분쇄기로 분쇄하거나 소각을 통하여 파기합니다."
      }
    ]
  }
];
</script>
