package com.example.sab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
 
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject; 
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
 
public class ChildrenSchedules extends Fragment {

	private ProgressDialog pDialog;
    //private TextView txtChildrenList;
    String studentID;
    
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
    ListView listview;
    ArrayList<HashMap<String, String>> childrenList;
 
    // url to get all products list
    private static String url_all_products = "http://justfortestbit302.site40.net/get_all_schedules.php";
 
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_SCHEDULES = "schedules";
    private static final String TAG_SCHEDULETIME = "scheduleTime";
    private static final String TAG_SCHEDULEDAY = "scheduleDay";
    private static final String TAG_SUBNAME = "subName";
 
    // products JSONArray
    JSONArray products = null;
	@Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
          View ios = inflater.inflate(R.layout.all_schedules, container, false);
          
          listview = (ListView)ios.findViewById(R.id.country_list);
          childrenList = new ArrayList<HashMap<String, String>>();
         // Bundle b = getActivity().getIntent().getExtras();
          studentID=getActivity().getIntent().getExtras().getString("studentID");
          Log.e("studentid", studentID);
          //studentID = getActivity().getIntent().getExtras().getString("studentID");
          new LoadAllChildren().execute(studentID);
          
          
          return ios;
}
	//if you put this, then absence will load the schedules tabs, look closely
	//hence, when swiping to tab "schedules" it will reload the pDialog again
/*	 public static ChildrenSchedules newInstance(String index) {
		 	 f = new ChildrenSchedules();

	        // Supply index input as an argument.
	        Bundle args = new Bundle();
	        args.putString("studentID", index);
	        f.setArguments(args);

	        return f;
	    }*/


	
	class LoadAllChildren extends AsyncTask<String, String, String> {
		 
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
            JSONObject json = jParser.makeHttpRequest(url_all_products, "POST", params);
 
            // Check your log cat for JSON reponse
            Log.d("All Products: ", json.toString());
 
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    products = json.getJSONArray(TAG_SCHEDULES);
 
                    // looping through All Products
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);
 
                        // Storing each json item in variable
                        String scheTime = c.getString(TAG_SCHEDULETIME);
                        String scheDay = c.getString(TAG_SCHEDULEDAY);
                        String subName = c.getString(TAG_SUBNAME);
 
                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();
 
                        // adding each child node to HashMap key => value
                        map.put(TAG_SCHEDULETIME, "Time: "+scheTime);
                        map.put(TAG_SCHEDULEDAY, "Day: "+scheDay);
                        map.put(TAG_SUBNAME, "Subject: "+subName);
 
                        // adding HashList to ArrayList
                        childrenList.add(map);
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
                            getActivity(), childrenList,
                            R.layout.selected_schedules, new String[] { TAG_SCHEDULETIME,
                            		TAG_SCHEDULEDAY, TAG_SUBNAME},
                            new int[] { R.id.scheduleTime, R.id.scheduleDay, R.id.subName });
                    // updating listview
                    listview.setAdapter(adapter);
                }
            });
        }
 
    }

}


