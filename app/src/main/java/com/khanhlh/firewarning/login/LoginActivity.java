package com.khanhlh.firewarning.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import com.khanhlh.firewarning.BR;
import com.khanhlh.firewarning.R;
import com.khanhlh.firewarning.base.BaseActivity;
import com.khanhlh.firewarning.databinding.ActivityLoginBinding;
import com.khanhlh.firewarning.main.MainActivity;
import com.khanhlh.firewarning.mqtt.MqttCommon;
import com.khanhlh.firewarning.utils.CommonUtils;

public class LoginActivity extends BaseActivity<ActivityLoginBinding, LoginViewModel>
        implements LoginNavigator, MqttCommon.ReceiveMessage {
    public static final String SUB_SIGN_UP = "statususer";
    public static final String PUB_SIGN_UP = "registeruser";

    public static final String SUB_LOGIN = "statususer";
    public static final String PUB_LOGIN = "registeruser";

    private LoginViewModel mLoginViewModel;
    private ActivityLoginBinding binding;
    private boolean login = false;
    private MqttCommon mqttSignUp;
    private MqttCommon mqttLogin;

    public static Intent newIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public LoginViewModel getViewModel() {
        mLoginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        return mLoginViewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewDataBinding();
        mLoginViewModel.setNavigator(this);
    }

    @Override
    public void handleError(Throwable throwable) {
        CommonUtils.toast(this, R.string.exception);
        showBackground();
    }

    @Override
    public void login() {
        if (!isNetworkConnected()) {
            return;
        }
        mqttLogin = new MqttCommon(this);
        mqttLogin.subscribeTopic(SUB_LOGIN);
        mqttLogin.setReceiveMessage(this);
        String email = binding.txtUsername.getText().toString();
        String password = binding.txtPassword.getText().toString();
        if (mLoginViewModel.isEmailAndPasswordValid(email, password)) {
            mLoginViewModel.login(mqttLogin, PUB_LOGIN, email, password);
        } else {
            CommonUtils.toast(this, R.string.login_fail);
        }
    }

    @Override
    public void signUp() {
        if (!isNetworkConnected()) {
            return;
        }
        mqttSignUp = new MqttCommon(this);
        login = false;
        mqttSignUp.subscribeTopic(SUB_SIGN_UP);
        mqttSignUp.setReceiveMessage(this);
        String email = binding.txtUsername.getText().toString();
        String password = binding.txtPassword.getText().toString();
        String rePassword = binding.txtRePassword.getText().toString();
        String name = binding.txtName.getText().toString();
        String phoneNumber = binding.txtPhoneNumber.getText().toString();
        if (mLoginViewModel.validateSignUp(email, password, rePassword, name, phoneNumber)) {
            mLoginViewModel.signUp(mqttSignUp, PUB_SIGN_UP, email, password, name, phoneNumber);
        } else {
            CommonUtils.toast(this, R.string.input_not_enough);
        }
    }

    @Override
    public void createAccount() {
        showSignUpUI();
    }

    @Override
    public void onLoading() {
        binding.blurView.setVisibility(View.VISIBLE);
    }

    @Override
    public void openMainActivity() {

    }

    @Override
    public void onSignUpSuccess() {

    }

    private void showSignUpUI() {
        binding.llSignUp.setVisibility(View.VISIBLE);
        binding.rlSignUp.setVisibility(View.GONE);
        binding.btnLogin.setVisibility(View.GONE);
        binding.btnSignUp.setVisibility(View.VISIBLE);
    }

    private void showLoginUI() {
        binding.llSignUp.setVisibility(View.GONE);
        binding.rlSignUp.setVisibility(View.VISIBLE);
        binding.btnLogin.setVisibility(View.VISIBLE);
        binding.btnSignUp.setVisibility(View.GONE);
    }

    private void showBackground() {
        binding.blurView.setVisibility(View.GONE);
        mLoginViewModel.setIsLoading(false);
    }

    @Override
    public void onSubSuccess(String message) {
        if (!TextUtils.isEmpty(message)) {
            showBackground();
            if (Boolean.parseBoolean(message)) {
                if (login) {
                    CommonUtils.toast(this, R.string.login_success);
                    navigateToMain();
                } else {
                    CommonUtils.toast(this, R.string.sign_up_success);
                    showLoginUI();
                }
            } else {
                if (login) {
                    CommonUtils.toast(this, R.string.login_fail);
                } else {
                    CommonUtils.toast(this, R.string.sign_up_fail);
                }
            }
        }
    }

    private void navigateToMain() {
        new Handler().postDelayed(()
                -> startActivity(MainActivity.newIntent(this)), 500);
    }

    @Override
    public void onSubError(String message) {
        Log.e("MQTT Error", message);
    }

    @Override
    public void onPubSuccess(String message) {

    }

    @Override
    public void onPubError(String message) {

    }
}
