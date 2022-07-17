package ru.otus.database.crm.service;

import ru.otus.database.crm.model.User;

import java.util.List;
import java.util.Optional;

public interface DBServiceUser {

    User saveUser(User user);

    Optional<User> findById(long id);

    Optional<User> findByLogin(String login);

    List<User> findAll();
}
