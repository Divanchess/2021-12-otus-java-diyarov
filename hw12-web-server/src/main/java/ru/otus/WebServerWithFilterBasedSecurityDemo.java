package ru.otus;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.cfg.Configuration;
import ru.otus.database.core.repository.DataTemplateHibernate;
import ru.otus.database.core.repository.HibernateUtils;
import ru.otus.database.core.sessionmanager.TransactionManagerHibernate;
import ru.otus.database.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.database.crm.model.Address;
import ru.otus.database.crm.model.Client;
import ru.otus.database.crm.model.Phone;
import ru.otus.database.crm.model.User;
import ru.otus.database.crm.service.DbServiceClientImpl;
import ru.otus.database.crm.service.DbServiceUserImpl;
import ru.otus.server.webserver.ClientsWebServer;
import ru.otus.server.webserver.ClientsWebServerWithFilterBasedSecurity;
import ru.otus.server.services.TemplateProcessor;
import ru.otus.server.services.TemplateProcessorImpl;
import ru.otus.server.services.UserAuthService;
import ru.otus.server.services.UserAuthServiceImpl;

/*
    Полезные для демо ссылки

    // Стартовая страница
    http://localhost:8080

    // Страница пользователей
    http://localhost:8080/users

    // REST сервис
    http://localhost:8080/api/user/3
*/
public class WebServerWithFilterBasedSecurityDemo {
    private static final int WEB_SERVER_PORT = 8080;
    private static final String TEMPLATES_DIR = "/templates/";
    public static final String HIBERNATE_CFG_FILE = "config/hibernate.cfg.xml";


    public static void main(String[] args) throws Exception {
        var configuration = new Configuration().configure(HIBERNATE_CFG_FILE);

        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();

        var sessionFactory = HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class, User.class);

        var transactionManager = new TransactionManagerHibernate(sessionFactory);

        var clientTemplate = new DataTemplateHibernate<>(Client.class);
        var userTemplate = new DataTemplateHibernate<>(User.class);

        var dbServiceClient = new DbServiceClientImpl(transactionManager, clientTemplate);
        var dbServiceUser = new DbServiceUserImpl(transactionManager, userTemplate);

        ObjectMapper objectMapper = new ObjectMapper();
        TemplateProcessor templateProcessor = new TemplateProcessorImpl(TEMPLATES_DIR);
        UserAuthService authService = new UserAuthServiceImpl(dbServiceUser);

        ClientsWebServer clientsWebServer = new ClientsWebServerWithFilterBasedSecurity(WEB_SERVER_PORT,
                authService, dbServiceUser, dbServiceClient, objectMapper, templateProcessor);

        clientsWebServer.start();
        clientsWebServer.join();
    }
}
