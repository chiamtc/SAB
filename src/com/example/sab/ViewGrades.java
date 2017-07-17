package com.example.sab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
public class ViewGrades extends Fragment {
	 
	private ProgressDialog pDialog;
	String studentID;
	JSONParser jParser = new JSONParser();
	ListView listview;
	ArrayList<HashMap<String, String>> gradeList;
	
	private static String url_all_results = "http://justfortestbit302.site40.net/get_all_results.php";
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_GRADES = "grades";
	private static final String TAG_SUBNAME = "subName";
	private static final String TAG_SUBGRADE = "subGrade";
	JSONArray grades= null;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
          View android = inflater.inflate(R.layout.all_results, container, false);
          
          studentID=getActivity().getIntent().getExtras().getString("studentID");
          listview = (ListView)android.findViewById(R.id.grade_list);
          gradeList = new ArrayList<HashMap<String, String>>();
          
          new LoadAllResults().execute(studentID);
          
          return android;
	}
	
	class LoadAllResults extends AsyncTask<String, String, String> {
		 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading everything about your child. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("studentID", args[0]));
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_results, "POST", params);
 
            // Check your log cat for JSON reponse
            Log.d("All Products: ", json.toString());
 
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    grades = json.getJSONArray(TAG_GRADES);
 
                    // looping through All Products
                    for (int i = 0; i < grades.length(); i++) {
                        JSONObject c = grades.getJSONObject(i);
 
                        // Storing each json item in variable
                        String subName = c.getString(TAG_SUBNAME);
                        String subGrade = c.getString(TAG_SUBGRADE);
 
                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();
 
                        // adding each child node to HashMap key => value
                        map.put(TAG_SUBNAME, "Subject: "+subName);
                        map.put(TAG_SUBGRADE, "Grade: "+subGrade);
 
                        // adding HashList to ArrayList
                        gradeList.add(map);
                    }
                } else {
                    // no products found
                    // Launch Add New product Activity
                    
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
 
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    SimpleAdapter adapter = new SimpleAdapter(
                            getActivity(), gradeList,
                            R.layout.selected_grade, new String[] { TAG_SUBNAME,
                            		TAG_SUBGRADE},
                            new int[] { R.id.subName, R.id.subGrade});
                    // updating listview
                    listview.setAdapter(adapter);
                }
            });
        }
 
    }
	
	
}