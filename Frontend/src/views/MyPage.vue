<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import {
  UserCog,
  User,
  Heart,
  Settings,
  LogOut,
  ChevronRight,
  Mail,
  MapPin,
} from "lucide-vue-next";
import axios from "axios";
import { inject } from "vue";

const router = useRouter();
const showLogoutModal = ref(false);
const imageTrue = ref("");
const imgName = ref("");
const imgPath = ref("");

const user = ref({
  name: "",
  email: "",
  location: "",
  membershipLevel: "Member",
  profileImageUrl: "",
  stats: [
    { label: "예약", value: "0" },
    { label: "리뷰", value: "0" },
    { label: "포인트", value: "0" },
  ],
});

//프로필이미지 캐싱
const cacheImage = async (cat) => {
  imgPath.value = "http://localhost:8080/uhpooh/api/file/images/" + cat + "/" + imgName.value;  
  const response = await axios.get(
    imgPath.value,
    { timeout: 5000 }
  );
  
};

// 사용자 데이터 로드
onMounted(async () => {
  const userId = localStorage.getItem("userId");
  const isAdmin = localStorage.getItem("isAdmin");
  const pImage = localStorage.getItem("pImage");
  const userProfileImage = localStorage.getItem("userProfileImage");
  imageTrue.value = pImage;
  imgName.value = imageTrue.value.replace("/images/profiles/", "");
  console.log(imgName.value);
  if (imgName.value !== "") {await cacheImage("profiles");}
  if (!userId) {
    alert("로그인이 필요한 서비스입니다.");
    router.push("/login");
    return;
  }

  // 프로필 이미지 설정 로직 수정
  user.value.profileImageUrl =
    userProfileImage ||
    (pImage ? `http://localhost:8080/uhpooh/api/images/${pImage}` : "");

  // localStorage에서 기본 정보 가져오기
  user.value = {
    ...user.value,
    name: localStorage.getItem("userName") || "사용자",
    email: localStorage.getItem("userEmail") || "이메일 미설정",
    location: localStorage.getItem("userAddress") || "주소 미설정",
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

const handleImageError = (event) => {
  event.target.src = ""; // 기본 이미지를 표시하기 위해 src를 비움
  user.value.profileImageUrl = ""; // 상태도 업데이트
};

const menuItems = computed(() => {
  const items = [
    {
      icon: UserCog,
      label: "내 정보 수정",
      path: "/edit",
      description: "내 정보 수정",
    },
    {
      icon: Heart,
      label: "My 수영장",
      path: "/mypools",
      description: "좋아요 및 리뷰 조회",
    },
    {
      icon: Settings,
      label: "설정",
      path: "/settings",
      description: "앱 설정 및 개인정보 관리",
    },
  ];

  if (localStorage.getItem("isAdmin") === "1") {
    items.push({
      icon: User,
      label: "Admin",
      path: "/admin",
      description: "전체 회원 목록 관리",
      adminStyle: true,
    });
  }

  return items;
});

const isLoggined = inject("isLoggedIn");

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

    localStorage.removeItem("userToken");
    localStorage.removeItem("userId");
    localStorage.removeItem("userName");
    localStorage.removeItem("userAddress");
    localStorage.removeItem("userProfileImage");

    isLoggined.value = false;
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
        <!-- 프로필 이미지 -->
        <div class="relative">
          <div
            class="w-24 h-24 rounded-full border-4 border-blue-100 overflow-hidden"
          >
            <img
              v-if="imageTrue !== 'null' && imageTrue !== ''"
              :src="imgPath"
              alt="Profile"
              class="w-full h-full object-cover"
              @error="handleImageError"
            />
            <div
              v-else
              class="w-full h-full bg-gray-200 flex items-center justify-center"
            >
              <User class="w-12 h-12 text-gray-400" />
            </div>
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
        <div
          class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity"
          @click="showLogoutModal = false"
        ></div>

        <div
          class="relative transform overflow-hidden rounded-lg bg-white text-left shadow-xl transition-all sm:my-8 sm:w-full sm:max-w-lg"
        >
          <div class="bg-white px-4 pb-4 pt-5 sm:p-6 sm:pb-4">
            <div class="sm:flex sm:items-start">
              <div
                class="mx-auto flex h-12 w-12 flex-shrink-0 items-center justify-center rounded-full bg-red-100 sm:mx-0 sm:h-10 sm:w-10"
              >
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
