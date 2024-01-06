package com.easym.vegie.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.easym.vegie.R;
import com.easym.vegie.Utils.RecyclerItemClickListener;
import com.easym.vegie.Utils.Utility;
import com.easym.vegie.activities.MyOrderDetailsActivity;
import com.easym.vegie.adapter.MyOrderAdapter;
import com.easym.vegie.databinding.FragmentMyOrderBinding;
import com.easym.vegie.model.getorder.Result;
import com.skbfinance.BaseFragment;

import org.jetbrains.annotations.NotNull;

import java.net.ConnectException;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyOrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyOrderFragment extends BaseFragment {


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private Context mContext;

    private FragmentMyOrderBinding binding;
    private MyOrderAdapter myOrderAdapter;

    public MyOrderFragment() {
        // Required empty public constructor
    }


    public static MyOrderFragment newInstance(String param1, String param2) {
        MyOrderFragment fragment = new MyOrderFragment();
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

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_order, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getOrdersList();

    }

    @Override
    public void onResume() {
        super.onResume();
        getOrdersList();
    }

    private void getOrdersList(){

        if (Utility.getInstance().checkInternetConnection(getContext())) {

            apiService.getOrder(
                    userPref.getUser().getId(),
                    userPref.getUser().getToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(this::showProgressDialog)
                    .doOnCompleted(this::hideProgressDialog)
                    .subscribe(response -> {

                        if (response.getResponseCode().equals("200")) {

                            List<Result> list = response.getData().getOrderList().getResult();

                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                                    LinearLayoutManager.VERTICAL,false);

                            myOrderAdapter = new MyOrderAdapter(getContext(),list);
                            binding.rvMyOrder.setAdapter(myOrderAdapter);
                            binding.rvMyOrder.setLayoutManager(linearLayoutManager);
                            binding.rvMyOrder.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                                    new RecyclerItemClickListener.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(@NotNull View view, int position) {

                                            Intent intent = new Intent(getContext(), MyOrderDetailsActivity.class);
                                            intent.putExtra("OrderId",list.get(position).getOrderId());
                                            startActivity(intent);

                                        }
                                    }));
                        } else if(response.getResponseCode().equals("403")){

                            utils.openLogoutDialog(getContext(),userPref);

                        }else{
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