package com.lucasmoraist.lms.infrastructure.database.persistence;

import com.lucasmoraist.lms.domain.model.catalog.Module;
import com.lucasmoraist.lms.infrastructure.database.entity.catalog.ModuleEntity;
import com.lucasmoraist.lms.infrastructure.database.repository.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ModulePersistence {

    private final ModuleRepository moduleRepository;
    private final ModelMapper modelMapper;

    public Optional<Module> findById(UUID moduleId) {
        return this.moduleRepository.findById(moduleId)
                .map(entity -> this.modelMapper.map(entity, Module.class));
    }

    public Module save(Module module) {
        ModuleEntity entity = this.modelMapper.map(module, ModuleEntity.class);
        ModuleEntity savedEntity = this.moduleRepository.save(entity);
        return this.modelMapper.map(savedEntity, Module.class);
    }

}
