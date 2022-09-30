package com.dadm.reto07;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button aboutButton = (Button) findViewById(R.id.aboutHome);
        aboutButton.setOnClickListener(view -> showAboutDialog());
    }

    public void startNewGame(View view){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("isOnline", false);
        startActivity(intent);
    }

    public void getOnlinePlayers(View view){
        Intent intent = new Intent(this, PlayersActivity.class);
        startActivity(intent);
    }

    public void showAboutDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Context context = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.about_dialog, null);
        builder.setView(layout);
        builder.show();
    }

}