package com.fortitude.shamsulkarim.ieltsfordory.ui;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import com.fortitude.shamsulkarim.ieltsfordory.data.FavLearnedState;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.ui.fragments.FavoriteFragment;
import com.fortitude.shamsulkarim.ieltsfordory.ui.fragments.HomeFragment;
import com.fortitude.shamsulkarim.ieltsfordory.ui.fragments.LearnedFragment;
import com.fortitude.shamsulkarim.ieltsfordory.ui.fragments.AllWordsFragment;
import com.fortitude.shamsulkarim.ieltsfordory.ui.fragments.ProfileFragment;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.GREWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.IELTSWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.SATWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.TOEFLWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.utility.connectivity.ConnectivityHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.jetbrains.annotations.NotNull;
import mehdi.sakout.fancybuttons.FancyButton;


public class MainActivity extends AppCompatActivity  implements View.OnClickListener{


    // getting database instances

    // Data
    private IELTSWordDatabase ieltsWordDatabase;
    private TOEFLWordDatabase toeflWordDatabase;
    private SATWordDatabase   satWordDatabase;
    private GREWordDatabase   greWordDatabase;

    //-------------------------------

    private FancyButton homeButton, wordButton, learnedButton, favoriteButton, profileButton;

    //-------------------------------
    private DatabaseReference ref;
    private FirebaseUser user;
    private StringBuilder ieltsFavNumBuilder, toeflFavNumBuilder, satFavNumBuilder, greFavNumBuilder;
    private StringBuilder ieltsLearnedNumBuilder, toeflLearnedNumBuilder, satLearnedNumBuilder, greLearnedNumBuilder;
    private Toast toast;
    private long lastBackPressTime = 0;
    private boolean connected;
    private View homeBottomLine, wordBottomLine, learnedBottomLine, favoriteBottomLine, profileBottomLine;
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;


        String toastMsg;
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

        connected = isOnline();

        //    BottomNavigation bottomNavigation;
        SharedPreferences sp = this.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);

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


        homeButton = findViewById(R.id.home_button);
        wordButton = findViewById(R.id.word_button);
        learnedButton = findViewById(R.id.learned_button);
        favoriteButton = findViewById(R.id.favorite_button);
        profileButton = findViewById(R.id.profile_button);

        homeBottomLine = findViewById(R.id.home_bottom_line);
        wordBottomLine = findViewById(R.id.words_bottom_line);
        learnedBottomLine = findViewById(R.id.learned_bottom_line);
        favoriteBottomLine = findViewById(R.id.favorite_bottom_line);
        profileBottomLine = findViewById(R.id.profile_bottom_line);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

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

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
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
                    public void onChildAdded(@NotNull DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildChanged(@NotNull DataSnapshot dataSnapshot, String s) {

                        dataSnapshot.getValue(String.class);
                        dataSnapshot.getKey();

                    }

                    @Override
                    public void onChildRemoved(@NotNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NotNull DataSnapshot dataSnapshot, String s) {



                    }

                    @Override
                    public void onCancelled(@NotNull DatabaseError databaseError) {


                    }
                });

            }catch (NullPointerException n){

                Toast.makeText(this,"Reference exception",Toast.LENGTH_SHORT).show();

            }


        }


        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                if (lastBackPressTime < System.currentTimeMillis() - 4000) {
                    toast = Toast.makeText(getApplicationContext(), "Press back again to close this app", Toast.LENGTH_LONG);
                    toast.show();
                    lastBackPressTime = System.currentTimeMillis();

                } else {
                    if (toast != null) {
                        toast.cancel();
                    }
                    finish();
                }
            }
        });


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
        ieltsRes.close();
        ieltsWordDatabase.close();

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
        toeflRes.close();
        toeflWordDatabase.close();

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

        satRes.close();
        satWordDatabase.close();

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
        greRes.close();
        greWordDatabase.close();




    }




    // Overriden Methods
    @Override
    protected void onStop() {
        super.onStop();

        if(firebaseAuth.getCurrentUser() !=null && connected){


            updateFirebase();

        }


    }
//
//    @Override
//    public void onBackPressed() {
//        if (this.lastBackPressTime < System.currentTimeMillis() - 4000) {
//            toast = Toast.makeText(this, "Press back again to close this app", Toast.LENGTH_LONG);
//            toast.show();
//            this.lastBackPressTime = System.currentTimeMillis();
//        } else {
//            if (toast != null) {
//                toast.cancel();
//            }
//            super.onBackPressed();
//        }
//
//    }



    @Override
    public void onClick(View v) {

        if( v == homeButton){

            HomeFragment homeFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.frag,homeFragment).commit();


            homeBottomLine.setVisibility(View.VISIBLE);
            wordBottomLine.setVisibility(View.INVISIBLE);
            learnedBottomLine.setVisibility(View.INVISIBLE);
            favoriteBottomLine.setVisibility(View.INVISIBLE);
            profileBottomLine.setVisibility(View.INVISIBLE);



        }

        if(v == wordButton){

            AllWordsFragment wordFragment = new AllWordsFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.frag,wordFragment).commit();

            homeBottomLine.setVisibility(View.INVISIBLE);
            wordBottomLine.setVisibility(View.VISIBLE);
            learnedBottomLine.setVisibility(View.INVISIBLE);
            favoriteBottomLine.setVisibility(View.INVISIBLE);
            profileBottomLine.setVisibility(View.INVISIBLE);



        }
        if(v == learnedButton){

            LearnedFragment learnedWords = new LearnedFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.frag,learnedWords).commit();

            homeBottomLine.setVisibility(View.INVISIBLE);
            wordBottomLine.setVisibility(View.INVISIBLE);
            learnedBottomLine.setVisibility(View.VISIBLE);
            favoriteBottomLine.setVisibility(View.INVISIBLE);
            profileBottomLine.setVisibility(View.INVISIBLE);


        }
        if(v == favoriteButton){

            FavoriteFragment favoriteWords = new FavoriteFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.frag,favoriteWords).commit();

            homeBottomLine.setVisibility(View.INVISIBLE);
            wordBottomLine.setVisibility(View.INVISIBLE);
            learnedBottomLine.setVisibility(View.INVISIBLE);
            favoriteBottomLine.setVisibility(View.VISIBLE);
            profileBottomLine.setVisibility(View.INVISIBLE);

        }
        if(v == profileButton){

            ProfileFragment setting = new ProfileFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.frag,setting).commit();

            homeBottomLine.setVisibility(View.INVISIBLE);
            wordBottomLine.setVisibility(View.INVISIBLE);
            learnedBottomLine.setVisibility(View.INVISIBLE);
            favoriteBottomLine.setVisibility(View.INVISIBLE);
            profileBottomLine.setVisibility(View.VISIBLE);

        }
    }

    private boolean isOnline() {

        return ConnectivityHelper.isConnectedToNetwork(this);

    }
}

