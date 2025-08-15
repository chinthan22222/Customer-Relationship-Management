package org.assignment.crm.controller;

import org.assignment.crm.entity.User;
import org.assignment.crm.exception.UserNotFound;
import org.assignment.crm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.findById(id)
                .orElseThrow(() -> new UserNotFound("User not found"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/username/{username}")
    public User getUserByUsername(@PathVariable String username) {
        return userService.findByUserName(username)
                .orElseThrow(() -> new UserNotFound("User not found with username: " + username));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/email/{email}")
    public User getUserByEmail(@PathVariable String email) {
        return userService.findByEmail(email)
                .orElseThrow(() -> new UserNotFound("User not found with email: " + email));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/activate")
    public User activateUser(@PathVariable Long id) {
        return userService.activateUser(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/deactivate")
    public User deactivateUser(@PathVariable Long id) {
        return userService.deactivateUser(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
