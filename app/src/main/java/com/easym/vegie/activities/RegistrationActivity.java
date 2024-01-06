package com.easym.vegie.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.easym.vegie.R;
import com.easym.vegie.SessionData.SessionManager;
import com.easym.vegie.Utils.Utility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.net.ConnectException;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.easym.vegie.Utils.Utility.isEmail;

import androidx.annotation.NonNull;

/**
 * Created by Arti on 28th Sept 2020.
 */
public class RegistrationActivity extends BaseActivity implements View.OnClickListener {

    private Context context;
    private EditText edtName, edtEmailId, edtPhoneNo, edtPassword, edtConfirmPass;
    private Button btnRegister;
    private LinearLayout loginLayout;
    private String emailId = "";
    private String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
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

        context = this;

        edtName = findViewById(R.id.edt_name);
        edtEmailId = findViewById(R.id.edt_email_id);
        edtPhoneNo = findViewById(R.id.edt_phone_no);
        edtPassword = findViewById(R.id.edt_password);
        edtConfirmPass = findViewById(R.id.edt_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        loginLayout = findViewById(R.id.login_layout);

        btnRegister.setOnClickListener(this);
        loginLayout.setOnClickListener(this);

        edtEmailId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (isEmail(edtEmailId.getText().toString().trim())) {
                    emailId = edtEmailId.getText().toString().trim();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                if (validateFiled()) {
                    registerApi();
                }
                break;

            case R.id.login_layout:
                Intent intent = new Intent(context, LoginPageActivity.class);
                startActivity(intent);
                break;
        }

    }

    //hit register API
    private void registerApi() {
        if (Utility.getInstance().checkInternetConnection(context)) {
            apiService.callSignUpAPI(edtName.getText().toString().trim(), edtPhoneNo.getText().toString().trim(), edtEmailId.getText().toString().trim(), edtPassword.getText().toString().trim(), token, "")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(this::showProgressDialog)
                    .doOnCompleted(this::hideProgressDialog)
                    .subscribe(response -> {
                        if (response.getResponseStatus().equals("success")) {

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
                                startActivity(new Intent(this, LoginPageActivity.class));
                                finish();

                        } else {
                            hideProgressDialog();
                            Utility.simpleAlert(this, getString(R.string.info_dialog_title), String.valueOf(Html.fromHtml(response.getResponseMessage())));
                        }
                    }, e -> {

                        try{

                            if (e instanceof ConnectException) {
                                hideProgressDialog();
                                Utility.simpleAlert(this, getString(R.string.error), getString(R.string.check_network_connection));
                            } else {
                                hideProgressDialog();
                                e.printStackTrace();
                                Utility.simpleAlert(context,"",getString(R.string.something_went_wrong));
                            }

                        }catch (Exception ex){
                            Log.e( "Registration Activity","Within Throwable Exception::" + e.getMessage());
                        }

                    });

        } else {
            hideProgressDialog();
            Utility.simpleAlert(this, getString(R.string.error), getString(R.string.check_network_connection));
        }
    }

    boolean validateFiled() {
        if (Utility.getInstance().isEmpty(edtName.getText().toString().trim())) {
            edtName.setError(getResources().getString(R.string.name_error));
            return false;
        } else if (Utility.getInstance().isEmpty(emailId)) {
            edtEmailId.setError("Please enter email");
            return false;
        }else if(!Utility.getInstance().isValidMail(emailId)){
            edtEmailId.setError("Please enter valid email");
            return false;
        } else if (Utility.getInstance().isEmpty(edtPhoneNo.getText().toString().trim())) {
            edtPhoneNo.setError("Please enter mobile number");
            return false;
        }else if(!Utility.getInstance().isValidMobile(edtPhoneNo.getText().toString().trim())){
            edtPhoneNo.setError("Please enter valid mobile number of 10 digits");
            return false;
        }else if(Utility.getInstance().isEmpty(edtPassword.getText().toString().trim())){
            edtPassword.setError("Please enter password");
            return false;
        }else if(Utility.getInstance().isEmpty(edtConfirmPass.getText().toString().trim())){
            edtConfirmPass.setError("Please enter confirm password");
            return false;
        } else if (Utility.getInstance().isEmpty(edtPassword.getText().toString().trim())) {
            edtPassword.setError(getResources().getString(R.string.pass_error));
            return false;
        } else if (!edtPassword.getText().toString().trim().equals(edtConfirmPass.getText().toString().trim())) {
            edtConfirmPass.setError(getResources().getString(R.string.pass_miss));
            return false;
        } else
            return true;

    }
}