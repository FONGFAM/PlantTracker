package com.planttracker.Services;

import com.planttracker.Models.Users;
import com.planttracker.Repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

        private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

        @Autowired
        private UserRepository userRepo;

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                Users user = userRepo.findByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException("User Not Found: " + username));

                logger.debug("Login: {} | roles: {}", user.getUsername(),
                                user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toList()));

                return new org.springframework.security.core.userdetails.User(
                                user.getUsername(),
                                user.getPassword(),
                                user.getRoles().stream()
                                                .map(role -> new SimpleGrantedAuthority(role.getName()))
                                                .collect(Collectors.toList()));
        }
}
