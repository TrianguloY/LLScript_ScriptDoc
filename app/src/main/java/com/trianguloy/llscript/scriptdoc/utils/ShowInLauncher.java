package com.trianguloy.llscript.scriptdoc.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

/**
 * Class that contains functions to show a link in a browser.
 */
public class ShowInLauncher {

    /**
     * A static-only class. Private constructor
     */
    private ShowInLauncher() {}

    /**
     * The easy-to-make show in browser (WARNING!!! possibly infinite loop)
     *
     * Currently unimplemented (to test-only)
     *
     * @param url the url to show
     * @param context the context to use
     */
    static public void basicShowInLauncher(String url,Context context){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(browserIntent);
    }

    /**
     * Shows a list of browsers to open the page, ensuring that this app in not one of them
     * @param url the page's url
     */
    public static void forceShowInLauncher(String url,Context context) {
        if(url==null){
            Log.d("check", "null url");
            return;
        }

        //TODO do this!!!!!!
        Toast.makeText(context,"Currently unimplemented, sorry",Toast.LENGTH_LONG).show();
    }
}
