package com.trianguloy.llscript.scriptdoc.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.trianguloy.llscript.scriptdoc.Constants;
import com.trianguloy.llscript.scriptdoc.R;
import com.trianguloy.llscript.scriptdoc.utils.DocumentManager;

import java.io.IOException;
import java.util.Stack;

/**
 * Class that represents the webview used in the overlay.
 * It extends a normal WebView
 */
public class CustomWebView extends WebView {

    /**
     * The current url
     */
    private String currentUrl = null;

    /**
     * The listener attached
     */
    private Listener listener;

    /**
     * The browsing history
     */
    private Stack<String> history;

    /**
     * Default constructor.
     * Sets the long-click and the overrideable links.
     *
     * @param context a Context object used to access application assets
     * @param attrs an AttributeSet passed to our parent
     */
    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //override links clicked
        setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, final String url) {

                //if the url doesn't ends with # its a valid link (otherwise it's a javascript link)
                if(!url.endsWith(Constants.URL_SEPARATOR))
                    requestPage(url.replaceAll(Constants.URL_INTERCEPT, ""));

                return true;
            }
        });

        //Only vertical scrolling
        getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        //Initialize the history
        history = new Stack<>();
    }



    /**
     * The interface that represents how a listener should be
     */
    public interface Listener {

        /**
         * Called when the url changes
         * @param url the new url
         */
        void onUrlChange(String url,Stack<String> history);
    }


    /**
     * Registers the listener to use with this class.
     * No register-null check yet
     * @param listener the listener to register
     */
    public void registerListener(final Listener listener){
        //register the listener
        this.listener =listener;
    }



    /**
     * Returns the current url related to what is displayed
     * @return currentUrl
     */
    public String getCurrentUrl() {
        return currentUrl;
    }






    /**
     * Request the page and extracts the function, showing the data/error directly on the webview
     * @param page the page to get
     */
    public void requestPage(final String page){

        Log.d("request", page);

//        if(!page.matches("http://www.lightninglauncher.com/scripting/reference/(api|api-beta)/reference/.*")){
//            ShowInLauncher.basicShowInLauncher(page, getContext());
//            return;
//        }

        new Thread(new Runnable(){
            @Override
            public void run() {
                _requestPage(page);
            }
        }).start();

    }

    /**
     * The same as {@link #requestPage(String)} but should be done on a non-main thread
     * @param page the page to display
     */
    private void _requestPage(String page){
        try {

            //Shows 'loading...'
            showMessage(R.string.webData_loading);

            String element;

            //If the 'always-api' preference is set, omit the normal non-beta getter
            //The same is the page is already a beta one
            if(!getContext().getSharedPreferences(Constants.PREFERENCES_FILENAME, Context.MODE_PRIVATE).getBoolean(Constants.PREFERENCES_APIALWAYSBETA,false)
                    && !page.contains(Constants.URL_BETAREPLACE)){

                element = DocumentManager.getDataFromPage(page);
                if(element!=null){
                    showData(element,page);
                    return;
                }
                //The element was shown if found, otherwise lets try in the beta-api (notifies the user)
                showMessage(R.string.webData_tryInBeta);

            }

            //Convert to beta
            page = page.replace(Constants.URL_BETASEARCH, Constants.URL_BETAREPLACE);

            element = DocumentManager.getDataFromPage(page);
            if(element!=null){
                showData(element,page);
                return;
            }
            //The element was shown if found, otherwise a 'no data found' is shown
            showMessage(R.string.webData_notFound_Ppage, page);

        } catch (IOException e) {
            //error occurred
            showMessage(R.string.webData_error_Ppage, page);
            e.printStackTrace();
        }
    }


    /**
     * Shows the data treated as html in the webView
     *
     * TODO keep css style somehow
     *
     * @param data the html-String to show
     * @param url the url corresponding on the data
     */
    private void showData(final String data, final String url){

        //update url
        if(currentUrl!=null) history.push(currentUrl);
        currentUrl = url;

        post(new Runnable() {
            @Override
            public void run() {
                listener.onUrlChange(url,history);

                loadDataWithBaseURL(url, data, "text/html", "UTF-8", null);
                //loadData(data, "text/html", "UTF-8");
            }
        });

    }



    /**
     * Shows the message in the webview.
     * The message should be a resourceString
     * @param messageId the string id of the message
     * @param parameters the parameters in case the string contains them (String.format is applied)
     */
    private void showMessage(int messageId,Object... parameters){
        showData("<p>"+String.format(getContext().getString(messageId), parameters)+"</p>", null);
    }


    /**
     * An 'up' implementation:
     * method description -> its class description
     * class description -> package description
     * package description -> list of packages
     * other -> list of packages
     */
    public void navigateUp() {

        if(currentUrl==null){
            requestPage(Constants.URL_MAINAPIPAGE);
            return;
        }

        if(currentUrl.contains(Constants.URL_SEPARATOR)){
            requestPage(currentUrl.replaceAll(Constants.URL_SEPARATOR + ".*", ""));
        }else{
            if(currentUrl.contains(Constants.URL_PACKAGEREPLACE) || currentUrl.equals(Constants.URL_MAINAPIPAGE)){
                requestPage(Constants.URL_MAINAPIPAGE);
            }else{
                requestPage(currentUrl.replaceAll(Constants.URL_PACKAGESEARCH,Constants.URL_PACKAGEREPLACE));
            }
        }
    }

    /**
     * A 'back' implementation
     * Request the visualization of the previous page
     * It removes it from the stack, and also sets the current page to null so it is not readded back
     */
    public void navigateBack() {
        if(history.empty()) return;

        String prev = history.pop();

        currentUrl=null;
        requestPage(prev);
    }

}
