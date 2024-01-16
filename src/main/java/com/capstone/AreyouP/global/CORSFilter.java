//package com.capstone.AreyouP.Configuration;
//
//import jakarta.servlet.*;
//import jakarta.servlet.http.HttpServletResponse;
//
//import java.io.IOException;
//
//public class CORSFilter implements Filter {
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        HttpServletResponse res = (HttpServletResponse) response;
//
//        res.setHeader("Access-Control-Allow-Origin", "*");
//        res.setHeader("Access-Control-Allow-Method", "POST,GET,OPTIONS,DELETE,PUT");
//        res.setHeader("Access-Control-Max-Age", "3600");
//        res.setHeader("Access-Control-Allow-Headers", "Content-Type, x-requested-with, Authorization, Access-Control-Allow-Origin");
//
//        chain.doFilter(request, response);
//    }
//}
