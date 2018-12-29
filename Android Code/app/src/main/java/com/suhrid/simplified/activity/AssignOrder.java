package com.suhrid.simplified.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.suhrid.simplified.R;
import com.suhrid.simplified.app.AppController;
import com.suhrid.simplified.helper.SQLiteHandler;
import com.suhrid.simplified.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.suhrid.simplified.app.AppController.TAG;

public class AssignOrder extends BaseActivity implements View.OnClickListener {
    public String date;
    private EditText dateet;
    private SQLiteHandler db;
    private SessionManager session;
    private TextView fname;
    private TextView s_in;
    private TextView s_out;
    private DatePickerDialog datePickerDialog;
    private DrawerLayout sDrawerLayout;
    private ActionBarDrawerToggle sToggle;
    //An ArrayList for Spinner Items
    private ArrayList<String> eidlist;
    private int status1;
    String u_id, eid, c_iid,o_iid,o_name,c_name;
    private ProgressDialog pDialog;
    //JSON Array
    private EditText datepicktd,amount,cid;
    private JSONArray data1;

    public MaterialSpinner eidspinner;
    //public MaterialSpinner cidspinner;
    private Button assignvisit;

    private DatePickerDialog datePickerDialog3;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentOfView(R.layout.activity_assignorder,this);
        sDrawerLayout = (DrawerLayout) findViewById(R.id.adrawerlayout);
        sToggle = new ActionBarDrawerToggle(this, sDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        sDrawerLayout.addDrawerListener(sToggle);
        sToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigation = (NavigationView) findViewById(R.id.snavigation);
        navigation.setNavigationItemSelectedListener(this);


        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        assignvisit=(Button)findViewById(R.id.btnasignvisit);

        Bundle bundle = getIntent().getExtras();

        //Extract the data…
         o_iid = bundle.getString("ORDER_ID");
         o_name = bundle.getString("ORDER_NAME");
         c_iid = bundle.getString("CUSTOMER_ID");
         c_name = bundle.getString("CUSTOMER_NAME");


        fname = (TextView) findViewById(R.id.welcome);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Ordinary.ttf");
        fname.setTypeface(typeface);
        fname.setText("ASSIGN ORDER");


        //sec1=(TextView)findViewById(R.id.section);
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        isNetworkConnectionAvailable();
        final HashMap<String, String> user = db.getUserDetails();


        // session manager
        session = new SessionManager(getApplicationContext());
        u_id = user.get("u_id");

//Initializing the ArrayList
        eidlist = new ArrayList<String>();
        eidlist.add("Select Employee");

        //Initializing Spinner
        eidspinner = (MaterialSpinner) findViewById(R.id.eidspin);
        cid = (EditText) findViewById(R.id.cid);
        datepicktd = (EditText) findViewById(R.id.datePickertd);
        amount = (EditText) findViewById(R.id.oid);
        amount.setText(o_name);
        cid.setText(c_name);
        //Adding an Item Selected Listener to our Spinner
        //As we have implemented the class Spinner.OnItemSelectedListener to this class iteself we are passing this to setOnItemSelectedListener
        //eidspinner.setOnItemSelectedListener(this);
        //cidspinner.setOnItemSelectedListener(this);
        //datespinner.setOnItemSelectedListener(this);



        setViewActions();

        prepareDatePickerDialog();
        String tag_string_req2 = "req_login";
        //pDialog.setMessage("Adding Order...");
        //showDialog();
        String url2 = "http://suhrid1theinceptor.000webhostapp.com/userlogin/employee_list_api.php";
        StringRequest strReq2 = new StringRequest(Request.Method.GET,
                url2, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.i("tagconvertstr", "[" + response + "]");
                Log.d(TAG, " Response: " + response);
                // hideDialog();
                //Toast.makeText(getApplicationContext(),"Order added", Toast.LENGTH_LONG).show();
                try {
                    JSONObject jObj = new JSONObject(response);
                    data1 = jObj.getJSONArray("respond");

                    for (int i = 0; i < data1.length(); i++) {
                        try {
                            //Getting json object
                            JSONObject json = data1.getJSONObject(i);

                            //Adding the name of the student to array list
                            eidlist.add(json.getString("ename"));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(AssignOrder.this,
                            android.R.layout.simple_spinner_dropdown_item, eidlist) {
                        @Override
                        public boolean isEnabled(int position) {
                            if (position == 0) {
                                // Disable the first item from Spinner
                                // First item will be use for hint
                                return false;
                            } else {
                                return true;
                            }
                        }

                        @Override
                        public View getDropDownView(int position, View convertView,
                                                    ViewGroup parent) {
                            View view = super.getDropDownView(position, convertView, parent);
                            TextView tv = (TextView) view;
                            if (position == 0) {
                                // Set the hint text color gray
                                tv.setTextColor(Color.GRAY);
                            } else {
                                tv.setTextColor(Color.BLACK);
                            }
                            return view;
                        }
                    };

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    eidspinner.setAdapter(adapter);


                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, " Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Wrong Credentials!", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {


        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq2, tag_string_req2);



        eidspinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                eid = getid(position-1);

            }

        });

    }

    private String getid(int position) {
        String desc="" ;

        try {
            //Getting json object
            //JSONObject json = data1.getJSONObject(i);

            JSONObject json1 = data1.getJSONObject(position);

            desc = json1.getString("eid");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return desc;
    }


    private void setViewActions() {
        datepicktd.setOnClickListener(this);
        // submit.setOnClickListener(this);
        //datepickcw.setOnClickListener(this);
    }

    private void prepareDatePickerDialog() {
        //Get current date
        Calendar calendar = Calendar.getInstance();


        //Create datePickerDialog with initial date which is current and decide what happens when a date is selected.
        datePickerDialog3 = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear += 1;
                String mt, dt;
                if (monthOfYear < 10)
                    mt = "0" + monthOfYear;

                else
                    mt = String.valueOf(monthOfYear);
                if (dayOfMonth < 10)
                    dt = "0" + dayOfMonth;

                else
                    dt = String.valueOf(dayOfMonth);
                //When a date is selected, it comes here.
                //Change birthdayEdittext's text and dismiss dialog.
                DateFormat df = new SimpleDateFormat("yyyy");
                //DateFormat dm = new SimpleDateFormat("00");
                String formattedDatey = df.format(Calendar.getInstance().getTime());
                //String formattedDatem = dm.format(Calendar.getInstance().getTime());
                datepicktd.setTextColor(Color.GRAY);
                datepicktd.setText(dt + "/" + mt + "/" + formattedDatey);

                //date1 = mt + "/" + formattedDatey;
                //date = dt + "/" + mt + "/" + formattedDatey;
                date = formattedDatey + "-" + mt + "-" + dt;
                //datePickerDialog.dismiss();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));



    }

    public void onclick2(View view) {


        studentattendance(eid,c_iid,date,o_iid);
    }


    private void studentattendance(final String eid,final String cid,final String date,final String oid) {



        String tag_string_req = "req_login";
        pDialog.setMessage("Assigning...");
        showDialog();
        String url = "http://suhrid1theinceptor.000webhostapp.com/userlogin/daily_visit.php";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.i("tagconvertstr", "[" + response + "]");
                Log.d(TAG, "Loginnnnnnnnnnnnnnn Response: " + response);
                hideDialog();
                Toast.makeText(getApplicationContext(),
                        "Task Assigned successfully for "+ eid, Toast.LENGTH_LONG).show();
                Intent i = new Intent(AssignOrder.this, AssignedOrders.class);
                startActivity(i);
                finish();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Wrong Credentials!", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("eid", eid);
                params.put("cid", cid);
                params.put("date", date);
                params.put("oid",oid);
                //params.put("token", SharedPreference.getInstance(getApplicationContext()).getToken());
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.datePickertd:
                datePickerDialog3.show();
                break;
        }
    }


}
