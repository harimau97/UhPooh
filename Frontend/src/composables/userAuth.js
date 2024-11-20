import axios from "axios";
import { ref } from "vue";
const userAuthenticated = ref(false);

const isAuthenticated = async () => {
  const token = localStorage.getItem("accessToken");
  if (!token) return false;

  try {
    // 서버로 토큰 검증 요청
    const response = await axios.get(
      "/api/user/validate",
      { userId: localStorage.getItem("userId") },
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );
    userAuthenticated.value = true; // 서버에서 유효성 확인 결과 반환
  } catch (error) {
    console.error("Token validation failed:", error);
    userAuthenticated.value = false;
    return false;
  }
};

const getUserInfo = async () => {
  if (userAuthenticated.value) {
    try {
      const response = await axios.get(
        "http://localhost:8080/uhpooh/api/user/" +
          localStorage.getItem("userId")
      );
      localStorage.setItem("userName", response.data.userName);
      localStorage.setItem("userId", response.data.userId);
      localStorage.setItem("userAddress", response.data.userAddress);
      localStorage.setItem("pImage", response.data.pImage);
    } catch (error) {
      console.log(error);
    }
  }
};

export { isAuthenticated, getUserInfo, userAuthenticated };
