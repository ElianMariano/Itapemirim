package com.prefeitura.myapplication;

import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class HandleRequests {
    public static LatLng getCordenatesByRequest(String response){
        LatLng latLng = new LatLng(0, 0);
        try {
            JSONArray array = new JSONArray(response);

            JSONObject json = array.getJSONObject(0);

            double latdouble = json.getDouble("lat");

            double londouble = json.getDouble("lon");

            latLng = new LatLng(latdouble, londouble);
        } catch (JSONException e) {
            Log.d("HandleRequests", e.toString());
        }

        return latLng;
    }

    public static ArrayList<LatLng> getRouteByRequest(String response){
        ArrayList<LatLng> route = new ArrayList<>();

        try{
            JSONObject json = new JSONObject(response);

            JSONObject features = json.getJSONArray("features").getJSONObject(0);

            JSONObject geometry = features.getJSONObject("geometry");

            JSONArray coordinates = geometry.getJSONArray("coordinates");

            int i = 0;
            do{
                JSONArray coord = coordinates.getJSONArray(i);

                LatLng co = new LatLng(coord.getDouble(1), coord.getDouble(0));

                route.add(co);

                i++;
            }
            while(!coordinates.isNull(i));
        }
        catch(JSONException e){
            Log.d("HandleRequests", e.toString());
        }

        return route;
    }
}
