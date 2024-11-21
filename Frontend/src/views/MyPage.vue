<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from 'vue-router';
import {
 User, Clock, Calendar, Star, 
 Settings, LogOut, ChevronRight,
 Mail, MapPin, Award, Camera
} from "lucide-vue-next";

const router = useRouter();
const user = ref({
 name: "",
 email: "",
 location: "",
 membershipLevel: "Member",
 profileImage: "default-profile.png",
 stats: [
   { label: "예약", value: "0" },
   { label: "리뷰", value: "0" },
   { label: "포인트", value: "0" }
 ]
});

// 파일 입력을 위한 ref
const fileInput = ref(null);

// 사용자 데이터 로드
onMounted(async () => {
 const userId = localStorage.getItem("userId");
 
 if (!userId) {
   alert("로그인이 필요한 서비스입니다.");
   router.push('/login');
   return;
 }

 user.value = {
   ...user.value,
   name: localStorage.getItem("userName") || "사용자",
   email: localStorage.getItem("userEmail") || "이메일 미설정",
   location: localStorage.getItem("userAddress") || "주소 미설정",
   profileImage: localStorage.getItem("pImage") || "default-profile.png",
 };

 try {
   const response = await fetch(`http://localhost:8080/uhpooh/api/user/${userId}`);
   if (response.ok) {
     const userData = await response.json();
     
     user.value.stats = [
       { label: "예약", value: userData.reservationCount || "0" },
       { label: "리뷰", value: userData.reviewCount || "0" },
       { label: "포인트", value: userData.points?.toLocaleString() || "0" }
     ];
   }
 } catch (error) {
   console.error("사용자 데이터 로딩 중 오류 발생:", error);
 }
});

// 파일 업로드 처리
const handleFileUpload = async (event) => {
 const file = event.target.files[0];
 if (!file) return;

 const userId = localStorage.getItem("userId");
 if (!userId) return;

 // 이미지 파일 형식 검사
 if (!file.type.startsWith('image/')) {
   alert('이미지 파일만 업로드 가능합니다.');
   return;
 }

 // 파일 크기 제한 (예: 5MB)
 if (file.size > 5 * 1024 * 1024) {
   alert('파일 크기는 5MB 이하여야 합니다.');
   return;
 }

 const formData = new FormData();
 formData.append('file', file);

 try {
   const response = await fetch(`http://localhost:8080/uhpooh/api/file/profile/${userId}`, {
     method: 'POST',
     body: formData
   });

   if (response.ok) {
     const result = await response.json();
     // 프로필 이미지 경로 업데이트 및 localStorage 저장
     user.value.profileImage = result.imageUrl || result.path || result;
     localStorage.setItem('pImage', user.value.profileImage);
     alert('프로필 이미지가 업데이트되었습니다.');
   } else {
     throw new Error('이미지 업로드에 실패했습니다.');
   }
 } catch (error) {
   console.error('프로필 이미지 업로드 중 오류:', error);
   alert('이미지 업로드에 실패했습니다. 다시 시도해주세요.');
 }
};

const menuItems = [
 { 
   icon: Clock, 
   label: "이용 내역", 
   path: "/history",
   description: "수영장 예약 및 이용 기록" 
 },
 { 
   icon: Star, 
   label: "즐겨찾기", 
   path: "/favorites",
   description: "자주 가는 수영장 모음" 
 },
 { 
   icon: Settings, 
   label: "설정", 
   path: "/settings",
   description: "앱 설정 및 개인정보 관리" 
 },
];

const handleLogout = () => {
 localStorage.clear();
 router.push('/');
};
</script>

<template>
  <div class="max-w-3xl mx-auto p-4 space-y-6">
    <!-- 프로필 헤더 -->
    <div class="bg-white rounded-2xl p-6 shadow-sm">
      <div class="flex items-center gap-6">
        <div class="relative">
          <img 
            :src="user.profileImage" 
            alt="Profile" 
            class="w-24 h-24 rounded-full object-cover border-4 border-blue-100"
          />
          <!-- 프로필 사진 업로드 버튼 -->
          <div class="absolute -bottom-2 -right-2">
            <input
              type="file"
              ref="fileInput"
              @change="handleFileUpload"
              accept="image/*"
              class="hidden"
            />
            <button
              @click="fileInput.click()"
              class="w-8 h-8 rounded-full bg-blue-500 flex items-center justify-center hover:bg-blue-600 transition-colors"
            >
              <Camera class="w-4 h-4 text-white" />
            </button>
          </div>
        </div>
        <div class="flex-1">
          <div class="flex items-center gap-2">
            <h1 class="text-2xl font-bold">{{ user.name }}</h1>
            <span class="bg-blue-100 text-blue-600 px-3 py-1 rounded-full text-sm">
              {{ user.membershipLevel }}
            </span>
          </div>
          <div class="mt-2 space-y-1 text-gray-600">
            <div class="flex items-center gap-2">
              <Mail class="w-4 h-4" />
              <span>{{ user.email }}</span>
            </div>
            <div class="flex items-center gap-2">
              <MapPin class="w-4 h-4" />
              <span>{{ user.location }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 통계 -->
      <div class="grid grid-cols-3 gap-4 mt-6 pt-6 border-t">
        <div 
          v-for="stat in user.stats" 
          :key="stat.label"
          class="text-center"
        >
          <div class="text-2xl font-bold text-blue-600">{{ stat.value }}</div>
          <div class="text-sm text-gray-600">{{ stat.label }}</div>
        </div>
      </div>
    </div>
 
    <!-- 메뉴 아이템 -->
    <div class="bg-white rounded-2xl shadow-sm divide-y">
      <div 
        v-for="item in menuItems" 
        :key="item.label"
        class="flex items-center justify-between p-4 hover:bg-gray-50 cursor-pointer"
        @click="router.push(item.path)"
      >
        <div class="flex items-center gap-4">
          <div class="w-10 h-10 rounded-full bg-blue-50 flex items-center justify-center">
            <component :is="item.icon" class="w-5 h-5 text-blue-600" />
          </div>
          <div>
            <div class="font-medium">{{ item.label }}</div>
            <div class="text-sm text-gray-600">{{ item.description }}</div>
          </div>
        </div>
        <ChevronRight class="w-5 h-5 text-gray-400" />
      </div>
    </div>
 
    <!-- 로그아웃 버튼 -->
    <button
      @click="handleLogout"
      class="w-full bg-white rounded-2xl p-4 shadow-sm flex items-center justify-between hover:bg-gray-50"
    >
      <div class="flex items-center gap-4">
        <div class="w-10 h-10 rounded-full bg-red-50 flex items-center justify-center">
          <LogOut class="w-5 h-5 text-red-600" />
        </div>
        <div>
          <div class="font-medium">로그아웃</div>
          <div class="text-sm text-gray-600">안전하게 로그아웃합니다</div>
        </div>
      </div>
      <ChevronRight class="w-5 h-5 text-gray-400" />
    </button>
  </div>
 </template>
 
 <style scoped>
 /* 이미지 업로드 버튼 호버 효과 */
 .upload-button {
  transition: all 0.2s ease;
 }
 
 .upload-button:hover {
  transform: scale(1.1);
 }
 </style>