import { useEffect } from "react";
import Header from "../components/Header";
import Sidebar from "../components/Sidebar";
import useAuthStore from "../store/useAuthStore";
import { useNavigate } from "react-router-dom";

const Activity = () => {
  const { user } = useAuthStore();
  const navigate = useNavigate();

  useEffect(() => {
    if (!user) {
      alert("로그인 후 이용 가능합니다.");
      navigate("/");
    }
  }, [user, navigate]);

  return (
    <div className="flex w-full h-screen bg">
      <Sidebar />
      <div className="flex-1 px-8 py-5 overflow-auto">
        <Header />
        대외활동
      </div>
    </div>
  );
};

export default Activity;
