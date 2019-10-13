package com.fortitude.shamsulkarim.ieltsfordory.WordAdapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fortitude.shamsulkarim.ieltsfordory.Language;
import com.fortitude.shamsulkarim.ieltsfordory.MainActivity;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.util.List;

/**
 * Created by karim on 9/12/17.
 */

public class ChooseLanguageAdapter extends RecyclerView.Adapter<ChooseLanguageAdapter.LanguageViewHolder>{

    List<Language> languages;
    Language language = new Language(0,0,"");

    public ChooseLanguageAdapter() {
        languages = language.getLanguages();

    }

    @Override
    public LanguageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.language_row,parent,false);

        LanguageViewHolder viewHolder = new LanguageViewHolder(view);

        return viewHolder;
    }



    @Override
    public void onBindViewHolder(LanguageViewHolder holder, int position) {


        Language language = languages.get(position);

        holder.flag.setImageResource(language.getImage());
        holder.languageName.setText(language.getName());

    }

    @Override
    public int getItemCount() {
        return languages.size();
    }

    public class LanguageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView flag;
        TextView languageName;
        SharedPreferences sp;
        Context ctx;

        public LanguageViewHolder(View itemView) {
            super(itemView);
            ctx= itemView.getContext();
            sp = ctx.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);

            flag = (ImageView)itemView.findViewById(R.id.flag);
            languageName = (TextView)itemView.findViewById(R.id.language_name);

            flag.setOnClickListener(this);
            languageName.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();


            if(v == flag  || v == languageName){

                sp.edit().putInt("language",getAdapterPosition()).apply();

                StyleableToast.makeText(ctx, languages.get(getAdapterPosition()).getName()+" selected", 10, R.style.favorite).show();

                Handler handler = new Handler();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ctx.startActivity(new Intent(ctx, MainActivity.class));
                    }
                },200L);



            }



        }
    }
}
