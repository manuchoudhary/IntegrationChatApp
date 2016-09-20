package com.social.mannu.integrationchatapp.fcmnotification;

import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FCMInstanceIDListnerService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        if(MobiComUserPreference.getInstance(this).isRegistered()){
            FCMRegistrationUtils fcmRegistrationUtils = new FCMRegistrationUtils(this);
            fcmRegistrationUtils.setUpFcmNotification();
        }
    }
}
