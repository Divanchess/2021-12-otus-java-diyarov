package ru.otus.server.servlet;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.database.crm.model.Address;
import ru.otus.database.crm.model.Client;
import ru.otus.database.crm.model.Phone;
import ru.otus.database.crm.service.DBServiceClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.SC_CREATED;


public class ClientsApiServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(ClientsApiServlet.class);

    private static final int ID_PATH_PARAM_POSITION = 1;

    private final DBServiceClient dbServiceClient;
    private final ObjectMapper objectMapper ;

    public ClientsApiServlet(DBServiceClient dbServiceClient, ObjectMapper objectMapper) {
        this.dbServiceClient = dbServiceClient;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long id = extractIdFromRequest(request);
        Client client = dbServiceClient.findById(id).orElse(null);
        LOG.info("Received GET client request client_id: [{}]", id);

        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream out = response.getOutputStream();
        out.print(objectMapper.writeValueAsString(client));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = mapper.readTree(getBody(request));

        String clientName = actualObj.get("clientName").textValue();
        String clientAddress = actualObj.get("clientAddress").textValue();
        JsonNode phoneNumbersJson = actualObj.get("clientPhoneNumbers");

        Address address = new Address(null, clientAddress);
        List<Phone> phones = new ArrayList<>();
        if (phoneNumbersJson.isArray()) {
            for (JsonNode objNode : phoneNumbersJson) {
                phones.add(new Phone(objNode.asText()));
            }
        }

        Client client = new Client(null, clientName, address, phones);
        Client savedClient = dbServiceClient.saveClient(client);

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(SC_CREATED);
        ServletOutputStream out = response.getOutputStream();
        out.print(objectMapper.writeValueAsString(savedClient));
    }

    private long extractIdFromRequest(HttpServletRequest request) {
        String[] path = request.getPathInfo().split("/");
        String id = (path.length > 1)? path[ID_PATH_PARAM_POSITION]: String.valueOf(- 1);
        return Long.parseLong(id);
    }

    public static String getBody(HttpServletRequest request)  {
        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            // throw ex;
            return "";
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {

                }
            }
        }

        body = stringBuilder.toString();
        return body;
    }

}
