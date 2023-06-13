package com.nick.escanertraductor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.nick.escanertraductor.activities.MainActivity;

public class Bienvenida extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenida);

        int tiempo = 3000;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Bienvenida.this, MainActivity.class));
                finish();
            }
        }, tiempo);
    }
}