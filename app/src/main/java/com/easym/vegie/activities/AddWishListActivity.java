package com.easym.vegie.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.easym.vegie.R;
import com.easym.vegie.Utils.Utility;

import java.net.ConnectException;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AddWishListActivity extends BaseActivity {
    private ImageView addwishImage, unSaveImage;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wish_list);

        context = this;

        addwishImage = findViewById(R.id.save_image_view);
        unSaveImage = findViewById(R.id.unSave_image_view);

        addwishImage.setOnClickListener(view -> {
            addwishImage.setImageResource(R.drawable.unsaved);
            addWishListApi();
        });

        unSaveImage.setOnClickListener(view -> {
            addwishImage.setImageResource(R.drawable.saved);
            removeWishListApi();
        });
    }

    private void addWishListApi() {
        if (Utility.getInstance().checkInternetConnection(context)) {
            apiService.addWishList(
                    userPref.getUser().getId(),
                    "af",
                    userPref.getUser().getToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(this::showProgressDialog)
                    .doOnCompleted(this::hideProgressDialog)
                    .subscribe(response -> {
                        if (response.getResponseStatus().equals("success")) {
                            Toast.makeText(context, response.getResponseMessage(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, HomePageActivity.class);
                            startActivity(intent);
                            finish();

                        }else if(response.getResponseCode().equals("403")){

                            utils.openLogoutDialog(this,userPref);

                        } else {
                            hideProgressDialog();
                            Utility.simpleAlert(context, getString(R.string.info_dialog_title), response.getResponseMessage());
                        }
                    }, e -> {

                        try {
                            if (e instanceof ConnectException) {
                                hideProgressDialog();
                                Utility.simpleAlert(context, getString(R.string.error), getString(R.string.check_network_connection));
                            } else {
                                hideProgressDialog();
                                e.printStackTrace();
                                Utility.simpleAlert(context, "", getString(R.string.something_went_wrong));
                            }
                        }catch (Exception ex){
                            Log.e( "AddWishList Activity","Within Throwable Exception::" + e.getMessage());
                        }

                    });

        } else {
            hideProgressDialog();
            Utility.simpleAlert(context, getString(R.string.error), getString(R.string.check_network_connection));
        }
    }

    private void removeWishListApi() {
        if (Utility.getInstance().checkInternetConnection(context)) {
            apiService.removeWishList(userPref.getUser().getId(),
                    "af",
                    userPref.getUser().getToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(this::showProgressDialog)
                    .doOnCompleted(this::hideProgressDialog)
                    .subscribe(response -> {
                        if (response.getResponseStatus().equals("success")) {
                            Toast.makeText(context, response.getResponseMessage(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, HomePageActivity.class);
                            startActivity(intent);
                            finish();

                        }else if(response.getResponseCode().equals("403")){

                            utils.openLogoutDialog(this,userPref);

                        } else
                            hideProgressDialog();
                        Utility.simpleAlert(context, getString(R.string.error), response.getResponseMessage());
                    }, e -> {

                        try {
                            if (e instanceof ConnectException) {
                                hideProgressDialog();
                                Utility.simpleAlert(context, getString(R.string.error), getString(R.string.check_network_connection));
                            } else {
                                hideProgressDialog();
                                e.printStackTrace();
                                Utility.simpleAlert(context, "", getString(R.string.something_went_wrong));
                            }
                        }catch (Exception ex){
                            Log.e( "","Within Throwable Exception::" + e.getMessage());
                        }
                    });

        } else {
            hideProgressDialog();
            Utility.simpleAlert(context, getString(R.string.error), getString(R.string.check_network_connection));
        }
    }
}
