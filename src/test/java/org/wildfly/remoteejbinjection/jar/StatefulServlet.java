package org.wildfly.remoteejbinjection.jar;

import org.wildfly.remoteejbinjection.ejbs.StatefulEjbInterface;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "/stateful")
public class StatefulServlet extends HttpServlet {

    @Inject
    private Instance<StatefulEjbInterface> statefulEjbInterface;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StatefulEjbInterface statefulEjbInterface = this.statefulEjbInterface.get();
        int count = 0;
        if(statefulEjbInterface.increment(2) != 2) {
            throw new RuntimeException("wrong");
        }
        if(statefulEjbInterface.increment(2) != 4) {
            throw new RuntimeException("wrong");
        }
        if(statefulEjbInterface.increment(2) != 6) {
            throw new RuntimeException("wrong");
        }
        resp.getWriter().write("ok");
    }
}
