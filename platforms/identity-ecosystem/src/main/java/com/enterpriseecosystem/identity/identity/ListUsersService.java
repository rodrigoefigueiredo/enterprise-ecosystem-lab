package com.enterpriseecosystem.identity.identity;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListUsersService implements ListUsersUseCase {

    private final UserDao userDao;

    @Autowired
    public ListUsersService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Transactional(readOnly = true)
    public List<User> listUsers() {
        return userDao.findAllOrderByCreatedAtDesc();
    }
}
