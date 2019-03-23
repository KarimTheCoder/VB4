package com.fortitude.shamsulkarim.ieltsfordory;

import android.widget.Filter;

import com.fortitude.shamsulkarim.ieltsfordory.WordAdapters.IntermediateAdapter;

import java.util.ArrayList;

/**
 * Created by shamsul on 2/11/18.
 */

public class CustomFilterIntermediate extends Filter {

    IntermediateAdapter adapter;
    ArrayList<Object> filterList;


    public CustomFilterIntermediate(ArrayList<Object> filterList,IntermediateAdapter adapter)
    {

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
