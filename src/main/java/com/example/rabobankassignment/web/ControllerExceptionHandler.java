package com.example.rabobankassignment.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseBody
@ControllerAdvice
public class ControllerExceptionHandler {
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({JobInstanceAlreadyCompleteException.class, JobExecutionAlreadyRunningException.class, JobParametersInvalidException.class, JobRestartException.class})
    public Map<String, String> handleException(Exception ex) {
        Map<String, String> errorObject = new HashMap<>();
        errorObject.put("statusCode", String.valueOf(HttpStatus.CONFLICT));
        errorObject.put("errorMessage", "Something went wrong during batch execution.");
        return errorObject;
    }
}
