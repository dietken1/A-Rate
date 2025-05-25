export interface LectureDetailProfessor {
  id: number;
  name: string;
  department: string;
}

export interface LectureDetailEvaluation {
  id: number;
  content: string;
  scores: {
    delivery: number;
    expertise: number;
    generosity: number;
    effectiveness: number;
    character: number;
    difficulty: number;
  };
  assignment: {
    amount: "NONE" | "FEW" | "NORMAL" | "MANY";
    difficulty: "EASY" | "NORMAL" | "HARD";
  };
  examType: "NONE" | "MIDTERM" | "FINAL" | "MIDTERM_FINAL";
  teamProject: boolean;
  createdAt: string;
  author: {
    id: number;
    nickname: string;
  };
  isRestricted: boolean;
}

export interface LectureDetailAverageScores {
  delivery: number;
  expertise: number;
  generosity: number;
  effectiveness: number;
  character: number;
  difficulty: number;
}

export default interface LectureDetailInfo {
  id: number;
  title: string;
  professor: LectureDetailProfessor;
  department: string;
  evaluations: LectureDetailEvaluation[];
  averageScores: LectureDetailAverageScores;
}
