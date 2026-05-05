package com.lucasmoraist.lms.adapter.web.dto.user;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class UpdateUserDTO {

    private String email;
    private String password;
    private ProfileUpdateDTO profile;

    @Getter
    @ToString
    public static class ProfileUpdateDTO {
        private String name;
        private String bio;
        private List<SocialLinkUpdateDTO> socialLinks;
    }

    @Getter
    @ToString
    public static class SocialLinkUpdateDTO {
        private String name;
        private String link;
    }

}
