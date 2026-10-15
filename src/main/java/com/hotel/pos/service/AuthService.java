package com.hotel.pos.service;

import com.hotel.pos.dto.LoginRequest;
import com.hotel.pos.dto.LoginResponse;
import com.hotel.pos.model.Role;
import com.hotel.pos.model.User;
import com.hotel.pos.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final Map<String, User> tokens = new ConcurrentHashMap<>();

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LoginResponse login(LoginRequest req) {
        // Recherche de l'utilisateur ACTIF dont le mot de passe correspond au code fourni
        User user = userRepository.findAll().stream()
                .filter(User::isActif)
                .filter(u -> encoder.matches(req.getCode(), u.getPassword()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Code invalide"));

        String token = UUID.randomUUID().toString();
        tokens.put(token, user);
        return new LoginResponse(token, user.getUsername(), user.getRole().name());
    }

    public void logout(String token) {
        if (token != null) tokens.remove(token);
    }

    public Optional<User> getUser(String token) {
        if (token == null) return Optional.empty();
        return Optional.ofNullable(tokens.get(token));
    }

    public User requireAuth(String token) {
        return getUser(token).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Non authentifie"));
    }

    public User requireRole(String token, Role role) {
        User user = requireAuth(token);
        if (user.getRole() != role) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acces refuse");
        }
        return user;
    }

    /** Autorise l'acces si l'utilisateur possede l'un des roles fournis. */
    public User requireAnyRole(String token, Role... roles) {
        User user = requireAuth(token);
        for (Role r : roles) {
            if (user.getRole() == r) return user;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acces refuse");
    }
}