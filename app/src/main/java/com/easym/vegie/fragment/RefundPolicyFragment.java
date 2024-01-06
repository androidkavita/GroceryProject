package com.easym.vegie.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
 * Use the {@link RefundPolicyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RefundPolicyFragment extends BaseFragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private Context mcontext;
    private TextView txtRefund;

    public RefundPolicyFragment() {
        // Required empty public constructor
    }

    public static RefundPolicyFragment newInstance(String param1, String param2) {
        RefundPolicyFragment fragment = new RefundPolicyFragment();
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
        View view = inflater.inflate(R.layout.fragment_refund_policy, container, false);
        txtRefund = view.findViewById(R.id.txt_address);
        getRefundPolicyData();
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mcontext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mcontext = null;
    }

    private void getRefundPolicyData() {
        if (Utility.getInstance().checkInternetConnection(mcontext)) {
            apiService.aboutUs("privacypolicy")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(this::showProgressDialog)
                    .doOnCompleted(this::hideProgressDialog)
                    .subscribe(response -> {
                        if (response.getResponseCode().equals("200")) {
                            try {
                                JSONObject jsonObject = new JSONObject(new Gson().toJson(response.getData()));
                                txtRefund.setText(Html.fromHtml(jsonObject.optString("content")));
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
                                Utility.simpleAlert(mcontext, getString(R.string.error), getString(R.string.check_network_connection));
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
            Utility.simpleAlert(mcontext, getString(R.string.error), getString(R.string.check_network_connection));
        }
    }
}