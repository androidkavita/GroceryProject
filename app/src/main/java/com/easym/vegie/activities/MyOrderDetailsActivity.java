package com.easym.vegie.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.easym.vegie.R;
import com.easym.vegie.Utils.Utility;
import com.easym.vegie.Utils.Utils;
import com.easym.vegie.adapter.OrderItemRVAdapter;
import com.easym.vegie.adapter.OrderSequenceAdapter;
import com.easym.vegie.databinding.ActivityMyOrderBinding;
import com.easym.vegie.model.getorderdetail.ProductList;
import com.easym.vegie.model.getorderdetail.Result;
import com.easym.vegie.model.getorderdetail.Status;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Refund;
import com.transferwise.sequencelayout.SequenceLayout;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;



public class MyOrderDetailsActivity extends BaseActivity implements View.OnClickListener {

    ImageView iv_Back,iv_Search,iv_Cart;
    TextView tv_Title;

    SequenceLayout sequenceLayout_Parent;
    Button button_cancelOrder,buttonBackHome;


    ActivityMyOrderBinding binding;
    String orderId;
    OrderItemRVAdapter orderItemRVAdapter;
    boolean isOrderedProductVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this,R.layout.activity_my_order);

        iv_Back = findViewById(R.id.iv_Back);
        tv_Title = findViewById(R.id.tv_Title);
        iv_Search = findViewById(R.id.iv_Search);
        iv_Cart = findViewById(R.id.iv_Cart);
        sequenceLayout_Parent = findViewById(R.id.sequenceLayout_Parent);
        button_cancelOrder = findViewById(R.id.button_cancelOrder);
        buttonBackHome = findViewById(R.id.buttonBackHome);


        iv_Back.setOnClickListener(this);
        button_cancelOrder.setOnClickListener(this);
        binding.buttonReturn.setOnClickListener(this);
        buttonBackHome.setOnClickListener(this);

        tv_Title.setText("Order Details");
        iv_Search.setVisibility(View.GONE);
        iv_Cart.setVisibility(View.GONE);
        tv_Title.setVisibility(View.VISIBLE);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            orderId = bundle.getString("OrderId");
            getOrderDetails(orderId);
        }



    }


    private void getOrderDetails(String orderId){

        if (Utility.getInstance().checkInternetConnection(this)) {

            apiService.getOrderDetail(
                    userPref.getUser().getId(),
                    orderId,
                    userPref.getUser().getToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(this::showProgressDialog)
                    .doOnCompleted(this::hideProgressDialog)
                    .subscribe(response -> {

                        if (response.getResponseCode().equals("200")) {

                  Result results =  response.getData().getOrderList().getResult();

                         List<ProductList> productLists = results.getProductList();
                         List<Status> statusList = results.getStatusDetails();

                         List<String> productNameList = new ArrayList<>();
                         String orderedProduct = "";
                         for(int i=0;i<productLists.size();i++){
                             productNameList.add(productLists.get(i).getMenuName());
                         }
                         orderedProduct = TextUtils.join(",",productNameList);
                         binding.tvOrderedProduct.setText(productLists.get(0).getMenuName()
                                 +"("+productLists.get(0).getQuantity()+" "+productLists.get(0).getUnit()+")");

                         if( results.getTransaction_key() != null &&  TextUtils.isEmpty(results.getTransaction_key())){
                            /* binding.rlPaidAmount.setVisibility(View.GONE);
                             binding.rlRemainingAmount.setVisibility(View.GONE);*/
                             binding.tvTotalTitle.setText("Amount to be paid");
                            // binding.tvPaymentMode.setText("Cash");

                         }

                         binding.tvOrderId.setText(results.getOrderId());
                         if(results.getExpectedDeliveryDate() != null) {
                             binding.tvExpectedDeliveryDate.setText(utils.getDate(results.getExpectedDeliveryDate(), "yyyy-MM-dd HH:mm:ss"));
                         }
                         binding.tvTotal.setText(""+results.getTotal());
                         binding.tvDeliveryCharge.setText(results.getDeliveryCharge());
                         /*binding.tvPaidAmount.setText(results.getPaidAmount());
                         binding.tvRemainingAmount.setText(results.getRemainingAmount());*/

                            if(results.getPayment_type().equalsIgnoreCase("1")){
                                binding.tvPaymentMode.setText("Cash");
                            } else if(results.getPayment_type().equalsIgnoreCase("2")){
                                binding.tvPaymentMode.setText("Online");
                            }

                         showOrderItemInRecyclerView(productLists);

                         binding.llOrderProduct.setOnClickListener(this);


                         if(results.getStatus().equals("Cancel")){

                             binding.sequenceLayoutParent.setVisibility(View.GONE);
                             binding.tvDelivered.setVisibility(View.GONE);
                             binding.buttonCancelOrder.setVisibility(View.GONE);
                             binding.infoFrame.setVisibility(View.VISIBLE);
                             binding.tvCancelled.setVisibility(View.VISIBLE);


                         }  else {

                             if(results.getStatus().equals("Delivered")){
                                 binding.tvDelivered.setVisibility(View.VISIBLE);
                                 binding.tvCancelled.setVisibility(View.GONE);
                                 binding.buttonCancelOrder.setVisibility(View.GONE);
                                 binding.buttonReturn.setVisibility(View.VISIBLE);
                                 binding.infoFrame.setVisibility(View.VISIBLE);

                             } else if(results.getStatus().equals("return")){
                                 binding.tvDelivered.setVisibility(View.GONE);
                                 binding.tvCancelled.setVisibility(View.GONE);
                                 binding.tvRetured.setVisibility(View.VISIBLE);
                                 binding.buttonCancelOrder.setVisibility(View.GONE);
                                 binding.buttonReturn.setVisibility(View.GONE);
                                 binding.sequenceLayoutParent.setVisibility(View.GONE);
                                 binding.infoFrame.setVisibility(View.VISIBLE);

                             } else {

                                 if(statusList.size() != 0) {
                                     binding.tvDelivered.setVisibility(View.GONE);
                                     binding.tvCancelled.setVisibility(View.GONE);
                                     binding.infoFrame.setVisibility(View.VISIBLE);
                                     binding.sequenceLayoutParent.setVisibility(View.VISIBLE);

                                     // status details
                                     List<OrderSequenceAdapter.OrderSequenceItem> itemList =
                                             new ArrayList<OrderSequenceAdapter.OrderSequenceItem>();

                                     for (int i = 0; i < statusList.size(); i++) {

                                         boolean isActive = false;
                                         String date = new Utils(this).getFormattedDate(
                                                 statusList.get(i).getCreatedAt(),
                                                 "yyyy-MM-dd hh:mm:ss");

                                         if (statusList.get(i).getIsActive().equals("0")) {
                                             isActive = false;
                                         } else if (statusList.get(i).getIsActive().equals("1")) {
                                             isActive = true;
                                         }
                                         itemList.add(new OrderSequenceAdapter.OrderSequenceItem(
                                                 isActive,
                                                 statusList.get(i).getStatus(),
                                                 "",
                                                 date));
                                     }

                                     sequenceLayout_Parent.setAdapter(new OrderSequenceAdapter(itemList));


                                 } else {
                                     binding.sequenceLayoutParent.setVisibility(View.GONE);
                                     binding.tvDelivered.setVisibility(View.GONE);
                                     binding.tvCancelled.setVisibility(View.GONE);
                                     binding.infoFrame.setVisibility(View.GONE);
                                     binding.buttonCancelOrder.setVisibility(View.VISIBLE);

                                 }

                             }



                            /* itemList.add(new OrderSequenceAdapter.OrderSequenceItem(true,
                                     "Order Placed","",""));
                             itemList.add(new OrderSequenceAdapter.OrderSequenceItem(false,
                                     "Order Accepted","",""));
                             itemList.add(new OrderSequenceAdapter.OrderSequenceItem(false,
                                     "Order Being Prepa","",""));*/


                         }

                        }
                        else if(response.getResponseCode().equals("403")){
                            utils.openLogoutDialog(this,userPref);

                        }else {
                            hideProgressDialog();
                            Utility.simpleAlert(this, getString(R.string.info_dialog_title), response.getResponseMessage());
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
                        }catch (Exception ex){
                            Log.e( "","Within Throwable Exception::" + e.getMessage());
                        }
                    });

        } else {
            hideProgressDialog();
            Utility.simpleAlert(this, getString(R.string.error), getString(R.string.check_network_connection));
        }

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id){

            case R.id.button_cancelOrder :
                showCancelAlertDialog();
                break;

            case R.id.buttonBackHome:

                Intent send = new Intent(this, HomePageActivity.class);
                startActivity(send);
                break;

            case R.id.ll_OrderProduct:

              /*  if(isOrderedProductVisible){

                    binding.ivOrderedProductArrow.setImageResource(R.drawable.forward_icon);
                    binding.tvOrderedProduct.setVisibility(View.VISIBLE);
                    binding.rvOrderedProduct.setVisibility(View.GONE);

                    isOrderedProductVisible = false;

                }else {

                    binding.ivOrderedProductArrow.setImageResource(R.drawable.down_icon);
                    binding.tvOrderedProduct.setVisibility(View.GONE);
                    binding.rvOrderedProduct.setVisibility(View.VISIBLE);

                    isOrderedProductVisible = true;
                }*/

                break;

            case R.id.iv_Back:
                finish();
                break;

            case R.id.button_return:
                showReturnAlertDialog();
                break;

        }
    }

    private void showReturnAlertDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = LayoutInflater.from(this).inflate(R.layout.return_order_popup,
                null,false);

        ImageView iv_Close = view.findViewById(R.id.iv_Close);
        EditText et_ReturnOrderMessage = view.findViewById(R.id.et_ReturnOrderMessage);
        Button btn_Submit = view.findViewById(R.id.btn_Submit);

        builder.setView(view);

        AlertDialog dialog = builder.show();

        btn_Submit.setOnClickListener(v -> {

            String message = et_ReturnOrderMessage.getText().toString().trim();
            if(!TextUtils.isEmpty(message)) {
                performReturnOrder(orderId, message,dialog);
            }else{
                Toast.makeText(MyOrderDetailsActivity.this,
                        "Please enter cancellation reason in details", Toast.LENGTH_SHORT).show();
            }
        });

        iv_Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void performReturnOrder(String orderId, String message, AlertDialog dialog) {

        if (Utility.getInstance().checkInternetConnection(this)) {

            apiService.submitReturnRequest(
                    userPref.getUser().getToken(),
                    orderId,
                    message)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(this::showProgressDialog)
                    .doOnCompleted(this::hideProgressDialog)
                    .subscribe(response -> {

                        if (response.getResponseCode().equals("200")) {

                            Toast.makeText(this, ""+response.getResponseMessage(),
                                    Toast.LENGTH_SHORT).show();

                            binding.sequenceLayoutParent.setVisibility(View.GONE);
                            binding.tvDelivered.setVisibility(View.GONE);
                            binding.tvCancelled.setVisibility(View.GONE);
                            binding.buttonCancelOrder.setVisibility(View.GONE);
                            binding.buttonReturn.setVisibility(View.GONE);
                            binding.infoFrame.setVisibility(View.VISIBLE);
                            binding.tvRetured.setVisibility(View.VISIBLE);

                            //refundAmountRequest("");

                            dialog.dismiss();

                        }
                        else if(response.getResponseCode().equals("403")){

                            utils.openLogoutDialog(this,userPref);

                        } else {

                            hideProgressDialog();
                            Utility.simpleAlert(this, getString(R.string.info_dialog_title), response.getResponseMessage());

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

                        }catch (Exception ex){
                            Log.e( "","Within Throwable Exception::" + e.getMessage());
                        }

                    });

        } else {
            hideProgressDialog();
            Utility.simpleAlert(this, getString(R.string.error), getString(R.string.check_network_connection));
        }

    }

    private void showCancelAlertDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = LayoutInflater.from(this).inflate(R.layout.cancel_order_popup,
                null,false);

        ImageView iv_Close = view.findViewById(R.id.iv_Close);
        Spinner spinner_Reason = view.findViewById(R.id.spinner_Reason);
        EditText et_CancelOrderMessage = view.findViewById(R.id.et_CancelOrderMessage);
        Button btn_Cancel = view.findViewById(R.id.btn_Cancel);

        List<String> reasonList = new ArrayList<>();
        reasonList.add("Delivery is too late.");

        String selectedReason = addCancellationReasonInSpinner(spinner_Reason,reasonList);

        builder.setView(view);

        AlertDialog dialog = builder.show();

        btn_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = et_CancelOrderMessage.getText().toString().trim();
                if(!TextUtils.isEmpty(message)) {
                    performCancelOrder(orderId, selectedReason, "",dialog);
                }else{
                    Toast.makeText(MyOrderDetailsActivity.this,
                            "Please enter cancellation reason in details", Toast.LENGTH_SHORT).show();
                }
            }
        });

        iv_Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    private String addCancellationReasonInSpinner(Spinner spinnerReason,List<String> list){

        final String[] reason = {""};

        ArrayAdapter stateAdapter = new ArrayAdapter(this,
                R.layout.support_simple_spinner_dropdown_item, list);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerReason.setAdapter(stateAdapter);


        spinnerReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {

                reason[0] = list.get(i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return reason[0];
    }

    private void performCancelOrder(String orderId, String reason,
                                    String message, AlertDialog dialog){

        if (Utility.getInstance().checkInternetConnection(this)) {

            apiService.cancelOrder(
                    userPref.getUser().getId(),
                    orderId,
                    reason,
                    message,
                    userPref.getUser().getToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(this::showProgressDialog)
                    .doOnCompleted(this::hideProgressDialog)
                    .subscribe(response -> {

                        if (response.getResponseCode().equals("200")) {

                            Toast.makeText(this, ""+response.getResponseMessage(),
                                    Toast.LENGTH_SHORT).show();

                            binding.sequenceLayoutParent.setVisibility(View.GONE);
                            binding.tvDelivered.setVisibility(View.GONE);
                            binding.buttonCancelOrder.setVisibility(View.GONE);
                            binding.infoFrame.setVisibility(View.VISIBLE);
                            binding.tvCancelled.setVisibility(View.VISIBLE);

                            //refundAmountRequest("");

                            dialog.dismiss();

                        } else if(response.getResponseCode().equals("403")){

                            utils.openLogoutDialog(this,userPref);

                        } else {

                            hideProgressDialog();
                            Utility.simpleAlert(this, getString(R.string.info_dialog_title), response.getResponseMessage());

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
                        }catch (Exception ex){
                            Log.e( "","Within Throwable Exception::" + e.getMessage());
                        }
                    });

        } else {
            hideProgressDialog();
            Utility.simpleAlert(this, getString(R.string.error), getString(R.string.check_network_connection));
        }

    }
    private void showOrderItemInRecyclerView(List<ProductList> list){

        LinearLayoutManager llm = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,false);

        orderItemRVAdapter = new OrderItemRVAdapter(this,list);
        binding.rvOrderedProduct.setLayoutManager(llm);
        binding.rvOrderedProduct.setAdapter(orderItemRVAdapter);

    }

    private void refundAmountRequest(String paymentId){


        try {

            RazorpayClient razorpay = new RazorpayClient(getString(R.string.razor_pay_api_key),
                    getString(R.string.razor_pay_secret_key));

            // Full Refund
            Refund refund = razorpay.Payments.refund(paymentId);


           /* //Partial Refund
            JSONObject refundRequest = new JSONObject();
            refundRequest.put("amount", amount); // Amount should be in paise
            Refund refund = razorpay.Payments.refund("<payment_id>", refundRequest);*/


        } catch (RazorpayException e) {
            // Handle Exception
            System.out.println(e.getMessage());
        }
    }

}