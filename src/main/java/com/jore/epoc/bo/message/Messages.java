package com.jore.epoc.bo.message;

import java.text.MessageFormat;

import org.springframework.context.support.ResourceBundleMessageSource;

public class Messages {
    private static ResourceBundleMessageSource myRessources = null;

    public static String getMessage(String key, Object... param) {
        if (myRessources == null) {
            load();
        }
        MessageFormat format = new MessageFormat("");
        format.applyPattern(myRessources.getMessage(key, null, null));
        return format.format(param);
    }

    public static void load() {
        load("ApplicationMessages");
    }

    public static void load(String resourceName) {
        myRessources = new ResourceBundleMessageSource();
        myRessources.setBasename(resourceName);
        myRessources.setDefaultEncoding("UTF-8");
        myRessources.setUseCodeAsDefaultMessage(true);
    }
}
