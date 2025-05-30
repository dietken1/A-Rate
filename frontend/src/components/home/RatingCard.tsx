import { GET_DEFAULT_PROFILE_IMAGE } from "../../config";

interface Rating {
  profile?: string;
  lecture_name: string;
  professor_name: string;
  rating: number;
}

const RatingCard = (rating: Rating) => (
  <div className="flex items-center justify-between w-full">
    <div className="flex w-full">
      <img
        src={rating.profile ?? GET_DEFAULT_PROFILE_IMAGE()}
        alt="Rating"
        className="w-[44px] h-[44px] rounded-full object-cover"
      />
      <div className="ml-[14px] flex flex-col">
        <div className="text-sm font-medium">{rating.lecture_name}</div>
        <div className="text-xs text-gray-600 font-regular">
          {rating.professor_name}
        </div>
      </div>
    </div>
    <RatingTag rating={rating.rating} />
  </div>
);

const RatingTag = ({ rating }: { rating: number }) => {
  const bgColor = rating >= 2.5 ? "bg-tag" : "bg-redtag";
  const circleColor = rating >= 2.5 ? "bg-system-success" : "bg-system-error";
  return (
    <div
      className={`flex items-center justify-evenly rounded-full w-[100px] py-[3px] px-[6px] ${bgColor}`}
    >
      <div className={`w-[10px] h-[10px] rounded-full ${circleColor}`}></div>
      <div className="text-sm font-md">{rating.toFixed(1)}/5.0</div>
    </div>
  );
};

export default RatingCard;
