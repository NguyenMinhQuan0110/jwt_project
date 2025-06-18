package com.example.demo.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
//@RestControllerAdvice = @ControllerAdvice(Đánh dấu lớp này là nơi xử lý lỗi toàn cục) + @ResponseBody(Tự động trả JSON khi có lỗi.)
//Nếu chỉ dùng @ControllerAdvice thì phải viết thêm @ResponseBody trong mỗi method
public class GlobalExceptionHandler {
	
	 // Bắt lỗi Validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(),
                "Validation failed", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    // Bắt lỗi NotFoundException(404)
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(NotFoundException ex) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND.value(),
                ex.getMessage(), List.of());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }
    //bắt lỗi BadRequestException
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequestException(BadRequestException ex) {
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                List.of()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }
 // Bắt lỗi ForbiddenException(403)
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiError> handleForbiddenException(ForbiddenException ex) {
        ApiError apiError = new ApiError(
            HttpStatus.FORBIDDEN.value(),
            ex.getMessage(),
            List.of()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiError);
    }
    // Bắt lỗi khác
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleOtherExceptions(Exception ex) {
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(), List.of());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }
}
