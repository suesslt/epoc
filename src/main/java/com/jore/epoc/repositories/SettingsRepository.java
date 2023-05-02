package com.jore.epoc.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.jore.epoc.bo.settings.EpocSettings;

public interface SettingsRepository extends CrudRepository<EpocSettings, Integer> {
    Optional<EpocSettings> findByIsTemplate(boolean isTemplate);
}
