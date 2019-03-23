package com.fortitude.shamsulkarim.ieltsfordory;

import android.widget.Filter;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by shamsul on 3/10/18.
 */

public class CustomFilterFavorite extends Filter {


    private FavoriteRecyclerViewAdapter adapter;
    private List<Word> filterList;


    public CustomFilterFavorite(List<Word> filterList,FavoriteRecyclerViewAdapter adapter) {
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
            List<Word> filteredPlayers=new ArrayList<>();

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

        adapter.words = (ArrayList<Word>) results.values;

        //REFRESH
        adapter.notifyDataSetChanged();
    }

}
