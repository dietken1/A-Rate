import ReactTimeAgo from "react-time-ago";
import { GET_DEFAULT_PROFILE_IMAGE } from "../../config";

interface Comment {
  profile?: string;
  semester?: string;
  lecture_name: string;
  professor_name: string;
  content: string;
  rating: number;
  writer: string;
  created_at: Date;
  is_department_specific?: boolean;
}

const CommentCard = (comment: Comment) => {
  return (
    <div className="flex flex-col items-start w-full bg-card rounded-md p-[15px] gap-[10px] cursor-pointer">
      {/* <div className="flex w-full items-center gap-[6px]">
        <Tag tag={`${comment.semester}학기`} />
        {comment.is_department_specific && <Tag tag="전공" />}
      </div>*/}
      <div className="w-full">
        <div className="flex w-full items-center gap-[10px]">
          <div className="flex-1 font-semibold text-md">
            {comment.lecture_name.length > 10
              ? comment.lecture_name.slice(0, 10) + "..."
              : comment.lecture_name}
          </div>
          <div className="text-sm font-regular">
            {comment.professor_name}
            <span className="text-xs"> 교수님</span>
          </div>
        </div>
        <div className="text-xs text-gray-600 font-regular">
          {comment.content.length > 30
            ? comment.content.slice(0, 30) + "..."
            : comment.content}
        </div>
      </div>
      <div className="flex items-center justify-between w-full">
        <div className="flex items-center gap-[6px]">
          <img
            src={comment.profile ?? GET_DEFAULT_PROFILE_IMAGE()}
            alt={comment.writer}
            className="w-[18px] h-[18px] rounded-full object-cover"
          />

          <div className="text-xs font-bold">{comment.writer}</div>
          <div className="ml-[12px] text-xs font-semilight">
            <ReactTimeAgo date={comment.created_at} />
          </div>
        </div>
        <div className="flex items-center gap-[6px]">
          <img
            src="/icons/stars.svg"
            alt="star"
            className="w-[18px] h-[18px]"
          />
          <div className="text-xs font-semilight">
            {comment.rating.toFixed(1)}/5.0
          </div>
        </div>
      </div>
    </div>
  );
};

const Tag = ({ tag }: { tag: string }) => {
  return (
    <div className="bg-tag rounded-sm text-system-success text-xs px-[8px] py-[3px]">
      {tag}
    </div>
  );
};

export default CommentCard;
