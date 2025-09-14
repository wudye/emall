package com.mwu.profileservice.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "user-service", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "mobile_number")
})
@JsonPropertyOrder({ "profileId", "userName", "email", "mobileNumber", "dob", "gender", "role", "password", "address" })
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotBlank
    @Size(min = 5 , max = 25)
    private String userName;

    @Column(name = "email", unique = true, nullable = false)
    @Email
    @Size(min = 10 , max = 50)
    private String email;

    @Column(name = "mobile_number" , unique = true, nullable = false)
    private Long mobileNumber;

    @JsonProperty("dob")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    private String gender;

    private String profilePictureId;

    private String role;

    @Column(name = "password",nullable = false)
//	@Size(min = 8, max = 50, message = "Password must contain atleast 8 characters")
    private String password;

    @JsonManagedReference
    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Address> address;

}
