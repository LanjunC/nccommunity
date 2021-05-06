package cn.codingcrea.nccommunity.controller.advice;

import cn.codingcrea.nccommunity.util.NcCommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

//统一异常处理，1.记录日志 2.对于异步/非异步请求给予友好提示
//如果无上述需求，可以用spring自带的异常处理（error包和404.html/500.html）
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler({Exception.class})
    public void handlerException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.error("服务器发生异常: " + e.getMessage());
        for(StackTraceElement element : e.getStackTrace()) {
            logger.error(element.toString());
        }

        String xRequestedWith = request.getHeader("x-requested-with");
        //如果是异步请求，返回普通字符串
        if("XMLHttpRequest".equals(xRequestedWith)) {
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(NcCommunityUtil.getJSONString(1, "服务器异常"));
        } else {
            //非异步请求
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}
