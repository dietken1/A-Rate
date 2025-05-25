import { useMutation } from "@tanstack/react-query";
import { post } from "../../lib/api";
import React from "react";
import { Sheet } from "react-modal-sheet";
import LectureDetailInfo from "../../types/LectureDetailInfo";
import { SurveyData } from "../../types/SurveyData";
import useAuthStore from "../../store/useAuthStore";

const RatingModal = ({
  lectureId,
  open,
  setOpen,
}: {
  lectureId: string;
  open: boolean;
  setOpen: (open: boolean) => void;
}) => {
  const [stage, setStage] = React.useState<number>(1);

  const [assignmentAmount, setAssignmentAmount] = React.useState<number | null>(
    null
  );
  const [assignmentDifficulty, setAssignmentDifficulty] = React.useState<
    number | null
  >(null);
  const [examAmount, setExamAmount] = React.useState<number | null>(null);
  const [hasTeamProject, setHasTeamProject] = React.useState<boolean | null>(
    null
  );

  const [delivery, setDelivery] = React.useState<number | null>(null);
  const [expertise, setExpertise] = React.useState<number | null>(null);
  const [generosity, setGenerosity] = React.useState<number | null>(null);
  const [effectiveness, setEffectiveness] = React.useState<number | null>(null);
  const [character, setCharacter] = React.useState<number | null>(null);
  const [difficulty, setDifficulty] = React.useState<number | null>(null);
  const [content, setContent] = React.useState<string | null>(null);

  const { user } = useAuthStore();

  const mutation = useMutation({
    mutationFn: (data: SurveyData) =>
      post<LectureDetailInfo>(`/v1/lectures/${lectureId}/evaluations`, data, {
        headers: {
          Authorization: `Bearer ${user?.token}`,
        },
      }),
    onSuccess: (data) => {
      if (data && data.success) {
        alert("강의 평가가 성공적으로 제출되었습니다.");
        setOpen(false);
      } else {
        alert(
          data?.error || "강의 평가 제출에 실패했습니다. 다시 시도해주세요."
        );
      }
    },
    onError: (error: any) => {
      alert(
        error?.message ||
          "강의 평가 제출에 실패했습니다. 네트워크 상태를 확인해주세요."
      );
    },
  });

  const onClick = () => {
    if (stage === 1) {
      if (
        assignmentAmount === null ||
        assignmentDifficulty === null ||
        examAmount === null ||
        hasTeamProject === null
      ) {
        alert("모든 필드를 입력해주세요.");
        return;
      }
      setStage(2);
    } else {
      if (
        delivery === null ||
        expertise === null ||
        generosity === null ||
        effectiveness === null ||
        character === null ||
        difficulty === null
      ) {
        alert("모든 필드를 입력해주세요.");
        return;
      } else if (content === null || content.length < 10) {
        alert("내용을 더 길게 입력해주세요.");
        return;
      }

      const assignmentAmountMap: Record<number, string> = {
        0: "NONE",
        1: "FEW",
        2: "NORMAL",
        3: "MANY",
      };
      const assignmentDifficultyMap: Record<number, string> = {
        0: "EASY",
        1: "NORMAL",
        2: "HARD",
      };
      const examMap: Record<number, string> = {
        0: "NONE",
        1: "MIDTERM",
        2: "FINAL",
        3: "MIDTERM_FINAL",
      };

      mutation.mutate({
        content: content!,
        deliveryScore: delivery!,
        expertiseScore: expertise!,
        generosityScore: generosity!,
        effectivenessScore: effectiveness!,
        characterScore: character!,
        difficultyScore: difficulty!,
        assignmentAmount: assignmentAmountMap[assignmentAmount!] as
          | "NONE"
          | "FEW"
          | "NORMAL"
          | "MANY",
        assignmentDifficulty: assignmentDifficultyMap[assignmentDifficulty!] as
          | "EASY"
          | "NORMAL"
          | "HARD",
        exam: examMap[examAmount!] as
          | "NONE"
          | "MIDTERM"
          | "FINAL"
          | "MIDTERM_FINAL",
        teamProject: hasTeamProject!,
        semester: "2024-2", // TODO: 실제 학기 정보로 변경 필요
      });
    }
  };

  return (
    <Sheet isOpen={open} onClose={() => setOpen(false)} snapPoints={[1000]}>
      <Sheet.Container className="px-[100px] py-[20px]">
        <Sheet.Header className="text-center">
          <div className="text-xl font-bold">
            {stage === 1
              ? "수업의 기본 정보를 알려주세요."
              : "수업의 만족도를 자세하게 적어주세요!"}
          </div>
        </Sheet.Header>
        <Sheet.Content>
          {stage === 1 && (
            <div className="flex flex-col gap-[15px] my-[5px]">
              <SurveyQuestion
                values={[1, 2, 3, 4, 5]}
                label="과제의 양"
                value={assignmentAmount}
                onChange={setAssignmentAmount}
              />
              <SurveyQuestion
                values={[1, 2, 3, 4, 5]}
                label="과제의 난이도"
                value={assignmentDifficulty}
                onChange={setAssignmentDifficulty}
              />
              <SurveyQuestion
                values={[0, 1, 2]}
                label="시험 횟수"
                value={examAmount}
                onChange={setExamAmount}
              />
              <SurveyQuestion
                values={["없음", "있음"]}
                label="팀 프로젝트가 있었나요?"
                value={hasTeamProject === null ? null : hasTeamProject ? 1 : 0}
                onChange={(value) => setHasTeamProject(value === 1)}
              />
            </div>
          )}{" "}
          {stage === 2 && (
            <div className="flex flex-col gap-[15px] my-[5px]">
              <SurveyQuestion
                values={[1, 2, 3, 4, 5]}
                label="강의 전달력"
                value={delivery}
                onChange={setDelivery}
              />
              <SurveyQuestion
                values={[1, 2, 3, 4, 5]}
                label="내용의 실효성"
                value={effectiveness}
                onChange={setEffectiveness}
              />
              <SurveyQuestion
                values={[1, 2, 3, 4, 5]}
                label="수업의 난이도"
                value={difficulty}
                onChange={setDifficulty}
              />
              <SurveyQuestion
                values={[1, 2, 3, 4, 5]}
                label="교수님의 전문성"
                value={expertise}
                onChange={setExpertise}
              />
              <SurveyQuestion
                values={[1, 2, 3, 4, 5]}
                label="교수님의 인품"
                value={character}
                onChange={setCharacter}
              />
              <SurveyQuestion
                values={[1, 2, 3, 4, 5]}
                label="후한 성적"
                value={generosity}
                onChange={setGenerosity}
              />
              <div className="w-full text-center">
                <textarea
                  className="h-[100px] p-[10px] border border-gray-300 rounded-md mt-[5px] w-[500px] resize-none"
                  placeholder="강의에 대한 자세한 의견을 작성해주세요. (최소 10자)"
                  value={content ?? ""}
                  onChange={(e) => setContent(e.target.value)}
                  minLength={100}
                  required
                />
              </div>
            </div>
          )}
          <button
            onClick={onClick}
            className="bg-primary text-white rounded-md px-[20px] py-[10px] hover:scale-[101%] transition duration-fast mt-[20px]"
          >
            {stage === 1 ? "계속하기" : "제출하기"}
          </button>
        </Sheet.Content>
      </Sheet.Container>
      <Sheet.Backdrop
        onTap={() => {
          setOpen(false);
          setStage(1);
        }}
      />
    </Sheet>
  );
};

interface SurveyQuestionProps {
  values: any[];
  label: string;
  value: number | null;
  onChange: (value: number | null) => void;
}

const SurveyQuestion = ({
  values,
  label,
  value,
  onChange,
}: SurveyQuestionProps) => {
  const target = [];
  for (let i = 0; i < values.length; i++) {
    target.push(i);
  }

  return (
    <div className="w-full mt-[5px]">
      <div className="font-semibold text-center">{label}</div>
      <div className="flex items-center justify-center w-full gap-4 mt-2">
        {target.map((num) => (
          <label key={num} className="relative cursor-pointer">
            <input
              type="radio"
              value={num}
              checked={value === num}
              onChange={() => onChange(num)}
              className="absolute w-0 h-0 opacity-0 peer"
            />
            <span className="flex items-center justify-center font-medium transition bg-gray-100 border-2 border-transparent rounded-full text-mmd w-[40px] h-[40px] bg-grayscale-100 peer-checked:bg-blue-100 peer-checked:text-primary peer-hover:bg-gray-200">
              {values[num]}
            </span>
          </label>
        ))}
      </div>
    </div>
  );
};

export default RatingModal;
