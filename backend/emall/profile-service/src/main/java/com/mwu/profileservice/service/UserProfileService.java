package com.mwu.profileservice.service;

import com.mwu.profileservice.DTO.LoginDto;
import com.mwu.profileservice.DTO.ProfileUpdate;
import com.mwu.profileservice.DTO.UserProfileDTO;
import com.mwu.profileservice.entity.UserProfile;
import org.springframework.stereotype.Component;

import java.util.List;


public interface UserProfileService {

    UserProfileDTO addNewCustomerProfile(UserProfileDTO userProfileDTO);
    UserProfile loginProfile(LoginDto loginDto);
    List<UserProfile> getAllProfiles();
    UserProfile getByProfileId(Long id);
    UserProfile updateProfile(ProfileUpdate profileUpdate, Long id);
    void deleteProfile(Long id);
    Long getProfileIdByEmail(String email);
    UserProfile updateProfilePicture(Long userId, String imageId);
    String getProfilePictureId(Long userId);
}
