package com.lucasmoraist.lms.infrastructure.database.persistence;

import com.lucasmoraist.lms.domain.enums.RoleType;
import com.lucasmoraist.lms.domain.model.user.Identity;
import com.lucasmoraist.lms.domain.model.user.Profile;
import com.lucasmoraist.lms.domain.model.user.Role;
import com.lucasmoraist.lms.infrastructure.database.entity.user.IdentityEntity;
import com.lucasmoraist.lms.infrastructure.database.repository.IdentityRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("default")
class IdentityPersistenceIntegrationTest {

    @Autowired
    IdentityPersistence identityPersistence;

    @Autowired
    IdentityRepository identityRepository;

    Identity identity;

    @BeforeEach
    void setUp() {
        this.identityRepository.deleteAll();

        Role role = Role.builder()
                .name(RoleType.USER)
                .build();
        Profile profile = Profile.builder()
                .name("John Doe")
                .build();

        this.identity = Identity.builder()
                .email("johndoe@email.com")
                .password("password123")
                .isActive(true)
                .roles(Set.of(role))
                .profile(profile)
                .build();
    }

    @Test
    @DisplayName("Should save an identity successfully")
    void case01() {
        this.identityPersistence.save(this.identity);

        List<IdentityEntity> entities = this.identityRepository.findAll();
        assertThat(entities).hasSize(1);
        assertThat(entities.getFirst().getEmail()).isEqualTo("johndoe@email.com");
    }

    @Test
    @DisplayName("Should find identity by email")
    void case02() {
        this.identityPersistence.save(this.identity);

        Optional<Identity> found = this.identityPersistence.findByEmail("johndoe@email.com");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("johndoe@email.com");
    }

    @Test
    @DisplayName("Should return empty when email not found")
    void case03() {
        Optional<Identity> found = this.identityPersistence.findByEmail("nonexistent@example.com");
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should delete identity by id")
    void case04() {
        Identity savedIdentity = this.identityPersistence.save(this.identity);
        this.identityPersistence.deleteByEntity(savedIdentity.getId());

        Optional<Identity> found = this.identityPersistence.findById(savedIdentity.getId());
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should return empty when identity not found by id")
    void case05() {
        Optional<Identity> found = this.identityPersistence.findById(UUID.randomUUID());
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent identity")
    void case06() {
        UUID nonExistentId = UUID.randomUUID();
        assertThrows(EntityNotFoundException.class,
                () -> this.identityPersistence.deleteByEntity(nonExistentId));
    }

}