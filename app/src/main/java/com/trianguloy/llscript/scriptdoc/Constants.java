package com.trianguloy.llscript.scriptdoc;

/**
 * Class that contains all static variables that should not be translated.
 * Note: This doesn't mean that the app will be translated (doesn't make sense, the api is untranslatable)
 * but it is a good way to determine if a string should be here or not
 */
public class Constants {

    /**
     * A static-only class. Private constructor
     */
    private Constants() {}



    //////////   Related to preferences   //////////
    /**
     * The name of the preferences file.
     * To use in {@link android.content.Context#getSharedPreferences(String, int)}
     */
    static public final String PREFERENCES_FILENAME = "pref";
    /**
     * The name of the preference 'always use beta-api'
     * To use in {@link android.content.SharedPreferences#getString(String, String)} & {@link android.content.SharedPreferences.Editor#putString(String, String)}
     */
    public static final String PREFERENCES_APIALWAYSBETA = "betaApi";


    //////////   Related to intents   //////////
    /**
     * The extra of the url
     * Used between {@link com.trianguloy.llscript.scriptdoc.activities.TransparentIntentReceiver} --> {@link com.trianguloy.llscript.scriptdoc.services.ServiceGetter}
     */
    public static final String INTENTEXTRA_URL = "url";



    //////////   Related to urls   //////////
    /**
     * Main api page. Packages list
     */
    public static final String URL_MAINAPIPAGE = "http://www.lightninglauncher.com/scripting/reference/api/reference/packages.html";

    /**
     * The separator between the page and the function in the url
     */
    public static final String URL_SEPARATOR = "#";

    /**
     * The part to search when converting to beta-api
     */
    public static final String URL_BETASEARCH = "reference/api/reference";
    /**
     * The part which to replace the {@link #URL_BETASEARCH}
     */
    public static final String URL_BETAREPLACE = "reference/api-beta/reference";

    /**
     * The part to search when converting a class url to its package one
     */
    public static final String URL_PACKAGESEARCH = "(?<=/)[^/]*$";
    /**
     * The part wich to replace the {@link #URL_PACKAGESEARCH}
     */
    public static final String URL_PACKAGEREPLACE = "package-summary.html";
    /**
     * The prefix used in links so that they are intercepted.
     * This is done to be able to use {@link android.webkit.WebView#loadDataWithBaseURL(String, String, String, String, String)} in {@link com.trianguloy.llscript.scriptdoc.views.CustomWebView#showData(String, String)}
     */
    public static final String URL_INTERCEPT = "intercept:";
}

