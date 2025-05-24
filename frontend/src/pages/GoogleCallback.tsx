import { useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import { get, ApiResponse } from "../lib/api";
import UserInfo from "../types/UserInfo";
import useAuthStore from "../store/useAuthStore";

const GoogleCallback = () => {
  const [searchParams] = useSearchParams();
  const { authorize } = useAuthStore();
  const navigate = useNavigate();

  const code = searchParams.get("code");
  const state = searchParams.get("state");

  const { data, isLoading, error } = useQuery<ApiResponse<UserInfo>>({
    queryKey: ["login", code, state],
    queryFn: () =>
      get<UserInfo>(`/login/oauth2/code/google?code=${code}&state=${state}`),
    enabled: !!code && !!state,
    retry: false,
  });

  useEffect(() => {
    if (!code || !state) {
      alert("구글 인증 코드가 없습니다. 다시 시도해주세요.");
      navigate("/");
    } else if (data) {
      if (data.success) {
        authorize(data.data);
        navigate("/");
      } else {
        alert("로그인 실패: " + data.error);
        navigate("/auth?error=oauth");
      }
    }
  }, [code, state, data, navigate, authorize]);

  if (isLoading) return <div>구글 인증 처리 중...</div>;
  if (error) return <div>에러가 발생했습니다.</div>;
  return null;
};

export default GoogleCallback;
