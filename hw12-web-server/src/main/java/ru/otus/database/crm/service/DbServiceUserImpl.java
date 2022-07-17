package ru.otus.database.crm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.database.core.repository.DataTemplate;
import ru.otus.database.core.sessionmanager.TransactionManager;
import ru.otus.database.crm.model.User;
import ru.otus.database.crm.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class DbServiceUserImpl implements DBServiceUser {
    private static final Logger log = LoggerFactory.getLogger(DbServiceUserImpl.class);

    private final DataTemplate<User> userDataTemplate;
    private final TransactionManager transactionManager;

    public DbServiceUserImpl(TransactionManager transactionManager, DataTemplate<User> userDataTemplate) {
        this.transactionManager = transactionManager;
        this.userDataTemplate = userDataTemplate;
    }

    @Override
    public User saveUser(User user) {
        return transactionManager.doInTransaction(session -> {
            var userCloned = user.clone();
            if (user.getId() == null) {
                userDataTemplate.insert(session, userCloned);
                log.info("created user: {}", userCloned);
                return userCloned;
            }
            userDataTemplate.update(session, userCloned);
            log.info("updated user: {}", userCloned);
            return userCloned;
        });
    }

    @Override
    public Optional<User> findById(long id) {
        return transactionManager.doInReadOnlyTransaction(session -> {
            var userOptional = userDataTemplate.findById(session, id);
            log.info("user: {}", userOptional);
            return userOptional;
        });
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return transactionManager.doInReadOnlyTransaction(session -> {
            var userOptional = Optional.ofNullable(userDataTemplate.findByEntityField(session, "login", login).stream().findFirst()).orElse(null);
            log.info("user: {}", userOptional);
            return userOptional;
        });
    }

    @Override
    public List<User> findAll() {
        return transactionManager.doInReadOnlyTransaction(session -> {
            var userList = userDataTemplate.findAll(session);
            log.info("userList:{}", userList);
            return userList;
       });
    }
}
