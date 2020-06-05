package com.khanhlh.firewarning.login;

public interface LoginNavigator {

    void handleError(Boolean login);

    void login();

    void signUp();

    void createAccount();

    void onLoading();

    void backToLogin();

    void openMainActivity();

    void onSignUpSuccess();
}
