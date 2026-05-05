package com.lucasmoraist.lms.domain.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Identity {

    private UUID id;
    private String email;
    private String password;
    private Set<Role> roles;
    private Boolean isActive;
    @JsonManagedReference
    private Profile profile;

}
