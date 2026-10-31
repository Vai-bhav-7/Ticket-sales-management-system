package org.example.ticketSalesSystem.Controller;

import org.example.ticketSalesSystem.Model.User;
import org.example.ticketSalesSystem.Repository.UserRepository;
import org.example.ticketSalesSystem.service.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {

        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody User user) {

        User existingUser = userRepository
                .findByUsername(user.getUsername())
                .orElse(null);

        if (existingUser == null) {
            return "User not found";
        }

        if (!passwordEncoder.matches(
                user.getPassword(),
                existingUser.getPassword())) {

            return "Invalid password";
        }

        return jwtService.generateToken(existingUser);
    }

    @GetMapping
    public List<User> getUsers() {
        return userRepository.findAll();
    }
}
