package com.easym.vegie.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easym.vegie.R;
import com.easym.vegie.Utils.Utility;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.net.ConnectException;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Arti on 28th Sept 2020.
 */

public class MyProfilePageActivity extends BaseActivity implements View.OnClickListener {

    private TextView txtProfilePage, txtForgotPassword, txtUserName, txtUserEmail, txtUserPhone, toolbarTxt;
    private LinearLayout backLayout;
    private EditText edtOldPassword, edtNewPassword;
    private Button btnSaveChanges;
    private Context context;
    private String userId = "";
    private CircularImageView profilePic;
    private String userName = "", userEmailId = "", userProfilePic = "", userMobileNo = "";

    RelativeLayout rl_ChangePassword;
    LinearLayout ll_Password;


    boolean isChangePasswordOpen = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile_page);

        context = this;

        txtProfilePage = findViewById(R.id.txt_edit_deatils);
        backLayout = findViewById(R.id.back_layout);
        txtForgotPassword = findViewById(R.id.forgot_txt);
        txtUserName = findViewById(R.id.txt_name);
        txtUserEmail = findViewById(R.id.txt_email);
        txtUserPhone = findViewById(R.id.txt_phone_no);
        edtOldPassword = findViewById(R.id.edt_password);
        edtNewPassword = findViewById(R.id.edt_new_password);
        btnSaveChanges = findViewById(R.id.btn_save_changes);
        profilePic = findViewById(R.id.circular_image);

        rl_ChangePassword = findViewById(R.id.rl_ChangePassword);
        ll_Password = findViewById(R.id.ll_Password);

        toolbarTxt = findViewById(R.id.txt_back);
        toolbarTxt.setText("Back");

        txtProfilePage.setOnClickListener(this);
        backLayout.setOnClickListener(this);
        txtForgotPassword.setOnClickListener(this);
        btnSaveChanges.setOnClickListener(this);

        rl_ChangePassword.setOnClickListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserData();
    }

    //api hit for get user details
    private void getUserData() {
        if (Utility.getInstance().checkInternetConnection(context)) {
            apiService.getUserDetails(userPref.getUser().getId(),
                    userPref.getUser().getToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(this::showProgressDialog)
                    .doOnCompleted(this::hideProgressDialog)
                    .subscribe(response -> {
                        if (response.getResponseStatus().equals("success")) {

                            //Toast.makeText(context, response.getResponseMessage(), Toast.LENGTH_SHORT).show();
                           //userPref.setUser(response.getData());

                            if (!TextUtils.isEmpty(response.getData().getProfile_image())) {
                                userProfilePic = (response.getData().getProfile_image());
                                Picasso.get().load(userProfilePic).into(profilePic);
                            }

                            userName = response.getData().getUsername();
                            userEmailId = response.getData().getEmail();
                            userMobileNo = response.getData().getMobile_number();
                            txtUserName.setText(userName);
                            txtUserEmail.setText(userEmailId);
                            txtUserPhone.setText(userMobileNo);

                        }else if(response.getResponseCode().equals("403")){

                            utils.openLogoutDialog(this,userPref);

                        }else {
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
                            Log.e( "MyProfilePage Activity","Within Throwable Exception::" + e.getMessage());
                        }

                    });

        } else {
            hideProgressDialog();
            Utility.simpleAlert(this, getString(R.string.error), getString(R.string.check_network_connection));
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.txt_edit_deatils:
                intent = new Intent(context, EditProfileActivity.class);
                intent.putExtra("userName", userName);
                intent.putExtra("userEmail", userEmailId);
                intent.putExtra("userMobileNo", userMobileNo);
                intent.putExtra("userProfilePic", userProfilePic);
                startActivity(intent);
                break;
            case R.id.forgot_txt:
                intent = new Intent(context, ForgotPasswordActivity.class);
                intent.putExtra("userEmail", userEmailId);
                intent.putExtra("userMobileNo", userMobileNo);
                startActivity(intent);
                break;
            case R.id.back_layout:
                finish();
                break;

            case R.id.btn_save_changes:
                if (validateField()) {
                    updatePasswordAPIHit();
                }
                break;

            case R.id.rl_ChangePassword:

                if(isChangePasswordOpen){
                    //code to close  Change password layout

                    ll_Password.setVisibility(View.GONE);
                    isChangePasswordOpen = false;

                }else{
                    //code to open change password layout
                    ll_Password.setVisibility(View.VISIBLE);
                    isChangePasswordOpen = true;

                }

                break;
        }
    }

    //change user password
    private void updatePasswordAPIHit() {
        if (Utility.getInstance().checkInternetConnection(context)) {

            apiService
                    .changeUserPassword(userPref.getUser().getId(),
                    edtOldPassword.getText().toString().trim(),
                    edtNewPassword.getText().toString().trim(),
                    userPref.getUser().getToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(this::showProgressDialog)
                    .doOnCompleted(this::hideProgressDialog)
                    .subscribe(response -> {

                        if (response.getResponseCode().equals("200")) {
                            Toast.makeText(context, response.getResponseMessage(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, HomePageActivity.class);
                            startActivity(intent);
                            finish();
                        }else if(response.getResponseCode().equals("403")){

                            utils.openLogoutDialog(this,userPref);

                        } else {

                            hideProgressDialog();
                            String msg = utils.fromHtml(response.getResponseMessage()).toString();
                            Utility.simpleAlert(this, getString(R.string.info_dialog_title), msg);
                            //Toast.makeText(context, ""+Html.fromHtml(response.getResponseMessage()), Toast.LENGTH_SHORT).show();
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
                        }catch (Exception ex){
                            Log.e( "MyProfilePage Activity","Within Throwable Exception::" + e.getMessage());
                        }
                    });

        } else {
            hideProgressDialog();
            Utility.simpleAlert(this, getString(R.string.error), getString(R.string.check_network_connection));
        }
    }

    boolean validateField() {
        if (Utility.getInstance().isEmpty(edtOldPassword.getText().toString().trim())) {
            edtOldPassword.setError(getString(R.string.old_pass));
            return false;
        } else if (Utility.getInstance().isEmpty(edtNewPassword.getText().toString().trim())) {
            edtNewPassword.setError(getString(R.string.new_password));
            return false;
        }
        return true;

    }
}