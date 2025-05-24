package com.example.arate.lectures.exception;

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

    public static class EnrollmentNotFoundException extends LectureException {
        public EnrollmentNotFoundException() {
            super("수강 인증 정보를 찾을 수 없습니다.");
        }
    }

    public static class DuplicateEnrollmentException extends LectureException {
        public DuplicateEnrollmentException() {
            super("이미 해당 강의에 대한 수강 인증을 요청했습니다.");
        }
    }

    public static class UnauthorizedEnrollmentException extends LectureException {
        public UnauthorizedEnrollmentException() {
            super("해당 수강 인증 정보를 수정할 권한이 없습니다.");
        }
    }

    public static class NotCertifiedEnrollmentException extends LectureException {
        public NotCertifiedEnrollmentException() {
            super("인증되지 않은 수강생입니다. 강의평을 작성하려면 수강 인증이 필요합니다.");
        }
    }

    public static class NoEvaluationWrittenException extends LectureException {
        public NoEvaluationWrittenException() {
            super("강의평을 조회하려면 최소 1개 이상의 강의평을 작성해야 합니다.");
        }
    }

    public static class InvalidSearchParameterException extends LectureException {
        public InvalidSearchParameterException() {
            super("검색하려면 과목명 또는 교수명 중 하나는 반드시 입력해야 합니다.");
        }
    }
} 