package com.example.SpringJWT.service;

import com.example.SpringJWT.domain.User;
import com.example.SpringJWT.exception.domain.EmailExistsException;
import com.example.SpringJWT.exception.domain.EmailNotFoundException;
import com.example.SpringJWT.exception.domain.NotAnImageFileException;
import com.example.SpringJWT.exception.domain.UsernameExistsException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public interface UserService {
    User register(String firstName, String lastName, String username, String email) throws UsernameExistsException, EmailExistsException, MessagingException;

    List<User> getUsers();
    User findUserByUsername(String username);

    User findUserByEmail(String email);

    User addNewUser(String firstName, String lastName,
                    String username, String email,
                    String role, boolean isNotLocked,
                    boolean isActive, MultipartFile profileImage) throws UsernameExistsException, EmailExistsException, IOException, NotAnImageFileException;

    User updateUser(String currentUsername, String newFirstName, String newLastName,
                    String newUsername, String newEmail,
                    String role, boolean isNotLocked,
                    boolean isActive, MultipartFile profileImage) throws UsernameExistsException, EmailExistsException, IOException, NotAnImageFileException;

//    void deleteUser(long id);
    void deleteUser(String username);

    void resetPassword(String email) throws MessagingException, EmailNotFoundException;

    User updateProfileImage(String username, MultipartFile profileImage) throws UsernameExistsException, EmailExistsException, IOException, NotAnImageFileException;
}
