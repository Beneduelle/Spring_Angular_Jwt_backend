package com.example.SpringJWT.resource;

import com.example.SpringJWT.domain.HttpResponse;
import com.example.SpringJWT.domain.User;
import com.example.SpringJWT.domain.UserPrincipal;
import com.example.SpringJWT.exception.domain.*;
import com.example.SpringJWT.service.UserService;
import com.example.SpringJWT.utility.JWTTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.example.SpringJWT.constant.FileConstant.*;
import static com.example.SpringJWT.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
//@RequestMapping(value = "/user")
@RequestMapping(path = {"/","/user"})
//@CrossOrigin("http://localhost:4200")
public class UserResource extends ExceptionHandling {

    public static final String EMAIL_SENT = "An email with a new password was sent to: ";
    public static final String USER_DELETED_SUCCESSFULLY = "User deleted successfully";
    private UserService userService;
    private AuthenticationManager authenticationManager;
    private JWTTokenProvider jwtTokenProvider;
    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    public UserResource(UserService userService, AuthenticationManager authenticationManager, JWTTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    //curl --location 'localhost:8081/user/login' \
    //--header 'Content-Type: application/json' \
    //--data '{
    //    "username":"rick4G",
    //    "password":"sUIcQmsSpx"
    //}'
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) {
        authenticate(user.getUsername(), user.getPassword());//breakpoint
        User loginUser = userService.findUserByUsername(user.getUsername());
        LOGGER.info("Found user: " + loginUser);
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        LOGGER.info("Users principals: " + userPrincipal);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        LOGGER.info("With headers: " + jwtHeader);
        return new ResponseEntity<>(loginUser, jwtHeader, OK);
    }

    //curl --location 'localhost:8081/user/register' \
    //--header 'Content-Type: application/json' \
    //--data-raw '{
    //    "firstName":"Rick",
    //    "lastName":"Guilermo",
    //    "email":"email@rick4.com",
    //    "username":"rick4G"
    //}
    //'
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) throws UserNotFoundException, UsernameExistsException, EmailExistsException, MessagingException {
        User newUser = userService.register(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail());
        return new ResponseEntity<>(newUser, OK);
    }

    //curl --location 'localhost:8081/user/add' \
    //--header 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJhdWQiOiJVc2VyIE1hbmFnZW1lbnQgUG9ydGFsIiwic3ViIjoicmljazRHIiwiaXNzIjoiR2V0IEFycmF5cywgTExDIiwiQXV0aG9yaXRpZXMiOlsidXNlcjpyZWFkIl0sImV4cCI6MTY4OTkzMjE5NCwiaWF0IjoxNjg5NTAwMTk0fQ.wdbDaCUI7HnDB9o3NVa-ZUdDpiEyulZ7j4S9LU4nlpesW2yih1wx5iwHogGTFWj2EzofrdxoTrx-7uihUJRXBg' \
    //--form 'firstName="Polly"' \
    //--form 'lastName="Galtieri"' \
    //--form 'username="PollyG"' \
    //--form 'email="pollyG@gmail.com"' \
    //--form 'role="ROLE_USER"' \
    //--form 'isActive="true"' \
    //--form 'isNotLocked="true"' \
    //--form 'profileImage=@"/home/user/Downloads/user.jpg"'
    @PostMapping("/add")
    public ResponseEntity<User> addNewUser(@RequestParam("firstName") String firstName,
                                           @RequestParam("lastName") String lastName,
                                           @RequestParam("username") String username,
                                           @RequestParam("email") String email,
                                           @RequestParam("role") String role,
                                           @RequestParam("isActive") String isActive,
                                           @RequestParam("isNotLocked") String isNotLocked,
                                           @RequestParam(value = "profileImage",
                                                   required = false) MultipartFile profileImage)
            throws UsernameExistsException, EmailExistsException, IOException {
        User newUser = userService.addNewUser(firstName, lastName,
                username, email, role, Boolean.parseBoolean(isNotLocked),
                Boolean.parseBoolean(isActive), profileImage);
        return new ResponseEntity<>(newUser, OK);
    }

    @PostMapping("/update")
    public ResponseEntity<User> update(@RequestParam("currentUsername") String currentUsername,
                                       @RequestParam("firstName") String firstName,
                                           @RequestParam("lastName") String lastName,
                                           @RequestParam("username") String username,
                                           @RequestParam("email") String email,
                                           @RequestParam("role") String role,
                                           @RequestParam("isActive") String isActive,
                                           @RequestParam("isNotLocked") String isNotLocked,
                                           @RequestParam(value = "profileImage",
                                                   required = false) MultipartFile profileImage)
            throws UsernameExistsException, EmailExistsException, IOException {
        LOGGER.info("Update user");
        User updatedUser = userService.updateUser(currentUsername,firstName, lastName,
                username, email, role, Boolean.parseBoolean(isNotLocked),
                Boolean.parseBoolean(isActive), profileImage);
        return new ResponseEntity<>(updatedUser, OK);
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<User> getUser(@PathVariable("username") String username) {
        User user = userService.findUserByUsername(username);
        return new ResponseEntity<>(user, OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getUsers();
        return new ResponseEntity<>(users, OK);
    }

    @GetMapping("/resetPassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) throws EmailNotFoundException, MessagingException {
        userService.resetPassword(email);
        return response(OK, EMAIL_SENT + email);
    }

//    @DeleteMapping("/delete/{id}")
//    @PreAuthorize("hasAuthority('user:delete')")
//    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("id") long id) {
//        userService.deleteUser(id);
////        return response(NO_CONTENT, USER_DELETED_SUCCESSFULLY);
//        return response(OK, USER_DELETED_SUCCESSFULLY);
//    }

    @DeleteMapping("/delete/{username}")
    @PreAuthorize("hasAuthority('user:delete')")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("username") String username) {
        userService.deleteUser(username);
//        return response(NO_CONTENT, USER_DELETED_SUCCESSFULLY);
        return response(OK, USER_DELETED_SUCCESSFULLY);
    }

    @PostMapping("/updateProfileImage")
    public ResponseEntity<User> updateProfileImage(@RequestParam("username") String username,
                                       @RequestParam(value = "profileImage") MultipartFile profileImage) throws UsernameExistsException, EmailExistsException, IOException {
        User user = userService.updateProfileImage(username, profileImage);
        return new ResponseEntity<>(user, OK);
    }

//    @GetMapping(path = "/image/{username}/{filename}", produces = {MediaType.IMAGE_JPEG_VALUE,GIF})
    @GetMapping(path = "/image/{username}/{filename}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable("username") String username, @PathVariable("filename") String filename) throws IOException {
        return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + filename));
    }

    @GetMapping(path = "/image/profile/{username}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getTempProfileImage(@PathVariable("username") String username) throws IOException {
        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
        LOGGER.info("getTempProfileImage: " + url);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (InputStream inputStream = url.openStream()) {
            int bytesRead;
            byte[] chunk = new byte[1024];
            while((bytesRead = inputStream.read(chunk)) > 0) {
                byteArrayOutputStream.write(chunk, 0,bytesRead);
                //0-1024 bytes
                //0-1024 bytes
                //0-1024 bytes
            }

        }
        return byteArrayOutputStream.toByteArray();
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        HttpResponse body = new HttpResponse(httpStatus.value(),
                httpStatus,
                httpStatus.getReasonPhrase().toUpperCase(),
                message.toUpperCase());
        return new ResponseEntity<>(body, httpStatus);
    }


    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(userPrincipal));
        return headers;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
