package com.example.SpringJWT.resource;

import com.example.SpringJWT.exception.domain.EmailExistsException;
import com.example.SpringJWT.exception.domain.ExceptionHandling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping(value = "/user")
@RequestMapping(path = {"/","/user"})
public class UserResource extends ExceptionHandling {

    @GetMapping("/home")
    public String showUser() throws EmailExistsException {
//        return "application works";
        throw new EmailExistsException("this email address is already taken");
    }
}
