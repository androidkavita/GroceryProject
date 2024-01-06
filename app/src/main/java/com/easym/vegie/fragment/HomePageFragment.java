package com.easym.vegie.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.easym.vegie.AndroidApp;
import com.easym.vegie.R;
import com.easym.vegie.SessionData.SessionManager;
import com.easym.vegie.Utils.RecyclerItemClickListener;
import com.easym.vegie.Utils.Utility;
import com.easym.vegie.activities.FragmentContainerActivity;
import com.easym.vegie.activities.ShopByCategoryActivity;
import com.easym.vegie.activities.ShopByQuotationActivity;
import com.easym.vegie.adapter.BannerPagerAdapter;
import com.easym.vegie.adapter.ShopByCategoryRVAdapter;
import com.easym.vegie.databinding.FragmentHomePageBinding;
import com.easym.vegie.model.DashboardModel;
import com.easym.vegie.model.home.Banner;
import com.easym.vegie.model.home.CategoryName;
import com.easym.vegie.model.home.TimeSlot;
import com.skbfinance.BaseFragment;

import org.jetbrains.annotations.NotNull;

import java.net.ConnectException;
import java.util.List;
import java.util.Objects;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomePageFragment extends BaseFragment implements View.OnClickListener {
    private Context mContext;
    private DashboardModel dashboardModel;

    private FragmentHomePageBinding binding;
    private BannerPagerAdapter bannerPagerAdapter;
    private ShopByCategoryRVAdapter shopByCategoryRVAdapter;

    private Handler mainHandler = null;
    private int currentPage = 0;
    List<Banner> bannerList;


    int[] imgArr = {R.drawable.dummy_banner_2, R.drawable.dummy_banner_2,
            R.drawable.dummy_banner_2, R.drawable.dummy_banner_2};


    public HomePageFragment() {
        // Required empty public constructor
    }

    public static HomePageFragment newInstance(Context mContext) {
        HomePageFragment fragment = new HomePageFragment();
        fragment.mContext = mContext;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_page, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getHome();

        binding.tvShopByQuotation.setOnClickListener(this);
        binding.tvShopByCategory.setOnClickListener(this);
        binding.imageLang.setOnClickListener(this);
        binding.ivSingleImageBanner.setOnClickListener(this);
        binding.ivBanner1.setOnClickListener(this);
        binding.ivBanner2.setOnClickListener(this);

    }

    private void getHome() {

        if (Utility.getInstance().checkInternetConnection(mContext)) {
//            Log.e("UserId",userPref.getUser().getId()+"");
            try {
                apiService.getHome(
                             userPref.getUser().getId(),
                               userPref.getUser().getToken())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(this::showProgressDialog)
                        .doOnCompleted(this::hideProgressDialog)
                        .subscribe(response -> {
                            if (response.getResponseCode().equals("200")) {
                                TimeSlot timeSlot = response.getData().getTimeSlot();
                                bannerList = response.getData().getBanner();
                                List<CategoryName> categoryNames = response.getData().getCaegoryName();
                                SessionManager manager = new SessionManager(requireContext());


                                userPref.setWhatsappNumber(response.getData().getWhatsapp_number());

                                if (timeSlot.getSlotDate() != null) {
                                    binding.tvNextAvailableSlot.setText(
                                            utils.formatDate(timeSlot.getSlotDate())
                                                    + " " + "between " +
                                                    timeSlot.getStartTime() + "-" + timeSlot.getEndTime());
                                    AndroidApp application = new AndroidApp();
                                    if (!AndroidApp.Companion.isAppOpenFirstTime()) {
                                        AndroidApp.Companion.setIsAppOpenFirstTime();
                                        showSlotAlertDialog(timeSlot.getSlotDate(), timeSlot.getStartTime(), timeSlot.getEndTime());
                                    }
                                } else {
                                    binding.tvNextAvailableSlot.setText(timeSlot.getStartTime() + "-" + timeSlot.getEndTime());
                                }

                                if (bannerList.size() != 0) {

                                    bannerPagerAdapter = new BannerPagerAdapter(getContext(), bannerList);
                                    binding.viewPagerBanner.setPageMargin(4);
                                    binding.viewPagerBanner.setAdapter(bannerPagerAdapter);
                                    binding.indicator.setViewPager(binding.viewPagerBanner);

                                    Float density = getContext().getResources().getDisplayMetrics().density;
                                    //Set circle indicator radius
                                    binding.indicator.setRadius(4 * density);

                                    mainHandler = new Handler(Looper.getMainLooper());
                                    //onResume()
                                    mainHandler.post(updateTextTask);
                                    binding.indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                        @Override
                                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                                        }

                                        @Override
                                        public void onPageSelected(int position) {
                                            Log.e("CurrentPosition", "" + position);
                                            // currentPage = position;
                                            // binding.viewPagerBanner.setCurrentItem(currentPage++, true);
                                        }

                                        @Override
                                        public void onPageScrollStateChanged(int state) {

                                        }
                                    });

                                } else {
                                    binding.rlBannerParent.setVisibility(View.GONE);
                                }


                                GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
                                shopByCategoryRVAdapter = new ShopByCategoryRVAdapter(getContext(), categoryNames);
                                binding.rvCategory.setAdapter(shopByCategoryRVAdapter);
                                binding.rvCategory.setLayoutManager(gridLayoutManager);
                                binding.rvCategory.addOnItemTouchListener(new RecyclerItemClickListener(
                                        getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(@NotNull View view, int position) {

                                        Intent intent = new Intent(getContext(), ShopByCategoryActivity.class);
                                        intent.putExtra("position", "" + position);
                                        startActivity(intent);
                                        //startActivity(new Intent(getContext(), ShopByCategoryActivity.class));

                                    }
                                }
                                ));


                            } else if (response.getResponseCode().equals("403")) {
                                utils.openLogoutDialog(getContext(), userPref);
                            } else {
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

                            } catch (Exception ex) {
                                Log.e("", "Within Throwable Exception::" + e.getMessage());
                            }

                        });

            } catch (Exception e) {
                e.printStackTrace();
            }


        } else {
            Utility.simpleAlert(mContext, getString(R.string.error), getString(R.string.check_network_connection));
        }

    }


    private void showSlotAlertDialog(String slotDate, String startTime, String endTime) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        // Setting Dialog Title
        alertDialog.setTitle("Available Slot");
        // Setting Dialog Message
        alertDialog.setMessage("Your next available slot is " + utils.formatDate(slotDate)
                + " " + "between" +
                startTime + " - " + endTime);
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Showing Alert Message
        alertDialog.show();

    }

    private final Runnable updateTextTask = new Runnable() {
        @Override
        public void run() {
            //changePosition();
            mainHandler.postDelayed(this, 500);
        }
    };

    private void changePosition() {
        Log.e("ChangePosition", "" + currentPage);
        if (currentPage == 1) {
            currentPage = 0;
        }
        binding.viewPagerBanner.setCurrentItem(currentPage, true);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mainHandler != null) {
            mainHandler.removeCallbacks(updateTextTask);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mainHandler != null) {
            mainHandler.post(updateTextTask);
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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_shopByQuotation) {
            startActivity(new Intent(getContext(), ShopByQuotationActivity.class));
        } else if (id == R.id.tv_shopByCategory) {
            startActivity(new Intent(getContext(), ShopByCategoryActivity.class));
        } else if (id == R.id.image_lang) {
            Intent intent = new Intent(getContext(), FragmentContainerActivity.class);
            intent.putExtra("FragmentName", "Change Language Fragment");
            startActivity(intent);
        } else if (id == R.id.iv_SingleImageBanner) {
            startActivity(new Intent(getContext(), ShopByCategoryActivity.class));
        } else if (id == R.id.iv_Banner1) {
            startActivity(new Intent(getContext(), ShopByCategoryActivity.class));
        } else if (id == R.id.iv_Banner2) {
            startActivity(new Intent(getContext(), ShopByCategoryActivity.class));
        }
    }


}