package com.chiricker.areas.logger.interceptors;

import com.chiricker.areas.logger.annotations.Logger;
import com.chiricker.areas.logger.models.entities.enums.Operation;
import com.chiricker.areas.logger.services.log.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.persistence.Table;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class LogInterceptor extends HandlerInterceptorAdapter {

    private final LogService logService;

    @Autowired
    public LogInterceptor(LogService logService) {
        this.logService = logService;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (!request.getMethod().equals("POST")) {
            return;
        }

        Method method = ((HandlerMethod) handler).getMethod();
        if (!method.isAnnotationPresent(Logger.class)) {
            return;
        }

        String handle = SecurityContextHolder.getContext().getAuthentication().getName();
        Operation operation = method
                .getAnnotation(Logger.class)
                .operation();
        String tableName = method
                .getAnnotation(Logger.class).entity()
                .getAnnotation(Table.class)
                .name();

        this.logService.createLog(handle, operation, tableName);

        super.postHandle(request, response, handler, modelAndView);
    }
}