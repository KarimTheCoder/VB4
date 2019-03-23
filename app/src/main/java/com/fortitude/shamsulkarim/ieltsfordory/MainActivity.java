package com.fortitude.shamsulkarim.ieltsfordory;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.fortitude.shamsulkarim.ieltsfordory.WordAdapters.profile_fragment;
import com.fortitude.shamsulkarim.ieltsfordory.databases.GREWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.IELTSWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.SATWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.databases.TOEFLWordDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.util.ArrayList;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;


public class MainActivity extends AppCompatActivity  implements View.OnClickListener{


    // getting database instances

    // Data
    private IELTSWordDatabase ieltsWordDatabase;
    private TOEFLWordDatabase toeflWordDatabase;
    private SATWordDatabase   satWordDatabase;
    private GREWordDatabase   greWordDatabase;

    //-------------------------------

//    BottomNavigation bottomNavigation;
    private SharedPreferences sp ;
    private FancyButton homeButton, wordButton, learnedButton, favoriteButton, profileButton;

    //-------------------------------
    private DatabaseReference ref;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser user;
    private StringBuilder ieltsFavNumBuilder, toeflFavNumBuilder, satFavNumBuilder, greFavNumBuilder;
    private StringBuilder ieltsLearnedNumBuilder, toeflLearnedNumBuilder, satLearnedNumBuilder, greLearnedNumBuilder;


    private Toast toast;
    private long lastBackPressTime = 0;
    private boolean connected;
    private FirebaseAuth mAuth;
    private String toastMsg;
    private View homeBottomLine, wordBottomLine, learnedBottomLine, favoriteBottomLine, profileBottomLine;

    private  static String practice;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        switch(screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                toastMsg = "Large";
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                toastMsg = "Normal";
                break;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                toastMsg = "Small screen";
                break;
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                toastMsg = "Xlarge";
                break;
            default:
                toastMsg = "Screen size is neither large, normal or small";
        }

        try {

            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                //we are connected to a network
                connected = true;
            }
            else{
                connected = false;
            }

        }catch (NullPointerException n){

            Toast.makeText(this,"Connection error",Toast.LENGTH_SHORT).show();
        }


        sp = this.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);

        if(!sp.contains("soundState")){

            sp.edit().putBoolean("soundState",true).apply();
            sp.edit().putInt("totalCorrects",0).apply();
            sp.edit().putInt("noshowads",0).apply();


        }
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        double x = Math.pow(dm.widthPixels/dm.xdpi,2);
        double y = Math.pow(dm.heightPixels/dm.ydpi,2);
        double screenInches = Math.sqrt(x+y);


        homeButton = (FancyButton)findViewById(R.id.home_button);
        wordButton = (FancyButton)findViewById(R.id.word_button);
        learnedButton = (FancyButton)findViewById(R.id.learned_button);
        favoriteButton = (FancyButton)findViewById(R.id.favorite_button);
        profileButton = (FancyButton)findViewById(R.id.profile_button);

        homeBottomLine = findViewById(R.id.home_bottom_line);
        wordBottomLine = findViewById(R.id.words_bottom_line);
        learnedBottomLine = findViewById(R.id.learned_bottom_line);
        favoriteBottomLine = findViewById(R.id.favorite_bottom_line);
        profileBottomLine = findViewById(R.id.profile_bottom_line);

        mAuth = FirebaseAuth.getInstance();

        //database
        ieltsWordDatabase = new IELTSWordDatabase(this);
        satWordDatabase = new SATWordDatabase(this);
        toeflWordDatabase = new TOEFLWordDatabase(this);
        greWordDatabase = new GREWordDatabase(this);


        if(toastMsg.equalsIgnoreCase("normal")){
            if(screenInches <=4){

                homeButton.setPadding(12,12,12,12);
                wordButton.setPadding(12,12,12,12);
                learnedButton.setPadding(12,12,12,12);
                favoriteButton.setPadding(12,12,12,12);
                profileButton.setPadding(12,12,12,12);

            }else {
           homeButton.setPadding(25,25,25,25);
           wordButton.setPadding(25,25,25,25);
           learnedButton.setPadding(25,25,25,25);
           favoriteButton.setPadding(25,25,25,25);
           profileButton.setPadding(25,25,25,25);

            }

        }







        homeButton.setOnClickListener(this);
        wordButton.setOnClickListener(this);
        learnedButton.setOnClickListener(this);
        favoriteButton.setOnClickListener(this);
        profileButton.setOnClickListener(this);

        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference();
        user = firebaseAuth.getCurrentUser();
        ieltsFavNumBuilder = new StringBuilder();
        toeflFavNumBuilder = new StringBuilder();
        satFavNumBuilder = new StringBuilder();
        greFavNumBuilder = new StringBuilder();







        HomeFragment homeFragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frag,homeFragment).commit();



        // firebase auto sync
        if(mAuth.getCurrentUser() != null && connected) {

            try {

                ref.child(mAuth.getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {

                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

//                if(dataSnapshot.exists()) {
                        String data = dataSnapshot.getValue(String.class);
                        String key = dataSnapshot.getKey();

//                    Toast.makeText(getApplicationContext(), key + " " + data, Toast.LENGTH_SHORT).show();


                        // if key equals to favorite
                        if( key.equalsIgnoreCase("satFavCount") || key.equalsIgnoreCase("toeflFavCount") || key.equalsIgnoreCase("ieltsFavCount")){

                            //syncDatabasesIfFavDataChanged(data, key);

                        }


                        //if key equals to learned

                        if(key.equalsIgnoreCase("satLearnedCount") || key.equalsIgnoreCase("toeflLearnedCount") || key.equalsIgnoreCase("ieltsLearnedCount")){

                          //  syncSPIfLearnedDataChanged(data, key);
                        }


                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {


                    }
                });

            }catch (NullPointerException n){

                Toast.makeText(this,"Reference exception",Toast.LENGTH_SHORT).show();

            }


        }


    }






    // Firebase
    //----------------------------------------------------------------------------------------------
    private void updateFirebase(){
        addFavAndLearnedNumber();
        SharedPreferences sp = this.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String advanceLearnedNum = satLearnedNumBuilder.toString();
        String beginnerLearnedNum = ieltsLearnedNumBuilder.toString();
        String intermediateLearnedNum = toeflLearnedNumBuilder.toString();
        String greLearnedNum = greLearnedNumBuilder.toString();

        String advanceFavNumString = String.valueOf(satFavNumBuilder);
        String intermediateNumString = String.valueOf(toeflFavNumBuilder);
        String beginnerNumString  = String.valueOf(ieltsFavNumBuilder);
        String greFavNumString = String.valueOf(greFavNumBuilder);

        FavLearnedState favLearnedState = new FavLearnedState(sp.getString("userName","Boo"),beginnerLearnedNum,intermediateLearnedNum,advanceLearnedNum,greLearnedNum,beginnerNumString,intermediateNumString,advanceFavNumString,greFavNumString);


        if( currentUser != null && connected){

            try{
                ref.child(user.getUid()).setValue(favLearnedState);
            }catch (NullPointerException n){

                Toast.makeText(this,"update failure",Toast.LENGTH_SHORT).show();
            }

        }

    }

    private void syncDatabasesIfFavDataChanged(String newData, String key){

        List<Integer> newDataList;





        // ADVANC FAVORITE COUNT

        if(key.equalsIgnoreCase("satFavCount")){

            int advanceSize = sp.getInt("advance",getResources().getStringArray(R.array.SAT_words).length);
            // cleaning database

            for(int i = 0; i < advanceSize; i++){
                satWordDatabase.updateFav(""+(i+1),"False");
            }

            // adding new data

            newDataList = builderToNums(new StringBuilder(newData));
            if(newDataList.size() > 0){

                for(int k = 0; k < newDataList.size(); k++){

                    int adva = newDataList.get(k)+1;
                    satWordDatabase.updateFav(""+adva,"True");



                }

            }


            StyleableToast.makeText(this, "Advance favorite synced",10, R.style.syncing).show();
        }

        // INTERMEDIATE FAVORITE COUNT

        if(key.equalsIgnoreCase("toeflFavCount")){

            int intermediateSize = sp.getInt("intermediate",getResources().getStringArray(R.array.TOEFL_words).length);
            // cleaning database

            for(int i = 0; i < intermediateSize; i++){
                toeflWordDatabase.updateFav(""+(i+1),"False");
            }

            // adding new data

            newDataList = builderToNums(new StringBuilder(newData));
            if(newDataList.size() > 0){

                for(int k = 0; k < newDataList.size(); k++){

                    int adva = newDataList.get(k)+1;
                    toeflWordDatabase.updateFav(""+adva,"True");



                }

            }

            StyleableToast.makeText(this, "Intermediate favorite synced",10, R.style.syncing).show();

        }


        // BEGINNER FAVORITE COUNT

        if(key.equalsIgnoreCase("ieltsFavCount")){

            int beginnerSize = sp.getInt("beginner",getResources().getStringArray(R.array.IELTS_words).length);
            // cleaning database

            for(int i = 0; i < beginnerSize; i++){
                ieltsWordDatabase.updateFav(""+(i+1),"False");
            }

            // adding new data

            newDataList = builderToNums(new StringBuilder(newData));
            if(newDataList.size() > 0){

                for(int k = 0; k < newDataList.size(); k++){

                    int adva = newDataList.get(k)+1;
                    ieltsWordDatabase.updateFav(""+adva,"True");



                }

            }
            StyleableToast.makeText(this, "Beginner favorite synced",10, R.style.syncing).show();

        }





    }

    private void syncSPIfLearnedDataChanged(String data, String key){


        if(key.equalsIgnoreCase("satLearnedCount")){

            int localAdvanceLearned = sp.getInt("advance",0);
            int firebaseSaved = Integer.parseInt(data);

            if( firebaseSaved > localAdvanceLearned){

                sp.edit().putInt("advance",firebaseSaved).apply();
                StyleableToast.makeText(this, "Advance learned words synced",10, R.style.syncing).show();

            }

        }

        if(key.equalsIgnoreCase("toeflLearnedCount")){

            int localAdvanceLearned = sp.getInt("intermediate",0);
            int firebaseSaved = Integer.parseInt(data);

            if( firebaseSaved > localAdvanceLearned){

                sp.edit().putInt("intermediate",firebaseSaved).apply();

                StyleableToast.makeText(this, "Intermediate learned words synced",10, R.style.syncing).show();

            }

        }

        if(key.equalsIgnoreCase("ieltsLearnedCount")){

            int localAdvanceLearned = sp.getInt("beginner",0);
            int firebaseSaved = Integer.parseInt(data);

            if( firebaseSaved > localAdvanceLearned){

                sp.edit().putInt("beginner",firebaseSaved).apply();

                StyleableToast.makeText(this, "Beginner learned words synced",10, R.style.syncing).show();

            }

        }

    }

    private List<Integer> builderToNums(StringBuilder numBuilder){

        List<Integer> backToNums = new ArrayList<>();
        int plusCount = 0;
        int plusI = 0;

        for(int i = 0; i < numBuilder.length(); i++){

            if(numBuilder.charAt(i) == '+'){

                plusCount++;
            }
        }

        int plusPosition[] = new int[plusCount];

        for(int j = 0; j < numBuilder.length(); j++){

            if(numBuilder.charAt(j) == '+'){
                plusPosition[plusI] = j;

                plusI++;

            }

        }

        for( int k = 0; k < plusPosition.length-1; k++){


            if(numBuilder.charAt(plusPosition[k]) == '+'){

                String strNum = numBuilder.substring(plusPosition[k]+1, plusPosition[k+1]);
                backToNums.add(Integer.parseInt(strNum));
            }
        }


        if( numBuilder.length() > 0 && numBuilder != null){

            String lastNum = numBuilder.substring(plusPosition[plusCount-1]+1, numBuilder.length());
            backToNums.add(Integer.parseInt(lastNum));

        }

        return backToNums;
    }

    private void addFavAndLearnedNumber(){

        Cursor ieltsRes = ieltsWordDatabase.getData();
        Cursor toeflRes = toeflWordDatabase.getData();
        Cursor satRes = satWordDatabase.getData();
        Cursor greRes = greWordDatabase.getData();
        ieltsFavNumBuilder = new StringBuilder();
        toeflFavNumBuilder = new StringBuilder();
        satFavNumBuilder = new StringBuilder();
        greFavNumBuilder = new StringBuilder();

        ieltsLearnedNumBuilder = new StringBuilder();
        toeflLearnedNumBuilder = new StringBuilder();
        satLearnedNumBuilder = new StringBuilder();
        greLearnedNumBuilder = new StringBuilder();



        //------------FAVORITE And LEARNED

        while (ieltsRes.moveToNext()){

            if(ieltsRes.getString(2).equalsIgnoreCase("true")){
                ieltsFavNumBuilder.append(1+"+");
            }else{

                ieltsFavNumBuilder.append(0+"+");

            }

            if(ieltsRes.getString(3).equalsIgnoreCase("true")){
                ieltsLearnedNumBuilder.append(1+"+");
            }else{

                ieltsLearnedNumBuilder.append(0+"+");

            }

        }

        while (toeflRes.moveToNext()){

            if(toeflRes.getString(2).equalsIgnoreCase("true")){
                toeflFavNumBuilder.append(1+"+");
            }else {
                toeflFavNumBuilder.append(0+"+");
            }

            if(toeflRes.getString(3).equalsIgnoreCase("true")){
                toeflLearnedNumBuilder.append(1+"+");
            }else {
                toeflLearnedNumBuilder.append(0+"+");
            }
        }

        while (satRes.moveToNext()){

            if(satRes.getString(2).equalsIgnoreCase("true")){
                satFavNumBuilder.append(1+"+");
            }else {

                satFavNumBuilder.append(0+"+");
            }

            if(satRes.getString(3).equalsIgnoreCase("true")){
                satLearnedNumBuilder.append(1+"+");
            }else {

                satLearnedNumBuilder.append(0+"+");
            }

        }

        while (greRes.moveToNext()){

            if(greRes.getString(2).equalsIgnoreCase("true")){
                greFavNumBuilder.append(1+"+");
            }else {

                greFavNumBuilder.append(0+"+");
            }

            if(greRes.getString(3).equalsIgnoreCase("true")){
                greLearnedNumBuilder.append(1+"+");
            }else {

                greLearnedNumBuilder.append(0+"+");
            }
        }




    }




    // Overriden Methods
    @Override
    protected void onStop() {
        super.onStop();

        if(firebaseAuth.getCurrentUser() !=null && connected){


            updateFirebase();

        }


    }

    @Override
    public void onBackPressed() {
        if (this.lastBackPressTime < System.currentTimeMillis() - 4000) {
            toast = Toast.makeText(this, "Press back again to close this app", Toast.LENGTH_LONG);
            toast.show();
            this.lastBackPressTime = System.currentTimeMillis();
        } else {
            if (toast != null) {
                toast.cancel();
            }
            super.onBackPressed();
        }



    }

    @Override
    public void onClick(View v) {

        if( v == homeButton){

//            homeButton.setIconResource(R.drawable.ic_home);
//            wordButton.setIconResource(R.drawable.ic_words);
//            learnedButton.setIconResource(R.drawable.ic_school);
//            favoriteButton.setIconResource(R.drawable.ic_favorite);
//            profileButton.setIconResource(R.drawable.ic_person);
            HomeFragment homeFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.frag,homeFragment).commit();


            homeBottomLine.setVisibility(View.VISIBLE);
            wordBottomLine.setVisibility(View.INVISIBLE);
            learnedBottomLine.setVisibility(View.INVISIBLE);
            favoriteBottomLine.setVisibility(View.INVISIBLE);
            profileBottomLine.setVisibility(View.INVISIBLE);



        }

        if(v == wordButton){

//            homeButton.setIconResource(R.drawable.ic_home);
//            wordButton.setIconResource(R.drawable.ic_library_books_active);
//            learnedButton.setIconResource(R.drawable.ic_school);
//            favoriteButton.setIconResource(R.drawable.ic_favorite);
//            profileButton.setIconResource(R.drawable.ic_person);
            NewWordFragment wordFragment = new NewWordFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.frag,wordFragment).commit();

            homeBottomLine.setVisibility(View.INVISIBLE);
            wordBottomLine.setVisibility(View.VISIBLE);
            learnedBottomLine.setVisibility(View.INVISIBLE);
            favoriteBottomLine.setVisibility(View.INVISIBLE);
            profileBottomLine.setVisibility(View.INVISIBLE);



        }
        if(v == learnedButton){

//            homeButton.setIconResource(R.drawable.ic_home);
//            wordButton.setIconResource(R.drawable.ic_library_books);
//            learnedButton.setIconResource(R.drawable.ic_school_active);
//            favoriteButton.setIconResource(R.drawable.ic_favorite);
//            profileButton.setIconResource(R.drawable.ic_person);
            LearnedWords learnedWords = new LearnedWords();
            getSupportFragmentManager().beginTransaction().replace(R.id.frag,learnedWords).commit();

            homeBottomLine.setVisibility(View.INVISIBLE);
            wordBottomLine.setVisibility(View.INVISIBLE);
            learnedBottomLine.setVisibility(View.VISIBLE);
            favoriteBottomLine.setVisibility(View.INVISIBLE);
            profileBottomLine.setVisibility(View.INVISIBLE);


        }
        if(v == favoriteButton){

//            homeButton.setIconResource(R.drawable.ic_home);
//            wordButton.setIconResource(R.drawable.ic_library_books);
//            learnedButton.setIconResource(R.drawable.ic_school);
//            favoriteButton.setIconResource(R.drawable.ic_favorite_active);
//            profileButton.setIconResource(R.drawable.ic_person);
            FavoriteWords favoriteWords = new FavoriteWords();
            getSupportFragmentManager().beginTransaction().replace(R.id.frag,favoriteWords).commit();

            homeBottomLine.setVisibility(View.INVISIBLE);
            wordBottomLine.setVisibility(View.INVISIBLE);
            learnedBottomLine.setVisibility(View.INVISIBLE);
            favoriteBottomLine.setVisibility(View.VISIBLE);
            profileBottomLine.setVisibility(View.INVISIBLE);

        }
        if(v == profileButton){
//            homeButton.setIconResource(R.drawable.ic_home);
//            wordButton.setIconResource(R.drawable.ic_library_books);
//            learnedButton.setIconResource(R.drawable.ic_school);
//            favoriteButton.setIconResource(R.drawable.ic_favorite);
//            profileButton.setIconResource(R.drawable.ic_person_active);
            profile_fragment setting = new profile_fragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.frag,setting).commit();

            homeBottomLine.setVisibility(View.INVISIBLE);
            wordBottomLine.setVisibility(View.INVISIBLE);
            learnedBottomLine.setVisibility(View.INVISIBLE);
            favoriteBottomLine.setVisibility(View.INVISIBLE);
            profileBottomLine.setVisibility(View.VISIBLE);

        }
    }
}

