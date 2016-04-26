package com.skyuma.myfragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    ListView listView;
    private View mProgressView;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView =  inflater.inflate(R.layout.fragment_home, container, false);

        //GPSDBManager gpsdbManager = new GPSDBManager(getActivity());
        //final ArrayList<GPSActivity> gpsActivities = gpsdbManager.getActivities();

        listView = (ListView) rootView.findViewById(R.id.mobile_list);
        mProgressView = rootView.findViewById(R.id.home_progress);

        showProgress(true);
        MyTask mAuthTask = new MyTask();
        mAuthTask.execute((Void) null);
        /*ActivityAdapter adapter = new ActivityAdapter(getActivity(), gpsActivities);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GPSActivity gpsActivity = gpsActivities.get(position);
                Intent intent = new Intent(getActivity(), DetailMapActivity.class);
                intent.putExtra("name", gpsActivity.getName());
                intent.putExtra("datetime", gpsActivity.get_datetime());
                intent.putExtra("timezone", gpsActivity.get_timezone());
                getActivity().startActivity(intent);
            }
        });*/
        return rootView;
    }

    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
    public class MyTask extends AsyncTask <Void, Void, String>{

        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            try {
                // Simulate network access.
                Thread.sleep(2000);
                String strurl = "http://115.159.188.64:8080/activity/read_activity_list";
                URL url = new URL(strurl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(3000);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setUseCaches(false);
                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                OutputStream outputStream = httpURLConnection.getOutputStream();
                int response = httpURLConnection.getResponseCode();
                if (response == httpURLConnection.HTTP_OK){
                    InputStream inputStream = httpURLConnection.getInputStream();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    byte[] value = new byte[1024];
                    int len = 0;
                    try{
                        while((len = inputStream.read(value)) != -1){
                            byteArrayOutputStream.write(value, 0, len);
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    result = new String(byteArrayOutputStream.toByteArray());
                    //System.out.println(result);
                }
            }catch (IOException e){
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            showProgress(false);
            try {
                JSONArray jsonArray = new JSONArray(s);
                System.out.println(jsonArray.toString());
                final ArrayList<GPSActivity> gpsActivities = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++){
                    JSONArray jsonArray1 = jsonArray.getJSONArray(i);
                    GPSActivity gpsActivity = new GPSActivity();
                    gpsActivity.setName(jsonArray1.getString(1));
                    gpsActivity.set_datetime(jsonArray1.getLong(2));
                    gpsActivity.set_timezone(jsonArray1.getString(3));
                    gpsActivities.add(gpsActivity);
                }
                ActivityAdapter adapter = new ActivityAdapter(getActivity(), gpsActivities);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        GPSActivity gpsActivity = gpsActivities.get(position);
                        Intent intent = new Intent(getActivity(), DetailMapActivity.class);
                        intent.putExtra("name", gpsActivity.getName());
                        intent.putExtra("datetime", gpsActivity.get_datetime());
                        intent.putExtra("timezone", gpsActivity.get_timezone());
                        getActivity().startActivity(intent);
                    }
                });
                adapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            showProgress(false);
        }
    }
}
