package com.easym.vegie.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easym.vegie.R;
import com.easym.vegie.Utils.Utility;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.skbfinance.BaseFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.net.URLEncoder;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class HelpFragment extends BaseFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private Context mContext;
    private TextView txtName, txtCalling,txtWhatsApp;
    RelativeLayout rlWhatsapp;

    public HelpFragment() {
        // Required empty public constructor
    }
    public static HelpFragment newInstance(String param1, String param2) {
        HelpFragment fragment = new HelpFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_help, container, false);
        txtName = view.findViewById(R.id.txt_name);
        txtCalling = view.findViewById(R.id.txt_calling_no);
        txtWhatsApp = view.findViewById(R.id.text_for_whatsapp);
        rlWhatsapp = view.findViewById(R.id.rlWhatsapp);
        rlWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectSupportOnWhatsapp(container);
            }
        });

        getHelpUsData();
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    private void getHelpUsData() {
        if (Utility.getInstance().checkInternetConnection(mContext)) {
            apiService.aboutUs("help")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(this::showProgressDialog)
                    .doOnCompleted(this::hideProgressDialog)
                    .subscribe(response -> {
                        if (response.getResponseCode().equals("200")) {
                            try {
                                JSONObject jsonObject = new JSONObject(new Gson().toJson(response.getData()));
                                txtCalling.setText(jsonObject.optString("mobile_number"));
                                txtName.setText(jsonObject.optString("email"));
                                txtWhatsApp.setText(jsonObject.optString("whatsapp_number"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            hideProgressDialog();
                            Utility.simpleAlert(getContext(), getString(R.string.info_dialog_title), response.getResponseMessage());
                        }
                    }, e -> {

                        try {

                            if (e instanceof ConnectException) {
                                hideProgressDialog();
                                Utility.simpleAlert(mContext, getString(R.string.error), getString(R.string.check_network_connection));
                            } else {
                                hideProgressDialog();
                                e.printStackTrace();
                                Utility.simpleAlert(getContext(), "", getString(R.string.something_went_wrong));
                            }

                        }catch (Exception ex){
                            Log.e( "","Within Throwable Exception::" + e.getMessage());
                        }
                    });

        } else {
            hideProgressDialog();
            Utility.simpleAlert(mContext, getString(R.string.error), getString(R.string.check_network_connection));
        }
    }

    private void connectSupportOnWhatsapp(ViewGroup container){

        PackageManager packageManager = getContext().getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);

        String customerSupportNumber = getString(R.string.customer_support_whatsapp_number);
        if(customerSupportNumber.equals("")){
            Snackbar.make(container.findViewById(android.R.id.content),
                    "Please provide customer support number",Snackbar.LENGTH_SHORT).show();
            return;
        }

        try {
            String url = "https://api.whatsapp.com/send?phone="+ customerSupportNumber
                    +"&text=" + URLEncoder.encode("Hello", "UTF-8");
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                startActivity(i);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}