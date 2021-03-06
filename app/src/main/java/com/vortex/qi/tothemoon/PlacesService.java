package com.vortex.qi.tothemoon;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by qw on 1/3/2016.
 */
public class PlacesService {

    private String API_KEY;

    public PlacesService(String apikey) {
        this.API_KEY = apikey;
    }

    public void setApiKey(String apikey) {
        this.API_KEY = apikey;
    }

    public ArrayList<Place> findPlaces(double latitude, double longitude,
                                       String placeSpacification) {

        String urlString = makeUrl(latitude, longitude, placeSpacification);

        try {
            String json = getJSON(urlString);
//            Log.d("QiWu1", json+"a");
            JSONObject object = new JSONObject(json);
            JSONArray array = object.getJSONArray("results");

            ArrayList<Place> arrayList = new ArrayList<Place>();
            for (int i = 0; i < array.length(); i++) {
                try {
                    Place place = Place.jsonToPontoReferencia((JSONObject) array.get(i));
                    Log.v("Places Services ", "" + place);
                    arrayList.add(place);
                } catch (Exception e) {
                }
            }
            return arrayList;
        } catch (JSONException ex) {
            Logger.getLogger(PlacesService.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
        return null;
    }
//    https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&type=restaurant&name=cruise&key=YOUR_API_KEY

//    https://maps.googleapis.com/maps/api/place/search/json?      location=-28.6328081,177.2182706&radius=500&types=atm000000&sensor=false&key=apikey
    private String makeUrl(double latitude, double longitude, String place) {
        StringBuilder urlString = new StringBuilder(
                "https://maps.googleapis.com/maps/api/place/nearbysearch/json?");

        if (place.equals("")) {
            urlString.append("&location=");
            urlString.append(Double.toString(latitude));
            urlString.append(",");
            urlString.append(Double.toString(longitude));
            urlString.append("&radius=1000");
            // urlString.append("&types="+place);
            urlString.append("&name=cruise&key=" + API_KEY);
        } else {
            urlString.append("&location=");
            urlString.append(Double.toString(latitude));
            urlString.append(",");
            urlString.append(Double.toString(longitude));
            urlString.append("&radius=1000");
            urlString.append("&types=" + place);
            urlString.append("&name=cruise&key=" + API_KEY);
        }
        return urlString.toString();
    }

    protected String getJSON(String url) {
//        Log.d("QiWu1", "getJSON method" + url);
        return getUrlContents(url);
    }

    private String getUrlContents(String theUrl) {
        StringBuilder content = new StringBuilder();
        try {
            URL url = new URL(theUrl);
//            Log.d("QiWu1", "getUrlContents method" + url);
            URLConnection urlConnection = url.openConnection();
//            Log.d("QiWu1", "getUrlContents, urlConnection: " + urlConnection.toString());
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream()), 8);
            Log.d("QiWu1", "getUrlContents, bufferedReader: " + bufferedReader.readLine());
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
//                Log.d("QiWu1", "getUrlContents, buffered: " + content.toString());
            }
            bufferedReader.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }
}
