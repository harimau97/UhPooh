<script setup>
import { ref, onMounted } from "vue";
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

const router = useRouter();
const users = ref([]);
const loading = ref(false);

// 검색 관련 상태
const searchKeyword = ref("");
const searchType = ref("all");
const searchTypes = [
  { value: "all", label: "전체" },
  { value: "name", label: "닉네임" },
  { value: "email", label: "이메일" },
  { value: "admin", label: "관리자 여부" },
];

// 페이징 관련 상태
const currentPage = ref(1);
const pageSize = ref(12);
const totalItems = ref(0);
const displayedTotal = ref(0);

// 이미지 처리
const userImages = ref(new Map());

const cacheUserImage = (cat, pimgName) => {
  if (!pimgName) return null;
  const cleanImgName = pimgName.replace("/images/profiles/", "");
  const imgPath = `http://localhost:8080/uhpooh/api/file/images/${cat}/${cleanImgName}`;
  userImages.value.set(pimgName, imgPath);
  return imgPath;
};

// 전체 회원 목록을 가져오는 함수
const loadAllUsers = async () => {
  const requestUserId = localStorage.getItem("userId");
  if (!requestUserId) return;

  loading.value = true;
  try {
    // 전체 목록도 페이징 처리된 API를 호출
    const response = await fetch(
      `http://localhost:8080/uhpooh/api/user/list?page=${currentPage.value}&size=${pageSize.value}&requestUserId=${requestUserId}`
    );

    if (!response.ok) {
      throw new Error("회원 목록을 불러오는데 실패했습니다.");
    }

    const result = await response.json();
    if (result.success && result.data) {
      users.value = result.data; // 페이징된 사용자 목록
      console.log(result.data);
      totalItems.value = result.data.totalItems; // 전체 사용자 수
      displayedTotal.value = result.data.length;
      // Pre-cache all user images
      users.value.forEach((user) => {
        if (user.pimage) {
          cacheUserImage("profiles", user.pimage);
        }
      });
    }
  } catch (error) {
    console.error("데이터 로딩 중 오류:", error);
    showModalMessage("회원 목록을 불러오는데 실패했습니다.");
  } finally {
    loading.value = false;
  }
};

// 검색 함수
const handleSearch = async () => {
  const requestUserId = localStorage.getItem("userId");
  if (!requestUserId) return;
  const isAll = ref(false);
  loading.value = true;
  try {
    let url;

    if (searchType.value === "all") {
      if (!searchKeyword.value.trim()) {
        // 전체 선택에서 검색어가 없을 때는 기본 페이징된 목록 호출
        isAll.value = true;
        url = `http://localhost:8080/uhpooh/api/user/list?page=${currentPage.value}&size=${pageSize.value}&requestUserId=${requestUserId}`;
      } else {
        // 전체 선택에서 검색어가 있을 때는 통합 검색 API 호출
        isAll.value = false;
        url = `http://localhost:8080/uhpooh/api/user/search/integrated?keyword=${searchKeyword.value}&page=${currentPage.value}&size=${pageSize.value}&requestUserId=${requestUserId}`;
      }
    } else if (searchType.value === "admin") {
      if (!searchKeyword.value.trim()) {
        isAll.value = true;
        url = `http://localhost:8080/uhpooh/api/user/list?page=${currentPage.value}&size=${pageSize.value}&requestUserId=${requestUserId}`;
      } else {
        isAll.value = false;
        url = `http://localhost:8080/uhpooh/api/user/search/admin?isAdmin=${
          searchKeyword.value === "관리자" ? "1" : "0"
        }&page=${currentPage.value}&size=${
          pageSize.value
        }&requestUserId=${requestUserId}`;
      }
    } else {
      if (!searchKeyword.value.trim()) {
        isAll.value = true;
        url = `http://localhost:8080/uhpooh/api/user/list?page=${currentPage.value}&size=${pageSize.value}&requestUserId=${requestUserId}`;
      } else {
        isAll.value = false;
        url = `http://localhost:8080/uhpooh/api/user/search/${searchType.value}?keyword=${searchKeyword.value}&page=${currentPage.value}&size=${pageSize.value}&requestUserId=${requestUserId}`;
      }
    }

    const response = await fetch(url);
    if (!response.ok) {
      const errorText = await response.text();
      console.error("Search error response:", errorText);
      throw new Error("검색에 실패했습니다.");
    }

    const result = await response.json();
    if (result.success) {
      // 모든 응답이 동일한 형식을 가진다고 가정

      if (isAll.value) {
        users.value = result.data;
      } else {
        users.value = result.data.users;
      }
      totalItems.value = result.data.totalItems;
      if (isAll.value) {
        displayedTotal.value = result.data.length;
      } else {
        displayedTotal.value = result.data.totalItems;
      }
      // Pre-cache all user images
      users.value.forEach((user) => {
        if (user.pimage) {
          cacheUserImage("profiles", user.pimage);
        }
      });
    } else {
      throw new Error(result.message || "데이터 형식이 올바르지 않습니다.");
    }
  } catch (error) {
    console.error("검색 중 오류:", error);
    alert("검색에 실패했습니다.");
  } finally {
    loading.value = false;
  }
};

// 검색 타입 변경 시
const handleSearchTypeChange = () => {
  searchKeyword.value = "";
  currentPage.value = 1;
  loadAllUsers();
};

// 페이지 변경 시
const handlePageChange = async (page) => {
  currentPage.value = page;
  if (searchType.value === "all" && !searchKeyword.value.trim()) {
    await loadAllUsers();
  } else {
    await handleSearch();
  }
};

// 날짜 포맷 함수
const formatDate = (dateString) => {
  if (!dateString) return "날짜 정보 없음";
  return dateString.replace("T", " ");
};

// 초기 데이터 로드
onMounted(async () => {
  const userId = localStorage.getItem("userId");
  const isAdmin = localStorage.getItem("isAdmin");

  if (!userId || isAdmin !== "1") {
    alert("접근 권한이 없습니다.");
    router.push("/");
    return;
  }

  await loadAllUsers();
});
</script>

<template>
  <div class="p-4 mx-auto max-w-6xl">
    <div class="bg-white rounded-2xl shadow-sm">
      <div class="p-6 border-b">
        <h1 class="text-2xl font-bold">전체 회원 관리</h1>
        <p class="mt-2 text-gray-600">
          총 {{ displayedTotal }}명의 회원이 검색되었습니다.
        </p>

        <!-- 검색 영역 -->
        <div class="flex gap-4 mt-4">
          <select
            v-model="searchType"
            @change="handleSearchTypeChange"
            class="px-3 py-2 rounded-lg border focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option
              v-for="type in searchTypes"
              :key="type.value"
              :value="type.value"
            >
              {{ type.label }}
            </option>
          </select>

          <div class="relative flex-1">
            <template v-if="searchType === 'admin'">
              <select
                v-model="searchKeyword"
                class="px-4 py-2 w-full rounded-lg border focus:outline-none focus:ring-2 focus:ring-blue-500"
                @change="handleSearch"
              >
                <option value="">전체</option>
                <option value="관리자">관리자</option>
                <option value="일반회원">일반회원</option>
              </select>
            </template>
            <template v-else>
              <input
                v-model="searchKeyword"
                type="text"
                :placeholder="`${
                  searchTypes.find((t) => t.value === searchType)?.label
                }으로 검색`"
                class="px-4 py-2 w-full rounded-lg border focus:outline-none focus:ring-2 focus:ring-blue-500"
                @keyup.enter="handleSearch"
              />
            </template>
          </div>

          <button
            @click="handleSearch"
            class="px-6 py-2 text-white bg-blue-500 rounded-lg hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            검색
          </button>
        </div>
      </div>

      <!-- 로딩 상태 -->
      <div v-if="loading" class="p-6 text-center">
        <div class="text-gray-600">데이터를 불러오는 중...</div>
      </div>

      <!-- 회원 목록 -->
      <div v-else class="overflow-x-auto">
        <table class="w-full min-w-max">
          <thead>
            <tr class="bg-gray-50">
              <th class="p-4 text-sm font-medium text-left text-gray-600">
                프로필
              </th>
              <th class="p-4 text-sm font-medium text-left text-gray-600">
                ID
              </th>
              <th class="p-4 text-sm font-medium text-left text-gray-600">
                이름
              </th>
              <th class="p-4 text-sm font-medium text-left text-gray-600">
                이메일
              </th>
              <th class="p-4 text-sm font-medium text-left text-gray-600">
                주소
              </th>
              <th class="p-4 text-sm font-medium text-left text-gray-600">
                권한
              </th>
              <th class="p-4 text-sm font-medium text-left text-gray-600">
                로그인 상태
              </th>
              <th class="p-4 text-sm font-medium text-left text-gray-600">
                등록일
              </th>
            </tr>
          </thead>

          <tbody class="divide-y">
            <tr
              v-for="user in users"
              :key="user.userId"
              class="hover:bg-gray-50"
            >
              <!-- 프로필 이미지 -->
              <td class="p-4">
                <img
                  v-if="user.pimage !== null"
                  :src="
                    userImages.get(user.pimage) ||
                    cacheUserImage('profiles', user.pimage)
                  "
                  alt="Profile"
                  class="object-cover w-12 h-12 rounded-full border border-gray-200"
                />
                <User v-else class="w-12 h-12 text-gray-400" />
              </td>

              <!-- 유저 ID -->
              <td class="p-4 font-medium">{{ user.userId }}</td>

              <!-- 이름 -->
              <td class="p-4 font-medium">{{ user.userName }}</td>

              <!-- 이메일 -->
              <td class="p-4 text-gray-600">{{ user.userEmail }}</td>

              <!-- 주소 -->
              <td class="p-4 text-gray-600">
                {{ user.userAddress || "주소 미설정" }}
              </td>

              <!-- Admin 여부 -->
              <td class="p-4">
                <span
                  :class="
                    user.isAdmin === 1
                      ? 'bg-red-100 text-red-600'
                      : 'bg-blue-100 text-blue-600'
                  "
                  class="px-2 py-1 text-sm rounded-full"
                >
                  {{ user.isAdmin === 1 ? "Admin" : "Member" }}
                </span>
              </td>

              <!-- 로그인 상태 -->
              <td class="p-4">
                <span
                  :class="
                    user.isLogin === 1
                      ? 'bg-green-100 text-green-600'
                      : 'bg-gray-100 text-gray-600'
                  "
                  class="px-2 py-1 text-sm rounded-full"
                >
                  {{ user.isLogin === 1 ? "로그인" : "로그아웃" }}
                </span>
              </td>

              <!-- 등록일 -->
              <td class="p-4 text-gray-600">
                {{ formatDate(user.regTime) }}
              </td>
            </tr>
          </tbody>
        </table>

        <!-- 페이지네이션 -->
        <div v-if="totalItems > pageSize" class="flex gap-2 justify-center p-4">
          <button
            v-for="page in Math.ceil(totalItems / pageSize)"
            :key="page"
            @click="handlePageChange(page)"
            :class="[
              'px-4 py-2 rounded-lg',
              currentPage === page
                ? 'bg-blue-500 text-white'
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200',
            ]"
          >
            {{ page }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.min-w-max {
  min-width: max-content;
}

/* 테이블 반응형 스타일 */
@media (max-width: 1024px) {
  .max-w-6xl {
    max-width: 100%;
  }
}

/* 스크롤바 스타일링 */
.overflow-x-auto {
  scrollbar-width: thin;
  scrollbar-color: #e2e8f0 #ffffff;
}

.overflow-x-auto::-webkit-scrollbar {
  height: 8px;
}

.overflow-x-auto::-webkit-scrollbar-track {
  background: #ffffff;
}

.overflow-x-auto::-webkit-scrollbar-thumb {
  background-color: #e2e8f0;
  border-radius: 4px;
}

/* 테이블 호버 효과 부드럽게 */
.hover\:bg-gray-50 {
  transition: background-color 0.2s ease;
}

/* 버튼 호버 효과 부드럽게 */
button {
  transition: all 0.2s ease;
}
</style>
