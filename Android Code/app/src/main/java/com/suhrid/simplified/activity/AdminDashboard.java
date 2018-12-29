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

public class AdminDashboard extends AppCompatActivity implements View.OnClickListener,NavigationView.OnNavigationItemSelectedListener {
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
    private ArrayList<String> students;
    private int status1;
    public String u_id, eid, cid;
    private ProgressDialog pDialog;
    //JSON Array
    private EditText datepicktd,amount;
    private JSONArray data;
    public MaterialSpinner eidspinner;
    public MaterialSpinner cidspinner;
    private Button assignvisit;

    private DatePickerDialog datePickerDialog3;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admindashboard);
        sDrawerLayout = (DrawerLayout) findViewById(R.id.adrawerlayout);
        sToggle = new ActionBarDrawerToggle(this, sDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        sDrawerLayout.addDrawerListener(sToggle);
        sToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Toast.makeText(getApplicationContext(), "Welcome admin", Toast.LENGTH_LONG).show();
        NavigationView navigation = (NavigationView) findViewById(R.id.snavigation);
        navigation.setNavigationItemSelectedListener(this);
       // navigation.setItemBackgroundResource(R.color.red);


        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        assignvisit=(Button)findViewById(R.id.btnasignvisit);

        fname = (TextView) findViewById(R.id.welcome);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Ordinary.ttf");
        fname.setTypeface(typeface);
        fname.setText("WELCOME ADMIN");


        //sec1=(TextView)findViewById(R.id.section);
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        isNetworkConnectionAvailable();
        final HashMap<String, String> user = db.getUserDetails();


        // session manager
        session = new SessionManager(getApplicationContext());
        u_id = user.get("u_id");

//Initializing the ArrayList
        students = new ArrayList<String>();

        //Initializing Spinner
        eidspinner = (MaterialSpinner) findViewById(R.id.eidspin);
        cidspinner = (MaterialSpinner) findViewById(R.id.cidspin);
        datepicktd = (EditText) findViewById(R.id.datePickertd);
        amount = (EditText) findViewById(R.id.oid);
        //Adding an Item Selected Listener to our Spinner
        //As we have implemented the class Spinner.OnItemSelectedListener to this class iteself we are passing this to setOnItemSelectedListener
        //eidspinner.setOnItemSelectedListener(this);
        //cidspinner.setOnItemSelectedListener(this);
        //datespinner.setOnItemSelectedListener(this);







        setViewActions();

        prepareDatePickerDialog();

        String[] arraySpinner1 = new String[]{
                "Select Employee", "e01", "e02","e04"
        };

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, arraySpinner1) {
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

        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eidspinner.setAdapter(adapter1);


        String[] arraySpinner2 = new String[]{
                "Select Customer", "c01", "c02", "c03", "c04"
        };
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, arraySpinner2) {
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

        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cidspinner.setAdapter(adapter2);

        eidspinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                eid = item;

            }

        });
        cidspinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                cid = item;

            }

        });
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

        String amount1 = amount.getText().toString().trim();
        studentattendance(eid,cid,date,amount1);
    }


    private void studentattendance(final String eid,final String cid,final String date,final String amount1) {



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
                params.put("oid",amount1);
                //params.put("token", SharedPreference.getInstance(getApplicationContext()).getToken());
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(sToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {

        if (this.sDrawerLayout.isDrawerOpen(GravityCompat.START))
        {
            this.sDrawerLayout.closeDrawer(GravityCompat.START);
            new AlertDialog.Builder(this)
                   // .setIcon(R.drawable.dialog_warning)
                    .setTitle("Closing ATTENDR")
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent a = new Intent(Intent.ACTION_MAIN);
                            a.addCategory(Intent.CATEGORY_HOME);
                            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(a);

                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        } else
        {
            this.sDrawerLayout.closeDrawer(GravityCompat.START);
            new AlertDialog.Builder(this)
                    //.setIcon(R.drawable.dialog_warning)
                    .setTitle("Closing App")
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent a = new Intent(Intent.ACTION_MAIN);
                            a.addCategory(Intent.CATEGORY_HOME);
                            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(a);

                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        }

    }
    public void checkNetworkConnection(){
        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        builder.setTitle("No internet Connection");
        builder.setMessage("Please turn on internet connection to continue");
        builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public boolean isNetworkConnectionAvailable(){
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        if(isConnected) {
            Log.d("Network", "Connected");
            return true;
        }
        else{
            checkNetworkConnection();
            Log.d("Network","Not Connected");
            return false;
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.logout:

                session.setLogin(false);

                db.deleteUsers();
                new android.support.v7.app.AlertDialog.Builder(this)
                        //  .setIcon(R.drawable.dialog_warning)
                        .setTitle("Logging Out")
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(AdminDashboard.this, LoginActivity.class);
                                startActivity(intent);
                                finish();

                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;

            case R.id.tprofile:

                // Launching the login activity
                Intent ipr = new Intent(AdminDashboard.this, AdminProfile.class);
                startActivity(ipr);
                return true;

            case R.id.addemployee:
                // Launching the login activity
                Intent i = new Intent(AdminDashboard.this, AddEmployee.class);
                startActivity(i);
                return true;
            case R.id.viewemployee:

                // Launching the login activity
                Intent ih = new Intent(AdminDashboard.this, ViewEmployee.class);
                startActivity(ih);
                return true;

            case R.id.assignorder:

                // Launching the login activity
                Intent ic = new Intent(AdminDashboard.this, AssignedOrders.class);
                startActivity(ic);
                return true;


            case R.id.pendingorder:
                // Launching the login activity
                Intent ip = new Intent(AdminDashboard.this, PendingOrders.class);
                startActivity(ip);
                return true;

            case R.id.gpsinfo:
                // Launching the login activity
                Intent ig = new Intent(AdminDashboard.this, MapsActivity.class);
                startActivity(ig);
                return true;

            case R.id.orderhistory:
                // Launching the login activity
                Intent io = new Intent(AdminDashboard.this, OrderHistory.class);
                startActivity(io);
                return true;

            case R.id.dashboard:
                // Launching the login activity
                Intent id = new Intent(AdminDashboard.this, AdminDashboard.class);
                startActivity(id);
                return true;

            case R.id.addorder:
                // Launching the login activity
                Intent iadd = new Intent(AdminDashboard.this, AddOrders.class);
                startActivity(iadd);
                return true;

            case R.id.rememployee:
                // Launching the login activity
                Intent irem = new Intent(AdminDashboard.this, RemoveEmployee.class);
                startActivity(irem);
                return true;


          /*  case R.id.addorder2:
                // Launching the login activity
                Intent ia = new Intent(AdminDashboard.this, AddOrders2.class);
                startActivity(ia);
                return true;*/

            case R.id.addclient:
                // Launching the login activity
                Intent icl = new Intent(AdminDashboard.this, AddCompany.class);
                startActivity(icl);
                return true;

            case R.id.remclient:
                // Launching the login activity
                Intent irc = new Intent(AdminDashboard.this, RemoveCompany.class);
                startActivity(irc);
                return true;

            case R.id.viewclient:
                // Launching the login activity
                Intent iv = new Intent(AdminDashboard.this, ViewCustomer.class);
                startActivity(iv);
                return true;



            default:
                sDrawerLayout =(DrawerLayout)findViewById(R.id.adrawerlayout);
                sDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
        }
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
