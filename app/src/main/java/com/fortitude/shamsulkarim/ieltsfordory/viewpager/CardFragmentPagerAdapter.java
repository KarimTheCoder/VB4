package com.fortitude.shamsulkarim.ieltsfordory.viewpager;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.cardview.widget.CardView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shamsul on 2/18/18.
 */

public class CardFragmentPagerAdapter extends FragmentStatePagerAdapter implements CardAdapter  {

    private List<Fragment> mFragments;
    private float mBaseElevation;
    private SharedPreferences sp;
    private int cb;

    public CardFragmentPagerAdapter(FragmentManager fm, float baseElevation, Context ctx) {
        super(fm);
        mFragments = new ArrayList<>();
        mBaseElevation = baseElevation;
        sp = ctx.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);
        cb = sp.getInt("cb",0);


        if( cb == 0){
            addCardFragment(new CardFramgmentBuy());
            addCardFragment(new CardFragment());
        }else {
            addCardFragment(new CardFramgmentBuy());

        }


    }

    @Override
    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {

        if( position == 0){

            CardFramgmentBuy buy = (CardFramgmentBuy)mFragments.get(position);
            return  buy.getCardView();
        }else {
            CardFragment cardFragment= (CardFragment) mFragments.get(position);
            return cardFragment.getCardView();
        }

    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object fragment = super.instantiateItem(container, position);
        if( position == 0){
            mFragments.set(position, (CardFramgmentBuy) fragment);
            return fragment;
        }else {
            mFragments.set(position, (CardFragment) fragment);
            return fragment;
        }

    }

    public void addCardFragment(Fragment fragment) {
        mFragments.add(fragment);
    }
}
