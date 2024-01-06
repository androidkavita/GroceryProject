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
import android.widget.TextView;
import android.widget.Toast;

import com.easym.vegie.R;
import com.easym.vegie.Utils.Utility;

import java.net.ConnectException;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Arti on 28th Sept 2020.
 */

public class VerifyAccountActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout backLayout;
    private Button btnProceed;
    private EditText txtFirstBox, txtSecondBox, txtThirdBox, txtForthBox;
    private String radionEmail = "", radioPhone = "";
    private TextView txtResendOtp, otpTextView;
    private String firstTextNo = "", secondTextNo = "", thirdTxtNo = "", forthTxtNo = "", finalValue = "";
    private TextView toolbarTxt;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_account);

        context = this;
        backLayout = findViewById(R.id.back_layout);
        btnProceed = findViewById(R.id.btn_proceed);
        txtResendOtp = findViewById(R.id.txt_resend_otp);
        txtFirstBox = findViewById(R.id.txtFirst);
        txtSecondBox = findViewById(R.id.txtSecond);
        txtThirdBox = findViewById(R.id.txtThird);
        txtForthBox = findViewById(R.id.txtForth);
        otpTextView = findViewById(R.id.otp_text);
        toolbarTxt = findViewById(R.id.txt_back);
        toolbarTxt.setText("Back");

        if (!TextUtils.isEmpty(getIntent().getStringExtra("radioEmailKey"))) {
            radionEmail = getIntent().getStringExtra("radioEmailKey");
            if (radionEmail.contains("@")) {
                otpTextView.setText(R.string.otpTextEmail);
            } else {
                otpTextView.setText(R.string.otpTextPhone);
            }
        }
        backLayout.setOnClickListener(this);
        btnProceed.setOnClickListener(this);
        txtResendOtp.setOnClickListener(this);

        txtFirstBox.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (txtFirstBox.getText().toString().trim().length() == 1)     //size as per your requirement
                {
                    txtSecondBox.requestFocus();
                }


            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub
            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        txtSecondBox.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (txtSecondBox.getText().toString().trim().length() == 1)     //size as per your requirement
                {
                    txtThirdBox.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub
            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        txtThirdBox.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (txtThirdBox.getText().toString().trim().length() == 1)     //size as per your requirement
                {
                    txtForthBox.requestFocus();
                }


            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub
            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_layout:
                finish();
                break;
            case R.id.btn_proceed:
                if (validateField()) {
                    if (!radionEmail.isEmpty()) {
                        verifyOtpApi(radionEmail);
                    }
                }
                break;

            case R.id.txt_resend_otp:
                resendOtpApi();
                break;
        }
    }

    //resend otp API hit
    private void resendOtpApi() {
        if (Utility.getInstance().checkInternetConnection(context)) {
            apiService.resendOtp(radionEmail)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(this::showProgressDialog)
                    .doOnCompleted(this::hideProgressDialog)
                    .subscribe(response -> {
                        if (response.getResponseStatus().equals("success")) {

                            //Toast.makeText(context, response.getResponseMessage(), Toast.LENGTH_SHORT).show();
                            if (radionEmail.contains("@")){
                                Toast.makeText(context, response.getResponseMessage(), Toast.LENGTH_SHORT).show();
                            }else{

                                Toast.makeText(context, "OTP sent to your mobile is :-"+response.getData().getOtp(), Toast.LENGTH_LONG).show();

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

    //verify otp API hit
    private void verifyOtpApi(String radioEmail) {
        firstTextNo = txtFirstBox.getText().toString().trim();
        secondTextNo = txtSecondBox.getText().toString().trim();
        thirdTxtNo = txtThirdBox.getText().toString().trim();
        forthTxtNo = txtForthBox.getText().toString().trim();
        finalValue = firstTextNo + secondTextNo + thirdTxtNo + forthTxtNo;
        if (Utility.getInstance().checkInternetConnection(context)) {
            apiService.verifyOtp(radioEmail, finalValue)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(this::showProgressDialog)
                    .doOnCompleted(this::hideProgressDialog)
                    .subscribe(response -> {
                        if (response.getResponseStatus().equals("success")) {
                            //.Toast.makeText(context, response.getResponseMessage(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, CreateNewPasswordActivity.class);
                            intent.putExtra("userId", radionEmail);
                            startActivity(intent);
                        } else {
                            hideProgressDialog();
                            Utility.simpleAlert(this, getString(R.string.info_dialog_title), response.getResponseMessage());
                        }
                    }, e -> {

                        try {

                            if (e instanceof ConnectException) {
                                hideProgressDialog();
                                Utility.simpleAlert(this, getString(R.string.error), getString(R.string.check_network_connection));
                            } else {
                                hideProgressDialog();
                                e.printStackTrace();
                                Utility.simpleAlert(context,"",getString(R.string.something_went_wrong));
                            }

                        }catch (Exception ex){
                            Log.e( "VerifyAccount Activity","Within Throwable Exception::" + e.getMessage());
                        }
                    });

        } else {
            hideProgressDialog();
            Utility.simpleAlert(this, getString(R.string.error), getString(R.string.check_network_connection));
        }
    }

    boolean validateField() {

        if(Utility.getInstance().isEmpty(txtFirstBox.getText().toString().trim()) &&
                Utility.getInstance().isEmpty(txtSecondBox.getText().toString().trim()) &&
                        Utility.getInstance().isEmpty(txtThirdBox.getText().toString().trim()) &&
                        Utility.getInstance().isEmpty(txtForthBox.getText().toString().trim())){

            Toast.makeText(context, "Please enter otp", Toast.LENGTH_SHORT).show();
            return false;

        } else if (Utility.getInstance().isEmpty(txtFirstBox.getText().toString().trim())) {
            Toast.makeText(context, "Please fill all field", Toast.LENGTH_SHORT).show();
            return false;
        } else if (Utility.getInstance().isEmpty(txtSecondBox.getText().toString().trim())) {
            Toast.makeText(context, "Please fill all field", Toast.LENGTH_SHORT).show();
            return false;
        } else if (Utility.getInstance().isEmpty(txtThirdBox.getText().toString().trim())) {
            Toast.makeText(context, "Please fill all field", Toast.LENGTH_SHORT).show();
            return false;
        } else if (Utility.getInstance().isEmpty(txtForthBox.getText().toString().trim())) {
            Toast.makeText(context, "Please fill all field", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }
}