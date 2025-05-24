interface Rating {
  profile: string;
  lecture_name: string;
  professor_name: string;
  rating: number;
}

const RatingCard = ({ rating }: { rating: Rating }) => (
  <div className="flex w-full justify-between items-center">
    <div className="flex w-full">
      <img
        src={rating.profile}
        alt="Rating"
        className="w-[44px] h-[44px] rounded-full"
      />
      <div className="ml-[14px] flex flex-col">
        <div className="font-medium text-sm">{rating.lecture_name}</div>
        <div className="font-regular text-xs text-gray-600">
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
      <div className="font-md text-sm">{rating.toFixed(1)}/4.5</div>
    </div>
  );
};

export default RatingCard;
