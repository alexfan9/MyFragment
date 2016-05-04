package com.skyuma.myfragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    TextView textView;
    ListView listView;
    private View mProgressView;
    ActivityAdapter adapter = null;
    GPSDBManager gpsdbManager = null;
    SwipeRefreshLayout swipeRefreshLayout;

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
        gpsdbManager = new GPSDBManager(getActivity());
        textView = (TextView) rootView.findViewById(R.id.textViewRefresh);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                textView.setText("Refreshing...");
                textView.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(true);
                MyDownloadTask myDownloadTask = new MyDownloadTask();
                myDownloadTask.execute((Void) null);
            }
        });
        listView = (ListView) rootView.findViewById(R.id.mobile_list);
        Button btnUpload = (Button) rootView.findViewById(R.id.btnUpload);
        mProgressView = rootView.findViewById(R.id.home_progress);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress(true);
                MyUpLoadTask myUpLoadTask = new MyUpLoadTask();
                myUpLoadTask.execute((Void) null);
            }
        });
        setListViewAdapter();
        return rootView;
    }

    public void setListViewAdapter() {
        final ArrayList<GPSActivity> gpsActivities = gpsdbManager.getActivities();
        if (adapter == null) {
            adapter = new ActivityAdapter(getActivity(), gpsActivities);
        }else {
            adapter.updateAdapterContent(gpsActivities);
        }
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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
    }
    public void updateAdapter() {
        if (adapter == null){
            return;
        }
        final ArrayList<GPSActivity> gpsActivities = gpsdbManager.getActivities();
        adapter.updateAdapterContent(gpsActivities);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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

    @Override
    public void onRefresh() {
        showProgress(true);
        swipeRefreshLayout.setRefreshing(true);
        MyDownloadTask myDownloadTask = new MyDownloadTask();
        myDownloadTask.execute((Void) null);
    }

    public class MyUpLoadTask extends AsyncTask <Void, Void, String>{

        @Override
        protected String doInBackground(Void... params) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String result = "";
            GPSDBManager gpsdbManager = new GPSDBManager(getActivity());
            ArrayList<GPSActivity> gpsActivities = gpsdbManager.getActivities();
            Iterator iterator = gpsActivities.iterator();
            while (iterator.hasNext()){
                GPSActivity gpsActivity = (GPSActivity) iterator.next();
                if (gpsActivity.get_new() == 1) {
                    JSONArray jsonArray = gpsdbManager.getActivityContent(gpsActivity.getName());
                    result = doUpLoad(gpsActivity.getName(),
                            gpsActivity.get_datetime(),
                            "China",
                            jsonArray.toString());
                }
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            showProgress(false);
            if (s.isEmpty() == false) {
                Toast.makeText(getActivity(), "Upload successfully " + s, Toast.LENGTH_SHORT).show();
                GPSDBManager gpsdbManager = new GPSDBManager(getActivity());
                gpsdbManager.setActivityUnRead(s);
                setListViewAdapter();
            }else{
                Toast.makeText(getActivity(), "Upload successfully s is empty", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private String doUpLoad(String name, long date, String zone, String content){
        String result = "OK";
        StringBuffer stringBuffer = new StringBuffer();
        try {
            String strurl = "http://115.159.188.64:8080/activity/upload_activity";
            stringBuffer.append("name").append("=").append(name)
                    .append("&")
                    .append("a_date").append("=").append(date)
                    .append("&")
                    .append("a_zone").append("=").append(zone)
                    .append("&")
                    .append("content").append("=").append(URLEncoder.encode(content, "utf-8"));

            byte[] data = stringBuffer.toString().getBytes();
            URL url = new URL(strurl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(3000);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));

            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(data);

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
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return result;
    }
    public class MyDownloadTask extends AsyncTask <Void, Void, String>{
        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            try {
                // Simulate network access.
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
                }
            }catch (IOException e){
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
                GPSDBManager gpsdbManager = new GPSDBManager(getActivity());
                for (int i = 0; i < jsonArray.length(); i++){
                    JSONArray jsonArray1 = jsonArray.getJSONArray(i);
                    GPSActivity gpsActivity = new GPSActivity();
                    gpsActivity.setName(jsonArray1.getString(1));
                    gpsActivity.set_datetime(jsonArray1.getLong(2));
                    gpsActivity.set_timezone(jsonArray1.getString(3));
                    String content = jsonArray1.getString(4);
                    gpsdbManager.saveActivity(gpsActivity);
                    gpsdbManager.saveActivityContent2(gpsActivity.getName(), content);
                }
                setListViewAdapter();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            swipeRefreshLayout.setRefreshing(false);
            textView.setVisibility(View.GONE);
        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
            showProgress(false);
        }
    }
}
