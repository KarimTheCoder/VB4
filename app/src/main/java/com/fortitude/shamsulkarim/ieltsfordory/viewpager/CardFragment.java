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


import com.fortitude.shamsulkarim.ieltsfordory.R;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by shamsul on 2/18/18.
 */

public class CardFragment extends Fragment {


    private CardView mCardView;
    private FancyButton removeAds;

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


        removeAds = (FancyButton)view.findViewById(R.id.remove_ads);

        return view;
    }

    public CardView getCardView() {
        return mCardView;
    }




}
