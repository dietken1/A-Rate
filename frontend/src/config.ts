export const DEPARTMENTS = [
  "전체",
  "디지털미디어학과",
  "소프트웨어학과",
  "인공지능융합학과",
  "건축학과",
  "국방디지털융합학과",
  "경영인텔리전스학과",
  "산업공학과",
];

export const LECTURE_TYPES = [
  "전체",
  "교선",
  "교필",
  "일선",
  "전기",
  "전선",
  "전필",
];

export const GET_DEFAULT_PROFILE_IMAGE = () => {
  const rand = Math.floor(Math.random() * 4) + 1;
  return `/images/profiles/${rand}.png`;
};
export const API_URL = "http://localhost:8080/api";
