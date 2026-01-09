package com.garosugil.common.exception;

import com.garosugil.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateEmail(DuplicateEmailException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(409, e.getMessage()));
    }

    @ExceptionHandler(InvalidLoginException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidLogin(InvalidLoginException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(401, e.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(404, e.getMessage()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(UnauthorizedException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(401, e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalState(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(409, e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException e) {
        // 디버깅: validation 에러 상세 정보 출력
        System.out.println("DEBUG - Validation Error:");
        System.out.println("  BindingResult: " + e.getBindingResult());
        e.getBindingResult().getAllErrors().forEach((error) -> {
            System.out.println("  Field: " + ((FieldError) error).getField());
            System.out.println("  Message: " + error.getDefaultMessage());
            System.out.println("  RejectedValue: " + ((FieldError) error).getRejectedValue());
        });
        
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            // JSON 필드명으로 변환 (tagCode -> tag_code)
            String jsonFieldName = convertToJsonFieldName(fieldName);
            errors.put(jsonFieldName, errorMessage);
        });
        
        // 첫 번째 에러 메시지를 메인 메시지로 사용
        String mainMessage = errors.isEmpty() 
                ? "입력값 검증 실패" 
                : errors.values().iterator().next();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(400, mainMessage, errors));
    }
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException e) {
        System.out.println("DEBUG - Request Body Parsing Error:");
        System.out.println("  Message: " + e.getMessage());
        System.out.println("  Cause: " + (e.getCause() != null ? e.getCause().getMessage() : "null"));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(400, "요청 본문을 파싱할 수 없습니다: " + e.getMessage()));
    }
    
    private String convertToJsonFieldName(String fieldName) {
        // Java 필드명을 JSON 필드명으로 변환
        // tagCode -> tag_code
        if (fieldName.equals("tagCode")) {
            return "tag_code";
        }
        return fieldName;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, "서버 오류가 발생했습니다."));
    }
}
