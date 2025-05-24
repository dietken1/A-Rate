import { useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { post } from "../lib/api";

const GoogleCallback = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  useEffect(() => {
    const code = searchParams.get("code");
    if (code) {
      post("/api/auth/google/callback", { code })
        .then((res) => {
          const token = (res as { token: string }).token;
          localStorage.setItem("token", token);
          navigate("/");
        })
        .catch(() => {
          navigate("/auth?error=oauth");
        });
    } else {
      navigate("/auth?error=missing_code");
    }
  }, [searchParams, navigate]);

  return <div>구글 인증 처리 중...</div>;
};

export default GoogleCallback;
