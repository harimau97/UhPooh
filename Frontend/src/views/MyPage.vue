<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import {
  User,
  Clock,
  Calendar,
  Heart,
  Settings,
  LogOut,
  ChevronRight,
  Mail,
  MapPin,
  Award,
  Camera,
} from "lucide-vue-next";
import axios from "axios";
import { inject } from "vue";

const imgPath = ref("");
const router = useRouter();
const showLogoutModal = ref(false); // 모달 상태 추가

const user = ref({
  name: "",
  email: "",
  location: "",
  membershipLevel: "Member",
  profileImage: "default-profile.png",
  stats: [
    { label: "예약", value: "0" },
    { label: "리뷰", value: "0" },
    { label: "포인트", value: "0" },
  ],
});

// 파일 입력을 위한 ref
const fileInput = ref(null);

// 사용자 데이터 로드
onMounted(async () => {
  console.log(localStorage.getItem("pImage"));
  if (localStorage.getItem("pImage") === "undefined") {
    imgPath.value = "http://localhost:5173/src/assets/default-profile.png";
  }
  const userId = localStorage.getItem("userId");
  const isAdmin = localStorage.getItem("isAdmin");

  if (!userId) {
    alert("로그인이 필요한 서비스입니다.");
    router.push("/login");
    return;
  }

  // localStorage에서 기본 정보 가져오기
  user.value = {
    ...user.value,
    name: localStorage.getItem("userName") || "사용자",
    email: localStorage.getItem("userEmail") || "이메일 미설정",
    location: localStorage.getItem("userAddress") || "주소 미설정",
    profileImage: localStorage.getItem("pImage") || "default-profile.png",
    membershipLevel: isAdmin === "1" ? "Admin" : "Member",
  };

  try {
    const response = await fetch(
      `http://localhost:8080/uhpooh/api/user/${userId}`
    );
    if (response.ok) {
      const userData = await response.json();
      user.value.stats = [
        { label: "예약", value: userData.reservationCount || "0" },
        { label: "리뷰", value: userData.reviewCount || "0" },
        { label: "포인트", value: userData.points?.toLocaleString() || "0" },
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
  if (!file.type.startsWith("image/")) {
    alert("이미지 파일만 업로드 가능합니다.");
    return;
  }

  // 파일 크기 제한 (예: 5MB)
  if (file.size > 5 * 1024 * 1024) {
    alert("파일 크기는 5MB 이하여야 합니다.");
    return;
  }

  const formData = new FormData();
  formData.append("file", file);

  try {
    const response = await fetch(
      `http://localhost:8080/uhpooh/api/file/profile/${userId}`,
      {
        method: "POST",
        body: formData,
      }
    );

    if (response.ok) {
      const result = await response.json();
      // 프로필 이미지 경로 업데이트 및 localStorage 저장
      const imagePath = result.imageUrl || result.path || result;
      user.value.profileImage = imagePath;
      localStorage.setItem("pImage", imagePath);
      alert("프로필 이미지가 업데이트되었습니다.");
    } else {
      throw new Error("이미지 업로드에 실패했습니다.");
    }
  } catch (error) {
    console.error("프로필 이미지 업로드 중 오류:", error);
    alert("이미지 업로드에 실패했습니다. 다시 시도해주세요.");
  }
};

const menuItems = computed(() => {
  const items = [
    {
      icon: Clock,
      label: "이용 내역",
      path: "/history",
      description: "수영장 예약 및 이용 기록",
    },
    {
      icon: Heart,
      label: "좋아요한 수영장",
      path: "/favorites",
      description: "내가 좋아요한 수영장 모음",  // description 수정
    },
    {
      icon: Settings,
      label: "설정",
      path: "/settings",
      description: "앱 설정 및 개인정보 관리",
    },
  ];

  // isAdmin이 1인 경우에만 Admin 메뉴 추가
  if (localStorage.getItem("isAdmin") === "1") {
    items.push({
      icon: User,
      label: "Admin",
      path: "/admin",
      description: "전체 회원 목록 관리",
      adminStyle: true, // 스타일 구분을 위한 플래그
    });
  }

  return items;
});

const isLoggined = inject("isLoggedIn");

// 로그아웃 확인 모달 처리
const confirmLogout = () => {
  showLogoutModal.value = false;
  handleLogout();
};

const handleLogout = async () => {
  const uId = localStorage.getItem("userId");
  try {
    console.log("로그아웃 시작");
    await axios({
      method: "post",
      url: `http://localhost:8080/uhpooh/api/user/logout/${uId}`,
      headers: {
        "Content-Type": "application/json",
      },
      data: {},
    });
    
    // 로컬 스토리지 클리어
    localStorage.removeItem("userToken");
    localStorage.removeItem("userId");
    localStorage.removeItem("userName");
    localStorage.removeItem("userAddress");
    localStorage.removeItem("pImage");
    
    // 로그인 상태 업데이트
    isLoggined.value = false;
    
    // 홈으로 리다이렉트
    location.replace("/");
  } catch (error) {
    console.error("로그아웃 중 오류 발생:", error);
    alert("로그아웃 중 오류가 발생했습니다. 다시 시도해주세요.");
  }
};
</script>

<template>
  <div class="p-4 mx-auto space-y-6 max-w-3xl">
    <!-- 프로필 헤더 -->
    <div class="p-6 bg-white rounded-2xl shadow-sm">
      <div class="flex gap-6 items-center">
        <div class="relative">
          <img
            :src="imgPath"
            alt="Profile"
            class="object-cover w-24 h-24 rounded-full border-4 border-blue-100"
          />
          <!-- 프로필 사진 업로드 버튼 -->
          <div class="absolute -right-2 -bottom-2">
            <input
              type="file"
              ref="fileInput"
              @change="handleFileUpload"
              accept="image/*"
              class="hidden"
            />
            <button
              @click="fileInput.click()"
              class="flex justify-center items-center w-8 h-8 bg-blue-500 rounded-full transition-colors hover:bg-blue-600"
            >
              <Camera class="w-4 h-4 text-white" />
            </button>
          </div>
        </div>
        <div class="flex-1">
          <div class="flex gap-2 items-center">
            <h1 class="text-2xl font-bold">{{ user.name }}</h1>
            <span
              :class="[
                'px-3 py-1 text-sm rounded-full',
                user.membershipLevel === 'Admin'
                  ? 'text-red-600 bg-red-100'
                  : 'text-blue-600 bg-blue-100',
              ]"
            >
              {{ user.membershipLevel }}
            </span>
          </div>
          <div class="mt-2 space-y-1 text-gray-600">
            <div class="flex gap-2 items-center">
              <Mail class="w-4 h-4" />
              <span>{{ user.email }}</span>
            </div>
            <div class="flex gap-2 items-center">
              <MapPin class="w-4 h-4" />
              <span>{{ user.location }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 통계 -->
      <div class="grid grid-cols-3 gap-4 pt-6 mt-6 border-t">
        <div v-for="stat in user.stats" :key="stat.label" class="text-center">
          <div class="text-2xl font-bold text-blue-600">{{ stat.value }}</div>
          <div class="text-sm text-gray-600">{{ stat.label }}</div>
        </div>
      </div>
    </div>

    <!-- 메뉴 아이템 -->
    <div class="bg-white rounded-2xl divide-y shadow-sm">
      <div
        v-for="item in menuItems"
        :key="item.label"
        class="flex justify-between items-center p-4 cursor-pointer hover:bg-gray-50"
        @click="router.push(item.path)"
      >
        <div class="flex gap-4 items-center">
          <div
            :class="[
              'flex justify-center items-center w-10 h-10 rounded-full',
              item.adminStyle ? 'bg-red-50' : 'bg-blue-50',
            ]"
          >
            <component
              :is="item.icon"
              :class="[
                'w-5 h-5',
                item.adminStyle ? 'text-red-600' : 'text-blue-600',
              ]"
            />
          </div>
          <div>
            <div class="font-medium">{{ item.label }}</div>
            <div class="text-sm text-gray-600">{{ item.description }}</div>
          </div>
        </div>
        <ChevronRight class="w-5 h-5 text-gray-400" />
      </div>
    </div>

    <!-- 로그아웃 모달 -->
    <div v-if="showLogoutModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex min-h-full items-center justify-center p-4 text-center">
        <!-- 배경 오버레이 -->
        <div 
          class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity"
          @click="showLogoutModal = false"
        ></div>

        <!-- 모달 내용 -->
        <div class="relative transform overflow-hidden rounded-lg bg-white text-left shadow-xl transition-all sm:my-8 sm:w-full sm:max-w-lg">
          <div class="bg-white px-4 pb-4 pt-5 sm:p-6 sm:pb-4">
            <div class="sm:flex sm:items-start">
              <div class="mx-auto flex h-12 w-12 flex-shrink-0 items-center justify-center rounded-full bg-red-100 sm:mx-0 sm:h-10 sm:w-10">
                <LogOut class="h-6 w-6 text-red-600" />
              </div>
              <div class="mt-3 text-center sm:ml-4 sm:mt-0 sm:text-left">
                <h3 class="text-lg font-semibold leading-6 text-gray-900">
                  로그아웃
                </h3>
                <div class="mt-2">
                  <p class="text-sm text-gray-500">
                    정말 로그아웃 하시겠습니까?
                  </p>
                </div>
              </div>
            </div>
          </div>
          <div class="bg-gray-50 px-4 py-3 sm:flex sm:flex-row-reverse sm:px-6">
            <button
              type="button"
              class="inline-flex w-full justify-center rounded-md bg-red-600 px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-red-500 sm:ml-3 sm:w-auto"
              @click="confirmLogout"
            >
              로그아웃
            </button>
            <button
              type="button"
              class="mt-3 inline-flex w-full justify-center rounded-md bg-white px-3 py-2 text-sm font-semibold text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50 sm:mt-0 sm:w-auto"
              @click="showLogoutModal = false"
            >
              취소
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 로그아웃 버튼 -->
    <button
      @click="showLogoutModal = true"
      class="flex justify-between items-center p-4 w-full bg-white rounded-2xl shadow-sm hover:bg-gray-50"
    >
      <div class="flex gap-4 items-center">
        <div
          class="flex justify-center items-center w-10 h-10 bg-red-50 rounded-full"
        >
          <LogOut class="w-5 h-5 text-red-600" />
        </div>
        <div>
          <div class="font-medium justify-start flex">로그아웃</div>
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
