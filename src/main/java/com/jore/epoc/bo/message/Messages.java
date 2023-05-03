package com.jore.epoc.bo.message;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class Messages {
    private static ResourceBundle myRessources = null;

    public static String getMessage(String key, Object... param) {
        if (myRessources == null) {
            load();
        }
        MessageFormat format = new MessageFormat("");
        format.applyPattern(myRessources.getString(key));
        return format.format(param);
    }

    public static void load() {
        load("ApplicationMessages");
    }

    public static void load(String resourceName) {
        myRessources = ResourceBundle.getBundle(resourceName);
    }
}
