package com.easym.vegie.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.easym.vegie.R;
import com.easym.vegie.SessionData.SessionManager;
import com.easym.vegie.Utils.Utility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.net.ConnectException;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Arti on 26th Sept 2020.
 */
public class LoginPageActivity extends BaseActivity implements View.OnClickListener {

    private EditText edtEmailPhone, edtPassword;
    private Button btnLogin, button_login_as_a_guest;
    private Context context;
    private TextView forgotTxt;
    private LinearLayout actNotLayout;
    private String token;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        context = this;
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.d("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token1 = task.getResult();
                        token=token1;
                        // Log and toast
                        Log.d("TAG", token);
                    }
                });

        edtEmailPhone = findViewById(R.id.edt_phone_no);
        edtPassword = findViewById(R.id.edt_password);
        btnLogin = findViewById(R.id.button_login);
        forgotTxt = findViewById(R.id.forgot_txt);
        actNotLayout = findViewById(R.id.lin_not_acc);
        button_login_as_a_guest = findViewById(R.id.button_login_as_a_guest);

        btnLogin.setOnClickListener(this);
        forgotTxt.setOnClickListener(this);
        actNotLayout.setOnClickListener(this);
        button_login_as_a_guest.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.button_login:
                if (validateField()) {
                    LoginApi();
                }
                break;
               case R.id.button_login_as_a_guest:
               GuestUserLoginApi();
                break;

            case R.id.forgot_txt:
                intent = new Intent(context, ForgotPasswordActivity.class);
                startActivity(intent);
                break;

            case R.id.lin_not_acc:
                intent = new Intent(context, RegistrationActivity.class);
                startActivity(intent);
                break;

        }
    }

    //hit login API
    void LoginApi() {

        if (Utility.getInstance().checkInternetConnection(context)) {

            apiService.callLoginAPI(edtEmailPhone.getText().toString().trim(),
                            edtPassword.getText().toString().trim(), token, "")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(this::showProgressDialog)
                    .doOnCompleted(this::hideProgressDialog)
                    .subscribe(response -> {
                        if (response.getResponseCode().equals("200")) {
                            userPref.setUser(response.getData());
                            SessionManager manager = new SessionManager(context);
                            manager.userLoginSession(
                                    response.getData().getId(),
                                    response.getData().getUsername(),
                                    response.getData().getEmail(),
                                    response.getData().getDevice_id(),
                                    response.getData().getDevice_token(),
                                    response.getData().getCountry_code(),
                                    response.getData().getMobile_number(),
                                    response.getData().getPassword(),
                                    response.getData().getToken(),
                                    response.getData().getWhatsapp_number(),
                                    true);

                            userPref.setWhatsappNumber(response.getData().getWhatsapp_number());

                            if (userPref.isUserPreferLanguageSet()) {
                                startActivity(new Intent(this, HomePageActivity.class));
                                finish();
                            } else {
                                startActivity(new Intent(this, LanguageActivity.class));
                                finish();
                            }

                            /*startActivity(new Intent(this, HomePageActivity.class));
                            finish();*/
                        } else {
                            hideProgressDialog();
                            String msg = utils.fromHtml(response.getResponseMessage()).toString();
                            Utility.simpleAlert(this, getString(R.string.info_dialog_title), msg);
                        }
                    }, e -> {

                        try {

                            if (e instanceof ConnectException) {
                                hideProgressDialog();
                                Utility.simpleAlert(this, getString(R.string.error), getString(R.string.check_network_connection));
                            } else {
                                hideProgressDialog();
                                e.printStackTrace();
                                Utility.simpleAlert(context, "", getString(R.string.something_went_wrong));
                            }

                        } catch (Exception ex) {
                            Log.e("Login Activity", "Within Throwable Exception::" + e.getMessage());
                        }

                    });

        } else {
            hideProgressDialog();
            Utility.simpleAlert(this, getString(R.string.error), getString(R.string.check_network_connection));
        }
    }

    void GuestUserLoginApi() {
        if (Utility.getInstance().checkInternetConnection(context)) {
            apiService.gustuser(token)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(this::showProgressDialog)
                    .doOnCompleted(this::hideProgressDialog)
                    .subscribe(response -> {
                        if (response.getResponseCode().equals("200")) {
                            userPref.setUser(response.getData());

                            SessionManager manager = new SessionManager(context);
                            manager.userLoginSession(
                                    response.getData().getId(),
                                    response.getData().getUsername(),
                                    response.getData().getEmail(),
                                    response.getData().getDevice_id(),
                                    response.getData().getDevice_token(),
                                    response.getData().getCountry_code(),
                                    response.getData().getMobile_number(),
                                    response.getData().getPassword(),
                                    response.getData().getToken(),
                                    response.getData().getWhatsapp_number(),
                                    true);

                            userPref.setWhatsappNumber(response.getData().getWhatsapp_number());
                            if (userPref.isUserPreferLanguageSet()) {
                                startActivity(new Intent(this, HomePageActivity.class));
                                finish();
                            } else {
                                startActivity(new Intent(this, LanguageActivity.class));
                                finish();
                            }

//
//                            startActivity(new Intent(LoginPageActivity.this, HomePageActivity.class));
//                                finish();


                        }
                    }, e -> {

                        try {

                            if (e instanceof ConnectException) {
                                hideProgressDialog();
                                Utility.simpleAlert(this, getString(R.string.error), getString(R.string.check_network_connection));
                            } else {
                                hideProgressDialog();
                                e.printStackTrace();
                                Utility.simpleAlert(context, "", getString(R.string.something_went_wrong));
                            }

                        } catch (Exception ex) {
                            Log.e("Login Activity", "Within Throwable Exception::" + e.getMessage());
                        }

                    });

        } else {
            hideProgressDialog();
            Utility.simpleAlert(this, getString(R.string.error), getString(R.string.check_network_connection));
        }
    }

    boolean validateField() {
        if (Utility.getInstance().isEmpty(edtEmailPhone.getText().toString().trim())) {
            edtEmailPhone.setError(getString(R.string.email_err));
            return false;
        } else if (isMobile(edtEmailPhone)) {

            if (!Utility.getInstance().isValidMobile(edtEmailPhone.getText().toString().trim())) {
                edtEmailPhone.setError("Please enter valid mobile number of 10 digits");
                return false;
            }

        } else if (!Utility.getInstance().isValidMail(edtEmailPhone.getText().toString().trim())) {
            edtEmailPhone.setError("Please enter valid email");
            return false;
        } else if (Utility.getInstance().isEmpty(edtPassword.getText().toString().trim())) {
            edtPassword.setError(getString(R.string.pass_error));
            return false;
        }
        return true;
    }

    private boolean isMobile(EditText input) {
        String data = input.getText().toString().trim();
        for (int i = 0; i < data.length(); i++) {
            if (!Character.isDigit(data.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}