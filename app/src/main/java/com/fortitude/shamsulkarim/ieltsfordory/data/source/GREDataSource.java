package com.fortitude.shamsulkarim.ieltsfordory.data.source;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.GREWordDatabase;

import com.fortitude.shamsulkarim.ieltsfordory.data.models.Word;

import java.util.ArrayList;
import java.util.List;

public class GREDataSource {

    private String[] GREwordArray, GREtranslationArray, GREgrammarArray, GREpronunArray, GREexample1array, GREexample2array, GREexample3Array, GREvocabularyType;

    private int[] GREposition;
    private List<String> greFavPosition;
    private boolean isGreChecked;
    private GREWordDatabase GREdatabase;
    private SharedPreferences sp;
    private Context context;

    public GREDataSource(Context context){

        this.context = context;
        GREdatabase = new GREWordDatabase(context);
        sp = context.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);

        greFavPosition = new ArrayList<>();
        isGreChecked =   sp.getBoolean("isGREActive",true);

        arrayInit();
        getFavoritePosition();
    }


    private void arrayInit(){

        GREwordArray = context.getResources().getStringArray(R.array.GRE_words);
        GREtranslationArray = context.getResources().getStringArray(R.array.GRE_translation);
        GREgrammarArray = context.getResources().getStringArray(R.array.GRE_grammar);
        GREpronunArray = context.getResources().getStringArray(R.array.GRE_pronunciation);
        GREexample1array = context.getResources().getStringArray(R.array.GRE_example1);
        GREexample2array = context.getResources().getStringArray(R.array.GRE_example2);
        GREexample3Array = context.getResources().getStringArray(R.array.GRE_example3);
        GREvocabularyType = context.getResources().getStringArray(R.array.GRE_level);
        GREposition = context.getResources().getIntArray(R.array.GRE_position);

    }

    private void getFavoritePosition(){
        Cursor greRes = GREdatabase.getData();

        while (greRes.moveToNext()){
            greFavPosition.add(greRes.getString(2));
        }

        greRes.close();
    }
    private List<Word> listWords (int startPoint , int SATBeginnerNumber){
        //Todo define is learned

        List<Word> wordList = new ArrayList<>();

        if(isGreChecked){

            for(int i = (int) startPoint; i < SATBeginnerNumber; i++){

                wordList.add(new Word(GREwordArray[i], GREtranslationArray[i],"", GREpronunArray[i], GREgrammarArray[i], GREexample1array[i], GREexample2array[i], GREexample3Array[i],GREvocabularyType[i],GREposition[i], "",greFavPosition.get(i)));


            }

        }


        return wordList;



    }

    public List<Word> getBeginnerWordData(){




        int GREwordSize = context.getResources().getStringArray(R.array.GRE_words).length;


        int GREbeginnerNumber = 0;





        if(isGreChecked){


            GREbeginnerNumber = (int) getPercentageNumber(30, GREwordSize);

        }


        return listWords(0,GREbeginnerNumber);

    }

    public List<Word> getIntermediateWordData(){




        int GREwordSize = context.getResources().getStringArray(R.array.GRE_words).length;


        int GREintermediateNumber = 0;


        int GREbeginnerNumber = 0;


        if(isGreChecked){


            GREintermediateNumber = getPercentageNumber(40, GREwordSize);
            GREbeginnerNumber = getPercentageNumber(30, GREwordSize);

        }

        return listWords(GREbeginnerNumber,GREbeginnerNumber+GREintermediateNumber);



    }

    public List<Word> getAdvanceWordData(){


        int GREwordSize = context.getResources().getStringArray(R.array.GRE_words).length;
        int GREintermediateNumber = 0;
        int GREbeginnerNumber = 0;


        if(isGreChecked){

            GREintermediateNumber = getPercentageNumber(40, GREwordSize);
            GREbeginnerNumber = getPercentageNumber(30, GREwordSize);

        }

        return listWords(GREbeginnerNumber+GREintermediateNumber,GREwordSize);


    }
    private int getPercentageNumber(int percentage, int number) {


        double p = percentage / 100d;
        double beginnerNum = p * number;

        return (int)beginnerNum;

    }


}
