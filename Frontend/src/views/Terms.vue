<template>
  <div class="max-w-4xl mx-auto px-4 py-8">
    <h1 class="text-3xl font-bold mb-8 text-center">이용약관</h1>
    
    <div class="space-y-8">
      <section v-for="(section, index) in terms" :key="index" class="bg-white p-6 rounded-lg shadow-md">
        <h2 class="text-2xl font-semibold mb-4 text-blue-600">{{ section.title }}</h2>
        <div class="space-y-4">
          <div v-for="(content, contentIndex) in section.contents" :key="contentIndex">
            <h3 v-if="content.subtitle" class="text-lg font-medium text-gray-800 mb-2">
              {{ content.subtitle }}
            </h3>
            <p class="text-gray-700 leading-relaxed">{{ content.text }}</p>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
const terms = [
  {
    title: "제1장 총칙",
    contents: [
      {
        subtitle: "제1조 (목적)",
        text: "이 약관은 어푸어푸(이하 '회사')가 제공하는 수영장 정보 제공 서비스(이하 '서비스')의 이용조건 및 절차, 회사와 회원 간의 권리, 의무, 책임사항과 기타 필요한 사항을 규정함을 목적으로 합니다."
      },
      {
        subtitle: "제2조 (용어의 정의)",
        text: "'회원'이란 이 약관에 동의하고 회사가 제공하는 서비스를 이용하는 자를 말합니다. '콘텐츠'란 회사가 제공하는 모든 정보와 자료를 의미합니다."
      }
    ]
  },
  {
    title: "제2장 서비스 이용",
    contents: [
      {
        subtitle: "제3조 (서비스의 제공)",
        text: "회사는 수영장 위치 정보, 리뷰, 커뮤니티 등의 서비스를 제공합니다. 서비스의 내용은 회사의 정책에 따라 변경될 수 있습니다."
      },
      {
        subtitle: "제4조 (서비스 이용시간)",
        text: "서비스는 연중무휴, 1일 24시간 제공됩니다. 다만, 회사는 서비스의 종류나 성질에 따라 제공시간을 별도로 정할 수 있습니다."
      }
    ]
  },
  {
    title: "제3장 의무 및 책임",
    contents: [
      {
        subtitle: "제5조 (회사의 의무)",
        text: "회사는 안정적인 서비스 제공을 위해 최선을 다하며, 서비스 제공과 관련하여 취득한 회원의 개인정보를 본인의 승낙 없이 제3자에게 제공하지 않습니다."
      },
      {
        subtitle: "제6조 (회원의 의무)",
        text: "회원은 관계법령, 이 약관의 규정, 이용안내 및 주의사항 등 회사가 통지하는 사항을 준수하여야 하며, 기타 회사의 업무에 방해되는 행위를 하여서는 안 됩니다."
      }
    ]
  },
  {
    title: "제4장 기타",
    contents: [
      {
        subtitle: "제7조 (저작권의 귀속)",
        text: "서비스에 대한 저작권 및 지적재산권은 회사에 귀속됩니다. 회원은 회사가 제공하는 서비스를 이용함으로써 얻은 정보를 회사의 사전 승낙 없이 복제, 송신, 출판, 배포, 방송 등 기타 방법에 의하여 영리목적으로 이용하거나 제3자에게 이용하게 하여서는 안 됩니다."
      },
      {
        subtitle: "제8조 (분쟁해결)",
        text: "서비스 이용과 관련하여 회사와 회원 간에 발생한 분쟁은 원만한 합의에 의해 해결함을 원칙으로 합니다. 합의가 이루어지지 않은 경우에는 관련법령 및 상관례에 따릅니다."
      }
    ]
  }
];
</script>
