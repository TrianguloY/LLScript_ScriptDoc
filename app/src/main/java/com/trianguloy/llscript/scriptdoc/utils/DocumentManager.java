package com.trianguloy.llscript.scriptdoc.utils;

import com.trianguloy.llscript.scriptdoc.Constants;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Static class that contains functions relative to the managing of the documents
 */
public class DocumentManager {

    /**
     * A static-only class. Private constructor
     */
    private DocumentManager() {}

    /**
     * Gets the specific doc from the page.
     * Returns the detailed description of the function if any (.../Class#function)
     * Returns the summary of the class if there is no function.
     * Returns null if not found or an error occurred.
     *
     * @param page the page to load from (should be lightnings APIdoc)
     * @return the Element representing the function html doc, null if not found
     * @throws IOException from {@link Connection#get()}
     */
    static public String getDataFromPage(String page) throws IOException {

        //Gets the document
        Document document = getDocument(page);//Throws IOException

        //Will have the elements to show
        Elements elements = new Elements(2);

        //The header is always shown
        Element header = document.getElementById("jd-header");
        header.children().unwrap();
        elements.add(header);
        elements.append("<hr>");


        if(page.contains(Constants.URL_SEPARATOR)){
            //there is a function, show only its detailed description

            //selects the corresponding method/constant description
            Element description = document.getElementById(
                    page.substring( page.indexOf( Constants.URL_SEPARATOR ) +1 )
            );
            if(description==null) return null;
            elements.add(description.nextElementSibling());


        }else{
            //No function, show the summary of the class

            //selects the thing to show as description of a class
            //TODO better selection, more general
            elements.addAll( document.select("#jd-content>.jd-descr , #jd-content>.jd-inheritance-table , #jd-content>.jd-sumtable") );

        }

        return elements.html();

    }


    /**
     * Returns the document corresponding from the page.
     * Currently only from the internet
     *
     * //TODO use cache and take it from there
     * //TODO auto-download the offline api rar and use that if selected
     *
     * @param page the page to get the document
     * @throws IOException from {@link Connection#get()}
     */
    static private Document getDocument(String page) throws IOException {

        //Gets the document from the internet
        Document document = Jsoup.connect(page).get();

        //converts local links to global and interceptable (so the webview always ask to overrride its loading)
        Elements select = document.select("a");
        for (Element e : select){
            String absUrl = e.absUrl("href");
            e.attr("href", Constants.URL_INTERCEPT+absUrl);
        }

        //Unhides everything
        //TODO make this a bit more 'controlled'
        document.select("*[style=display: none;]").attr("style","display: block;");

        return document;
    }

}
