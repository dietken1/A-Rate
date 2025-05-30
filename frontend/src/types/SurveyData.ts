// 강의 평가(설문) 작성 요청 타입
export interface SurveyData {
    content: string;
    deliveryScore: number;
    expertiseScore: number;
    generosityScore: number;
    effectivenessScore: number;
    characterScore: number;
    difficultyScore: number;
    assignmentAmount: "NONE" | "FEW" | "NORMAL" | "MANY";
    assignmentDifficulty: "EASY" | "NORMAL" | "HARD";
    exam: "NONE" | "MIDTERM" | "FINAL" | "MIDTERM_FINAL";
    teamProject: boolean;
    semester: string;
}  