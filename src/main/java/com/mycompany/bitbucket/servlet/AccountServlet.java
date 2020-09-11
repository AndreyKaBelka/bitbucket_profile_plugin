package com.mycompany.bitbucket.servlet;

import com.atlassian.bitbucket.user.ApplicationUser;
import com.atlassian.bitbucket.user.UserService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Named
public class AccountServlet extends HttpServlet {
    private final SoyTemplateRenderer soyTemplateRenderer;
    private final UserService userService;

    @Inject
    public AccountServlet(@ComponentImport SoyTemplateRenderer soyTemplateRenderer, @ComponentImport UserService userService) {
        this.soyTemplateRenderer = soyTemplateRenderer;
        this.userService = userService;
    }

    private void render(HttpServletResponse resp, String templateName, Map<String, Object> data) throws IOException, ServletException {
        resp.setContentType("text/html;charset=UTF-8");
        try {
            soyTemplateRenderer.render(resp.getWriter(),
                    "com.mycompany.bitbucket.bitbucket-profile-plugin:account-soy",
                    templateName,
                    data);
        } catch (SoyException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw (IOException) cause;
            }
            throw new ServletException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();


        String userSlug = pathInfo.substring(1);
        ApplicationUser user = userService.getUserBySlug(userSlug);

        if (user == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        render(resp, "plugin.account.accountTab", ImmutableMap.<String, Object>of("user", user));
    }

}