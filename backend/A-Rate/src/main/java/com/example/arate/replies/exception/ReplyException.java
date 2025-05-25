package com.example.arate.replies.exception;

public class ReplyException extends RuntimeException {
    
    public ReplyException(String message) {
        super(message);
    }
    
    public static class ReplyNotFoundException extends ReplyException {
        public ReplyNotFoundException() {
            super("댓글을 찾을 수 없습니다.");
        }
    }
    
    public static class UnauthorizedReplyException extends ReplyException {
        public UnauthorizedReplyException() {
            super("댓글을 수정/삭제할 권한이 없습니다.");
        }
    }
} 