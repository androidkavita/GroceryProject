package com.easym.vegie.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.easym.vegie.R;
import com.easym.vegie.SessionData.SessionManager;
import com.easym.vegie.Utils.EnumClicks;
import com.easym.vegie.Utils.Utility;
import com.easym.vegie.adapter.MenuAdapter;
import com.easym.vegie.fragment.AboutUsFragment;
import com.easym.vegie.fragment.ContactUsFragment;
import com.easym.vegie.fragment.FAQFragment;
import com.easym.vegie.fragment.HelpFragment;
import com.easym.vegie.fragment.HomePageFragment;
import com.easym.vegie.fragment.MyOrderFragment;
import com.easym.vegie.fragment.ReachUsFragment;
import com.easym.vegie.interfaces.OnItemClickListener;
import com.easym.vegie.model.MenuModel;
import com.easym.vegie.pushnotification.Config;
import com.foodfairy.deliveryboy.Location.LocationUtil;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.snackbar.Snackbar;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomePageActivity extends BaseActivity implements View.OnClickListener,
        OnItemClickListener {
    LinearLayout ll_Category, ll_SearchProduct, ll_Cart, ll_Home, ll_WhatsApp;
    Geocoder geocoder;
    List<Address> addresses;
    int PERMISSION_ID = 44;
    int AUTOCOMPLETE_PICKUP_REQUEST_CODE = 503;
    //Checkout
    TextView tv_CartCount;
    String orderId = "";
    int cartCount;
    int totalAmount;
    int minimumOrderLimit;
    private RecyclerView recycler_menu;
    private Context mContext;
    private DrawerLayout drawer_layout;
    private ImageView img_menu, iv_lang;
    private Fragment mfragment;
    private TextView txtUserName, txtUserEmailId;
    private CircularImageView userProfile;
    private LinearLayout profileLayout;
    private LinearLayout ll_UserCurrentLocation;
    private TextView tv_PinCodeCity;
    private ImageView iv_User;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        mContext = this;
        recycler_menu = findViewById(R.id.recycler_menu);
        drawer_layout = findViewById(R.id.drawer_layout);
        img_menu = findViewById(R.id.img_menu);
        txtUserName = findViewById(R.id.txt_user_name);
        txtUserEmailId = findViewById(R.id.txt_email);
        userProfile = findViewById(R.id.img_profile);
        profileLayout = findViewById(R.id.lin_profile);
        ll_Home = findViewById(R.id.ll_Home);

        ll_Category = findViewById(R.id.ll_Category);
        ll_SearchProduct = findViewById(R.id.ll_SearchProduct);
        ll_WhatsApp = findViewById(R.id.ll_WhatsApp);

        ll_UserCurrentLocation = findViewById(R.id.ll_UserCurrentLocation);
        tv_PinCodeCity = findViewById(R.id.tv_PinCodeCity);
        iv_User = findViewById(R.id.iv_User);
        ll_Cart = findViewById(R.id.ll_Cart);
        iv_lang = findViewById(R.id.iv_lang);

        tv_CartCount = findViewById(R.id.tv_CartCount);

        toolbarSetting(true);
        Toast.makeText(mContext, ""+userPref.getUser().getId(), Toast.LENGTH_SHORT).show();

        DividerItemDecoration itemDecor = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
        recycler_menu.addItemDecoration(itemDecor);

        recycler_menu.setLayoutManager(new LinearLayoutManager(mContext));
        MenuAdapter adapter = new MenuAdapter(mContext, new MenuModel().addMenuData(), this);
        openDashboardFragment(HomePageFragment.newInstance(mContext));
        recycler_menu.setAdapter(adapter);

        profileLayout.setOnClickListener(this);
        ll_Category.setOnClickListener(this);
        ll_SearchProduct.setOnClickListener(this);
        iv_User.setOnClickListener(this);
        ll_Cart.setOnClickListener(this);
        ll_UserCurrentLocation.setOnClickListener(this);
        iv_lang.setOnClickListener(this);
        ll_Home.setOnClickListener(this);
        ll_WhatsApp.setOnClickListener(this);

        SharedPreferences pref = getSharedPreferences(Config.SHARED_PREF, Context.MODE_PRIVATE);

        String savedToken = pref.getString("regId", "");
        Log.e("SavedRegistrationToken", "savedToken 1 " + savedToken);
//        if (TextUtils.isEmpty(savedToken)) {
//            String token = FirebaseInstanceId.getInstance().getToken();
//            Log.e("SavedRegistrationToken", "savedToken 2 " + token);
//        }

//        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
//            String newToken = instanceIdResult.getToken();
//            Log.e("Token 3", newToken);
//
//        });

        try {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                if (getIntent().hasExtra("OrderId")) {
                    orderId = bundle.getString("OrderId");
                    String cod = bundle.getString("Cod");
                    showAlertDialogOnSuccessfulPayment(orderId, cod);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }



/*        Intent intent = getIntent();
        if (intent != null){
            orderId = intent.getStringExtra("OrderId");
            String cod = intent.getStringExtra("Cod");
            showAlertDialogOnSuccessfulPayment(orderId, cod);
        }*/


        if (!Places.isInitialized()) {
            Places.initialize(this, getString(R.string.google_map_key));
        }

        getLocation();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserData();
        getCartCount();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_PICKUP_REQUEST_CODE && data != null) {
            try {
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Place place = Autocomplete.getPlaceFromIntent(data);
                        Log.e("Address", "" + place.getAddress());
                        Log.e("Lat", "" + place.getLatLng().latitude);
                        Log.e("Lng", "" + place.getLatLng().longitude);

                        String latitude = "" + place.getLatLng().latitude;
                        String longitude = "" + place.getLatLng().longitude;

                        geocoder = new Geocoder(HomePageActivity.this, Locale.getDefault());

                        try {

                            addresses = geocoder.getFromLocation(
                                    place.getLatLng().latitude,
                                    place.getLatLng().longitude,
                                    1
                            ); // Here 1 represent max location result to returned, by documents it recommended 1 to 5


                            String address = addresses.get(0).getAddressLine(0);// If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            String city = addresses.get(0).getLocality();
                            String state = addresses.get(0).getAdminArea();
                            String country = addresses.get(0).getCountryName();
                            String postalCode = addresses.get(0).getPostalCode();
                            String knownName = addresses.get(0).getFeatureName();
                            String locality = addresses.get(0).getSubLocality();

                            if (locality != null) {
                                tv_PinCodeCity.setText("" + locality);
                            } else if (city != null) {
                                tv_PinCodeCity.setText("" + city);
                            }

                            if (latitude != null && !TextUtils.isEmpty(latitude)
                                    && longitude != null && !TextUtils.isEmpty(longitude)) {
                                checkSupplyLocation(latitude, longitude);
                            }


                            // Log.e("Address",""+address);
                            // Log.e("City",""+city);
                            // Log.e("State",""+state);
                            // Log.e("Country",""+country);
                            // Log.e("PostalCode",""+postalCode);
                            // Log.e("KnownName",""+knownName);
                            // Log.e("Locality",addresses.get(0).getSubLocality());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;

                    case AutocompleteActivity.RESULT_ERROR:
                        Status status = Autocomplete.getStatusFromIntent(data);
                        Log.i("TAG", status.getStatusMessage());
                        break;

                    case Activity.RESULT_CANCELED:
                        Log.i("TAG", "Result_Canceled");
                        break;

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.lin_profile:
                Intent intent = new Intent(mContext, MyProfilePageActivity.class);
                startActivity(intent);
                break;

            case R.id.ll_Home:

                Fragment f = getSupportFragmentManager().findFragmentById(R.id.content_frame);
                if (f instanceof HomePageFragment) {

                } else {
                    openDashboardFragment(HomePageFragment.newInstance(mContext));
                }
                break;

            case R.id.ll_Category:
                Intent intent1 = new Intent(mContext, ShopByCategoryActivity.class);
                startActivity(intent1);
                break;

            case R.id.ll_SearchProduct:
                Intent intent2 = new Intent(this, FragmentContainerActivity.class);
                intent2.putExtra("FragmentName", "Search Fragment");
                startActivity(intent2);
                break;

            case R.id.iv_User:
                Intent intent3 = new Intent(mContext, MyProfilePageActivity.class);
                startActivity(intent3);
                break;

            case R.id.ll_Cart:
                Intent intent4 = new Intent(this, FragmentContainerActivity.class);
                intent4.putExtra("FragmentName", "My Cart List Fragment");
                startActivity(intent4);

                /*if(cartCount == 0) {
                    Intent intent4 = new Intent(this, FragmentContainerActivity.class);
                    intent4.putExtra("FragmentName", "My Cart List Fragment");
                    startActivity(intent4);
                }else {

                    if(totalAmount >= minimumOrderLimit){

                        Intent intent4 = new Intent(this, FragmentContainerActivity.class);
                        intent4.putExtra("FragmentName", "My Cart List Fragment");
                        startActivity(intent4);

                    }else{

                        utils.simpleAlert(this,getString(R.string.minimum_order_amount),getString(R.string.minimum_order_amount_limit_is)+" "+minimumOrderLimit);

                    }

                }*/

                break;

            case R.id.ll_UserCurrentLocation:

                List<Place.Field> fields = new ArrayList<>();
                fields.add(Place.Field.ID);
                fields.add(Place.Field.NAME);
                fields.add(Place.Field.LAT_LNG);
                fields.add(Place.Field.ADDRESS);
                fields.add(Place.Field.ADDRESS_COMPONENTS);

                Intent intentPlace = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(this);
                startActivityForResult(intentPlace, AUTOCOMPLETE_PICKUP_REQUEST_CODE);

                break;

            case R.id.iv_lang:
                Intent intent6 = new Intent(this, FragmentContainerActivity.class);
                intent6.putExtra("FragmentName", "Change Language Fragment");
                startActivity(intent6);
                break;

            case R.id.ll_WhatsApp:
                //Connect with customer support on whatsapp..
                connectSupportOnWhatsapp();
                break;
        }

    }

    private void getUserData() {
        try {
            txtUserName.setText(userPref.getUser().getUsername());
            txtUserEmailId.setText(userPref.getUser().getEmail());
            if (TextUtils.isEmpty(userPref.getUser().getProfile_image())) {
                Picasso.get().load(R.drawable.user_profile).into(userProfile);
            } else {
                Picasso.get().load(userPref.getUser().getProfile_image()).into(userProfile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openDashboardFragment(Fragment fragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();
    }

    public void toolbarSetting(Boolean flag) {
        if (flag) {
            img_menu.setImageDrawable(getResources().getDrawable(R.drawable.icon_menu_black));
            img_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (drawer_layout.isShown())
                        drawer_layout.closeDrawers();
                    else
                        Toast.makeText(mContext, "www", Toast.LENGTH_SHORT).show();
                    drawer_layout.openDrawer(GravityCompat.START);
                }
            });
        }
    }

    @Override
    public void onItemClickListener(EnumClicks where, View view, int position, Object obj1, Object obj2, Object obj3, boolean isChecked) {
        toolbarSetting(isChecked);
        if (drawer_layout.isShown()) {
            drawer_layout.closeDrawers();
        }

        switch (where) {

            case MY_ORDER:
                // startActivity(new Intent(mContext, MyOrderActivity.class));
                mfragment = new MyOrderFragment();
                setFragment(mfragment, false, this, R.id.content_frame);
                break;

            case SEARCH_PRODUCT:
                // startActivity(new Intent(mContext, MyOrderActivity.class));
                Intent intent = new Intent(this, FragmentContainerActivity.class);
                intent.putExtra("FragmentName", "Search Fragment");
                startActivity(intent);
               /* mfragment = new SearchFragment();
                setFragment(mfragment, false, this, R.id.content_frame);*/
                break;

            case MY_CART:

                Intent intent4 = new Intent(this, FragmentContainerActivity.class);
                intent4.putExtra("FragmentName", "My Cart List Fragment");
                startActivity(intent4);

                // startActivity(new Intent(mContext, MyCartActivity.class));
               /* mfragment = new MyCartListFragment();
                setFragment(mfragment, false, this, R.id.content_frame);*/
                break;

            case OFFERS:
                // startActivity(new Intent(mContext, MyOrderActivity.class));
                Intent intent1 = new Intent(this, FragmentContainerActivity.class);
                intent1.putExtra("FragmentName", "Offers Fragment");
                startActivity(intent1);
               /* mfragment = new OffersFragment();
                setFragment(mfragment, false, this, R.id.content_frame);*/
                break;

            case SAVED_QUOTATIONS:
                //SavedQuotationsFragment
                Intent intentQuotation = new Intent(this, FragmentContainerActivity.class);
                intentQuotation.putExtra("FragmentName", "Saved Quotations Fragment");
                startActivity(intentQuotation);
                break;

           /* case PAYMENT_OPTION:
                mfragment = new SavedProductFragment();
                setFragment(mfragment, false, this, R.id.content_frame);
                break;*/

            case REFUND_POLICY:
                /*mfragment = new RefundPolicyFragment();
                setFragment(mfragment, false, this, R.id.content_frame);*/
                Intent intent2 = new Intent(this, FragmentContainerActivity.class);
                intent2.putExtra("FragmentName", "Refund Policy Fragment");
                startActivity(intent2);
                break;

            case REACH_US:
                mfragment = new ReachUsFragment();
                setFragment(mfragment, false, this, R.id.content_frame);
                break;


            case CONTACT_US:
                mfragment = new ContactUsFragment();
                setFragment(mfragment, false, this, R.id.content_frame);
                break;

            case HELP:
                mfragment = new HelpFragment();
                setFragment(mfragment, false, this, R.id.content_frame);
                break;

            case FAQ:
                mfragment = new FAQFragment();
                setFragment(mfragment, false, this, R.id.content_frame);
                break;


            case ABOUT_US:
                mfragment = new AboutUsFragment();
                setFragment(mfragment, false, this, R.id.content_frame);
                break;

            case LOGOUT:
                openLogoutDialog();
                break;

        }

    }

    private void openLogoutDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomePageActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("Confirm Logout...");

        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you want to logout?");

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                signOut();

            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                //  throw new RuntimeException("Test Crash");
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    private void signOut() {
        new SessionManager(mContext).clearUserSession();
        userPref.clearPref();
        Intent intent = new Intent(mContext, LoginPageActivity.class);
        startActivity(intent);
        finish();
    }

    void setFragment(Fragment fragment, Boolean removeStack, FragmentActivity activity, int mContainer) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction ftTransaction = fragmentManager.beginTransaction();
        if (removeStack) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            ftTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
            ftTransaction.replace(mContainer, fragment);
            ftTransaction.addToBackStack(null);
        } else {
            ftTransaction.replace(mContainer, fragment);
            ftTransaction.addToBackStack(null);
        }
        ftTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notification, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);

    }

    private void getLocation() {

        if (checkPermission()) {

            if (isLocationEnable()) {

                new LocationUtil(this, new LocationUtil.UserLocation() {
                    @Override
                    public void userLatLong(@NotNull String latitude, @NotNull String longitude) {

                        Log.e("Latitude", "" + latitude);
                        Log.e("Longitude", "" + longitude);

                        geocoder = new Geocoder(HomePageActivity.this, Locale.getDefault());

                        try {
                            addresses = geocoder.getFromLocation(Double.parseDouble(latitude),
                                    Double.parseDouble(longitude), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            String city = addresses.get(0).getLocality();
                            String state = addresses.get(0).getAdminArea();
                            String country = addresses.get(0).getCountryName();
                            String postalCode = addresses.get(0).getPostalCode();
                            String knownName = addresses.get(0).getFeatureName();

                            System.out.println(address);
                            System.out.println(city);
                            System.out.println(state);
                            System.out.println(country);
                            System.out.println(postalCode);
                            System.out.println(knownName);

                            if (city != null) {
                                tv_PinCodeCity.setText("" + city);
                            }

                         /*  if(postalCode != null && city != null){
                                tv_PinCodeCity.setText(""+postalCode+" "+city);
                            }else if(postalCode != null){
                                tv_PinCodeCity.setText(""+postalCode);
                            }else if(city != null){
                                tv_PinCodeCity.setText(""+city);
                            }*/

                            /*if(postalCode != null && !postalCode.equals("")){
                                checkDeliveryLocation(postalCode);
                            }*/

                            if (latitude != null && !TextUtils.isEmpty(latitude)
                                    && longitude != null && !TextUtils.isEmpty(longitude)) {
                                checkSupplyLocation(latitude, longitude);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }).getLastLocation();

            } else {
                Toast.makeText(HomePageActivity.this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }

        } else {
            requestPermission();
        }

    }

    private void checkSupplyLocation(String latitude, String longitude) {
try {
    if (Utility.getInstance().checkInternetConnection(this)) {

        apiService.checkByLocation(
                        latitude,
                        longitude,
                        userPref.getUser().getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {

                    if (response.getResponseCode().equals("200")) {

                    } else if (response.getResponseCode().equals("403")) {
                        utils.openLogoutDialog(this, userPref);
                    } else {
                        hideProgressDialog();

                        if (TextUtils.isEmpty(orderId)) {
                            showAlertDialogToChangeLocation();
                        }
                    }

                }, e -> {

                    try {

                        if (e instanceof ConnectException) {
                            Log.d("HomePageActivity", "Internet Connection issue");
                        } else {
                            Log.d("HomePageActivity", "" + e.getMessage());
                        }

                    } catch (Exception ex) {
                        Log.e("", "Within Throwable Exception::" + e.getMessage());
                    }

                });

    } else {
        hideProgressDialog();
        Utility.simpleAlert(this, getString(R.string.error), getString(R.string.check_network_connection));
    }
}catch (Exception e){
    e.printStackTrace();
}

    }

    private boolean checkPermission() {

        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }


    private boolean isLocationEnable() {

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if ((grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation();
            } else {
                Toast.makeText(this, "Permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showAlertDialogToChangeLocation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        View view = LayoutInflater.from(this).inflate(R.layout.no_supply_pop_screen_layout, null);
        ImageView iv_Close = view.findViewById(R.id.iv_Close);
        TextView tv_Description = view.findViewById(R.id.tv_Description);
        Button btn_ChangeAddress = view.findViewById(R.id.btn_ChangeAddress);
        builder.setView(view);
        AlertDialog dialog = builder.show();
        iv_Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_ChangeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent5 = new Intent(HomePageActivity.this,
                        FragmentContainerActivity.class);
                intent5.putExtra("FragmentName", "Add Address Fragment");
                startActivity(intent5);
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    private void showAlertDialogOnSuccessfulPayment(String orderId, String cod) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        View view = LayoutInflater.from(this).inflate(R.layout.payment_successfull_popup_layout, null);

        ImageView iv_Close = view.findViewById(R.id.iv_Close);
        Button btn_TrackOrder = view.findViewById(R.id.btn_TrackOrder);

        TextView tv_Description = view.findViewById(R.id.tv_Description);
        TextView tv_Message = view.findViewById(R.id.tv_Message);

        if (cod.equals("1")) {
            tv_Description.setVisibility(View.GONE);
            tv_Message.setText(getString(R.string.your_order_is_placed_successfully));
        }

        builder.setView(view);
        AlertDialog dialog = builder.show();

        iv_Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_TrackOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomePageActivity.this,
                        MyOrderDetailsActivity.class);
                intent.putExtra("OrderId", "" + orderId);
                startActivity(intent);

                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void checkDeliveryLocation(String pincode) {

        if (Utility.getInstance().checkInternetConnection(this)) {

            apiService.checkPinCode(
                            pincode,
                            userPref.getUser().getToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    /*.doOnSubscribe(this::showProgressDialog)
                    .doOnCompleted(this::hideProgressDialog)*/
                    .subscribe(response -> {

                        if (response.getResponseCode().equals("200")) {

                        } else if (response.getResponseCode().equals("403")) {

                            utils.openLogoutDialog(this, userPref);

                        } else {
                            hideProgressDialog();
                            showAlertDialogToChangeLocation();
                        }

                    }, e -> {

                        try {

                            if (e instanceof ConnectException) {

                                Log.d("HomePageActivity", "Internet Connection issue");
                                // hideProgressDialog();
                                // Utility.simpleAlert(this, getString(R.string.error), getString(R.string.check_network_connection));
                            } else {
                                Log.d("HomePageActivity", "" + e.getMessage());
                                // hideProgressDialog();
                                // Utility.simpleAlert(this, getString(R.string.error), e.getMessage());
                            }

                        } catch (Exception ex) {
                            Log.e("", "Within Throwable Exception::" + e.getMessage());
                        }
                    });

        } else {
            hideProgressDialog();
            Utility.simpleAlert(this, getString(R.string.error), getString(R.string.check_network_connection));
        }

    }

    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (f instanceof HomePageFragment) {
            openExistDialog();
        } else {
            super.onBackPressed();
        }
    }

    private void connectSupportOnWhatsapp() {

        PackageManager packageManager = getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);

        String customerSupportNumber = userPref.getWhatsappNumber();
        if (customerSupportNumber.equals("")) {
            Snackbar.make(findViewById(android.R.id.content),
                    "Please provide customer support number", Snackbar.LENGTH_SHORT).show();
            return;
        }
        try {
            String url = "https://api.whatsapp.com/send?phone=" + customerSupportNumber
                    + "&text=" + URLEncoder.encode("Hello", "UTF-8");
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            startActivity(i);

        } catch (Exception e) {
            Toast.makeText(mContext, "Whatsapp not found or something went wrong", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    private void openExistDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomePageActivity.this);
        // Setting Dialog Title
        alertDialog.setTitle("Exit");
        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you want to exit?");
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                //throw new RuntimeException("Test Crash");
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


    private void getCartCount() {
        try {
            if (Utility.getInstance().checkInternetConnection(this)) {

                apiService.userCartCount(
                                userPref.getUser().getId(),
                                userPref.getUser().getToken())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        /* .doOnSubscribe { showProgressDialog() }
                         .doOnCompleted { hideProgressDialog() }*/
                        .subscribe(response -> {

                            if (response.getResponseCode().equals("200")) {

                                //Toast.makeText(this,it.responseMessage,Toast.LENGTH_SHORT).show()

                                cartCount = response.getData().getCount_cart();
                                if (response.getData().getTotal_amount() != null) {
                                    totalAmount = response.getData().getTotal_amount();
                                } else {
                                    totalAmount = 0;
                                }

                                minimumOrderLimit = response.getData().getMinimum_order_limit();


                                if (cartCount != 0) {

                                    tv_CartCount.setText("" + cartCount);
                                    // tv_CartCount.setText(""+total)
                                    Log.d("UserCartCountApi", "" + cartCount);

                                } else {
                                    tv_CartCount.setText("");
                                }

                            } else if (response.getResponseCode().equals("403")) {

                                utils.openLogoutDialog(this, userPref);

                            } else {

                                tv_CartCount.setText("");
                                // tv_CartCount.setText("")
                                Log.d("UserCartCountApi", "" + response.getResponseMessage());
                            /* hideProgressDialog()
                             Utility.simpleAlert(this, getString(R.string.error), it.responseMessage)*/
                            }

                        }, e -> {

                            try {

                                if (e instanceof ConnectException) {

                                    Log.d("HomePageActivity", "Internet Connection issue");
                                    // hideProgressDialog();
                                    // Utility.simpleAlert(this, getString(R.string.error), getString(R.string.check_network_connection));
                                } else {
                                    Log.d("HomePageActivity", "" + e.getMessage());
                                    // hideProgressDialog();
                                    // Utility.simpleAlert(this, getString(R.string.error), e.getMessage());
                                }

                            } catch (Exception ex) {
                                Log.e("", "Within Throwable Exception::" + e.getMessage());
                            }
                        });
            } else {
                Log.d("UserCartCountApi", "" + getString(R.string.check_network_connection));
            /* hideProgressDialog()
             Utility.simpleAlert(this, getString(R.string.error), getString(R.string.check_network_connection))*/
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
