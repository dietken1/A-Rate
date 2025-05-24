import { useNavigate, useSearchParams } from "react-router-dom";
import Header from "../components/Header";
import Sidebar from "../components/Sidebar";
import { useEffect, useState } from "react";
import useAuthStore from "../store/useAuthStore";
import { useQuery } from "@tanstack/react-query";
import { ApiResponse, get } from "../lib/api";
import LectureDetailInfo, {
  LectureDetailAverageScores,
} from "../types/LectureDetailInfo";
import { ResponsiveRadar } from "@nivo/radar";

const RatingDetail = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const lectureId = searchParams.get("lectureId");
  const { user } = useAuthStore();

  const [lectureDetail, setLectureDetail] = useState<LectureDetailInfo | null>(
    null
  );

  const { data } = useQuery<ApiResponse<LectureDetailInfo>>({
    queryKey: ["lecture_detail", lectureId],
    queryFn: () => get<LectureDetailInfo>(`/v1/lectures/${lectureId}`),
    enabled: !!lectureId, // lectureId가 있을 때만 실행
    retry: false,
  });

  useEffect(() => {
    if (!user) {
      alert("로그인 후 이용 가능합니다.");
      navigate("/");
    }
  }, [user, navigate]);

  useEffect(() => {
    if (!lectureId) {
      alert("잘못된 접근입니다.");
      window.history.back();
    }
  });

  useEffect(() => {
    if (data && data.success && data.data) {
      setLectureDetail(data.data);
    }
  }, [data]);

  return (
    <div className="flex w-full h-screen bg">
      <Sidebar />
      <div className="flex-1 px-8 py-5 overflow-auto">
        <Header />
        <div className="flex w-full gap-[20px]">
          <div className="flex flex-col flex-[2] gap-[20px]">
            <Container>
              <div className="text-2xl font-bold">{lectureDetail?.title}</div>
              <div className="text-mmd font-medium mt-[10px]">
                {lectureDetail?.professor.name} 교수님
              </div>
              <div className="text-sm text-gray-600 mt-[10px]">
                #{lectureDetail?.department}
              </div>
              <hr className="my-[15px]" />
              <div className="text-xl font-bold">강의 평가 요약</div>
              <Graph data={lectureDetail?.averageScores} />
            </Container>
            <Container>123</Container>
          </div>
          <div className="flex flex-col flex-1 gap-[20px]">
            <Container>
              <div className="text-xl font-bold">과목 정보</div>
              <hr className="my-[15px]" />
            </Container>
            <Container>123</Container>
          </div>
        </div>
      </div>
    </div>
  );
};

const Graph = ({ data }: { data?: LectureDetailAverageScores }) => {
  if (!data) {
    return <div></div>;
  }

  const graphData = [
    { label: "전달력", value: data.delivery },
    { label: "교수님의 전문성", value: data.expertise },
    { label: "성적 반영비", value: data.generosity },
    { label: "내용의 실용성", value: data.effectiveness },
    { label: "인품", value: data.character },
    { label: "수업 난이도", value: data.difficulty },
  ];

  return (
    <ResponsiveRadar
      data={graphData}
      keys={["value"]}
      indexBy="label"
      maxValue={4.5}
      gridLevels={5}
      borderColor={{ from: "#003399" }}
    />
  );
};

const Container = ({
  children,
  className,
}: {
  children: React.ReactNode;
  className?: string;
}) => (
  <div className={`rounded-lg bg-white p-[20px] ${className}`}>{children}</div>
);

export default RatingDetail;
