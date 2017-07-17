package com.example.sab;
import java.io.*;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.*;

import android.util.Log;

import java.util.*;

public class JSONParser {
	static InputStream is= null;
	static JSONObject jArray= null;
	static String aString = null;
	
	public JSONParser() {
		
	}
	
	public JSONArray makeHttpRequest2(String url, String method, List<NameValuePair> params) {
		JSONArray jarray= null;
		try {
			jarray = new JSONArray(method);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jarray; 
	}
	
	public JSONObject makeHttpRequest(String url, String method, List<NameValuePair> params) {
		try {
			
			if(method.equals("POST")) {
				
				DefaultHttpClient http = new DefaultHttpClient();
				HttpPost post = new HttpPost(url);
				post.setEntity(new UrlEncodedFormEntity(params));
				
				HttpResponse response = http.execute(post);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
				
			}else if(method.equals("GET")) {
				
				DefaultHttpClient http = new DefaultHttpClient();
				String para = URLEncodedUtils.format(params, "UTF-8");
				url+= "?"+para;
				System.out.println(url);
				HttpGet get = new HttpGet(url);
				HttpResponse response = http.execute(get);
				HttpEntity entity = response.getEntity();
				is= entity.getContent();
			}
			
		}catch(UnsupportedEncodingException e) {
			e.printStackTrace();
			Log.e("encoding", "x supported encoding");
		}catch(ClientProtocolException e) {
			e.printStackTrace();
			Log.e("client protocol ", "protocol");
		}catch(IOException e) {
			e.printStackTrace();
			Log.e("io", "io exception");
		}
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"),8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while( (line =reader.readLine()) != null) {
				sb.append(line +"\n");
			}
			is.close();
			
			aString = sb.toString();
		}catch(Exception e) {
			e.printStackTrace();
			Log.e("io e2", "io exception at part 2");
		}
		
		try {
			jArray = new JSONObject(aString);
		}catch(JSONException e) {
			Log.e("json", aString);
		}
		
		return jArray;
	}
}
