package com.uas.dingzikaifa.aop;

import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class ExceptionHandlerAdvice {
    private static Logger logger = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);

    /**
     * 处理已捕获异常，明确传达给客户端错误码、错误信息
     *
     * @param e
     * @return
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ModelMap> handleError(Throwable e) {
        if(e instanceof ClientAbortException){
            logger.error(e.getMessage());
        }else{
            logger.error("", e);
        }
        ModelMap map = new ModelMap();
        map.put("success", false);
        map.put("message", e.getMessage());
        map.put("detailedMessage", getDetailedMessage(e));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<ModelMap>(map, headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public  String getDetailedMessage(Throwable e) {
        StringBuilder sb = new StringBuilder(e.toString());
        StackTraceElement[] stackTraceElements = e.getStackTrace();
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            sb.append("\n\t").append(stackTraceElement.toString());
        }
        if (e.getCause() != null) {
            sb.append("\nCaused by: ").append(getDetailedMessage(e.getCause()));
        }
        return sb.toString();
    }
}
