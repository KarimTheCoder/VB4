package com.fortitude.shamsulkarim.ieltsfordory;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
public class AppLauncher extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_launcher);

        this.startActivity(new Intent(this,SplashScreen.class));


    }


}
