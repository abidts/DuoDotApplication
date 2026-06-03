package com.duodot.constants;

import com.duodot.enums.PairNotificationTypeEnum;

public class NotificationTemplates {

    private NotificationTemplates() {}

    public static String getTitle(PairNotificationTypeEnum type) {
        return switch (type) {
            case PAIR_REQUEST_SENT -> "New Pair Request";
            case PAIRED            -> "Pair Request Accepted";
            case ACCEPTED          -> "Pair Request Accepted";
            case REJECTED          -> "Pair Request Declined";
            case UNPAIRED          -> "Pair Ended";
        };
    }

    public static String getBody(PairNotificationTypeEnum type, String actorUsername) {
        return switch (type) {
            case PAIR_REQUEST_SENT -> actorUsername + " wants to pair with you";
            case PAIRED            -> "You are now paired!";
            case ACCEPTED          -> "Your pair request was accepted";
            case REJECTED          -> "Your pair request was declined";
            case UNPAIRED          -> "Your partner has ended the pair";
        };
    }
}
