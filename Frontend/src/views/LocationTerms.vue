<template>
  <div class="max-w-4xl mx-auto px-4 py-8">
    <h1 class="text-3xl font-bold mb-8 text-center">위치기반서비스 이용약관</h1>
    
    <div class="space-y-8">
      <section v-for="(section, index) in locationTerms" :key="index" class="bg-white p-6 rounded-lg shadow-md">
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
const locationTerms = [
  {
    title: "제1조 목적",
    contents: [
      {
        text: "본 약관은 어푸어푸(이하 '회사')가 제공하는 위치기반서비스(이하 '서비스')를 이용함에 있어 회사와 이용자의 권리, 의무 및 책임사항을 규정함을 목적으로 합니다."
      }
    ]
  },
  {
    title: "제2조 이용약관의 효력 및 변경",
    contents: [
      {
        text: "① 본 약관은 서비스를 신청한 이용자 또는 개인위치정보주체가 본 약관에 동의하고 회사가 정한 소정의 절차에 따라 서비스의 이용자로 등록함으로써 효력이 발생합니다."
      },
      {
        text: "② 회사는 필요한 경우 위치정보의 보호 및 이용 등에 관한 법률, 콘텐츠산업 진흥법, 전자상거래 등에서의 소비자보호에 관한 법률, 소비자기본법 약관의 규제에 관한 법률 등 관련법령을 위배하지 않는 범위에서 본 약관을 변경할 수 있습니다."
      }
    ]
  },
  {
    title: "제3조 서비스의 내용",
    contents: [
      {
        text: "회사는 위치정보사업자로부터 위치정보를 전달받아 아래와 같은 위치기반서비스를 제공합니다.",
      },
      {
        subtitle: "1. 위치정보 수집",
        text: "이용자의 현재 위치를 기반으로 주변 수영장 정보를 제공하기 위해 위치정보를 수집합니다."
      },
      {
        subtitle: "2. 위치정보 이용",
        text: "수집된 위치정보는 이용자에게 가까운 수영장 검색, 길 안내 등의 서비스 제공에 활용됩니다."
      }
    ]
  },
  {
    title: "제4조 서비스 이용요금",
    contents: [
      {
        text: "회사가 제공하는 위치기반서비스는 기본적으로 무료입니다. 단, 무선 서비스 이용 시 발생하는 데이터 통신료는 별도이며, 이용자가 가입한 각 이동통신사의 정책에 따릅니다."
      }
    ]
  },
  {
    title: "제5조 개인위치정보의 보호",
    contents: [
      {
        subtitle: "1. 위치정보 수집 동의",
        text: "회사는 위치기반서비스를 제공하기 위해 이용자의 개인위치정보를 수집하며, 이용자는 본 약관에 동의함으로써 이에 동의한 것으로 간주됩니다."
      },
      {
        subtitle: "2. 개인위치정보의 이용 또는 제공",
        text: "회사는 위치기반서비스 제공을 위해 수집된 개인위치정보를 이용자에게 제공하는 서비스 이외의 목적으로 이용하거나 제3자에게 제공하지 않습니다."
      },
      {
        subtitle: "3. 개인위치정보의 보유기간",
        text: "회사는 위치기반서비스를 제공하기 위해 수집한 개인위치정보를 이용목적을 달성한 후에는 즉시 파기합니다. 단, 관계법령에서 정하는 경우에는 해당 법령에 따라 보관할 수 있습니다."
      }
    ]
  },
  {
    title: "제6조 이용자의 권리",
    contents: [
      {
        text: "① 이용자는 회사에 대하여 언제든지 개인위치정보를 이용한 위치기반서비스 제공 및 개인위치정보의 제3자 제공에 대한 동의의 전부 또는 일부를 철회할 수 있습니다."
      },
      {
        text: "② 이용자는 회사에 대하여 언제든지 개인위치정보의 수집, 이용 또는 제공의 일시적인 중지를 요구할 수 있습니다."
      }
    ]
  }
];
</script>
