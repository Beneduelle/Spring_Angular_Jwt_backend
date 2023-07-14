package com.example.SpringJWT.service;

import com.example.SpringJWT.domain.User;
import com.example.SpringJWT.exception.domain.EmailExistsException;
import com.example.SpringJWT.exception.domain.UsernameExistsException;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserService {
    User register(String firstName, String lastName, String username, String email) throws UsernameExistsException, EmailExistsException;

    List<User> getUsers();
    User findUserByUsername(String username);

    User findUserByEmail(String email);
}
