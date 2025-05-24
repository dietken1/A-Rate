export interface Lectures {
  content: LectureInfo[];
  totalElements: number;
}

export default interface LectureInfo {
  id: number;
  title: string;
  professorName: string;
  evaluationCount: number;
  averageScore: number;
  courseType: string;
  department: string;
  bestComment: string;
}
