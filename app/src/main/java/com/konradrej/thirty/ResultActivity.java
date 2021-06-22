package com.konradrej.thirty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResultActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtn;
    private final Integer[] mPointOptionValue = {
            R.id.pointOptionLowValue,
            R.id.pointOption4Value,
            R.id.pointOption5Value,
            R.id.pointOption6Value,
            R.id.pointOption7Value,
            R.id.pointOption8Value,
            R.id.pointOption9Value,
            R.id.pointOption10Value,
            R.id.pointOption11Value,
            R.id.pointOption12Value,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        try {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        try {
            ResultModel values;
            if (savedInstanceState == null) {
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    values = (ResultModel) extras.getSerializable(getString(R.string.EXTRA_OPTION_SCORES));
                } else {
                    throw new NullPointerException();
                }
            } else {
                values = (ResultModel) savedInstanceState.getSerializable(getString(R.string.EXTRA_OPTION_SCORES));
            }

            int totalScore = 0;

            String[] options = getResources().getStringArray(R.array.point_options);
            for(int i = 0; i < options.length; i++){
                int value = values.getOptionValue(options[i]);
                totalScore += value;

                TextView view = findViewById(mPointOptionValue[i]);
                view.setText(String.valueOf(value));
            }

            TextView totalScoreTv = findViewById(R.id.totalScore);
            totalScoreTv.setText(getString(R.string.total_score, totalScore));
        }catch(NullPointerException e){
            Context context = getApplicationContext();
            String message = "An error has occurred, data is missing.";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, message, duration);
            toast.show();
        }

        mBtn = findViewById(R.id.playAgain);
        mBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }
}