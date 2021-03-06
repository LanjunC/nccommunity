package cn.codingcrea.nccommunity.controller;

import cn.codingcrea.nccommunity.util.NcCommunityUtil;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @RequestMapping("/hello")
    @ResponseBody
    public String hello() {
        return "hello!!";
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response) {
        //获取请求数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + ":" + value);
        }
        System.out.println(request.getParameter("code"));

        //返回响应数据
        response.setContentType("text/html;charset=utf-8");
        try(
                PrintWriter writer = response.getWriter();
        ) {
            writer.write("<h1>testHttp<h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(path = "/teacher",method = RequestMethod.GET)
    public ModelAndView getTeacher() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("name", "张san");
        mav.setViewName("/demo/alpha");
        return mav;
    }

    @RequestMapping(path="/student",method = RequestMethod.GET)
    public String getStudent(Model model) {
        model.addAttribute("name", "Lisi");
        return "/demo/alpha";
    }

    //响应Json（Ajax）
    //Java对象-》json字符串-》js对象
    @RequestMapping("/school")
    @ResponseBody
    public Map<String,String> getSchool() {
        Map<String,String> map = new HashMap<>();
        map.put("name","USTC");
        map.put("where","jiangsu");
        return map; //自动转为json
    }

    //cookie测试
    @RequestMapping("/setcookie")
    @ResponseBody
    public String setCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("testCookieCode", "123123");
        cookie.setPath("/nccommunity/alpha");
        //生存时间，负数为临时性，即只在浏览器内存中，关闭就失效；0表示立刻失效，可配合add方法删除cookie
        cookie.setMaxAge(60 * 60);
        response.addCookie(cookie);
        return "set cookie";
    }

    @RequestMapping("/getcookie")
    @ResponseBody
    public String getCookie(@CookieValue("testCookieCode") String testCookieCode) {
        System.out.println(testCookieCode);
        return "get cookie";
    }

    @RequestMapping("/setsession")
    @ResponseBody
    //Session同MaV一样，可以声明并自动注入
    public String setSession(HttpSession session) {
        session.setAttribute("id", 123);
        session.setAttribute("name", "test");
        return "set session";
    }

    @RequestMapping("/getsession")
    @ResponseBody
    //Session同MaV一样，可以声明并自动注入
    public String getSession(HttpSession session) {
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session";
    }

}
