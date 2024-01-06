package com.easym.vegie.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.easym.vegie.R;
import com.easym.vegie.Utils.Utility;
import com.easym.vegie.adapter.OffersAdapter;
import com.easym.vegie.databinding.FragmentOffersBinding;
import com.easym.vegie.model.coupon.GetCouponListData;
import com.skbfinance.BaseFragment;

import java.net.ConnectException;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OffersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OffersFragment extends BaseFragment {


    private FragmentOffersBinding binding;
    OffersAdapter offersAdapter;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private Context mContext;

    public OffersFragment() {
        // Required empty public constructor
    }



    public static OffersFragment newInstance(String param1, String param2) {
        OffersFragment fragment = new OffersFragment();
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
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_offers, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getOfferList();
    }

    private void getOfferList(){

        if (Utility.getInstance().checkInternetConnection(getContext())) {

            apiService.getCouponList(
                    userPref.getUser().getId(),
                    userPref.getUser().getToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(this::showProgressDialog)
                    .doOnCompleted(this::hideProgressDialog)
                    .subscribe(response -> {

                        if (response.getResponseCode().equals("200")) {

                            List<GetCouponListData> list = response.getData();

                            LinearLayoutManager llm = new LinearLayoutManager(getContext(),
                                    LinearLayoutManager.VERTICAL,false);

                            offersAdapter = new OffersAdapter(getContext(), list, new OffersAdapter.CopyCoupon() {
                                @Override
                                public void copyCouponCode(String code) {

                                    ClipboardManager clipboard = (ClipboardManager)
                                            getContext().getSystemService(Context.CLIPBOARD_SERVICE);

                                    ClipData clip = ClipData.newPlainText("CouponCode", code);
                                    clipboard.setPrimaryClip(clip);

                                    Toast.makeText(mContext, "Code Copied", Toast.LENGTH_SHORT).show();
                                }
                            });

                            binding.rvOffer.setAdapter(offersAdapter);
                            binding.rvOffer.setLayoutManager(llm);

                        }else if(response.getResponseCode().equals("403")){

                            utils.openLogoutDialog(getContext(),userPref);

                        } else {
                            hideProgressDialog();
                            Utility.simpleAlert(getContext(), getString(R.string.info_dialog_title), response.getResponseMessage());
                        }

                    }, e -> {

                        try {

                            if (e instanceof ConnectException) {
                                hideProgressDialog();
                                Utility.simpleAlert(getContext(), getString(R.string.error), getString(R.string.check_network_connection));
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
            Utility.simpleAlert(getContext(), getString(R.string.error), getString(R.string.check_network_connection));
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