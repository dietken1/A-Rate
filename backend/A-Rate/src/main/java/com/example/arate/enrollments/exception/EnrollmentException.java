package com.example.arate.enrollments.exception;

public class EnrollmentException extends RuntimeException {
    
    public EnrollmentException(String message) {
        super(message);
    }
    
    public static class EnrollmentNotFoundException extends EnrollmentException {
        public EnrollmentNotFoundException() {
            super("수강 인증 정보를 찾을 수 없습니다.");
        }
    }
    
    public static class DuplicateEnrollmentException extends EnrollmentException {
        public DuplicateEnrollmentException() {
            super("이미 해당 강의에 대한 수강 인증을 요청했습니다.");
        }
    }
    
    public static class UnauthorizedEnrollmentException extends EnrollmentException {
        public UnauthorizedEnrollmentException() {
            super("해당 수강 인증 정보를 수정할 권한이 없습니다.");
        }
    }
    
    public static class NotCertifiedEnrollmentException extends EnrollmentException {
        public NotCertifiedEnrollmentException() {
            super("인증되지 않은 수강생입니다. 강의평을 작성하려면 수강 인증이 필요합니다.");
        }
    }
    
    public static class NoEvaluationWrittenException extends EnrollmentException {
        public NoEvaluationWrittenException() {
            super("강의평을 조회하려면 최소 1개 이상의 강의평을 작성해야 합니다.");
        }
    }
} 