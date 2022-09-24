package org.techtown.face.utilites;

import java.util.HashMap;

public class Constants {
    public static final String KEY_COLLECTION_IMAGES = "images";
    public static final String KEY_COLLECTION_USERS = "users";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PREFERENCE_NAME = "chatPreference";
    public static final String KEY_IS_SIGNED_IN = "isSignedIn";
    public static final String KEY_USER_ID = "userid";
    public static final String KEY_IMAGE = "image";

    public static final String KEY_FCM_TOKEN ="fcmToken";
    public static final String KEY_USER = "user";

    public static final String KEY_COLLECTION_CHAT = "chat";
    public static final String KEY_SENDER_ID = "senderId";
    public static final String KEY_RECEIVER_ID = "receiverId";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TIMESTAMP = "timestamp";

    public static final String KEY_COLLECTION_CONVERSIONS= "conversions";
    public static final String KEY_SENDER_NAME= "senderName";
    public static final String KEY_RECEIVER_NAME= "receiverName";
    public static final String KEY_SENDER_IMAGE= "senderImage";
    public static final String KEY_RECEIVER_IMAGE= "receiverImage";
    public static final String KEY_LAST_MESSAGE= "lastMessage";
    public static final String KEY_AVAILABILITY = "availability";

    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";

    public static final String KEY_BIRTHDAY = "birthday";
    public static final String KEY_MOBILE = "mobile";
    public static final String KEY_IDEAL_CONTACT = "ideal_contact";
    public static final String KEY_MIN_CONTACT = "min_contact";
    public static final String KEY_THEME_LIKE = "theme_like";
    public static final String KEY_THEME_DISLIKE = "theme_dislike";
    public static final String KEY_EXPRESSION = "expression";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_MEET = "meet";
    public static final String KEY_ANGLE = "angle";
    public static final String KEY_WINDOW = "window";

    public static final String KEY_PATH = "path";
    public static final String KEY_COLLECTION_NOTIFICATION = "notifications";
    public static final String KEY_NOTIFICATION = "notification";
    public static final String KEY_TYPE = "type";
    public static final String KEY_FAMILY_REQUEST = "family_request";
    public static final String KEY_MEET_REQUEST = "meet_request";

    public static HashMap<String, String> remoteMsgHeaders = null;
    public static HashMap<String,String> getRemoteMsgHeaders(){
        if (remoteMsgHeaders == null){
            remoteMsgHeaders = new HashMap<>();
            remoteMsgHeaders.put(REMOTE_MSG_AUTHORIZATION,
                    "key =AAAACLK5gyw:APA91bEg77UffytMora_BaOl7z-9Yl-f9TYS9IecisMeJmXk6u5480_EMlBwQ3dm3tmvo3JkXP9vZfEIC4CrFl1Vg8gyAJVAUNV4LWft10SOGG8lGe0VXGm3eDhb-TM0osdtdFKtzFWt"
            );
            remoteMsgHeaders.put(REMOTE_MSG_CONTENT_TYPE,
                    "application/json");
        }
        return remoteMsgHeaders;
    }


}
