package com.hotel.pos.service;

import com.hotel.pos.dto.CreateUserRequest;
import com.hotel.pos.dto.UserDto;
import com.hotel.pos.model.Role;
import com.hotel.pos.model.User;
import com.hotel.pos.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gestion des comptes utilisateurs (création de caissiers, activation, code de connexion).
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .sorted(Comparator.comparing(User::getRole).thenComparing(User::getUsername, String.CASE_INSENSITIVE_ORDER))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public UserDto create(CreateUserRequest req) {
        String username = req.getUsername() == null ? "" : req.getUsername().trim();
        String code = req.getCode() == null ? "" : req.getCode().trim();

        if (username.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le nom d'utilisateur est requis");
        }
        if (code.length() < 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le code doit contenir au moins 4 caracteres");
        }
        if (userRepository.findByUsername(username).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce nom d'utilisateur existe deja");
        }
        if (codeDejaUtilise(code)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce code est deja utilise par un autre compte");
        }

        Role role = req.getRole() != null ? req.getRole() : Role.CAISSIER;
        User user = new User(null, username, encoder.encode(code), role, true, LocalDateTime.now());
        return toDto(userRepository.save(user));
    }

    public UserDto setActif(Long id, boolean actif, Long actorId) {
        User user = find(id);
        if (!actif && user.getId().equals(actorId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vous ne pouvez pas desactiver votre propre compte");
        }
        if (!actif && user.getRole() == Role.ADMIN && compterAdminsActifs() <= 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Impossible de desactiver le dernier administrateur");
        }
        user.setActif(actif);
        return toDto(userRepository.save(user));
    }

    public UserDto resetCode(Long id, String code) {
        String c = code == null ? "" : code.trim();
        if (c.length() < 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le code doit contenir au moins 4 caracteres");
        }
        User user = find(id);
        if (codeDejaUtilise(c, user.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce code est deja utilise par un autre compte");
        }
        user.setPassword(encoder.encode(c));
        return toDto(userRepository.save(user));
    }

    // ------------------------------------------------------------------

    private boolean codeDejaUtilise(String code) {
        return codeDejaUtilise(code, null);
    }

    private boolean codeDejaUtilise(String code, Long exceptId) {
        return userRepository.findAll().stream()
                .filter(u -> exceptId == null || !u.getId().equals(exceptId))
                .anyMatch(u -> encoder.matches(code, u.getPassword()));
    }

    private long compterAdminsActifs() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.ADMIN && u.isActif())
                .count();
    }

    private User find(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
    }

    private UserDto toDto(User u) {
        return new UserDto(u.getId(), u.getUsername(), u.getRole(), u.isActif(), u.getDateCreation());
    }
}
