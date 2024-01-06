package com.easym.vegie.activities;

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

import com.easym.vegie.R;
import com.easym.vegie.Utils.Utility;

import java.net.ConnectException;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Arti on 28th Sept 2020.
 */

public class CreateNewPasswordActivity extends BaseActivity implements View.OnClickListener {

    private EditText newPasswordEdt, reenterPasswordEdt;
    private Button btnSave;
    private Context context;
    private LinearLayout backLayout;
    private String userName = "";
    private TextView toolbarTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_password);

        context = this;

        newPasswordEdt = findViewById(R.id.edt_password);
        reenterPasswordEdt = findViewById(R.id.edt_confirm_password);
        btnSave = findViewById(R.id.btn_save);
        backLayout = findViewById(R.id.back_layout);

        toolbarTxt = findViewById(R.id.txt_back);
        toolbarTxt.setText("Back");

        userName = getIntent().getStringExtra("userId");

        backLayout.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_layout:
                finish();
                break;

            case R.id.btn_save:
                if (validateField()) {

                    createNewPasswordApiHit(
                            userName,
                            newPasswordEdt.getText().toString().trim(),
                            reenterPasswordEdt.getText().toString().trim()
                    );
                }
                break;
        }

    }

    private void createNewPasswordApiHit(String userName,
                                         String password,
                                         String confirmPassword) {

        if (Utility.getInstance().checkInternetConnection(this)) {
            apiService.forgotPassword(userName,password,confirmPassword)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(this::showProgressDialog)
                    .doOnCompleted(this::hideProgressDialog)
                    .subscribe(response -> {

                        if (response.getResponseCode().equals("200")) {

                            Intent intent = new Intent(CreateNewPasswordActivity.this,
                                    LoginPageActivity.class);
                            Toast.makeText(context, "Password send your mail successfully", Toast.LENGTH_SHORT).show();
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }else{
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
                                Utility.simpleAlert(this, "", getString(R.string.something_went_wrong));
                            }

                        }catch (Exception ex){
                            Log.e( "","Within Throwable Exception::" + e.getMessage());
                        }
                    });

        } else {
            hideProgressDialog();
            Utility.simpleAlert(this, getString(R.string.error), getString(R.string.check_network_connection));
        }

    }

    boolean validateField() {
        if (Utility.getInstance().isEmpty(newPasswordEdt.getText().toString().trim())) {
            newPasswordEdt.setError(getString(R.string.new_password));
            return false;
        } else if (!newPasswordEdt.getText().toString().trim().equals(reenterPasswordEdt.getText().toString().trim())) {
            reenterPasswordEdt.setError(getResources().getString(R.string.pass_miss));
            return false;
        }
        return true;

    }
}