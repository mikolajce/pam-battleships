package com.example.statki;

import static com.example.statki.R.id.*;
import static com.example.statki.R.string.*;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Handler handler = new Handler();
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onResume(){
        super.onResume();
        if(new Random().nextInt(3) == 1) Toast.makeText(this, textHi, Toast.LENGTH_SHORT).show();
    }

    public void onClick(View v){
        if (textIsEmpty(findViewById(inputName))) disable(v);
        else {
            EditText ti = findViewById(inputName);
            String inserted = ti.getText().toString();
            easterEgg(inserted);
            launchSettings();
        }
    }

    private void easterEgg(String inserted) {
        switch (inserted){
            case "Mikołaj":
                Toast.makeText(this, "ADMIN MODE", Toast.LENGTH_SHORT).show();
            case "your text":
                Toast.makeText(this, "your toast", Toast.LENGTH_SHORT).show();
            default: Toast.makeText(this, inserted, Toast.LENGTH_SHORT).show(); break;
        }
    }

    private void launchSettings(){
        Intent i = new Intent(this, BattleAiActivity.class);
//        i.putExtra("EXTRA_patrolSize",1);
//        i.putExtra("EXTRA_patrolNumber",3);
        startActivity(i);
    }

    private void disable(View v){

        v.setEnabled(false);

        ((TextView) findViewById(helloWorldText))
                .setText(R.string.textSadFace);

        Toast.makeText(this, alertEmptyName, Toast.LENGTH_SHORT).show();

        runnable = () -> {

            v.setEnabled(true);

            ((Button) findViewById(welcomeButton))
                    .setText(textPlay);
            ((TextView) findViewById(R.id.helloWorldText))
                    .setText(R.string.textHelloWorld);

        }; handler.postDelayed(runnable, 2000);

    }

    private boolean textIsEmpty(View v){
        return ((TextView) v)
                .getText().toString()
                    .equals("");
    }
}