package ru.otus.server.webserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.otus.database.crm.service.DBServiceClient;
import ru.otus.database.crm.service.DBServiceUser;
import ru.otus.server.services.TemplateProcessor;
import ru.otus.server.services.UserAuthService;
import ru.otus.server.servlet.AuthorizationFilter;
import ru.otus.server.servlet.LoginServlet;

import java.util.Arrays;

public class ClientsWebServerWithFilterBasedSecurity extends ClientsWebServerSimple {
    private final UserAuthService authService;

    public ClientsWebServerWithFilterBasedSecurity(int port,
                                                   UserAuthService authService,
                                                   DBServiceUser dbServiceUser,
                                                   DBServiceClient dbServiceClient,
                                                   ObjectMapper objectMapper,
                                                   TemplateProcessor templateProcessor) {
        super(port, dbServiceUser, dbServiceClient, objectMapper, templateProcessor);
        this.authService = authService;
    }

    @Override
    protected Handler applySecurity(ServletContextHandler servletContextHandler, String... paths) {
        servletContextHandler.addServlet(new ServletHolder(new LoginServlet(templateProcessor, authService)), "/login");
        AuthorizationFilter authorizationFilter = new AuthorizationFilter();
        Arrays.stream(paths).forEachOrdered(path -> servletContextHandler.addFilter(new FilterHolder(authorizationFilter), path, null));
        return servletContextHandler;
    }
}
