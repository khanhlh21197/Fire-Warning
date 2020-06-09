package com.khanhlh.firewarning.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import com.khanhlh.firewarning.BR;
import com.khanhlh.firewarning.R;
import com.khanhlh.firewarning.base.BaseActivity;
import com.khanhlh.firewarning.data.model.api.BaseResponse;
import com.khanhlh.firewarning.data.model.db.User;
import com.khanhlh.firewarning.databinding.ActivityLoginBinding;
import com.khanhlh.firewarning.main.MainActivity;
import com.khanhlh.firewarning.mqtt.MqttCommon;
import com.khanhlh.firewarning.utils.CommonUtils;
import com.khanhlh.firewarning.utils.json.JsonConverter;

public class LoginActivity extends BaseActivity<ActivityLoginBinding, LoginViewModel>
        implements LoginNavigator, MqttCommon.ReceiveMessage {
    public static final String SUB_SIGN_UP = "statususer";
    public static final String PUB_SIGN_UP = "registeruser";

    public static final String SUB_LOGIN = "statuslogin";
    public static final String PUB_LOGIN = "loginuser";

    private LoginViewModel mLoginViewModel;
    private ActivityLoginBinding binding;
    private boolean login = false;
    private MqttCommon mqttSignUp;
    private MqttCommon mqttLogin;

    private SharedPreferences sharedPreferences;
    @SuppressLint("CommitPrefEdits")
    private SharedPreferences.Editor editor;
    public static final String MyPREFERENCES = "MyPrefs";

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
        mLoginViewModel.setShowPassword(true);
        return mLoginViewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewDataBinding();
        initSharedPreferences();
        mLoginViewModel.setNavigator(this);
    }

    @Override
    public void handleError(Boolean login) {
        CommonUtils.toast(this, R.string.exception);
        showBackground();
    }

    @Override
    public void login() {
        subscribeLogin();
        String email = binding.txtUsername.getText().toString();
        String password = binding.txtPassword.getText().toString();

        User user = new User(email, password);
        Log.d("Test JsonConverter", JsonConverter.objectToJson(user));

        if (mLoginViewModel.isEmailAndPasswordValid(email, password)) {
            mLoginViewModel.login(mqttLogin, PUB_LOGIN, email, password);
        } else {
            CommonUtils.toast(this, R.string.login_fail);
        }
    }

    @Override
    public void signUp() {
        subscribeSignUp();
        String email = binding.txtUsername.getText().toString();
        String password = binding.txtPassword.getText().toString();
        String rePassword = binding.txtRePassword.getText().toString();
        String name = binding.txtName.getText().toString();
        String phoneNumber = binding.txtPhoneNumber.getText().toString();
        String address = binding.txtAddress.getText().toString();

        if (mLoginViewModel.validateSignUp(email, password, rePassword, name, phoneNumber, address)) {
            mLoginViewModel.signUp(mqttSignUp, PUB_SIGN_UP, email, password, name, phoneNumber, address);
        } else {
            CommonUtils.toast(this, R.string.input_not_enough);
        }
    }

    private void subscribeSignUp() {
        login = false;
        mqttSignUp = new MqttCommon(this);
        mqttSignUp.subscribeTopic(SUB_SIGN_UP);
        mqttSignUp.setReceiveMessage(this);
    }

    private void subscribeLogin() {
        login = true;
        mqttLogin = new MqttCommon(this);
        mqttLogin.subscribeTopic(SUB_LOGIN);
        mqttLogin.setReceiveMessage(this);
    }

    private void unSubscribeSignUp() {
        mqttSignUp = new MqttCommon(this);
//        mqttSignUp.unSubscribe(SUB_SIGN_UP);
//        mqttSignUp.setReceiveMessage(this);
        mqttSignUp.unRegister();
    }

    private void unSubscribeLogin() {
        mqttLogin = new MqttCommon(this);
//        mqttLogin.unSubscribe(SUB_LOGIN);
//        mqttLogin.setReceiveMessage(this);
        mqttLogin.unRegister();
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
    public void backToLogin() {
        showLoginUI();
    }

    @Override
    public void showPassword() {
        binding.txtPassword.setTransformationMethod(new SingleLineTransformationMethod());
        mLoginViewModel.setShowPassword(true);
    }

    @Override
    public void hidePassword() {
        binding.txtPassword.setTransformationMethod(new PasswordTransformationMethod());
        mLoginViewModel.setShowPassword(false);
    }

    @Override
    public void showRepassword() {
        mLoginViewModel.setShowRePassword(true);
        binding.txtRePassword.setTransformationMethod(new SingleLineTransformationMethod());
    }

    @Override
    public void hideRePassword() {
        mLoginViewModel.setShowRePassword(false);
        binding.txtRePassword.setTransformationMethod(new PasswordTransformationMethod());
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
        binding.tvBackToLogin.setVisibility(View.VISIBLE);
        binding.btnLogin.setVisibility(View.GONE);
        binding.btnSignUp.setVisibility(View.VISIBLE);
        binding.label.setText(R.string.sign_up);
    }

    private void showLoginUI() {
        binding.llSignUp.setVisibility(View.GONE);
        binding.rlSignUp.setVisibility(View.VISIBLE);
        binding.tvBackToLogin.setVisibility(View.GONE);
        binding.btnLogin.setVisibility(View.VISIBLE);
        binding.btnSignUp.setVisibility(View.GONE);
        binding.label.setText(R.string.login);
    }

    private void showBackground() {
        if (binding.blurView.getVisibility() == View.VISIBLE)
            binding.blurView.setVisibility(View.GONE);
        mLoginViewModel.setIsLoading(false);
    }

    private void clarify() {
        if (binding.blurView.getVisibility() == View.GONE)
            binding.blurView.setVisibility(View.VISIBLE);
        mLoginViewModel.setIsLoading(true);
    }

    @Override
    public void onSubSuccess(BaseResponse response) {
        showBackground();
        if (Boolean.parseBoolean(response.getResult())) {
            if (login) {
                CommonUtils.toast(this, R.string.login_success);
                saveUser();
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

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void saveUser() {
        editor = sharedPreferences.edit();
        boolean saveUser = binding.cbSaveUser.isChecked();
        editor.putBoolean("saveUser", saveUser);
        if (binding.cbSaveUser.isChecked()) {
            String email = binding.txtUsername.getText().toString();
            String password = binding.txtPassword.getText().toString();

            editor.putString("email", email);
            editor.putString("password", password);
            editor.apply();
        }
    }

    private void initSharedPreferences() {
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        String password = sharedPreferences.getString("password", "");
        boolean saveUser = sharedPreferences.getBoolean("saveUser", false);
        if (!CommonUtils.isNullOrEmpty(email) && !CommonUtils.isNullOrEmpty(password)) {
            binding.txtUsername.setText(email);
            binding.txtPassword.setText(password);
            binding.cbSaveUser.setChecked(saveUser);
        }

        binding.cbSaveUser.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                binding.txtUsername.setText("");
                binding.txtPassword.setText("");
                editor.clear();
                editor.apply();
            }
        });
    }

    @Override
    public void onSubError(String message) {
        Log.e("MQTT Error", message);
        showBackground();
        handleError(null);
    }

    @Override
    public void onPubSuccess(String message) {
        showBackground();
    }

    @Override
    public void onPubError(String message) {
        showBackground();
    }

    @Override
    public void onLoading(boolean loading) {
        mLoginViewModel.setIsLoading(loading);
        if (!loading) {
            clarify();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unSubscribeLogin();
        unSubscribeSignUp();
    }
}
