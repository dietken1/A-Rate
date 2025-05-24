import Header from "../components/Header";
import Sidebar from "../components/Sidebar";
import CommentCard from "../components/home/CommentCard";
import RatingCard from "../components/home/RatingCard";
import { Link } from "react-router-dom";
import useAuthStore from "../store/useAuthStore";

const Home = () => {
  const { user } = useAuthStore();

  return (
    <div className="flex h-screen w-full h-full bg">
      <Sidebar />
      <div className="flex-1 overflow-auto py-5 px-8">
        <Header />
        <div className="font-bold text-xl">
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

const Body = () => (
  <div className="flex w-full gap-[20px]">
    <div className="flex flex-col flex-[2] gap-[20px]">
      <TimeTable />
      <RecentRatings />
    </div>
    <div className="flex flex-col flex-1 gap-[20px]">
      <Search />
      <SideRating1 />
      <SideRating2 />
    </div>
  </div>
);

const TimeTable = () => (
  <Container className="flex flex-col gap-[16px] items-center justify-center h-[200px]">
    <img
      src="/icons/book-bookmark-colored.svg"
      alt="chart"
      className="w-[50px] h-[50px]"
    />
    <div className="text-sm text-gray-600">시간표를 등록해주세요.</div>
    <button className="text-sm rounded-sm bg-white text-primary border-[1px] border-primary px-[12px] py-[6px] hover:bg-gray duration-fast">
      시간표 등록하기
    </button>
  </Container>
);

const RecentRatings = () => {
  const mockingCardData = {
    profile: "/icons/google.svg",
    semester: "2023-1",
    lecture_name: "프로그래밍 기초",
    professor_name: "홍길동",
    content:
      "이 강의는 정말 유익했습니다. 교수님도 친절하시고, 수업 내용도 알차요.",
    rating: 4.5,
    writer: "user123",
    created_at: new Date("2023-10-01"),
    is_department_specific: true,
  };

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
        <CommentCard comment={mockingCardData} />
        <CommentCard comment={mockingCardData} />
        <CommentCard comment={mockingCardData} />
        <CommentCard comment={mockingCardData} />
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

const Search = () => (
  <Container className="flex flex-col gap-[10px] h-[200px]">
    <div className="flex items-center gap-2">
      <div className="text-lg font-medium">과목 검색하기</div>
      <img
        src="/icons/magnifying-glass-bold.svg"
        alt="search"
        className="w-[22px] h-[22px]"
      />
    </div>
    <input
      type="text"
      placeholder="과목명을 입력해주세요."
      className="w-full h-[40px] mt-[10px] text-sm rounded-full border-[1px] border-gray-300 px-[16px] focus:outline-none focus:border-primary duration-fast"
    />
    <div className="w-full"></div>
  </Container>
);

const SideRating1 = () => {
  const mockingCardData = {
    profile: "/icons/google.svg",
    lecture_name: "프로그래밍 기초",
    professor_name: "홍길동",
    rating: 4.5,
  };

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
        <RatingCard rating={mockingCardData} />
        <RatingCard rating={mockingCardData} />
        <RatingCard rating={mockingCardData} />
        <RatingCard rating={mockingCardData} />
      </div>
    </Container>
  );
};

const SideRating2 = () => {
  const mockingCardData = {
    profile: "/icons/google.svg",
    lecture_name: "프로그래밍 기초",
    professor_name: "홍길동",
    rating: 1.5,
  };

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
        <RatingCard rating={mockingCardData} />
        <RatingCard rating={mockingCardData} />
        <RatingCard rating={mockingCardData} />
        <RatingCard rating={mockingCardData} />
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
