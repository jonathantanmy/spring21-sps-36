package com.google.sps.servlets;

import com.google.sps.OAuthUtils;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

@WebServlet("/login_screen")
public class LoginScreenServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    String sessionId = request.getSession().getId();
    boolean isUserLoggedIn =
        OAuthUtils.isUserLoggedIn(sessionId);
    
    if (!isUserLoggedIn) {
        response.sendRedirect("/login");
    }
    else{
        response.sendRedirect("/dashboard.html");
    }
  }
}