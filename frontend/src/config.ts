export const DEPARTMENTS = ["전체", "컴퓨터공학과", "수학과", "물리학과"];
export const LECTURE_TYPES = ["전체", "전공", "교양", "일반선택"];
export const GET_DEFAULT_PROFILE_IMAGE = () => {
  const rand = Math.floor(Math.random() * 4) + 1;
  return `/images/profiles/${rand}.png`;
};
