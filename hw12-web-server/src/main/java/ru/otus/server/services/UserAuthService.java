package ru.otus.server.services;

public interface UserAuthService {
    boolean authenticate(String login, String password);
}
