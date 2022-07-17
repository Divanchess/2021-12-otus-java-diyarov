package ru.otus.server.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.otus.database.crm.model.Client;
import ru.otus.database.crm.service.DBServiceClient;
import ru.otus.server.services.TemplateProcessor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class ClientsServlet extends HttpServlet {

    private static final String CLIENTS_PAGE_TEMPLATE = "clients.html";
    private static final String TEMPLATE_ATTR_RANDOM_CLIENT = "randomClient";

    private final DBServiceClient dbServiceClient;
    private final TemplateProcessor templateProcessor;

    public ClientsServlet(TemplateProcessor templateProcessor, DBServiceClient dbServiceClient) {
        this.templateProcessor = templateProcessor;
        this.dbServiceClient = dbServiceClient;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {
        Map<String, Object> paramsMap = new HashMap<>();
        Optional<Client> client = dbServiceClient.findRandomClient();
        System.out.println("This is random client:" + client);
        client.ifPresent(randomClient -> paramsMap.put(TEMPLATE_ATTR_RANDOM_CLIENT, randomClient));

        response.setContentType("text/html");
        response.getWriter().println(templateProcessor.getPage(CLIENTS_PAGE_TEMPLATE, paramsMap));
    }

}
