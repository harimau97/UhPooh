import axios from "axios";
import { ref } from "vue";
const userAuthenticated = ref(false);

const isAuthenticated = async () => {
  console.log("start validation!!!");
  const token = localStorage.getItem("userToken");
  if (!token) return false;

  try {
    // 서버로 토큰 검증 요청

    const response = await axios.get("/api/user/validate", {
      params: {
        userId: localStorage.getItem("userId"),
        accessToken: token,
      },
    });
    userAuthenticated.value = true; // 서버에서 유효성 확인 결과 반환
    console.log("검증 성공");
  } catch (error) {
    console.error("Token validation failed:", error);
    userAuthenticated.value = false;
    return false;
  }
};

const getUserInfo = async () => {
  console.log("Fetch user data!!!");
  console.log(userAuthenticated.value);
  console.log(localStorage.getItem("userId"));
  const userId = localStorage.getItem("userId");

  if (!userId) {
    console.error("No userId found in localStorage!");
    return;
  }

  try {
    const response = await axios.get(
      `http://localhost:8080/uhpooh/api/user/${userId}`,
      {
        params: {
          requestUserId: userId,
        },
      }
    );
    console.log(response.data);
    localStorage.setItem("userId", response.data.data.userId);
    localStorage.setItem("userName", response.data.data.userName);
    localStorage.setItem("userAddress", response.data.data.userAddress);
    localStorage.setItem("pImage", response.data.data.pimage);
    localStorage.setItem("isAdmin", response.data.data.isAdmin);
    localStorage.setItem("userEmail", response.data.data.userEmail);
    localStorage.setItem("provider", response.data.data.provider);
  } catch (error) {
    console.log(error);
  }
};

const logout = async () => {
  const userId = localStorage.getItem("userId");
  const userToken = localStorage.getItem("userToken");
  const provider = localStorage.getItem("provider");

  try {
    console.log("로그아웃 시작");
    const response = await axios.post(
      `http://localhost:8080/uhpooh/api/user/logout/${userId}`,
      {
        userId: userId,
        provider: provider || "local"
      },
      {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${userToken}`
        }
      }
    );

    if (response.status === 200) {
      // Clear all localStorage items
      localStorage.clear();
      userAuthenticated.value = false;
      // Redirect to home
      window.location.href = "/";
    } else {
      throw new Error("로그아웃 요청이 실패했습니다.");
    }
  } catch (error) {
    console.error("로그아웃 실패:", error);
    // Even if the API call fails, clear local state
    localStorage.clear();
    userAuthenticated.value = false;
    window.location.href = "/";
  }
};

export { isAuthenticated, getUserInfo, userAuthenticated, logout };
