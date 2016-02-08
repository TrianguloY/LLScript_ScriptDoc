package com.trianguloy.llscript.scriptdoc.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.trianguloy.llscript.scriptdoc.Constants;
import com.trianguloy.llscript.scriptdoc.services.ServiceGetter;

/**
 * Class that represents the activity that will handle the url-intent and send it to the Service.
 * It is a transparent activity and is finished as soon as created.
 * TODO is it possible to not use an activity somehow?
 */
public class TransparentIntentReceiver extends Activity {

    /**
     * When the activity is created, sends the url to the service and finfishes itself
     * @param savedInstanceState see {@link Activity#onCreate(Bundle)}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Log.d("main","start");

        Uri data = getIntent().getData();

        if(data!=null) {
            //Log.d("main",data.toString());

            //starts/notifies the service about a new page-function to load
            Intent intent = new Intent(this, ServiceGetter.class);
            intent.putExtra(Constants.INTENTEXTRA_URL, data.toString());
            startService(intent);
        }

        finish();

    }

}
