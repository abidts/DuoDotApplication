package com.duodot.Utils;

import com.duodot.constants.CommonConstants;

import java.util.UUID;

public enum CommonUtils {
    INSTANCE;



    public String uniqueIdentifier() {
        return UUID.randomUUID().toString().replace(CommonConstants.INSTANCE.HYPHEN_STRING, CommonConstants.INSTANCE.BLANK_STRING);
    }
}
