package com.comet.nfc_sever.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.Map;

@Slf4j
public class RestInterceptor implements AsyncHandlerInterceptor {

    /*POST 요청은 InputStream 으로 읽어야 하는데 객체 래핑으로 인한 오버헤드 발생가능성 있음*/
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getParameterMap().isEmpty())
            log.info("url : {}, type : {}", request.getRequestURL(), request.getMethod());
        else
            log.info("url : {}, type : {} | param : {}", request.getRequestURL(), request.getMethod(), mapFlatter(request.getParameterMap()));
        return AsyncHandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        log.info("response : {}", response.getStatus());
    }

    private String mapFlatter(Map<String, String[]> input) {
        StringBuilder builder = new StringBuilder();
        for (String key : input.keySet()) {
            String[] value = input.get(key);
            builder.append(key).append(" : ").append(Arrays.toString(value)).append(" ");
        }
        return builder.toString();

    }

}
