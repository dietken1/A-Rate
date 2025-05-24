package com.example.arate.lecture.exception;

public class LectureException extends RuntimeException {
    public LectureException(String message) {
        super(message);
    }

    public static class LectureNotFoundException extends LectureException {
        public LectureNotFoundException() {
            super("강의를 찾을 수 없습니다.");
        }
    }

    public static class EvaluationNotFoundException extends LectureException {
        public EvaluationNotFoundException() {
            super("평가를 찾을 수 없습니다.");
        }
    }

    public static class DuplicateEvaluationException extends LectureException {
        public DuplicateEvaluationException() {
            super("이미 해당 강의에 대한 평가를 작성했습니다.");
        }
    }

    public static class UnauthorizedEvaluationException extends LectureException {
        public UnauthorizedEvaluationException() {
            super("해당 평가를 수정할 권한이 없습니다.");
        }
    }

    public static class InvalidEvaluationException extends LectureException {
        public InvalidEvaluationException() {
            super("해당 강의에 속하지 않는 평가입니다.");
        }
    }
} 