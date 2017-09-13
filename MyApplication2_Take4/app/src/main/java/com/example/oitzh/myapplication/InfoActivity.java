package com.example.oitzh.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

    }


    public void onClickSubmit(View view) {
        Intent returnIntent = new Intent();
        EditText editText = (EditText) findViewById(R.id.dataBox);
        String message = editText.getText().toString();
        returnIntent.putExtra("result",message);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
}
