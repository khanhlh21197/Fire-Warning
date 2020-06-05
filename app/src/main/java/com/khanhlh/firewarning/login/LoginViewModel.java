package com.khanhlh.firewarning.login;

import android.text.TextUtils;

import com.khanhlh.firewarning.base.BaseViewModel;
import com.khanhlh.firewarning.mqtt.MqttCommon;
import com.khanhlh.firewarning.utils.CommonUtils;

public class LoginViewModel extends BaseViewModel<LoginNavigator> {
    public boolean isEmailAndPasswordValid(String email,
                                           String password) {
        // validate email and password
        if (TextUtils.isEmpty(email)) {
            return false;
        }
        if (!CommonUtils.isEmailValid(email)) {
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            return false;
        }
        return true;
    }

    public boolean validateSignUp(String email,
                                  String password,
                                  String rePassword,
                                  String name,
                                  String phoneNumber) {
        if (TextUtils.isEmpty(email)
                || TextUtils.isEmpty(password)
                || TextUtils.isEmpty(rePassword)
                || TextUtils.isEmpty(name)
                || TextUtils.isEmpty(phoneNumber)) {
            return false;
        }

        if (!CommonUtils.isEmailValid(email)) {
            return false;
        }

        if (!rePassword.equals(password)) {
            return false;
        }

        return true;
    }

    public void login(MqttCommon common,
                      String pubTopic,
                      String email,
                      String password) {
        loading();
        String publishMessage = email + "&" + password;
        common.publishMessage(pubTopic, publishMessage);
    }

    public void signUp(MqttCommon common,
                       String pubTopic,
                       String email,
                       String password,
                       String name,
                       String phoneNumber) {
        loading();
        String publishMessage = email + "&" + password + "&" + name + "&" + phoneNumber;
        common.publishMessage(pubTopic, publishMessage);
    }

    public void onCreateAccountClick() {
        getNavigator().createAccount();
    }

    public void onLoginClick() {
        getNavigator().login();
    }

    public void onSignUpClick() {
        getNavigator().signUp();
    }

    public void onBackToLoginClick() {
        getNavigator().backToLogin();
    }

    private void loading() {
        setIsLoading(true);
        getNavigator().onLoading();
    }
}
