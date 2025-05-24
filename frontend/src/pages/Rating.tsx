import { useEffect, useState } from "react";
import { Link, useSearchParams } from "react-router-dom";
import useAuthStore from "../store/useAuthStore";
import { useNavigate } from "react-router-dom";
import Sidebar from "../components/Sidebar";
import Header from "../components/Header";
import Dropdown from "../components/Dropdown";
import { DEPARTMENTS, LECTURE_TYPES } from "../config";
import { useQuery } from "@tanstack/react-query";
import { ApiResponse, get } from "../lib/api";
import { Lectures } from "../types/LectureInfo";

const Rating = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { user } = useAuthStore();

  const query = searchParams.get("q");
  const department = searchParams.get("department");
  const courseType = searchParams.get("type");

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
        <Body query={query} department={department} courseType={courseType} />
      </div>
    </div>
  );
};

interface BodyProps {
  query?: string | null;
  department?: string | null;
  courseType?: string | null;
}

const Body = (props: BodyProps) => {
  const navigate = useNavigate();

  const [expanded, setExpanded] = useState<boolean>(false);
  const [query, setQuery] = useState<string>(props.query || "");
  const [department, setDepartment] = useState<string>(
    props.department || "전체"
  );
  const [courseType, setCourseType] = useState<string>(
    props.courseType || "전체"
  );
  const [searchResults, setSearchResults] = useState<Lectures | null>(null);

  const { user } = useAuthStore();

  const { data } = useQuery<ApiResponse<Lectures>>({
    queryKey: ["query_lectures", department, query, courseType],
    queryFn: () => {
      const header = user?.token;
      let url = `/v1/lectures?query=${query}&size=10&courseType=${courseType}&department=${department}`;
      return get<Lectures>(url, {
        headers: {
          Authorization: `Bearer ${header}`,
        },
      });
    },
    enabled: !!query || !!department || !!courseType,
    retry: false,
  });

  useEffect(() => {
    const hasQuery =
      props.query || props.department || props.courseType ? true : false;
    setExpanded(hasQuery);
    // 실제 API 호출 시 주석 해제
  }, []); // 절대 비워

  useEffect(() => {
    if (data) {
      if (data.success) {
        setSearchResults(data.data!);
      } else {
        alert("강의 검색에 실패했습니다.");
        setSearchResults(null);
      }
    } else {
      setSearchResults(null);
    }
  }, [data]);

  useEffect(() => {
    const hasQuery =
      props.query || props.department || props.courseType ? true : false;
    setExpanded(hasQuery);
    // 실제 API 호출 시 주석 해제
  }, [props.courseType, props.department, props.query]); // 절대 비워

  const onExpand = () => {
    setExpanded(!expanded);
  };

  const onSearch = () => {
    if (!query && !department && !courseType) {
      alert("검색어를 입력해주세요.");
      return;
    }
    navigate(
      `/rating?q=${encodeURIComponent(query)}&department=${encodeURIComponent(
        department
      )}&type=${encodeURIComponent(courseType)}`
    );
  };

  return (
    <div>
      <div
        className={`w-full bg-white rounded-lg px-[45px] py-[25px] mb-[70px] shadow-md relative ${
          expanded ? "h-[350px]" : "h-[236px]"
        } trasition duration-fast ease-in-out`}
      >
        <div className="text-xl font-bold">강의 검색</div>
        <div className="flex justify-center w-full mt-[20px]">
          <div className="relative w-[700px]">
            <input
              type="text"
              placeholder="과목명, 교수명으로 입력해보세요."
              className="w-full px-[30px] py-[15px] mt-[10px] text-md rounded-full border-[1px] border-gray-600 focus:outline-none focus:border-primary duration pr-[60px]"
              value={query || ""}
              onChange={(e) => setQuery(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === "Enter") {
                  onSearch();
                }
              }}
            />
            <img
              src="/icons/circle-search.svg"
              alt="search"
              className="w-[37px] h-[37px] absolute top-[20px] right-[12px] cursor-pointer"
              onClick={onSearch}
            />
          </div>
        </div>
        <div
          className={`transition-all duration-fast ${
            expanded
              ? "max-h-[200px] opacity-100 mt-[40px]"
              : "max-h-0 opacity-0 mt-0"
          }`}
          style={{ pointerEvents: expanded ? "auto" : "none" }}
        >
          <div className="text-lg font-medium text-gray-400">검색 설정</div>
          <div className="flex w-full gap-[10px] mt-[10px]">
            <Dropdown
              options={DEPARTMENTS}
              placeholder="학과"
              onChange={(value) => setDepartment(value)}
              value={department || "전체"}
            />
            <Dropdown
              options={LECTURE_TYPES}
              placeholder="교과 구분"
              onChange={(value) => setCourseType(value)}
              value={courseType || "전체"}
            />
          </div>
        </div>
        <div
          className={`w-[111px] h-[111px] rounded-full absolute bg-primary left-1/2 -translate-x-1/2 bottom-0 translate-y-1/2 cursor-pointer shadow-md flex items-center justify-center pt-[10px] ${
            expanded ? "-rotate-180 bg-gray" : "bg-primary"
          } transition duration`}
          onClick={onExpand}
        >
          <img
            src="/icons/arrow-down.svg"
            alt="expand"
            className="w-[87px] h-[87px] mx-auto my-auto"
          />
        </div>
      </div>
      {searchResults === null ? (
        <div className="text-xl font-bold text-center">
          "강의를 검색해주세요!"
        </div>
      ) : (
        <Result lectures={searchResults} />
      )}
    </div>
  );
};

const Result = ({ lectures }: { lectures: Lectures }) => {
  return (
    <div className="w-full mb-[10px]">
      <div className="text-xl font-bold">
        강의 검색 결과({lectures.totalElements})
      </div>
      <div className="flex flex-col gap-[34px] mt-[18px]">
        {lectures.content.map((lecture) => (
          <Link to={`/rating/detail?lectureId=${lecture.id}`} key={lecture.id}>
            <div
              key={lecture.id}
              className="flex flex-col rounded-lg bg-white py-[13px] px-[24px] shadow-md gap-[10px] hover:scale-[101%] duration-fast"
            >
              <div className="flex w-full items-center gap-[6px]">
                <Tag tag={lecture.department} />
                <Tag tag={lecture.courseType} />
              </div>
              <div className="flex items-center justify-between gap-[100px]">
                <div>
                  <div className="flex-1 font-semibold text-md gap-[10px]">
                    {lecture.title.length > 20
                      ? lecture.title.slice(0, 20) + "..."
                      : lecture.title}
                  </div>
                  <div className="text-sm text-gray-600 font-regular">
                    {lecture.professorName} 교수님
                  </div>
                </div>
                <div className="flex-1 text-sm text-center text-gray-600 font-regular">
                  {lecture.bestComment.length > 100
                    ? lecture.bestComment.slice(0, 100) + "..."
                    : lecture.bestComment}
                </div>
                <div>
                  <Score score={lecture.averageScore} />
                  <div className="text-xs font-light text-gray-600 -mt-[3px]">
                    #{lecture.evaluationCount}개의 강의 평가
                  </div>
                </div>
              </div>
            </div>
          </Link>
        ))}
      </div>
    </div>
  );
};

const Tag = ({ tag }: { tag: string }) => {
  return (
    <div className="bg-primarylight rounded-sm text-primary text-xs px-[8px] py-[3px]">
      {tag}
    </div>
  );
};

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
    <div className="text-3xl font-extrabold text-right text-ajou-gold">
      {text}
    </div>
  );
};

export default Rating;
