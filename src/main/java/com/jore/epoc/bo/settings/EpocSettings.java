package com.jore.epoc.bo.settings;

import java.util.ArrayList;
import java.util.List;

import com.jore.jpa.BusinessObject;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

@Entity
public class EpocSettings extends BusinessObject {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "settings", orphanRemoval = true)
    private List<EpocSetting> settings = new ArrayList<>();
    private boolean isTemplate;

    public void addSetting(EpocSetting setting) {
        setting.setSettings(this);
        settings.add(setting);
    }

    public boolean isTemplate() {
        return isTemplate;
    }

    public void setTemplate(boolean isTemplate) {
        this.isTemplate = isTemplate;
    }
}
