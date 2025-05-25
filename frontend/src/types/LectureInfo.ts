export interface Lectures {
  content: LectureInfo[];
  numberOfElements: number;
}

export default interface LectureInfo {
  id: number;
  title: string;
  professorName: string;
  evaluationCount: number;
  averageScore: number;
  department: string;
}
