package com.fortitude.shamsulkarim.ieltsfordory.ui.purchase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class InAppPurchase extends AppCompatActivity implements PurchasesUpdatedListener, View.OnClickListener {

    private BillingClient billingClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_app_purchase);
        setBillingClient();
        Button loadProductButton = findViewById(R.id.onShowProducts);
         loadProductButton.setOnClickListener(this);




    }

    private void initProductAdapter(List<SkuDetails> skuDetails){


        RecyclerView recyclerView = findViewById(R.id.product_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        InAppPurchaseRecyclerView adapter = new InAppPurchaseRecyclerView(this, skuDetails);
        recyclerView.setAdapter(adapter);

    }

    public void setBillingClient(){

        billingClient = BillingClient.newBuilder(this)
                .setListener(this)
                .enablePendingPurchases()
                .build();


        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NotNull BillingResult billingResult) {
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.

                    List<String> skuList = new ArrayList<> ();
                    skuList.add("test_product");
                    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
                    billingClient.querySkuDetailsAsync(params.build(),
                            new SkuDetailsResponseListener() {
                                @Override
                                public void onSkuDetailsResponse(@NotNull BillingResult billingResult,
                                                                 List<SkuDetails> skuDetailsList) {

                                    // Process the result.
                                    initProductAdapter(skuDetailsList);
                                    BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                            .setSkuDetails(skuDetailsList.get(0))
                                            .build();
                                    billingClient.launchBillingFlow(InAppPurchase.this, billingFlowParams);



                                    Toast.makeText(InAppPurchase.this,billingResult.getResponseCode()+" Getting Data..."+skuDetailsList.size(),Toast.LENGTH_SHORT).show();
                                }
                            });
                    Toast.makeText(InAppPurchase.this,"BILLING | startConnection | RESULT OK",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(InAppPurchase.this,"BILLING | startConnection | RESULT: $billingResponseCode",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Toast.makeText(InAppPurchase.this,"BILLING | onBillingServiceDisconnected | DISCONNECTED",Toast.LENGTH_SHORT).show();

            }
        });

    }


    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {

    }

    @Override
    public void onClick(View view) {

        if(billingClient.isReady()){

            List<String> skuList = new ArrayList<> ();
            skuList.add("test_product");
            SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
            params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
            billingClient.querySkuDetailsAsync(params.build(),
                    new SkuDetailsResponseListener() {
                        @Override
                        public void onSkuDetailsResponse(@NotNull BillingResult billingResult,
                                                         List<SkuDetails> skuDetailsList) {

                            // Process the result.
                            initProductAdapter(skuDetailsList);
                            Toast.makeText(InAppPurchase.this,"Getting Data..."+skuDetailsList.size(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }


    }
}