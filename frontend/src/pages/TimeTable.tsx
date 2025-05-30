import { useNavigate } from "react-router-dom";
import Header from "../components/Header";
import Sidebar from "../components/Sidebar";
import useAuthStore from "../store/useAuthStore";
import { useEffect } from "react";

const TimeTable = () => {
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
        <img src="/images/p1.png" alt="Time Table" className="-ml-[10px]" />
      </div>
    </div>
  );
};

export default TimeTable;
