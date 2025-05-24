package com.example.arate.materials.exception;

public class MaterialException extends RuntimeException {
    
    public MaterialException(String message) {
        super(message);
    }
    
    public static class MaterialNotFoundException extends MaterialException {
        public MaterialNotFoundException() {
            super("공유 자료를 찾을 수 없습니다.");
        }
    }
    
    public static class UnauthorizedMaterialException extends MaterialException {
        public UnauthorizedMaterialException() {
            super("자료를 수정/삭제할 권한이 없습니다.");
        }
    }
    
    public static class TagProcessingException extends MaterialException {
        public TagProcessingException(Throwable cause) {
            super("태그 처리 중 오류가 발생했습니다: " + cause.getMessage());
        }
    }
} 