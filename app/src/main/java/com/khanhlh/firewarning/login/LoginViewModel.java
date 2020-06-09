package com.khanhlh.firewarning.login;

import android.text.TextUtils;

import androidx.databinding.ObservableBoolean;

import com.khanhlh.firewarning.base.BaseViewModel;
import com.khanhlh.firewarning.data.model.db.User;
import com.khanhlh.firewarning.mqtt.MqttCommon;
import com.khanhlh.firewarning.utils.CommonUtils;

public class LoginViewModel extends BaseViewModel<LoginNavigator> {
    private final ObservableBoolean showPassword = new ObservableBoolean();
    private final ObservableBoolean showRePassword = new ObservableBoolean();

    public ObservableBoolean getShowRePassword() {
        return showRePassword;
    }

    public void setShowRePassword(boolean showRePassword) {
        this.showRePassword.set(showRePassword);
    }

    public ObservableBoolean getShowPassword() {
        return showPassword;
    }

    public void setShowPassword(boolean showPassword) {
        this.showPassword.set(showPassword);
    }

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
                                  String phoneNumber,
                                  String address) {
        if (TextUtils.isEmpty(email)
                || TextUtils.isEmpty(password)
                || TextUtils.isEmpty(rePassword)
                || TextUtils.isEmpty(name)
                || TextUtils.isEmpty(phoneNumber)
                || TextUtils.isEmpty(address)) {
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
        User user = new User(email, password);
//        String publishMessage = email + "&" + password;
        common.publishMessage(pubTopic, user);
    }

    public void signUp(MqttCommon common,
                       String pubTopic,
                       String email,
                       String password,
                       String name,
                       String phoneNumber,
                       String address) {
        loading();
        User user = new User(email, password, name, phoneNumber, address);
//        String publishMessage = email + "&" + password + "&" + name + "&" + phoneNumber;
        common.publishMessage(pubTopic, user);
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

    public void onShowPasswordClick() {
        if (!CommonUtils.isNullOrEmpty(showPassword)) {
            if (showPassword.get()) {
                getNavigator().showPassword();
            } else {
                getNavigator().hidePassword();
            }
        }
    }

    public void onShowRePasswordClick() {
        if (!CommonUtils.isNullOrEmpty(showRePassword)) {
            if (showRePassword.get()) {
                getNavigator().showRepassword();
            } else {
                getNavigator().hideRePassword();
            }
        }
    }

    private void loading() {
        setIsLoading(true);
        getNavigator().onLoading();
    }
}
