import Header from "../components/Header";
import Sidebar from "../components/Sidebar";
import CommentCard from "../components/home/CommentCard";
import RatingCard from "../components/home/RatingCard";
import { Link, useNavigate } from "react-router-dom";
import useAuthStore from "../store/useAuthStore";
import Dropdown from "../components/Dropdown";
import React, { useEffect } from "react";
import { DEPARTMENTS, LECTURE_TYPES } from "../config";
import { useQuery } from "@tanstack/react-query";
import { ApiResponse, get } from "../lib/api";
import SummaryInfo, {
  SummaryEvaluationInfo,
  SummaryLectureInfo,
} from "../types/SummaryInfo";

const Home = () => {
  const { user } = useAuthStore();

  return (
    <div className="flex w-full h-screen bg">
      <Sidebar />
      <div className="flex-1 px-8 py-5 overflow-auto">
        <Header />
        <div className="text-xl font-bold">
          {user
            ? `${user.user.name}님, 안녕하세요!`
            : "로그인하고 원하는 강의평을 마음껏 찾아보세요."}
        </div>
        <div className="text-sm text-gray-600 mt-1 mb-[30px]">
          A:Rate와 함께 A<sup>+</sup>를 향한 여정을 시작하세요.
        </div>
        <Body />
      </div>
    </div>
  );
};

const Body = () => {
  const [datas, setDatas] = React.useState<SummaryInfo | null>(null);
  const { data, refetch } = useQuery<ApiResponse<SummaryInfo>>({
    queryKey: ["summary"],
    queryFn: () => get<SummaryInfo>(`/v1/dashboard/summary`),
    enabled: false, // 최초에는 자동 실행 안 함
    retry: false,
  });

  // 최초 1회만 실행
  useEffect(() => {
    refetch();
  }, []);

  useEffect(() => {
    if (data && data.success && data.data) {
      setDatas(data.data);
    }
  }, [data]);

  return (
    <div className="flex w-full gap-[20px]">
      <div className="flex flex-col flex-[2] gap-[20px]">
        <TimeTable />
        <RecentRatings data={datas?.recentEvaluations} />
      </div>
      <div className="flex flex-col flex-1 gap-[20px]">
        <Search />
        <SideRating1 data={datas?.topRatedLectures} />
        <SideRating2 data={datas?.bottomRatedLectures} />
      </div>
    </div>
  );
};

const TimeTable = () => (
  <Container className="flex flex-col gap-[16px] items-center justify-center h-[200px]">
    <img
      src="/icons/book-bookmark-colored.svg"
      alt="chart"
      className="w-[50px] h-[50px]"
    />
    <div className="text-sm text-gray-600">시간표를 등록해주세요.</div>
    <Link to="/timetable">
      <button className="text-sm rounded-sm bg-white text-primary border-[1px] border-primary px-[12px] py-[6px] hover:bg-gray duration-fast">
        시간표 등록하기
      </button>
    </Link>
  </Container>
);

const RecentRatings = ({ data }: { data?: SummaryEvaluationInfo[] }) => {
  return (
    <Container className="h-[600px]">
      <div className="flex justify-between">
        <div className="text-lg font-medium">최근 등록된 강의평</div>
        <Link to="/rating">
          <img
            src="/images/more.png"
            alt="arrow-right"
            className="w-[67px] h-[20px]"
          />
        </Link>
      </div>
      <div className="grid grid-cols-2 gap-[10px] p-[10px]">
        {data?.map((evaluation) => (
          <CommentCard
            key={evaluation.id}
            lecture_name={evaluation.lecture.title}
            professor_name={evaluation.lecture.professor}
            content={evaluation.content}
            rating={3.0}
            writer={"익명"}
            created_at={evaluation.createdAt}
          />
        ))}
      </div>
      <div className="flex justify-between mt-[10px]">
        <div className="text-lg font-medium">최근 등록된 자료집</div>
        <Link to="/resource">
          <img
            src="/images/more.png"
            alt="arrow-right"
            className="w-[67px] h-[20px]"
          />
        </Link>
      </div>
    </Container>
  );
};

const Search = () => {
  const [departmentValue, setDepartmentValue] = React.useState("전체");
  const [typeValue, setTypeValue] = React.useState("전체");
  const [inputValue, setInputValue] = React.useState("");
  const navigate = useNavigate();

  const onSubmit = () => {
    const params = new URLSearchParams();
    if (inputValue) params.append("q", inputValue);
    if (departmentValue && departmentValue !== "전체")
      params.append("department", departmentValue);
    if (typeValue && typeValue !== "전체") params.append("type", typeValue);
    navigate(`/rating?${params.toString()}`);
  };

  return (
    <Container className="flex flex-col gap-[10px] h-[200px]">
      <div className="flex items-center gap-2">
        <div className="text-lg font-medium">과목 검색하기</div>
        <img
          src="/icons/magnifying-glass-bold.svg"
          alt="search"
          className="w-[22px] h-[22px] cursor-pointer"
          onClick={onSubmit}
        />
      </div>
      <input
        type="text"
        placeholder="과목명을 입력해주세요."
        className="w-full h-[40px] mt-[10px] text-sm rounded-full border-[1px] border-gray-300 px-[16px] focus:outline-none focus:border-primary duration-fast"
        value={inputValue}
        onChange={(e) => setInputValue(e.target.value)}
        onKeyDown={(e) => {
          if (e.key === "Enter") {
            onSubmit();
          }
        }}
      />
      <div className="flex w-full gap-[8px] mt-[5px]">
        <Dropdown
          options={DEPARTMENTS}
          placeholder="학과"
          onChange={(value) => setDepartmentValue(value)}
          value={departmentValue}
        />
        <Dropdown
          options={LECTURE_TYPES}
          placeholder="교과 구분"
          onChange={(value) => setTypeValue(value)}
          value={typeValue}
        />
      </div>
    </Container>
  );
};

const SideRating1 = ({ data }: { data?: SummaryLectureInfo[] }) => {
  return (
    <Container className="h-[320px]">
      <div className="flex justify-between">
        <div className="text-lg font-medium">평가가 좋은 강의평</div>
        <Link to="/rating">
          <img
            src="/images/more.png"
            alt="arrow-right"
            className="w-[67px] h-[20px]"
          />
        </Link>
      </div>
      <div className="flex flex-col w-full gap-[15px] p-[10px] mt-[10px]">
        {data?.map((lecture) => (
          <RatingCard
            key={lecture.id}
            lecture_name={lecture.title}
            professor_name={lecture.professorName}
            rating={lecture.averageScore}
          />
        ))}
      </div>
    </Container>
  );
};

const SideRating2 = ({ data }: { data?: SummaryLectureInfo[] }) => {
  return (
    <Container className="h-[320px]">
      <div className="flex justify-between">
        <div className="text-lg font-medium">평가가 아쉬운 강의평</div>
        <Link to="/rating">
          <img
            src="/images/more.png"
            alt="arrow-right"
            className="w-[67px] h-[20px]"
          />
        </Link>
      </div>
      <div className="flex flex-col w-full gap-[15px] p-[10px] mt-[10px]">
        {data?.map((lecture) => (
          <RatingCard
            key={lecture.id}
            lecture_name={lecture.title}
            professor_name={lecture.professorName}
            rating={lecture.averageScore}
          />
        ))}
      </div>
    </Container>
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

export default Home;
