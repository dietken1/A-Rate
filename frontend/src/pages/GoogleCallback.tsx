import { useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import useAuthStore from "../store/useAuthStore";

const GoogleCallback = () => {
  const loc = useLocation();
  const { authorize } = useAuthStore();
  const navigate = useNavigate();

  useEffect(() => {
    const params = new URLSearchParams(loc.search);
    const token = params.get("token");
    const refreshToken = params.get("refreshToken");
    const error = params.get("error");
    
    if (error) {
      alert("로그인 실패: " + error);
      navigate("/auth?error=oauth");
      return;
    }
    
    if (token) {
      // 백엔드에서 전달받은 토큰들을 저장
      localStorage.setItem("accessToken", token);
      if (refreshToken) {
        localStorage.setItem("refreshToken", refreshToken);
      }
      
      // 사용자 정보를 조회하여 스토어에 저장
      fetch("/api/auth/me", {
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      .then(res => res.json())
      .then(userInfo => {
        authorize(userInfo);
        navigate("/");
      })
      .catch(() => {
        alert("사용자 정보 조회 실패");
        navigate("/auth?error=userinfo");
      });
    } else {
      alert("아주대학교 이메일로만 로그인 가능합니다. 다시 시도해주세요.");
      navigate("/auth");
    }
  }, [loc.search, navigate, authorize]);

  return <div>구글 인증 처리 중...</div>;
};

export default GoogleCallback;
