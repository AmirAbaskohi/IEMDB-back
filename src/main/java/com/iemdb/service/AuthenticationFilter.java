package com.iemdb.service;

import com.iemdb.system.IEMDBSystem;
import io.jsonwebtoken.*;
import org.springframework.http.HttpStatus;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.*;

@WebFilter(urlPatterns = {
        "/movies",
        "/movies/*",
        "/actors",
        "/actors/*",
        "/comment",
        "/comment/*",
        "/user",
        "/user/*",
        "/account"})
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {


        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;


        if(req.getMethod().equals("OPTIONS")){
            chain.doFilter(request, response);
            return;
        }

        if(req.getHeader("jwt")!= null && !req.getHeader("jwt").equals("null")){
            Claims claims = IEMDBSystem.getInstance().decodeJWT(req.getHeader("jwt"));
            if(IEMDBSystem.getInstance().validateJwt(claims)){
                request.setAttribute("userEmail", claims.get("userEmail"));
                chain.doFilter(request, response);
                return;
            }
        }
        res.sendError(403, "UnAuthorized");
    }
}