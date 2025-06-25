package com.example.elearning;

//thu vien
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends Activity {
    //khai bao bien tong
    ImageView logoImage;
    TextView txttenapp;
    //ham chinh
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        //anh xa
        logoImage = findViewById(R.id.logoImage);
        txttenapp = findViewById(R.id.txttenapp);
        //animation
        new Handler().postDelayed(() -> startAnimation(), 1000);
    }
    //ham con
    private void startAnimation() {
        // Di chuyen logo sang trai
        TranslateAnimation moveLeft = new TranslateAnimation(0, -120, 0, 0);
        moveLeft.setDuration(500);
        moveLeft.setFillAfter(true);
        logoImage.startAnimation(moveLeft);

        // hien thi ten app
        new Handler().postDelayed(() -> txttenapp.setVisibility(View.VISIBLE), 500);

        // sang man hinh chinh

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashScreen.this, MainActivity.class); // đổi MainActivity thành LoginActivity
            startActivity(intent);
            finish();
        }, 2500);
    }
}
