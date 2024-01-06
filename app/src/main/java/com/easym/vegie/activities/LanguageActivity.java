package com.easym.vegie.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.easym.vegie.R;
import com.easym.vegie.Utils.Utility;
import com.easym.vegie.model.language.LanguageListResult;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Arti on 29th Sept 2020.
 */
public class LanguageActivity extends BaseActivity implements View.OnClickListener {

    Spinner spinner_language;
    Button btn_next;
    String selectedLanguage, selectedLangCode;
    TextView tv_Skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        spinner_language = findViewById(R.id.spinner_language);
        btn_next = findViewById(R.id.btn_next);
        tv_Skip = findViewById(R.id.tv_Skip);

        getLanguage();

        btn_next.setOnClickListener(this);
        tv_Skip.setOnClickListener(this);

    }

    private void getLanguage() {
        try {
            if (Utility.getInstance().checkInternetConnection(this)) {
                apiService.getLanguage(
                                userPref.getUser().getToken())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(this::showProgressDialog)
                        .doOnCompleted(this::hideProgressDialog)
                        .subscribe(response -> {
                            if (response.getResponseCode().equals("200")) {

                                List<LanguageListResult> list = response.getData().getLanguageList().getResult();

                                List<String> languageList = new ArrayList<>();

                                for (int i = 0; i < list.size(); i++) {
                                    languageList.add(list.get(i).getName());
                                }

                                setLanguageInSpinner(languageList, list);

                            } else if (response.getResponseCode().equals("403")) {

                                utils.openLogoutDialog(this, userPref);

                            } else {

                                hideProgressDialog();
                                // Utility.simpleAlert(this, getString(R.string.error), response.getResponseMessage());
                                Toast.makeText(this, "" + response.getResponseMessage(), Toast.LENGTH_SHORT).show();
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

                            } catch (Exception ex) {
                                Log.e("", "Within Throwable Exception::" + e.getMessage());
                            }
                        });

            }
            else {
                hideProgressDialog();
                Utility.simpleAlert(this, getString(R.string.error), getString(R.string.check_network_connection));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        }


    private void setLanguageInSpinner(List<String> languageList, List<LanguageListResult> list) {

        ArrayAdapter languageAdapter = new ArrayAdapter(LanguageActivity.this,
                android.R.layout.simple_spinner_item, languageList);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_language.setAdapter(languageAdapter);

        spinner_language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                selectedLanguage = languageList.get(i);
                selectedLangCode = list.get(i).getLan_code();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {

            case R.id.btn_next:

                userPref.setUserPreferLanguage(selectedLanguage, selectedLangCode);

                Intent intent = new Intent(LanguageActivity.this, HomePageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                finish();
                break;

            case R.id.tv_Skip:
                Intent intent1 = new Intent(LanguageActivity.this, HomePageActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
                finish();
                break;
        }
    }
}