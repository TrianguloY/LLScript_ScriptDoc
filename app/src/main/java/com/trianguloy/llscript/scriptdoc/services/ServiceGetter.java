package com.trianguloy.llscript.scriptdoc.services;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.trianguloy.llscript.scriptdoc.Constants;
import com.trianguloy.llscript.scriptdoc.R;
import com.trianguloy.llscript.scriptdoc.utils.ShowInLauncher;
import com.trianguloy.llscript.scriptdoc.views.CustomWebView;

import java.util.Stack;

/**
 * Class that represents the service handling the overlay. This is the main class of the project.
 */
public class ServiceGetter extends Service {

    /**
     * The {@link #WINDOW_SERVICE} system service
     */
    private WindowManager windowManager;

    /**
     * The overlay. This is the view that is shown as an overlay
     */
    private View overlayView;

    /**
     * The webview from the overlay
     */
    private CustomWebView webView;



    /**
     * This service is not made to bind with callers. Returns null
     * @param intent intent
     * @return null
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * When creating the service the view created, initialized and shown.
     */
    @Override
    public void onCreate() {
        super.onCreate();

        //Window manager. Initializes the variable
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        //Overlay view. Creates the view inflating from xml
        overlayView = LayoutInflater.from(this).inflate(R.layout.view_popupdoc, null);

        //Exit button. Stops the service
        overlayView.findViewById(R.id.button_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService();
            }
        });

        //Open in browser button
        overlayView.findViewById(R.id.button_openInBrowser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowInLauncher.forceShowInLauncher(webView.getCurrentUrl(), getApplicationContext());
            }
        });

        //navigateUp button
        overlayView.findViewById(R.id.button_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.navigateUp();
            }
        });

        //back button
        overlayView.findViewById(R.id.button_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.navigateBack();
            }
        });

        //WebView. register listener
        webView= (CustomWebView) overlayView.findViewById(R.id.customWebView);
        webView.registerListener(new CustomWebView.Listener() {
            @Override
            public void onUrlChange(String url,Stack<String> history) {
                overlayView.findViewById(R.id.button_openInBrowser).setVisibility(url == null ? View.GONE : View.VISIBLE);
                overlayView.findViewById(R.id.button_back).setVisibility(history.empty() ? View.GONE : View.VISIBLE);
            }
        });


        //dragger. Drag to resize the overlay view
        final View content = overlayView.findViewById(R.id.content);
        overlayView.findViewById(R.id.dragger).setOnTouchListener(new View.OnTouchListener() {

            int savedIndex = -1;
            float lastY = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (savedIndex != -1 && event.getActionIndex() != savedIndex)
                    return false;

                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        savedIndex = event.getActionIndex();
                        lastY = event.getY(savedIndex);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        ViewGroup.LayoutParams lp = content.getLayoutParams();
                        lp.height = Math.round(Math.max(0, lp.height + event.getY(savedIndex) - lastY));
                        content.requestLayout();
                        content.invalidate();
                        //lastY = event.getY(savedIndex); //not neccesary? something related to the dragging-lag I guess...
                        //TODO fix the dragging-lag
                        break;
                    case MotionEvent.ACTION_UP:
                        savedIndex = -1;
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });


        //Layout parameters. Top, full width, only touches inside, no focusable
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP;


        //adds and shows the view
        try {
            windowManager.addView(overlayView, params);
        }catch(WindowManager.BadTokenException e){
            Toast.makeText(this,"Unable to add the view, did you gave permission?",Toast.LENGTH_LONG).show();
        }
    }







    /**
     * When a new request to show a page-function is received.
     * @param intent the intent received
     * @param flags flags
     * @param startId startId
     * @return START_STICKY
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //should be a non null intent
        if(intent==null){
            Log.d("error", "null intent");
            return super.onStartCommand(null,flags,startId);
        }

        //gets the extras
        final String url = intent.getStringExtra(Constants.INTENTEXTRA_URL);

        //should have the extra
        if (url == null){
            Log.d("error","no extra");
            return super.onStartCommand(intent,flags,startId);
        }

        //request the page loading in a new thread and shows it
        webView.requestPage(url);

        //this should be our return TODO check if START_STICKY is the correct thing to return
        return START_STICKY;
    }

    /**
     * When destroying the service the overlayView is removed
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(overlayView !=null && windowManager!=null)
            windowManager.removeView(overlayView);
    }


    /**
     * Stops the service. Allows to be called in different threads.
     */
    private void stopService(){
        stopForeground(true);
        stopSelf();
    }

}
