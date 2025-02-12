package com.fortitude.shamsulkarim.ieltsfordory.adapters;

import android.widget.Filter;

import com.fortitude.shamsulkarim.ieltsfordory.data.models.Word;

import java.util.ArrayList;

/**
 * Created by shamsul on 2/3/18.
 */

public class CustomFilter extends Filter {




    private final WordRecyclerViewAdapter adapter;
    private final ArrayList<Object> filterList;


    public CustomFilter(ArrayList<Object> filterList,WordRecyclerViewAdapter adapter) {
        this.adapter=adapter;
        this.filterList=filterList;

    }

    //FILTERING OCURS
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results=new FilterResults();

        //CHECK CONSTRAINT VALIDITY
        if(constraint != null && constraint.length() > 0)
        {
            //CHANGE TO UPPER
            constraint=constraint.toString().toUpperCase();
            //STORE OUR FILTERED PLAYERS
            ArrayList<Word> filteredPlayers=new ArrayList<>();

            for (int i=0;i<filterList.size();i++)
            {
                //CHECK
                Word word = (Word) filterList.get(i);
                if(word.getWord().toUpperCase().contains(constraint))
                {
                    //ADD PLAYER TO FILTERED PLAYERS
                    filteredPlayers.add(word);
                }
            }

            results.count=filteredPlayers.size();
            results.values=filteredPlayers;
        }else
        {
            results.count=filterList.size();
            results.values=filterList;

        }


        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

        adapter.words = (ArrayList<Object>) results.values;

        //REFRESH
        adapter.notifyDataSetChanged();
    }

}
