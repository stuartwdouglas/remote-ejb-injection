package org.wildfly.remoteejbinjection.jar;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.wildfly.remoteejbinjection.ejbs.StatelessEjbInterface;

@WebServlet(urlPatterns = "/stateless")
public class StatelessServlet extends HttpServlet {

    @Inject
    private StatelessEjbInterface statelessEjbInterface;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getHeader("name");
        resp.getWriter().write(statelessEjbInterface.sayHello(name));
    }
}
