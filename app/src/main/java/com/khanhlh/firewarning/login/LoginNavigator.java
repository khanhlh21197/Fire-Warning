package com.khanhlh.firewarning.login;

public interface LoginNavigator {

    void handleError(Boolean login);

    void login();

    void signUp();

    void createAccount();

    void onLoading();

    void backToLogin();

    void showPassword();

    void hidePassword();

    void showRepassword();

    void hideRePassword();

    void openMainActivity();

    void onSignUpSuccess();
}
