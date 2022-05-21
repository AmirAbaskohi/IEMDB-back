//package com.iemdb.service;
//
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.*;
//import javax.servlet.annotation.WebFilter;
//import javax.servlet.http.*;
//import java.io.*;
//
//
//@WebFilter(urlPatterns = {
//        "/movies"})
//public class AuthenticationFilter implements Filter {
//
//    @Override
//    public void doFilter(
//            ServletRequest request,
//            ServletResponse response,
//            FilterChain chain) throws IOException, ServletException {
//
//        HttpServletRequest req = (HttpServletRequest) request;
//        HttpServletResponse res = (HttpServletResponse) response;
//
//        System.out.println(req.getRequestURL());
//        res.setStatus(HttpServletResponse.SC_FORBIDDEN);
////        chain.doFilter(request, response);
//    }
//}