package com.lucasmoraist.lms.infrastructure.database.repository;

import com.lucasmoraist.lms.infrastructure.database.entity.catalog.ModuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ModuleRepository extends JpaRepository<ModuleEntity, UUID> {

}
