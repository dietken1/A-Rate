export default interface SummaryInfo {
  topRatedLectures: SummaryLectureInfo[];
  bottomRatedLectures: SummaryLectureInfo[];
  recentEvaluations: SummaryEvaluationInfo[];
  recentMaterials: SummaryMaterialInfo[];
}

export interface SummaryLectureInfo {
  id: number;
  title: string;
  professor: {
    name: string;
    department: string;
  };
  averageRating: number;
  evaluationCount: number;
}

export interface SummaryEvaluationInfo {
  id: number;
  content: string;
  createdAt: Date;
  lecture: {
    title: string;
    professor: string;
  };
}

export interface SummaryMaterialInfo {
  id: number;
  title: string;
  type: string;
  createdAt: Date;
  lecture: {
    title: string;
    professor: string;
  };
}
