package com.comet.nfc_sever.advice;

import com.comet.nfc_sever.response.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class ValidateExceptionHandler {

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ExceptionResponse> argumentNotValidHandler(BindException e) {
        BindingResult result = e.getBindingResult();
        StringBuilder builder = new StringBuilder();
        int size = result.getAllErrors().size();
        List<String> errorField = new ArrayList<>();
        result.getAllErrors().forEach((error) -> {
            if (!errorField.contains(error.getCode())) {
                errorField.add(error.getObjectName());
                builder.append(error.getDefaultMessage());
                if (size > 1 && result.getAllErrors().indexOf(error) != size - 1)
                    builder.append("\n");
            }
        });
        ExceptionResponse response = new ExceptionResponse(HttpStatus.BAD_REQUEST, builder.toString());
        return new ResponseEntity<>(response, response.getStatus());
    }

}
