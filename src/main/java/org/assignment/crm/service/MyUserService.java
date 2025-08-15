package org.assignment.crm.service;

import org.assignment.crm.entity.User;
import org.assignment.crm.entity.UserPrincipal;
import org.assignment.crm.exception.UsernameNotFound;
import org.assignment.crm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = this.userRepository.findUserByUserName(username);
        if (user.isEmpty()) {
            throw new UsernameNotFound("Not found");
        }
        return new UserPrincipal(user.get());
    }
}
