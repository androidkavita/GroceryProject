package com.easym.vegie.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easym.vegie.R;
import com.easym.vegie.Utils.Utility;
import com.google.gson.Gson;
import com.skbfinance.BaseFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReachUsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReachUsFragment extends BaseFragment  {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Context mContext;
    private TextView tvAddress;
    private TextView tvViewOnMap;

    public ReachUsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReachUsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReachUsFragment newInstance(String param1, String param2) {
        ReachUsFragment fragment = new ReachUsFragment();
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
        View view = inflater.inflate(R.layout.fragment_reach_us, container, false);
        tvAddress = view.findViewById(R.id.txt_address);
        tvViewOnMap = view.findViewById(R.id.tv_ViewOnMap);


        getReachUsData();
        return view;
    }

    private void getReachUsData() {
        if (Utility.getInstance().checkInternetConnection(mContext)) {
            apiService.aboutUs("reachus")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(this::showProgressDialog)
                    .doOnCompleted(this::hideProgressDialog)
                    .subscribe(response -> {
                        if (response.getResponseCode().equals("200")) {
                            try {
                                JSONObject jsonObject = new JSONObject(new Gson().toJson(response.getData()));
                                tvAddress.setText(jsonObject.optString("address"));

                                tvViewOnMap.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String address = jsonObject.optString("address");
                                        if(!address.equals("")) {


                                            try {

                                                String map = "http://maps.google.co.in/maps?q=" + address;
                                                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(map));
                                                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                                startActivity(intent);

                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }

                                        }


                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else{
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


}