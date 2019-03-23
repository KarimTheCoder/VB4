package com.fortitude.shamsulkarim.ieltsfordory.viewpager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.fortitude.shamsulkarim.ieltsfordory.R;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by shamsul on 2/18/18.
 */

public class CardFragment extends Fragment implements View.OnClickListener , BillingProcessor.IBillingHandler{


    private CardView mCardView;
    private FancyButton removeAds;
    BillingProcessor bp;
    private SharedPreferences sp;
    int cb;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_adapter, container, false);
        mCardView = (CardView) view.findViewById(R.id.cardView);
        mCardView.setMaxCardElevation(mCardView.getCardElevation()
                * CardAdapter.MAX_ELEVATION_FACTOR);

        sp = view.getContext().getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);


        bp = new BillingProcessor(getContext(), "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlOSgGRFe8nOo3UfthotojrY4Vb9nBXYjqNHHR8ZabmMdpMVQWjG5SpuaLCQ1e+AjX+2Zsw6t9AHt9IGxIJuTmqlTd2sr7gCvtKwn6nIHvkGzOvYUsaVXteUoGEJP9lPMvja3vo0CxKXLwtCHxE1rCeKa1Wr1E/H0gMXH6eS91O0VRjzGBW/e+zgYm0ek/0g1bt9e7Vz6P3BfOj6DFhfzYAoaMhT2iw8kOr8yups6aAkkAprvyoUz78Au6u141y9LGYgEZK0mEXB83vOWetlrigJmGyfyrKXfGMZMIxcFdW49ZdjYBIlomw++9MEmMb1KChcTQldKl2BZ4ATRw5BCswIDAQAB", this);

        removeAds = (FancyButton)view.findViewById(R.id.remove_ads);
        removeAds.setOnClickListener(this);
        return view;
    }

    public CardView getCardView() {
        return mCardView;
    }

    @Override
    public void onClick(View v) {

        bp.purchase(getActivity(),"com.fortitude.shamsulkarim.ieltsfordory.adremove");

    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {

        Toast.makeText(getContext(),"Purchased",Toast.LENGTH_SHORT).show();
        sp.edit().putInt("cb", 1).apply();
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {

    }

    @Override
    public void onBillingInitialized() {

    }
}
