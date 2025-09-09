package com.mwu.profileservice.service;

import com.mwu.profileservice.DTO.LoginDto;
import com.mwu.profileservice.DTO.ProfileUpdate;
import com.mwu.profileservice.DTO.UserProfileDTO;
import com.mwu.profileservice.entity.UserProfile;
import com.mwu.profileservice.exception.ResourceNotFoundException;
import com.mwu.profileservice.repository.AddressRepository;
import com.mwu.profileservice.repository.UserProfileRepository;
import com.mwu.profileservice.security.JwtService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AddressRepository addressRepo;

    public UserProfileServiceImpl(UserProfileRepository userRepo, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public UserProfileDTO addNewCustomerProfile(UserProfileDTO userProfileDto) {
        // Create a new UserProfile entity but DON'T map addresses yet
        UserProfile userProfile = new UserProfile();
        userProfile.setUserName(userProfileDto.getUserName());
        userProfile.setEmail(userProfileDto.getEmail());
        userProfile.setMobileNumber(userProfileDto.getMobileNumber());
        userProfile.setDateOfBirth(userProfileDto.getDateOfBirth());
        userProfile.setGender(userProfileDto.getGender());
        userProfile.setRole(userProfileDto.getRole());
        userProfile.setPassword(passwordEncoder.encode(userProfileDto.getPassword()));

        // Save user to get the ID
        UserProfile savedUser = userRepo.save(userProfile);

        // No address handling here

        return modelMapper.map(savedUser, UserProfileDTO.class);
    }

    // Login user
    @Override
    public UserProfile loginProfile(LoginDto loginDto) {
        UserProfile user = userRepo.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Email id: " + loginDto.getEmail() + " is not found."));

        if (passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            return user;
        } else {
            throw new BadCredentialsException("Invalid Password!");
        }
    }

    @Override
    public List<UserProfile> getAllProfiles() {
        List<UserProfile> allUsers = userRepo.findAll();
        if (allUsers.isEmpty()) {
            throw new ResourceNotFoundException("No Users Found");
        }
        return allUsers;
    }

    @Override
    @Transactional
    public UserProfile getByProfileId(Long id) {
        if (id <= 0) {
            throw new ResourceNotFoundException("User Id with Id: " + id + " not found!.");
        }
        return userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Id: " + id + " Not Found."));
    }

    @Override
    public UserProfile updateProfile(ProfileUpdate profileUpdate, Long id) {
        UserProfile existingUser = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        existingUser.setUserName(profileUpdate.getUserName());
        existingUser.setMobileNumber(profileUpdate.getMobileNumber());
        return userRepo.save(existingUser);
    }

    @Override
    public void deleteProfile(Long id) {
        if (!userRepo.existsById(id)) {
            throw new ResourceNotFoundException("Id: " + id + " Not Found.");
        }
        userRepo.deleteById(id);
    }

    public String generateToken(String userId, String email, String role) {
        return jwtService.generateToken(userId, email, role);
    }

    public void validateToken(String token) {
        if(token == null || token.trim().isEmpty()) {
            throw new ResourceNotFoundException("Token cannot be null or empty");
        }
        jwtService.validateToken(token);
    }

    @Override
    public Long getProfileIdByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email: " + email + " Not Found."))
                .getUserId();
    }

    public UserProfile updateProfilePicture(Long userId, String imageId) {
        UserProfile userProfile = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User Profile not found"));

        userProfile.setProfilePictureId(imageId);
        return userRepo.save(userProfile);
    }

    public String getProfilePictureId(Long userId) {
        UserProfile userProfile = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User Profile not found"));

        return userProfile.getProfilePictureId();
    }
}