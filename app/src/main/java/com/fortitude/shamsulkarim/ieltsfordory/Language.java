package com.fortitude.shamsulkarim.ieltsfordory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by karim on 1/25/17.
 */

public class Language {


    int image;
    int activeImage;
    String name;
    public Language(int image, int activeImage, String name) {
        this.image = image;
        this.activeImage = activeImage;
        this.name = name;
    }




    public List<Language> getLanguages(){

        //        Language bangla = new Language(R.drawable.bangla,R.drawable.bangla_active,"Bangla");
//        Language hindi = new Language(R.drawable.hindi,R.drawable.hindi_active,"Hindi");
//        Language english = new Language(R.drawable.english_flag,R.drawable.english_flag_selected,"English only");
//        Language spanish = new Language(R.drawable.spanish,R.drawable.spanish_active,"Spanish");
//
//        languages.add(english);
//        languages.add(spanish);
//        languages.add(hindi);
//        languages.add(bangla);
        return new ArrayList<>();



    }

    public int getActiveImage() {
        return activeImage;
    }

    public void setActiveImage(int activeImage) {
        this.activeImage = activeImage;
    }

    public int getImage() {
        return this.image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
