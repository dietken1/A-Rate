import { useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import { get, ApiResponse } from "../lib/api";
import { User } from "../types/UserInfo";
import useAuthStore from "../store/useAuthStore";

const GoogleCallback = () => {
  const loc = useLocation();
  const { authorize } = useAuthStore();
  const navigate = useNavigate();

  const { data, isLoading, error } = useQuery<ApiResponse<User>>({
    queryKey: ["login", loc.search],
    queryFn: () => {
      const params = new URLSearchParams(loc.search);
      const token = params.get("token");
      return get<User>("/auth/me", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
    },
    enabled: !!loc.search,
    retry: false,
  });

  useEffect(() => {
    const params = new URLSearchParams(loc.search);
    const token = params.get("token");

    if (!loc.search) {
      alert("아주대학교 이메일로만 로그인 가능합니다. 다시 시도해주세요.");
      navigate("/");
    } else if (data) {
      if (data.success) {
        authorize({
          token: token ?? "",
          user: data.data,
        });
        navigate("/");
      } else {
        alert("로그인 실패: " + data.error);
        navigate("/auth?error=oauth");
      }
    }
  }, [loc.search, data, navigate, authorize]);

  if (isLoading) return <div>구글 인증 처리 중...</div>;
  if (error) return <div>에러가 발생했습니다.</div>;
  return null;
};

export default GoogleCallback;
