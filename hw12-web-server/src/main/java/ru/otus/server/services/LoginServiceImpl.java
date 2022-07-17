package ru.otus.server.services;

import org.eclipse.jetty.security.AbstractLoginService;
import org.eclipse.jetty.security.RolePrincipal;
import org.eclipse.jetty.security.UserPrincipal;
import org.eclipse.jetty.util.security.Password;
import ru.otus.database.crm.service.DBServiceUser;
import ru.otus.database.crm.model.User;

import java.util.List;
import java.util.Optional;

public class LoginServiceImpl extends AbstractLoginService {

    private final DBServiceUser dBServiceUser;

    public LoginServiceImpl(DBServiceUser dBServiceUser) {
        this.dBServiceUser = dBServiceUser;
    }


    @Override
    protected List<RolePrincipal> loadRoleInfo(UserPrincipal userPrincipal) {
        return List.of(new RolePrincipal("user"));
    }

    @Override
    protected UserPrincipal loadUserInfo(String login) {
        System.out.println(String.format("LoginService#loadUserInfo(%s)", login));
        Optional<User> dbUser = dBServiceUser.findByLogin(login);
        return dbUser.map(u -> new UserPrincipal(u.getLogin(), new Password(u.getPassword()))).orElse(null);
    }
}
