package com.konradrej.thirty;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Handles result screen activity.
 *
 * @author Konrad Rej
 */
public class ResultActivity extends AppCompatActivity {
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
    private ResultModel results;

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);

        bundle.putSerializable(getString(R.string.EXTRA_OPTION_SCORES), results);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_result);

        // Hide action bar
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.hide();
        }

        // Tries to get ResultModel instance from Bundle or Intent
        try {
            if (bundle != null && bundle.containsKey(getString(R.string.EXTRA_OPTION_SCORES))) {
                results = (ResultModel) bundle.getSerializable(getString(R.string.EXTRA_OPTION_SCORES));
            } else {
                Bundle intentExtras = getIntent().getExtras();

                if (intentExtras != null && intentExtras.containsKey(getString(R.string.EXTRA_OPTION_SCORES))) {
                    results = (ResultModel) intentExtras.getSerializable(getString(R.string.EXTRA_OPTION_SCORES));
                } else {
                    // If ResultModel was not found in Bundle or Intent throw error
                    throw new NullPointerException();
                }
            }

            int totalScore = 0;

            // Fill table with values
            String[] options = getResources().getStringArray(R.array.point_options);
            for (int i = 0; i < options.length; i++) {
                int value = results.getOptionValue(options[i]);
                totalScore += value;

                TextView view = findViewById(mPointOptionValue[i]);
                view.setText(String.valueOf(value));
            }

            TextView totalScoreTv = findViewById(R.id.totalScore);
            totalScoreTv.setText(getString(R.string.total_score, totalScore));
        } catch (NullPointerException e) {
            // Inform about error using toast
            Context context = getApplicationContext();
            String message = "An error has occurred, data is missing.";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, message, duration);
            toast.show();
        }

        // Find button and set OnClickListener
        Button mBtn = findViewById(R.id.playAgain);
        mBtn.setOnClickListener(v -> this.finish());
    }
}