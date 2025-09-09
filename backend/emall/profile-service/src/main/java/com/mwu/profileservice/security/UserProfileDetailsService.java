package com.mwu.profileservice.security;

import com.mwu.profileservice.entity.UserProfile;
import com.mwu.profileservice.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserProfileDetailsService implements UserDetailsService {

    @Autowired
    private UserProfileRepository userRepo;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<UserProfile> credentials = userRepo.findByEmail(email);

        return credentials.map(UserProfileDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException( "User not found with mail id: "+ email));
    }
}
