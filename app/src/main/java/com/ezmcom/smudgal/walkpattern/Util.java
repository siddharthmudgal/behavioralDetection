package com.ezmcom.smudgal.walkpattern;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by SMudgal on 10/9/2016.
 */
public class Util{
    JSONObject outer_json = new JSONObject();
    String path = "http://%s:3000/";

    public int checkUser(String userName, Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.behavUsernames),
                Context.MODE_PRIVATE);
        return sharedPreferences.getInt(userName, 3);
    }
    public int checkUserName(String userName, Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.behavUsernames),
                Context.MODE_PRIVATE);
        Integer username_turn = sharedPreferences.getInt(userName, 3);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (username_turn >= 0){
            editor.putInt(userName,--username_turn);
            editor.commit();
            return username_turn;
        }
        return -1;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean convertToJson(String action, String name, float[] acc_x, float[] acc_y, float[] acc_z, float[] mag_x, float[] mag_y, float[] mag_z,
                                 int size, String host , Context context){
        try{
            outer_json.put("name",name);
            outer_json.put("acc_x", new JSONArray(acc_x));
            outer_json.put("acc_y", new JSONArray(acc_y));
            outer_json.put("acc_z", new JSONArray(acc_z));
            outer_json.put("mag_x", new JSONArray(mag_x));
            outer_json.put("mag_y", new JSONArray(mag_y));
            outer_json.put("mag_z", new JSONArray(mag_z));
            System.out.println(outer_json);
            NetworkCalls networkCalls = new NetworkCalls();
            networkCalls.execute(path.replace("%s", host), String.valueOf(outer_json), action);
            remove_unused_users(context);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }
    public void remove_unused_users(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.behavUsernames),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Map<String,Integer> map = (Map<String, Integer>) sharedPreferences.getAll();
        for (Map.Entry<String,Integer> m : map.entrySet()){
            if (m.getValue() >= 0){
                editor.remove(m.getKey());
            }
        }
        editor.commit();
    }
}
class NetworkCalls extends AsyncTask<String,String,String>{

    @Override
    protected String doInBackground(String... params) {
        try {
            System.out.println(params[0]);
            DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(params[0].concat(params[2]));
            System.out.println(params[1]);
            System.out.println(params[0].concat(params[2]));
            StringEntity stringEntity = new StringEntity(params[1].toString());
            httpPost.setEntity(stringEntity);
            httpPost.setHeader(new BasicHeader("Content-Type", "application/json"));
            ResponseHandler responseHandler = new BasicResponseHandler();
            defaultHttpClient.execute(httpPost, responseHandler);
        }catch (Exception e)
        {
            System.out.println(e.toString());
        }
        return null;
    }
}
