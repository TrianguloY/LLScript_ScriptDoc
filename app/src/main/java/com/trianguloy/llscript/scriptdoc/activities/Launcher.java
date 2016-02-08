package com.trianguloy.llscript.scriptdoc.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.CheckBox;

import com.trianguloy.llscript.scriptdoc.Constants;
import com.trianguloy.llscript.scriptdoc.R;

/**
 * Class that represents the main activity, the one with a launcher intent.
 * Currently contains a brief description and settings
 */
public class Launcher extends Activity {

    /**
     * The shared preferences
     */
    private SharedPreferences preferences;

    /**
     * Initializes the {@link #preferences} variable and the states of the checckboxs
     * @param savedInstanceState see {@link Activity#onCreate(Bundle, PersistableBundle)} (Bundle)}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        //initialize variable
        preferences = getSharedPreferences(Constants.PREFERENCES_FILENAME,MODE_PRIVATE);

        //state of checkbox
        ((CheckBox) findViewById(R.id.checkBox)).setChecked(preferences.getBoolean(Constants.PREFERENCES_APIALWAYSBETA, false));

    }



    /**
     * When clicking the checkbox of the 'always use beta-api'
     * Updates the saved preference
     * @param v the view
     */
    public void checkBox_alwaysBeta(View v){
        preferences.edit().putBoolean(Constants.PREFERENCES_APIALWAYSBETA, ((CheckBox) v).isChecked()).commit();
    }

}
