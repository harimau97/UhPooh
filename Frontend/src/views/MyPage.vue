<script setup>
import { ref, computed, onMounted, inject } from "vue";
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
import { logout } from "../composables/userAuth";

const router = useRouter();
const showLogoutModal = ref(false);
const myImageTrue = ref("");
const myImgName = ref("");
const myImgPath = ref("");
const countLikedPlaces = ref([]);
const countMyReviews = ref([]);
const countMyReviewsLength = computed(() => countMyReviews.value.length);
const countLikedPlacesLength = computed(() => countLikedPlaces.value.length);
const user = ref({
  name: "",
  email: "",
  location: "",
  membershipLevel: "Member",
  profileImageUrl: "",
  stats: [
    { label: "좋아요", value: "0" },
    { label: "리뷰", value: "0" },
    { label: "포인트", value: "0" },
  ],
});
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
      description: "웹사이트 설정",
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

// 내가 쓴 리뷰 목록 가져오기
const countFetchMyReviews = async () => {
  try {
    const userId = localStorage.getItem("userId");
    if (!userId) {
      console.log("User ID not found");
      return;
    }
    const response = await axios.get(
      `http://localhost:8080/uhpooh/api/review/search/writer`,
      {
        params: {
          userId: userId,
        },
      }
    );

    if (response.data) {
      countMyReviews.value = response.data.data.items;
      console.log("리뷰 데이터:", countMyReviews.value);
    } else {
      console.log("Invalid review data format", response.data);
      countMyReviews.value = [];
    }
  } catch (error) {
    console.log(
      "Error fetching reviews:",
      error.response?.data || error.message
    );
    countMyReviews.value = [];
  }
};

// 좋아요한 수영장 목록 가져오기
const countFetchLikedPlaces = async () => {
  try {
    const userId = localStorage.getItem("userId");
    if (!userId) {
      console.log("User ID not found");
      return;
    }
    const response = await axios.get(
      "http://localhost:8080/uhpooh/api/place/getplaceidbyuserid/" + userId
    );
    if (response.data && Array.isArray(response.data)) {
      countLikedPlaces.value = response.data;
      console.log("좋아요 데이터:", countLikedPlaces.value);
    } else {
      console.log("Invalid liked places data format", response.data);
      countLikedPlaces.value = [];
    }
  } catch (error) {
    console.log(
      "Error fetching liked places:",
      error.response?.data || error.message
    );
    countLikedPlaces.value = [];
  }
};

//프로필이미지 캐싱
const cacheMyImage = async (cat) => {
  try {
    console.log(cat);
    myImgPath.value =
      "http://localhost:8080/uhpooh/api/file/images/" +
      cat +
      "/" +
      myImgName.value;
    const response = await axios.get(myImgPath.value, { timeout: 5000 });
    console.log("이미지 캐싱 성공:", myImgPath.value);
  } catch (error) {
    console.log("이미지 캐싱 실패:", error.response?.data || error.message);
  }
};

// 사용자 데이터 로드
onMounted(async () => {
  const userId = localStorage.getItem("userId");
  const isAdmin = localStorage.getItem("isAdmin");
  const pImage = localStorage.getItem("pImage");
  const userProfileImage = localStorage.getItem("userProfileImage");

  if (!userId) {
    alert("로그인이 필요한 서비스입니다.");
    router.push("/login");
    return;
  }

  // 기본 사용자 정보 설정
  user.value = {
    ...user.value,
    name: localStorage.getItem("userName") || "사용자",
    email: localStorage.getItem("userEmail") || "이메일 미설정",
    location: localStorage.getItem("userAddress") || "주소 미설정",
    membershipLevel: isAdmin === "1" ? "Admin" : "Member",
  };

  // 프로필 이미지 처리
  myImageTrue.value = pImage || "";
  console.log(myImageTrue.value);

  if (pImage) {
    myImgName.value = myImageTrue.value.replace("/images/profiles/", "");
    await cacheMyImage("profiles");
    // user.value.profileImageUrl =
    //   userProfileImage || `http://localhost:8080/uhpooh/api/images/${pImage}`;
  }

  // 사용자 통계 데이터 로드
  try {
    await Promise.all([countFetchMyReviews(), countFetchLikedPlaces()]);

    // 포인트 계산: 좋아요 * 10 + 리뷰 * 20
    const calculatedPoints =
      countLikedPlacesLength.value * 10 + countMyReviewsLength.value * 20;

    user.value.stats = [
      { label: "좋아요", value: countLikedPlacesLength.value.toString() },
      { label: "리뷰", value: countMyReviewsLength.value.toString() },
      { label: "포인트", value: calculatedPoints.toLocaleString() },
    ];
  } catch (error) {
    console.log("사용자 데이터 로딩 중 오류 발생:", error);
  }
});

const handleImageError = (event) => {
  event.target.src = ""; // 기본 이미지를 표시하기 위해 src를 비움
  user.value.profileImageUrl = ""; // 상태도 업데이트
};

const confirmLogout = () => {
  showLogoutModal.value = false;
  handleLogout();
};

const handleLogout = async () => {
  showLogoutModal.value = false;
  try {
    await logout();
  } catch (error) {
    console.log("로그아웃 처리 중 오류 발생:", error);
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
            class="overflow-hidden w-24 h-24 rounded-full border-4 border-blue-100"
          >
            <img
              v-if="myImageTrue !== 'null' && myImageTrue !== ''"
              :src="myImgPath"
              alt="Profile"
              class="object-cover w-full h-full"
              @error="handleImageError"
            />
            <div
              v-else
              class="flex justify-center items-center w-full h-full bg-gray-200"
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
    <div v-if="showLogoutModal" class="overflow-y-auto fixed inset-0 z-50">
      <div class="flex justify-center items-center p-4 min-h-full text-center">
        <div
          class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity"
          @click="showLogoutModal = false"
        ></div>

        <div
          class="overflow-hidden relative text-left bg-white rounded-lg shadow-xl transition-all transform sm:my-8 sm:w-full sm:max-w-lg"
        >
          <div class="px-4 pt-5 pb-4 bg-white sm:p-6 sm:pb-4">
            <div class="sm:flex sm:items-start">
              <div
                class="flex flex-shrink-0 justify-center items-center mx-auto w-12 h-12 bg-red-100 rounded-full sm:mx-0 sm:h-10 sm:w-10"
              >
                <LogOut class="w-6 h-6 text-red-600" />
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
          <div class="px-4 py-3 bg-gray-50 sm:flex sm:flex-row-reverse sm:px-6">
            <button
              type="button"
              class="inline-flex justify-center px-3 py-2 w-full text-sm font-semibold text-white bg-red-600 rounded-md shadow-sm hover:bg-red-500 sm:ml-3 sm:w-auto"
              @click="confirmLogout"
            >
              로그아웃
            </button>
            <button
              type="button"
              class="inline-flex justify-center px-3 py-2 mt-3 w-full text-sm font-semibold text-gray-900 bg-white rounded-md ring-1 ring-inset ring-gray-300 shadow-sm hover:bg-gray-50 sm:mt-0 sm:w-auto"
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
          <div class="flex justify-start font-medium">로그아웃</div>
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
