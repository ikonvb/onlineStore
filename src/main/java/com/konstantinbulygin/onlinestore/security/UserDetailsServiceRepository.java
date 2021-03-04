package com.konstantinbulygin.onlinestore.security;

import com.konstantinbulygin.onlinestore.model.AdminRepository;
import com.konstantinbulygin.onlinestore.model.UserRepository;
import com.konstantinbulygin.onlinestore.model.data.Admin;
import com.konstantinbulygin.onlinestore.model.data.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceRepository implements UserDetailsService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    public UserDetailsServiceRepository(UserRepository userRepository, AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(userName);
        Admin admin = adminRepository.findByUsername(userName);

        if (user != null) {
            return user;
        }

        if (admin != null) {
            return admin;
        }

        throw new UsernameNotFoundException("User: " + userName + " not found!");
    }
}
