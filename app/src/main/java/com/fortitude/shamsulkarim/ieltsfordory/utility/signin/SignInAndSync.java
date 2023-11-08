package com.fortitude.shamsulkarim.ieltsfordory.utility.signin;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.data.repository.VocabularyRepository;
import com.fortitude.shamsulkarim.ieltsfordory.utility.connectivity.ConnectivityHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class SignInAndSync {


    private VocabularyRepository repository;
    private List<Integer> savedBeginnerFav, savedAdvanceFav,savedIntermediateFav, savedGreFav;
    private List<Integer> savedIeltsLearned, savedToeflLearned,savedSatLearned, savedGreLearned;
    private String ADVANCE_FAVORITE,ADVANCE_LEARNED, BEGINNER_FAVORITE, BEGINNER_LEARNED,INTERMEDIATE_FAVORITE,INTERMEDIATE_LEARNED, GRE_FAVORITE, GRE_LEARNED;
    private static final String TAG = "GoogleActivity";
    private final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private DatabaseReference ref;
    private boolean connected;
    private SharedPreferences sp;
    private Context context;

    private SignInAndSyncCallback callback;

    public SignInAndSync(Context context, SignInAndSyncCallback callback) {

        this.callback = callback;

        this.context = context;


        connected = ConnectivityHelper.isConnectedToNetwork(context);
        sp = context.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);

        repository = new VocabularyRepository(context);



        //.requestIdToken(getString(R.string.default_web_client_id))
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.google_client_id))
                .requestEmail()
                .build();
        // [END config_signin]
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
        mAuth = FirebaseAuth.getInstance();
        // firebase database
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference();



        //todo here are some sign in code

        if(mAuth.getCurrentUser() != null && connected){

            ref.child(mAuth.getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {


                @Override
                public void onChildAdded(@NotNull DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildChanged(@NotNull DataSnapshot dataSnapshot, String s) {

//                if(dataSnapshot.exists()) {
                    String data = dataSnapshot.getValue(String.class);
                    String key  = dataSnapshot.getKey();

                    // Toast.makeText(getApplicationContext(),key+" "+ data, Toast.LENGTH_SHORT).show();

                    // if key equals to favorite
                    if( Objects.requireNonNull(key).equalsIgnoreCase("advanceFavCount") || key.equalsIgnoreCase("intermediateFavCount") || key.equalsIgnoreCase("beginnerFavCount")){

                        syncDatabasesIfFavDataChanged(data, key);

                    }


                    //if key equals to learned

                    if(key.equalsIgnoreCase("advanceLearnedCount") || key.equalsIgnoreCase("intermediateLearnedCount") || key.equalsIgnoreCase("beginnerLearnedCount")){

                        syncSPIfLearnedDataChanged(data, key);
                    }




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
        }



    }


    public Intent getSignInIntent(){
        return mGoogleSignInClient.getSignInIntent();
    }

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public GoogleSignInClient getSignInClient(){

        return mGoogleSignInClient;
    }

    public int getRC_SIGN_IN() {
        return RC_SIGN_IN;
    }


    public FirebaseUser firebaseAuthenticationWithGoogle(GoogleSignInAccount acct){
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        callback.progressStatus(false);
        // [END_EXCLUDE]

        callback.progressStatus(false);

        final FirebaseUser[] user = new FirebaseUser[1];

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            user[0] = mAuth.getCurrentUser();
                            //updateUI(user);
                            callback.updateUI();
                            Toast.makeText(context, "Successfully signed in",Toast.LENGTH_SHORT).show();
                            getFirebase();

                            callback.progressStatus(true);

                            askToSync();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "Failed to sign in", task.getException());
                            Toast.makeText(context, "Failed to sign in",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                            callback.updateUI();
                        }



                    }
                });

        return user[0];

    }


    public void getFirebase(){


        try{
            ref.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).addChildEventListener(new ChildEventListener() {


                int i = 0;
                final String[] strData = new String[9];
                final HashMap<String,String> data = new HashMap<>();
                Boolean askOnce = false;

                @Override
                public void onChildAdded(@NotNull DataSnapshot dataSnapshot, String s) {
                    //Toast.makeText(getApplicationContext(),s+" : "+dataSnapshot.getValue(String.class),Toast.LENGTH_SHORT).show();




                    if(dataSnapshot.exists() &&  i == 8){

                        callback.progressStatus(true);
                        askOnce = true;
                        askToSync();

                    }


                    // getting data

                    String state = dataSnapshot.getValue(String.class);

                    strData[i] = state;
                    data.put(s,state);



                    if(strData[8] != null){

                        for(int j = 0; j < strData.length; j++){

                            if(j == 0){

                                GRE_FAVORITE = strData[0];

                            }
                            if(j == 1){

                                GRE_LEARNED = strData[1];

                            }
                            if(j == 2){

                                BEGINNER_FAVORITE = strData[2];


                            }
                            if(j == 3){


                                BEGINNER_LEARNED = strData[3];

                            }
                            if(j == 4){
                                sp.edit().putString("userName",strData[4]).apply();

                            }
                            if(j == 5){

                                ADVANCE_FAVORITE = strData[5];


                            }
                            if( j == 6){



                                ADVANCE_LEARNED = strData[6];

                            }

                            if( j == 7){



                                INTERMEDIATE_FAVORITE = strData[7];

                            }

                            if( j == 8){



                                INTERMEDIATE_LEARNED = strData[8];

                            }

                        }

                    }
                    i++;

                }


                @Override
                public void onChildChanged(@NotNull DataSnapshot dataSnapshot, String s) {

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

            Toast.makeText(context,"Reference exception",Toast.LENGTH_SHORT).show();

        }



    }

    public void authenticateUser(int requestCode, Intent data) {
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthenticationWithGoogle(account);


            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);

                // [START_EXCLUDE]
                callback.updateUI();
                // [END_EXCLUDE]
            }
        }
    }


    private void askToSync(){

        // todo this below code must be called if following condition
        //is met: if(!SettingActivity.this.isFinishing())
            new LovelyStandardDialog(context)
                    .setIcon(R.drawable.data_found)
                    .setTitle("Saved progress on the cloud found!")
                    .setMessage("Do you want to sync the progress?")
                    .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            callback.progressStatus(false);
                            syncSQL();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();

    }


    private void syncSQL(){
        addingBuilderToNums();

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {


                //Toast.makeText(getApplicationContext(),"Syncingdfksfjlsj"+savedBeginnerFav.size(),Toast.LENGTH_SHORT).show();

                if(savedBeginnerFav.size() > 0){

                    for(int i = 0; i <savedBeginnerFav.size();i++){

                        // Toast.makeText(getApplicationContext(),""+savedBeginnerFav.get(i),Toast.LENGTH_SHORT).show();

                        if(savedBeginnerFav.get(i) == 1){


                            repository.updateIELTSFavoriteState(""+(i+1),"True");
                        }else {
                            repository.updateIELTSFavoriteState(""+(i+1),"False");
                        }



                    }


                }

                if(savedIntermediateFav.size() > 0){

                    for(int j = 0; j < savedIntermediateFav.size(); j++){

                        if(savedIntermediateFav.get(j)== 1){

                            repository.updateTOEFLFavoriteState(""+(j+1),"True");
                        }else {
                            repository.updateTOEFLFavoriteState(""+(j+1),"False");
                        }




                    }

                }

                if(savedAdvanceFav.size() > 0){

                    for(int k = 0; k < savedAdvanceFav.size(); k++){


                        if(savedAdvanceFav.get(k) == 1){

                            repository.updateSATFavoriteState(""+(1+k),"True");
                        }else {

                            repository.updateSATFavoriteState(""+(1+k),"False");
                        }




                    }

                }

                if(savedGreFav.size() > 0){

                    for(int k = 0; k < savedGreFav.size(); k++){


                        if(savedGreFav.get(k) == 1){

                            repository.updateGREFavoriteState(""+(1+k),"True");
                        }else {

                            repository.updateGREFavoriteState(""+(1+k),"False");
                        }




                    }

                }


                // Syncing Learned


                if(savedIeltsLearned.size() > 0){

                    for(int k = 0; k < savedIeltsLearned.size(); k++){


                        if(savedIeltsLearned.get(k) == 1){

                            repository.updateIELTSLearnState(""+(1+k),"True");
                        }else {

                            repository.updateIELTSLearnState(""+(1+k),"False");
                        }




                    }

                }


                if(savedToeflLearned.size() > 0){

                    for(int k = 0; k < savedToeflLearned.size(); k++){


                        if(savedToeflLearned.get(k) == 1){

                            repository.updateTOEFLLearnState(""+(1+k),"True");
                        }else {

                            repository.updateTOEFLLearnState(""+(1+k),"False");
                        }




                    }

                }

                if(savedSatLearned.size() > 0){

                    for(int k = 0; k < savedSatLearned.size(); k++){


                        if(savedSatLearned.get(k) == 1){

                            repository.updateSATLearnState(""+(1+k),"True");
                        }else {

                            repository.updateSATLearnState(""+(1+k),"False");
                        }




                    }

                }

                if(savedGreLearned.size() > 0){

                    for(int k = 0; k < savedGreLearned.size(); k++){


                        if(savedGreLearned.get(k) == 1){

                            repository.updateGRELearnState(""+(1+k),"True");
                        }else {

                            repository.updateGRELearnState(""+(1+k),"False");
                        }
                    }

                }
            }
        }, 0L);





        callback.progressStatus(true);

    }

    private void addingBuilderToNums(){

        savedAdvanceFav = new ArrayList<>();
        savedIntermediateFav = new ArrayList<>();
        savedBeginnerFav = new ArrayList<>();
        savedGreFav = new ArrayList<>();

        savedIeltsLearned = new ArrayList<>();
        savedToeflLearned = new ArrayList<>();
        savedSatLearned = new ArrayList<>();
        savedGreFav = new ArrayList<>();


        savedIeltsLearned = builderToNums(new StringBuilder(BEGINNER_LEARNED));
        savedToeflLearned = builderToNums(new StringBuilder(INTERMEDIATE_LEARNED));
        savedSatLearned =   builderToNums(new StringBuilder(ADVANCE_LEARNED));
        savedGreLearned =   builderToNums(new StringBuilder(GRE_LEARNED));

        savedBeginnerFav =  builderToNums(new StringBuilder(BEGINNER_FAVORITE));
        savedIntermediateFav = builderToNums(new StringBuilder(INTERMEDIATE_FAVORITE));
        savedAdvanceFav  = builderToNums(new StringBuilder(ADVANCE_FAVORITE));
        savedGreFav = builderToNums(new StringBuilder(GRE_FAVORITE));

//        sp.edit().putInt("intermediate", Integer.parseInt(INTERMEDIATE_LEARNED)).apply();
//        sp.edit().putInt("beginner", Integer.parseInt(BEGINNER_LEARNED)).apply();
//        sp.edit().putInt("advance", Integer.parseInt(ADVANCE_LEARNED)).apply();

    }

    private List<Integer> builderToNums(StringBuilder numBuilder){

        List<Integer> backToNums = new ArrayList<>();
        String string = numBuilder.toString();

        for(int i = 0; i < string.length();){

            if(string.substring(i, i+1).equalsIgnoreCase("1")){

                backToNums.add(1);
            }else {


                backToNums.add(0);

            }
            i = i+2;
        }

        return backToNums;
    }

    private void syncDatabasesIfFavDataChanged(String newData, String key){


        // todo investigate why GRE not used in this method
        List<Integer> newDataList;





        // ADVANC FAVORITE COUNT

        if(key.equalsIgnoreCase("advanceFavCount")){

            int advanceSize = sp.getInt("advance",context.getResources().getStringArray(R.array.SAT_words).length);
            // cleaning database

            for(int i = 0; i < advanceSize; i++){
                repository.updateSATFavoriteState(""+(i+1),"False");
            }

            // adding new data

            newDataList = builderToNums(new StringBuilder(newData));
            if(newDataList.size() > 0){

                for(int k = 0; k < newDataList.size(); k++){

                    int adva = newDataList.get(k)+1;
                    repository.updateSATFavoriteState(""+adva,"True");



                }

            }

            //Toast.makeText(getApplicationContext(),"advance favorite synced", Toast.LENGTH_SHORT).show();
        }

        // INTERMEDIATE FAVORITE COUNT

        if(key.equalsIgnoreCase("intermediateFavCount")){

            int intermediateSize = sp.getInt("intermediate",context.getResources().getStringArray(R.array.TOEFL_words).length);
            // cleaning database

            for(int i = 0; i < intermediateSize; i++){
                repository.updateTOEFLFavoriteState(""+(i+1),"False");
            }

            // adding new data

            newDataList = builderToNums(new StringBuilder(newData));
            if(newDataList.size() > 0){

                for(int k = 0; k < newDataList.size(); k++){

                    int adva = newDataList.get(k)+1;
                    repository.updateTOEFLFavoriteState(""+adva,"True");



                }

            }
        }


        // BEGINNER FAVORITE COUNT

        if(key.equalsIgnoreCase("beginnerFavCount")){

            int beginnerSize = sp.getInt("beginner",context.getResources().getStringArray(R.array.TOEFL_words).length);
            // cleaning database

            for(int i = 0; i < beginnerSize; i++){
                repository.updateIELTSLearnState(""+(i+1),"False");
            }

            // adding new data

            newDataList = builderToNums(new StringBuilder(newData));
            if(newDataList.size() > 0){

                for(int k = 0; k < newDataList.size(); k++){

                    int adva = newDataList.get(k)+1;
                    repository.updateIELTSLearnState(""+adva,"True");



                }

            }

        }





    }

    private void syncSPIfLearnedDataChanged(String data, String key){


        if(key.equalsIgnoreCase("advanceLearndCount")){

            int localAdvanceLearned = sp.getInt("advance",0);
            int firebaseSaved = Integer.parseInt(data);

            if( firebaseSaved > localAdvanceLearned){

                sp.edit().putInt("advance",firebaseSaved).apply();
                // Toast.makeText(getApplicationContext(),"Synced "+ key, Toast.LENGTH_SHORT).show();

            }

        }

        if(key.equalsIgnoreCase("intermediateLearnedCount")){

            int localAdvanceLearned = sp.getInt("intermediate",0);
            int firebaseSaved = Integer.parseInt(data);

            if( firebaseSaved > localAdvanceLearned){

                sp.edit().putInt("intermediate",firebaseSaved).apply();
                //Toast.makeText(getApplicationContext(),"Synced "+ key, Toast.LENGTH_SHORT).show();

            }

        }

        if(key.equalsIgnoreCase("beginnerLearnedCount")){

            int localAdvanceLearned = sp.getInt("beginner",0);
            int firebaseSaved = Integer.parseInt(data);

            if( firebaseSaved > localAdvanceLearned){

                sp.edit().putInt("beginner",firebaseSaved).apply();
                // Toast.makeText(getApplicationContext(),"Synced "+ key, Toast.LENGTH_SHORT).show();

            }

        }

    }

}
