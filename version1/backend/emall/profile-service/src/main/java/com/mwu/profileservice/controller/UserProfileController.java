package com.mwu.profileservice.controller;

import com.mwu.profileservice.DTO.*;
import com.mwu.profileservice.client.ImageClient;
import com.mwu.profileservice.entity.UserProfile;
import com.mwu.profileservice.exception.ResourceNotFoundException;
import com.mwu.profileservice.repository.UserProfileRepository;
import com.mwu.profileservice.security.JwtService;
import com.mwu.profileservice.service.UserProfileService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UserProfileController {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);

    @Autowired
    private UserProfileService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserProfileRepository userRepo;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private ImageClient imgClient;


    @RateLimiter(name = "registerUser", fallbackMethod = "registerUserFallback")
    @PostMapping("/user/register")
    public ResponseEntity<UserProfileDTO> register(@Valid @RequestBody UserProfileDTO userProfileDto) {
        try {
            if (userProfileDto.getEmail() == null || userProfileDto.getEmail().isEmpty()) {
                throw new ResourceNotFoundException("Email cannot be null or empty");
            }

            UserProfileDTO newUser = userService.addNewCustomerProfile(userProfileDto);
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    public ResponseEntity<UserProfileDTO> registerUserFallback(@Valid @RequestBody UserProfileDTO userProfileDto, Throwable throwable) {

        UserProfileDTO newUser = userService.addNewCustomerProfile(userProfileDto);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }


    // Login User
    @PostMapping("/user/login")
    public ResponseEntity<ResponseDto> login(@RequestBody LoginDto loginDto) {
        try {

            Authentication authenticate = authManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
            if (authenticate.isAuthenticated()) {
                // Fetch user directly from the repository
                UserProfile user = userRepo.findByEmail(loginDto.getEmail())
                        .orElseThrow(() -> new ResourceNotFoundException("Email id: " + loginDto.getEmail() + " is not found."));

                String token = jwtService.generateToken(String.valueOf(user.getUserId()), user.getEmail(), user.getRole());

                ResponseDto resDto = new ResponseDto();
                resDto.setToken(token);
                resDto.setRole(user.getRole());

//                uthentication authenticate = authManager.authenticate(...) 这一步只是用用户名和密码进行认证，认证成功后返回的 Authentication 对象里包含用户的认证信息（如用户名、权限），但不包含 JWT token。
//                JWT token 是你手动生成的，通常会返回给前端，前端后续请求时带上 token，后端再通过过滤器（如 JwtAuthenticationFilter）解析 token，验证用户身份。
//                Authentication 只在认证流程中用，和 token 的存储、传递是分开的。

                return ResponseEntity.ok(resDto);
            } else {
                throw new BadCredentialsException("Invalid Login Credentials");
            }
        }  catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDto("Login failed: "+ e.getMessage()));
        }


    }


    // Get All User
    @GetMapping("/users")
    public ResponseEntity<List<UserProfile>> getAllProfiles() {
        List<UserProfile> profiles = userService.getAllProfiles();
        return ResponseEntity.ok(profiles);
    }


    // Get User by ID
    @Retry(name = "getProfileById", fallbackMethod = "getProfileByIdFallback")
    @GetMapping("/user/{id}")
    public ResponseEntity<UserProfile> getProfileById(@PathVariable Long id) {
        logger.debug("Get user by id controller method called");
        UserProfile profiles = userService.getByProfileId(id);
        return ResponseEntity.ok(profiles);
    }

    public ResponseEntity<UserProfile> getProfileByIdFallback(Long id, Throwable throwable) {
        UserProfile profile = userService.getByProfileId(id);
        return ResponseEntity.status(HttpStatus.OK).body(profile);
    }

    // Get Profile Id by Email
    @GetMapping("/users/profile/{email}")
    public ResponseEntity<Long> getProfileIdByEmail(@PathVariable String email) {
        Long profileId = userService.getProfileIdByEmail(email);
        return new ResponseEntity<>(profileId, HttpStatus.OK);
    }

    // Update User
    @PutMapping("/update/user/{id}")
    public ResponseEntity<String> updateProfile(@RequestBody ProfileUpdate userProfile, @PathVariable Long id) {
        userService.updateProfile(userProfile, id);
        return ResponseEntity.ok("Profile updated successfully");
    }

    // Delete user by ID
    @DeleteMapping("/delete/user/{id}")
    public ResponseEntity<String> deleteProfile(@PathVariable Long id) {
        userService.deleteProfile(id);
        return ResponseEntity.ok("Profile deleted successfully");
    }
    // Update profile picture
    @PutMapping("/users/{userId}/profile-picture")
    public ResponseEntity<?> updateProfilePicture(
            @PathVariable Long userId,
            @RequestParam("image") MultipartFile image) throws IOException {

        UserProfile updatedProfile = null;
        // Check file type
        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return new ResponseEntity<>("Only image files are allowed", HttpStatus.BAD_REQUEST);
        }

        // Call image service to upload the image
        ResponseEntity<ImageResponse> imageResponse =
                imgClient.uploadImage(image, "profile");
        if(null != imageResponse) {
            // Get the image ID and update the user profile
            String imageId = imageResponse.getBody().getId();

            // Update user profile with image ID reference
            updatedProfile = userService.updateProfilePicture(userId, imageId);
        }
        return new ResponseEntity<>(updatedProfile, HttpStatus.OK);
    }

    // Get profile picture
    @GetMapping("/users/profile-picture/{userId}")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable Long userId) {
        // Get the image ID from user profile
        String imageId = userService.getProfilePictureId(userId);

        if (imageId == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Proxy the request to image service
        return imgClient.getImage(imageId);
    }

    // Delete profile picture
    @DeleteMapping("/users/{userId}/profile-picture")
    public ResponseEntity<String> deleteProfilePicture(@PathVariable Long userId) {
        try {
            // Get the image ID from user profile
            String imageId = userService.getProfilePictureId(userId);

            if (imageId == null) {
                return new ResponseEntity<>("No profile picture found", HttpStatus.NOT_FOUND);
            }

            // Delete the image from image service
            imgClient.deleteImage(imageId);

            // Update user profile to remove image reference
            userService.updateProfilePicture(userId, null);

            return new ResponseEntity<>("Profile picture deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete profile picture: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
