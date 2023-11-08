package com.fortitude.shamsulkarim.ieltsfordory.data;

/**
 * Created by karim on 1/20/17.
 */

public class FavLearnedState {


     String ieltsLearnedCount, toeflLearnedCount, satLearnedCount, greLearnedCount;
    String ieltsFavCount, toeflFavCount, satFavCount, greFavCount;
    String name;

    public FavLearnedState(String name, String beginnerLearnedCount, String intermediateLearnedCount, String advanceLearnedCount,String greLearnedCount, String beginnerFavCount, String intermediateFavCount, String advanceFavCount, String greFavCount) {
        this.ieltsLearnedCount = beginnerLearnedCount;
        this.toeflLearnedCount = intermediateLearnedCount;
        this.satLearnedCount = advanceLearnedCount;
        this.ieltsFavCount = beginnerFavCount;
        this.toeflFavCount = intermediateFavCount;
        this.satFavCount = advanceFavCount;
        this.greFavCount = greFavCount;
        this.greLearnedCount = greLearnedCount;
        this.name = name;
    }


    public String getGreLearnedCount() {
        return greLearnedCount;
    }

    public void setGreLearnedCount(String greLearnedCount) {
        this.greLearnedCount = greLearnedCount;
    }

    public String getGreFavCount() {
        return greFavCount;
    }

    public void setGreFavCount(String greFavCount) {
        this.greFavCount = greFavCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIeltsLearnedCount() {
        return ieltsLearnedCount;
    }

    public void setIeltsLearnedCount(String ieltsLearnedCount) {
        this.ieltsLearnedCount = ieltsLearnedCount;
    }

    public String getToeflLearnedCount() {
        return toeflLearnedCount;
    }

    public void setToeflLearnedCount(String toeflLearnedCount) {
        this.toeflLearnedCount = toeflLearnedCount;
    }

    public String getSatLearnedCount() {
        return satLearnedCount;
    }

    public void setSatLearnedCount(String satLearnedCount) {
        this.satLearnedCount = satLearnedCount;
    }

    public String getIeltsFavCount() {
        return ieltsFavCount;
    }

    public void setIeltsFavCount(String ieltsFavCount) {
        this.ieltsFavCount = ieltsFavCount;
    }

    public String getToeflFavCount() {
        return toeflFavCount;
    }

    public void setToeflFavCount(String toeflFavCount) {
        this.toeflFavCount = toeflFavCount;
    }

    public String getSatFavCount() {
        return satFavCount;
    }

    public void setSatFavCount(String satFavCount) {
        this.satFavCount = satFavCount;
    }
}
