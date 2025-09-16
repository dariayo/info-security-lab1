package org.example.services;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.example.models.User;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(String username, String password, String email) {
        String hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());

        User user = new User(username, hashedPassword, email);
        return userRepository.save(user);
    }

    public Optional<User> validateUser(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
            if (result.verified) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}