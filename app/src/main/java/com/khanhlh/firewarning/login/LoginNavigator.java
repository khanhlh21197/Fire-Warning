package com.khanhlh.firewarning.login;

public interface LoginNavigator {

    void handleError(Throwable throwable);

    void login();

    void signUp();

    void createAccount();

    void onLoading();

    void openMainActivity();

    void onSignUpSuccess();
}
