package com.easym.vegie.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.easym.vegie.R;
import com.easym.vegie.Utils.Utility;

import java.net.ConnectException;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.easym.vegie.Utils.Utility.isEmail;

/**
 * Created by Arti on 26th Sept 2020.
 */
public class ForgotPasswordActivity extends BaseActivity implements View.OnClickListener {

    private Context context;
    private RadioButton radioEmail, radioPhone;
    private EditText edtEmail, edtPhoneNumber;
    private TextView toolbarTxt;
    private LinearLayout backLayout;
    private Button btnProceed;
    private String emailId = "", phoneNo = "",userEmail = "", userPhoneNumber = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        context = this;
        radioEmail = findViewById(R.id.radio_email);
        radioPhone = findViewById(R.id.radio_phone);
        edtEmail = findViewById(R.id.edt_email);
        edtPhoneNumber = findViewById(R.id.edt_phone);
        backLayout = findViewById(R.id.back_layout);
        btnProceed = findViewById(R.id.btn_proceed);
        toolbarTxt = findViewById(R.id.txt_back);
        toolbarTxt.setText("Back");

        if (!TextUtils.isEmpty(getIntent().getStringExtra("userEmail")) && !TextUtils.isEmpty(getIntent().getStringExtra("userMobileNo"))) {
            userEmail = getIntent().getStringExtra("userEmail");
            userPhoneNumber = getIntent().getStringExtra("userMobileNo");
        }

        backLayout.setOnClickListener(this);
        btnProceed.setOnClickListener(this);
        radioEmail.setOnClickListener(this);
        radioPhone.setOnClickListener(this);

        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (isEmail(edtEmail.getText().toString().trim())) {
                    emailId = edtEmail.getText().toString().trim();
                    phoneNo = "";
                } else {
                    emailId = "";
                }
            }
        });

        edtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                phoneNo = edtPhoneNumber.getText().toString().trim();
                emailId = "";
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!userPhoneNumber.isEmpty() && !userEmail.isEmpty()) {
            if (radioEmail.isChecked()) {
                edtEmail.setVisibility(View.VISIBLE);
                edtEmail.setText(userEmail);
                edtEmail.setEnabled(false);
            } else {
                edtEmail.setVisibility(View.GONE);
            }

            if (radioPhone.isChecked()) {
                edtPhoneNumber.setVisibility(View.VISIBLE);
                edtPhoneNumber.setText(userPhoneNumber);
                edtPhoneNumber.setEnabled(false);
            } else {
                edtPhoneNumber.setVisibility(View.GONE);
            }
        } else {
            if (radioEmail.isChecked()) {
                edtEmail.setVisibility(View.VISIBLE);
                edtEmail.setEnabled(true);
            } else {
                edtEmail.setVisibility(View.GONE);
            }

            if (radioPhone.isChecked()) {
                edtPhoneNumber.setVisibility(View.VISIBLE);
                edtPhoneNumber.setEnabled(true);
            } else {
                edtPhoneNumber.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_proceed:
                if (validateField()) {
                    resendOtpApi();
                }
                break;

            case R.id.back_layout:
                finish();
                break;
            case R.id.radio_email:
                boolean checked = ((RadioButton) view).isChecked();
                // Check which radiobutton was pressed
                if (checked) {
                    // Do your coding
                    if (!userEmail.isEmpty()) {
                        edtEmail.setVisibility(View.VISIBLE);
                        edtEmail.setText(userEmail);
                        edtEmail.setEnabled(false);
                        edtPhoneNumber.setVisibility(View.GONE);
                    } else {
                        edtEmail.setVisibility(View.VISIBLE);
                        edtEmail.setEnabled(true);
                        edtPhoneNumber.setVisibility(View.GONE);
                    }
                } else {
                    edtEmail.setVisibility(View.GONE);
                    edtPhoneNumber.setVisibility(View.GONE);
                }
                break;

            case R.id.radio_phone:
                boolean checked1 = ((RadioButton) view).isChecked();
                // Check which radiobutton was pressed
                if (checked1) {
                    // Do your coding
                    if (!userPhoneNumber.isEmpty()) {
                        edtPhoneNumber.setVisibility(View.VISIBLE);
                        edtPhoneNumber.setText(userPhoneNumber);
                        edtPhoneNumber.setEnabled(false);
                        edtEmail.setVisibility(View.GONE);
                    } else {
                        edtPhoneNumber.setVisibility(View.VISIBLE);
                        edtPhoneNumber.setEnabled(true);
                        edtEmail.setVisibility(View.GONE);
                    }
                } else {
                    edtPhoneNumber.setVisibility(View.GONE);
                    edtEmail.setVisibility(View.GONE);
                }
                break;

        }
    }

    boolean validateField() {
        if (edtEmail.getVisibility() == View.VISIBLE) {
            if (Utility.getInstance().isEmpty(emailId)) {
                edtEmail.setError(getString(R.string.email_err_forgot_pass));
                return false;
            }
        } else {
            if (Utility.getInstance().isEmpty(edtPhoneNumber.getText().toString().trim())) {
                edtPhoneNumber.setError(getString(R.string.phone_no));
                return false;
            }
        }
        return true;

    }


    private void resendOtpApi() {

        String userName = "";

        if (Utility.getInstance().checkInternetConnection(context)) {

            if (!emailId.isEmpty()) {
                phoneNo = "";

                userName = emailId;

            } else {
                emailId = "";

                userName = phoneNo;

            }

            String finalUserName = userName;
            apiService.resendOtp(
                    userName)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(this::showProgressDialog)
                    .doOnCompleted(this::hideProgressDialog)
                    .subscribe(response -> {
                        if (response.getResponseStatus().equals("success")) {

                            //Toast.makeText(context, response.getResponseMessage(), Toast.LENGTH_SHORT).show();

                            if(finalUserName.equals(emailId)){

                                Intent intent = new Intent(context, VerifyAccountActivity.class);
                                intent.putExtra("radioEmailKey", emailId);
                                startActivity(intent);

                            }else if(finalUserName.equals(phoneNo)){

                               // Toast.makeText(context, "OTP sent to your mobile is :-"+response.getData().getOtp(), Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(context, VerifyAccountActivity.class);
                                intent.putExtra("radioEmailKey", phoneNo);
                                startActivity(intent);
                            }


                        } else {
                            hideProgressDialog();
                            Utility.simpleAlert(this, getString(R.string.info_dialog_title), response.getResponseMessage());
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
                            Log.e( "ForgetPassword Activity","Within Throwable Exception::" + e.getMessage());
                        }


                    });

        } else {
            hideProgressDialog();
            Utility.simpleAlert(this, getString(R.string.error), getString(R.string.check_network_connection));
        }
    }
}