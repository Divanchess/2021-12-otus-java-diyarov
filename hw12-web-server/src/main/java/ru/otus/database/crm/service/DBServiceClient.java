package ru.otus.database.crm.service;

import ru.otus.database.crm.model.Client;

import java.util.List;
import java.util.Optional;

public interface DBServiceClient {

    Client saveClient(Client client);

    Optional<Client> findById(long id);

    List<Client> findAll();

    Optional<Client> findRandomClient();
}
