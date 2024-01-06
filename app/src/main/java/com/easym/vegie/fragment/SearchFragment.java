package com.easym.vegie.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.easym.vegie.R;
import com.easym.vegie.Utils.Utility;
import com.easym.vegie.activities.FragmentContainerActivity;
import com.easym.vegie.adapter.SearchAdapter;
import com.easym.vegie.databinding.FragmentSearchBinding;
import com.easym.vegie.model.searchproduct.SearchProductResult;
import com.skbfinance.BaseFragment;

import java.net.ConnectException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends BaseFragment implements View.OnClickListener {

    private FragmentSearchBinding binding;
    private SearchAdapter searchAdapter;


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    int LANGUAGECHANGEREQUESTCODE = 30;


    private String mParam1;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }


    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    binding.etSearch.clearFocus();
                    InputMethodManager in = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(binding.etSearch.getWindowToken(), 0);

                    //saveTask(et_Search.getText().toString());
                    if(!TextUtils.isEmpty(binding.etSearch.getText().toString().trim())) {

                        searchProduct(binding.etSearch.getText().toString().trim());

                    }else{
                        return true;
                    }

                    return true;
                }
                return false;
            }
        });

        binding.ivChangeLang.setOnClickListener(this);


    }

    private void searchProduct(String qs) {

        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"),userPref.getUser().getId());
        RequestBody queryString = RequestBody.create(MediaType.parse("text/plain"),qs);
        RequestBody token = RequestBody.create(MediaType.parse("text/plain"),userPref.getUser().getToken());
        RequestBody langCode = RequestBody.create(MediaType.parse("text/plain"),userPref.getUserPreferLanguageCode());

        if (Utility.getInstance().checkInternetConnection(getContext())) {
            apiService.searchProduct(
                    userId,
                    queryString,
                    token,
                    langCode)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(this::showProgressDialog)
                    .doOnCompleted(this::hideProgressDialog)
                    .subscribe(response -> {

                        if (response.getResponseCode().equals("200")) {

                            List<SearchProductResult> resultList = response.getData().getData();

                            LinearLayoutManager llm = new LinearLayoutManager(getContext());
                            searchAdapter = new SearchAdapter(getContext(), resultList,
                                    apiService, userPref,
                                    new SearchAdapter.WishList() {
                                        @Override
                                        public void addToWishList(ImageView iv_RemoveFromWishlist, ImageView iv_AddToWishlist, String menuId) {

                                            addWishListApi(iv_RemoveFromWishlist,iv_AddToWishlist,menuId);
                                        }

                                        @Override
                                        public void removeFromWishList(ImageView iv_AddToWishlist, ImageView iv_RemoveFromWishlist, String menuId) {

                                            removeWishListApi(iv_AddToWishlist,iv_RemoveFromWishlist,menuId);
                                        }
                            },utils);

                            binding.rvSearchItem.setAdapter(searchAdapter);
                            binding.rvSearchItem.setLayoutManager(llm);

                            binding.rvSearchItem.setVisibility(View.VISIBLE);


                        }
                        else if(response.getResponseCode().equals("403")){

                            utils.openLogoutDialog(getContext(),userPref);

                        }else {
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


    private void addWishListApi(ImageView iv_RemoveFromWishlist,
                                ImageView iv_AddToWishlist,
                                String menuId) {

        if (Utility.getInstance().checkInternetConnection(getContext())) {
            apiService.addWishList(
                    userPref.getUser().getId(),
                    menuId,
                    userPref.getUser().getToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            showProgressDialog();
                        }
                    })
                    .doOnCompleted(new Action0() {
                        @Override
                        public void call() {
                            hideProgressDialog();
                        }
                    })
                    .subscribe(response -> {

                        if (response.getResponseCode().equals("200")) {

                            iv_AddToWishlist.setVisibility(View.GONE);
                            iv_RemoveFromWishlist.setVisibility(View.VISIBLE);
                            Toast.makeText(getContext(), response.getResponseMessage(),
                                    Toast.LENGTH_SHORT).show();


                            iv_RemoveFromWishlist.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    removeWishListApi(iv_AddToWishlist,iv_RemoveFromWishlist,menuId);
                                }
                            });


                        }else if(response.getResponseCode().equals("403")){

                            utils.openLogoutDialog(getContext(),userPref);

                        } else {

                            hideProgressDialog();
                            Utility.simpleAlert(getContext(), getContext().getString(R.string.info_dialog_title), response.getResponseMessage());
                        }

                    }, e -> {

                        try {

                            if (e instanceof ConnectException) {
                                hideProgressDialog();
                                Utility.simpleAlert(getContext(), getContext().getString(R.string.error),
                                        getContext().getString(R.string.check_network_connection));
                            } else {
                                hideProgressDialog();
                                e.printStackTrace();
                                Utility.simpleAlert(getContext(), "", getString(R.string.something_went_wrong));
                            }
                        }catch (Exception ex){
                            Log.e( "MyProfilePage Activity","Within Throwable Exception::" + e.getMessage());
                        }

                    } );

        } else {
            hideProgressDialog();
            Utility.simpleAlert(getContext(),
                    getString(R.string.error), getString(R.string.check_network_connection));
        }
    }

    private void removeWishListApi(ImageView iv_AddToWishlist, ImageView iv_RemoveFromWishlist,String menuId) {

        if (Utility.getInstance().checkInternetConnection(getContext())) {
            apiService.removeWishList(userPref.getUser().getId(),
                    menuId,
                    userPref.getUser().getToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            showProgressDialog();
                        }
                    })
                    .doOnCompleted(new Action0() {
                        @Override
                        public void call() {
                            hideProgressDialog();
                        }
                    })
                    .subscribe(response -> {

                        if (response.getResponseCode().equals("200")) {

                            if (response.getResponseCode().equals("200")) {

                                iv_RemoveFromWishlist.setVisibility(View.GONE);
                                iv_AddToWishlist.setVisibility(View.VISIBLE);

                                Toast.makeText(getContext(), response.getResponseMessage(), Toast.LENGTH_SHORT).show();

                                iv_AddToWishlist.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        addWishListApi(iv_RemoveFromWishlist,iv_AddToWishlist,menuId);
                                    }
                                });

                            }else if(response.getResponseCode().equals("403")){

                                utils.openLogoutDialog(getContext(),userPref);

                            }else{

                                hideProgressDialog();
                                Utility.simpleAlert(getContext(), getString(R.string.error), response.getResponseMessage());
                            }


                        } else {

                            hideProgressDialog();
                            Utility.simpleAlert(getContext(), getContext().getString(R.string.error),
                                    response.getResponseMessage());
                        }

                    }, e -> {

                        try {

                            if (e instanceof ConnectException) {
                                hideProgressDialog();
                                Utility.simpleAlert(getContext(), getContext().getString(R.string.error),
                                        getContext().getString(R.string.check_network_connection));
                            } else {
                                hideProgressDialog();
                                e.printStackTrace();
                                Utility.simpleAlert(getContext(),"",getString(R.string.something_went_wrong));
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
    public void onClick(View v) {
        int id = v.getId();

        switch (id){

            case R.id.iv_ChangeLang :
                Intent intent = new Intent(getContext(), FragmentContainerActivity.class);
                intent.putExtra("FragmentName", "Change Language Fragment");
                intent.putExtra("IsForResult","True");
                startActivityForResult(intent,LANGUAGECHANGEREQUESTCODE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == LANGUAGECHANGEREQUESTCODE && resultCode == Activity.RESULT_OK) {

            Log.e("Lang Code1",userPref.getUserPreferLanguageCode()+"");

            String keyword = binding.etSearch.getText().toString().trim();

            if(!TextUtils.isEmpty(keyword)) {
                searchProduct(keyword);
            }

        }
    }

}