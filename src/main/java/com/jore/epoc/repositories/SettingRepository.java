package com.jore.epoc.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.jore.epoc.bo.settings.EpocSetting;

public interface SettingRepository extends CrudRepository<EpocSetting, Integer> {
    Optional<EpocSetting> findBySettingKey(String key);
}
