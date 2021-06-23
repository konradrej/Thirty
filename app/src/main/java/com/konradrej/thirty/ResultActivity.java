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

        bundle.putParcelable(getString(R.string.EXTRA_OPTION_SCORES), results);
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
            if (!restoreData(bundle)) {
                // Results was not restored
                throw new NullPointerException();
            }

            // Fill table with values
            populateScores();
        } catch (NullPointerException e) {
            // Inform about error using toast
            sendToast(getString(R.string.error_missing_data), Toast.LENGTH_SHORT);
        }

        // Find button and set OnClickListener
        Button mBtn = findViewById(R.id.playAgain);
        mBtn.setOnClickListener(v -> this.finish());
    }

    private void populateScores() {
        int totalScore = 0;

        String[] options = getResources().getStringArray(R.array.point_options);
        for (int i = 0; i < options.length; i++) {
            int value = results.getOptionValue(options[i]);
            totalScore += value;

            TextView view = findViewById(mPointOptionValue[i]);
            view.setText(String.valueOf(value));
        }

        TextView totalScoreTv = findViewById(R.id.totalScore);
        totalScoreTv.setText(getString(R.string.total_score, totalScore));
    }

    private boolean restoreData(Bundle bundle) {
        if (bundle != null && bundle.containsKey(getString(R.string.EXTRA_OPTION_SCORES))) {
            results = bundle.getParcelable(getString(R.string.EXTRA_OPTION_SCORES));
        } else {
            Bundle intentExtras = getIntent().getExtras();

            if (intentExtras != null && intentExtras.containsKey(getString(R.string.EXTRA_OPTION_SCORES))) {
                results = intentExtras.getParcelable(getString(R.string.EXTRA_OPTION_SCORES));
            } else {
                // If ResultModel was not found in Bundle or Intent return false
                return false;
            }
        }

        // Return true if results has been restored
        return true;
    }

    private void sendToast(String message, int duration) {
        Context context = getApplicationContext();

        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }
}