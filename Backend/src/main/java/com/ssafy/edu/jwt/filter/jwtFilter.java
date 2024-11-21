package com.ssafy.edu.jwt.filter;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class jwtFilter implements Filter {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    if (request instanceof HttpServletRequest) {
      HttpServletRequest httpRequest = (HttpServletRequest) request;

      if ("GET".equalsIgnoreCase(httpRequest.getMethod())) {

        if (httpRequest.getRequestURI().equals("/uhpooh/api/login")
            || httpRequest.getRequestURI().equals("/uhpooh/api/user/logout")) {
          HttpServletRequestWrapper wrapperRequest = new HttpServletRequestWrapper(httpRequest) {
            @Override
            public String getMethod() {
              return "POST";
            }
          };
          chain.doFilter(wrapperRequest, response);
          return;
        }

      }
    }
    chain.doFilter(request, response);
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {}

  @Override
  public void destroy() {}


}
