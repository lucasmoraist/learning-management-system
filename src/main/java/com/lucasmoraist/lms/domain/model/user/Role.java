package com.lucasmoraist.lms.domain.model.user;

import com.lucasmoraist.lms.domain.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    private UUID id;
    private RoleType name;

}
