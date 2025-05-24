import { Lectures } from "../types/LectureInfo";
import { Link } from "react-router-dom";

const Result = ({ lectures }: { lectures: Lectures }) => {
  return (
    <div className="w-full mb-[10px]">
      <div className="text-xl font-bold">
        강의 검색 결과({lectures.totalElements})
      </div>
      <div className="flex flex-col gap-[34px] mt-[18px]">
        {lectures.content.map((lecture) => (
          <Link to="/raiting/detail" state={{ lecture }} key={lecture.id}>
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
  if (score > 4.0) text = "A+";
  else if (score > 3.5) text = "A";
  else if (score > 3.0) text = "B+";
  else if (score > 2.5) text = "B";
  else if (score > 2.0) text = "C+";
  else if (score > 1.5) text = "C";
  else if (score > 1.0) text = "D+";
  else if (score > 0.5) text = "D";
  else text = "F";
  return (
    <div className="text-3xl font-extrabold text-right text-ajou-gold">
      {text}
    </div>
  );
};

export default Result;
