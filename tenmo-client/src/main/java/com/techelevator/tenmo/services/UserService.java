package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class UserService {

    private static final String API_BASE_URL = "http://localhost:8080/";
    private final RestTemplate restTemplate = new RestTemplate();

    private String baseUrl;
    private AuthenticatedUser currentUser;

    //Setter
    public void setCurrentUser(AuthenticatedUser currentUser) {
        this.currentUser = currentUser;
    }

    //Constructor
    public UserService(String baseUrl, AuthenticatedUser currentUser) {
        this.baseUrl = baseUrl;
        this.currentUser = currentUser;
    }

    public List<User> listUsers() {
        List<User> users = null;

        try {
            ResponseEntity<List<User>> response = restTemplate.exchange(API_BASE_URL + "users", HttpMethod.GET, makeAuthEntity(), new ParameterizedTypeReference<List<User>>() {});
            users =  response.getBody();
        } catch (Exception e) {
            BasicLogger.log(e.getMessage());
        }
        return users;
    }

    public User findUserByAccountId(Long accountId) {
        User user = null;

        try {
           ResponseEntity<User> response = restTemplate.exchange(API_BASE_URL + "user/account/" + accountId, HttpMethod.GET, makeAuthEntity(), User.class);
           user =  response.getBody();
        } catch (Exception e) {
           BasicLogger.log(e.getMessage());
        }
        return user;
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        return new HttpEntity<>(headers);
    }

}
