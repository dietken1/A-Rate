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
import RatingModal from "../components/rating-detail/RatingModal";
import { GET_DEFAULT_PROFILE_IMAGE } from "../config";

const RatingDetail = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const lectureId = searchParams.get("lectureId");
  const { user } = useAuthStore();

  const [lectureDetail, setLectureDetail] = useState<LectureDetailInfo | null>(
    null
  );
  const [modalOpen, setModalOpen] = useState<boolean>(false);

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

  // averageScores의 평균값을 구하는 함수
  const getAverage = (scores: LectureDetailAverageScores) => {
    const values = Object.values(scores);
    const sum = values.reduce((acc, cur) => acc + cur, 0);
    return sum / values.length;
  };

  const assignAmount = (a: "NONE" | "FEW" | "NORMAL" | "MANY") => {
    if (a === "NONE") return "없음";
    if (a === "FEW") return "적음";
    if (a === "NORMAL") return "보통";
    if (a === "MANY") return "많음";
  };

  const assignDifficulty = (a: "EASY" | "NORMAL" | "HARD") => {
    if (a === "EASY") return "쉬움";
    if (a === "NORMAL") return "보통";
    if (a === "HARD") return "어려움";
  };

  return (
    <div className="flex w-full h-screen bg">
      <Sidebar />
      <div className="flex-1 px-8 py-5 overflow-auto">
        <Header />
        <div className="flex w-full gap-[20px]">
          <div className="flex flex-col flex-[2] gap-[20px]">
            <Container>
              <div className="flex items-center justify-between">
                <div className="text-2xl font-bold">{lectureDetail?.title}</div>
                <button
                  className="bg-primary text-white py-[7px] px-[30px] rounded-md font-semibold"
                  onClick={() => setModalOpen(true)}
                >
                  리뷰 쓰기
                </button>
              </div>

              <div className="text-mmd font-medium mt-[10px]">
                {lectureDetail?.professor.name} 교수님
              </div>

              <div className="text-sm text-gray-600 mt-[5px]">
                #{lectureDetail?.department}
              </div>
              <hr className="my-[15px]" />
              <div className="flex gap-[20px] items-start">
                <div className="flex-1">
                  <div className="text-xl font-bold mb-[5px] text-center">
                    강의 평가 요약
                  </div>
                  <Container className="text-sm bg-grayscale-100 font-regular p-[24px]">
                    <ul>
                      <li>• 교수님께서 친절하고 재밌으시다.</li>
                      <li>• 배우는 내용이 실용적이다.</li>
                      <li>• 팀플이 많다.</li>
                    </ul>
                  </Container>
                </div>
                <div className="flex flex-col flex-1 w-full text-center">
                  <div className="text-xl font-bold mb-[5px] text-center">
                    강의 평점
                  </div>
                  <Score
                    score={
                      lectureDetail?.averageScores
                        ? getAverage(lectureDetail.averageScores)
                        : 0
                    }
                  />
                  <div className="text-sm font-semibold text-ajou-gold ">
                    평점:{" "}
                    {(lectureDetail?.averageScores
                      ? getAverage(lectureDetail.averageScores)
                      : 0
                    ).toFixed(2)}
                  </div>
                  <button
                    className="bg-primary text-white py-[5px] rounded-sm text-sm mt-[10px] font-semibold"
                    onClick={() => setModalOpen(true)}
                  >
                    인증된 리뷰 보기
                  </button>
                </div>
              </div>
              <div className="w-full h-[400px] mt-[20px]">
                <Graph data={lectureDetail?.averageScores} />
              </div>
            </Container>
            <div>
              <div className="text-xl font-bold my-[20px]">
                인증된 리뷰({lectureDetail?.evaluations?.length ?? 0}개)
              </div>
              <div className="flex flex-col gap-[20px]">
                {(lectureDetail?.evaluations ?? []).map((evaluation) => (
                  <Container key={evaluation.id}>
                    <div className="flex items-center justify-between">
                      <div className="flex gap-[10px] items-center justify-center">
                        <img
                          src={GET_DEFAULT_PROFILE_IMAGE()}
                          alt="Profile"
                          className="w-[52px] h-[52px]"
                        />
                        <div className="text-sm">
                          {evaluation.author.nickname}님
                        </div>
                      </div>
                      <MiniScore
                        score={
                          evaluation.scores ? getAverage(evaluation.scores) : 0
                        }
                      />
                    </div>
                    <div className="text-sm mt-[20px] font-light">
                      {evaluation.content ??
                        "강의평을 남기고 더 많은 강의평을 살펴보세요!"}
                    </div>
                    <div className="text-sm mt-[15px] font-light">
                      평점: 4.X
                    </div>
                  </Container>
                ))}
              </div>
            </div>
          </div>
          <div className="flex flex-col flex-1 gap-[20px]">
            <Container>
              <div className="text-xl font-bold">과목 정보</div>
              <hr className="my-[15px]" />
              <div className="flex flex-col gap-[30px] text-center">
                <div className="flex">
                  <div className="flex-1">과제의 양</div>
                  <div className="flex-1">
                    <b>많음</b>
                  </div>
                </div>
                <div className="flex">
                  <div className="flex-1">과제의 난이도</div>
                  <div className="flex-1">
                    <b>쉬움</b>
                  </div>
                </div>
                <div className="flex">
                  <div className="flex-1">시험 횟수</div>
                  <div className="flex-1">
                    <b>2번</b>
                  </div>
                </div>
                <div className="flex">
                  <div className="flex-1">팀플 유무</div>
                  <div className="flex-1">
                    <b>적음</b>
                  </div>
                </div>
              </div>
              <hr className="my-[15px]" />
              <div className="flex items-center justify-between">
                <div className="text-lg font-bold text-gray-600"> 자료집</div>
                <img
                  src="/images/more.png"
                  alt="더보기"
                  className="w-[67px] h-[20px]"
                />
              </div>
            </Container>
          </div>
        </div>
      </div>
      {lectureId && (
        <RatingModal
          open={modalOpen}
          setOpen={setModalOpen}
          lectureId={lectureId!}
        />
      )}
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
      margin={{ top: 70, right: 100, bottom: 70, left: 100 }}
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

const Score = ({ score }: { score: number }) => {
  let text;
  if (score > 4.5) text = "A+";
  else if (score > 4.0) text = "A";
  else if (score > 3.5) text = "B+";
  else if (score > 3.0) text = "B";
  else if (score > 2.5) text = "C+";
  else if (score > 2.0) text = "C";
  else if (score > 1.5) text = "D+";
  else if (score > 1.0) text = "D";
  else if (score > 0.5) text = "D-";
  else text = "F";
  return (
    <div className="text-5xl font-extrabold text-center text-ajou-gold">
      {text}
    </div>
  );
};

const MiniScore = ({ score }: { score: number }) => {
  let text;
  if (score > 4.5) text = "A+";
  else if (score > 4.0) text = "A";
  else if (score > 3.5) text = "B+";
  else if (score > 3.0) text = "B";
  else if (score > 2.5) text = "C+";
  else if (score > 2.0) text = "C";
  else if (score > 1.5) text = "D+";
  else if (score > 1.0) text = "D";
  else if (score > 0.5) text = "D-";
  else text = "F";
  return (
    <div className="text-xl font-extrabold text-center text-ajou-gold">
      {text}
    </div>
  );
};

export default RatingDetail;
