package keni.demo.orderservice.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import keni.demo.orderservice.dto.User;

@Service
public class UserClientService {

    private final RestTemplate restTemplate;

    public UserClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "getDefaultUser")
    public User getUserById(Long id) {
        return restTemplate.getForObject("http://user-service/users/" + id, User.class);
    }

    public User getDefaultUser(Long id, Throwable t) {
        System.out.println("Fallback triggered for userId=" + id + ", reason=" + t);
        User defaultUser = new User();
        defaultUser.setId(id);
        defaultUser.setName("Unknown User");
        defaultUser.setEmail("unavailable@service.com");
        return defaultUser;
    }
}

