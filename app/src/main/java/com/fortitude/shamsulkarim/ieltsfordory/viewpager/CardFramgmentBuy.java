package com.fortitude.shamsulkarim.ieltsfordory.viewpager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fortitude.shamsulkarim.ieltsfordory.R;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by shamsul on 2/18/18.
 */
public class CardFramgmentBuy extends Fragment implements View.OnClickListener{


    private CardView mCardView;
    private FancyButton buy;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buy, container, false);
        mCardView = (CardView) view.findViewById(R.id.cardView);
        mCardView.setMaxCardElevation(mCardView.getCardElevation()
                * CardAdapter.MAX_ELEVATION_FACTOR);

        buy = (FancyButton)view.findViewById(R.id.buy);
        buy.setOnClickListener(this);
        return view;
    }

    public CardView getCardView() {
        return mCardView;
    }

    @Override
    public void onClick(View v) {

        if( v == buy){

            Uri appUrl = Uri.parse("https://play.google.com/store/apps/details?id=com.fortitude.shamsulkarim.ieltspro");
            Intent rateApp = new Intent(Intent.ACTION_VIEW, appUrl);
            this.startActivity(rateApp);

        }

    }
}
