package com.lucasmoraist.lms.adapter.web.dto.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class UpdateUserDTO {

    @Email(message = "Invalid email format")
    private String email;
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;
    @Valid
    private ProfileUpdateDTO profile;

    @Getter
    @ToString
    public static class ProfileUpdateDTO {
        @Size(min = 2, max = 255, message = "Name must be between 3 and 255 characters")
        private String name;
        private String bio;
        @Valid
        private List<SocialLinkUpdateDTO> socialLinks;
    }

    @Getter
    @ToString
    public static class SocialLinkUpdateDTO {
        @Size(max = 100, message = "Name must be less than 100 characters")
        private String name;
        @Size(max = 255, message = "Link must be less than 255 characters")
        private String link;
    }

}
