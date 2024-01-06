package com.easym.vegie.fragment;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easym.vegie.R;
import com.easym.vegie.Utils.Utility;
import com.easym.vegie.adapter.FAQAdapter;
import com.easym.vegie.model.faq.GetFAQData;
import com.skbfinance.BaseFragment;

import java.net.ConnectException;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
public class FAQFragment extends BaseFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Context mContext;
    private RecyclerView faqRecyclerView;
    public FAQFragment() {
        // Required empty public constructor
    }
    public static FAQFragment newInstance(String param1, String param2) {
        FAQFragment fragment = new FAQFragment();
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
        View view = inflater.inflate(R.layout.fragment_f_a_q, container, false);
        faqRecyclerView = view.findViewById(R.id.recycler_view);
        getFaqApi();
        return view;
    }

    private void getFaqApi() {
        if (Utility.getInstance().checkInternetConnection(mContext)) {
            apiService.get_faq()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(this::showProgressDialog)
                    .doOnCompleted(this::hideProgressDialog)
                    .subscribe(response -> {
                        if (response.getResponseCode().equals("200")) {

                            List<GetFAQData> faqDataList = response.getData();

                            LinearLayoutManager llm = new LinearLayoutManager(getContext(),
                                    LinearLayoutManager.VERTICAL,false);

                            FAQAdapter faqAdapter = new FAQAdapter(getContext(),faqDataList);
                            faqRecyclerView.setAdapter(faqAdapter);
                            faqRecyclerView.setLayoutManager(llm);

                        }else{
                            hideProgressDialog();
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