package com.suhrid.simplified.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.suhrid.simplified.app.AppController.TAG;

public class AddOrderSem extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    EditText edit_eid;
    EditText edit_username;
    EditText edit_email;
    EditText edit_pass;
    EditText edit_phnno;
    EditText edit_dob;
    EditText edit_isadmin;
    EditText edit_quanavailable;
    EditText edit_quantity,prevamount;
    Button btn_sign,btn_later,btn_add,btn_sub;
    private SQLiteHandler db;
    private SessionManager session;
    private ProgressDialog pDialog;
    Button btn_login;
    private JSONArray data,data1,data2;
    private String eid,email,tm,u_id;
    private DrawerLayout sDrawerLayout;
    String oid,cid,cname,oname ;
    private ActionBarDrawerToggle sToggle;
    public Spinner pidspinner,cidspinner,taxspinner;
    int sellingprice,taxrate,tax2,avquan;
    private ArrayList<String> product,custid,taxlist;
    private int c;
// private static final String REGISTER_URL="http://suhrid1theinceptor.000webhostapp.com/userlogin/register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_orders_sem);
        sDrawerLayout = (DrawerLayout) findViewById(R.id.adrawerlayout);
        sToggle = new ActionBarDrawerToggle(this, sDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        sDrawerLayout.addDrawerListener(sToggle);
        sToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = new SQLiteHandler(getApplicationContext());
        isNetworkConnectionAvailable();
        final HashMap<String, String> user = db.getUserDetails();
        product = new ArrayList<String>();
        product.add("Order List");
        custid = new ArrayList<String>();
        custid.add("Customer List");
        taxlist = new ArrayList<String>();
        taxlist.add("Tax List");
        //Initializing Spinner
        pidspinner = (Spinner) findViewById(R.id.eidspin);
        cidspinner = (Spinner) findViewById(R.id.cidspinner);
        taxspinner = (Spinner) findViewById(R.id.taxspin);
        // pidspinner.setAdapter(adapter2);
        // session manager
        session = new SessionManager(getApplicationContext());
        u_id = user.get("u_id");
        NavigationView navigation = (NavigationView) findViewById(R.id.snavigation);
        navigation.setNavigationItemSelectedListener(this);
        prevamount = (EditText) findViewById(R.id.amount);
        prevamount.setTextColor(Color.WHITE);
        edit_username = (EditText) findViewById(R.id.id_username);
        edit_email = (EditText) findViewById(R.id.id_email);
        edit_pass = (EditText) findViewById(R.id.id_pass);
        edit_quanavailable=(EditText)findViewById(R.id.id_quanavailable);
        edit_quanavailable.setTextColor(Color.WHITE);
        edit_quantity=(EditText)findViewById(R.id.id_quantity);
        /*edit_dob = (EditText) findViewById(R.id.id_dob);
        edit_phnno = (EditText) findViewById(R.id.id_phone);
        edit_isadmin = (EditText) findViewById(R.id.id_isadmin);*/
        btn_sign = (Button) findViewById(R.id.btn_sign);
       // btn_later=(Button)findViewById(R.id.btn_later);
        btn_add = (Button) findViewById(R.id.plus1);
        btn_sub=(Button)findViewById(R.id.minus1);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        db = new SQLiteHandler(getApplicationContext());


        edit_quantity.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if((event.getAction()==KeyEvent.ACTION_DOWN) && (keyCode==KeyEvent.KEYCODE_ENTER)|| (keyCode==KeyEvent.KEYCODE_ENTER)){
                    c=Integer.parseInt(edit_quantity.getText().toString());
                    return true;
                }
                return false;
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c++;
                if ((c==avquan)){
                    btn_add.setEnabled(false);
                    Toast.makeText(getApplicationContext(),
                            "Quantity not available", Toast.LENGTH_LONG).show();
                }
                btn_sub.setEnabled(true);
                // btn_add.setEnabled(true);
                edit_quantity.setText(""+c);
                String am= String.valueOf(c*sellingprice);
                tax2=c * sellingprice;
                prevamount.setText("Amount:  "+am);
            }
        });

        btn_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c--;
                if(c<=0){
                    btn_sub.setEnabled(false);
                    Toast.makeText(getApplicationContext(),
                            "Wrong input", Toast.LENGTH_LONG).show();
                }
                btn_add.setEnabled(true);
                // btn_sub.setEnabled(true);
                edit_quantity.setText( ""+c);
                String am= String.valueOf(c*sellingprice);
                tax2=c * sellingprice;
                prevamount.setText("Amount:  "+am);
            }
        });

        pidspinner.setOnItemSelectedListener(new pidclass());
        cidspinner.setOnItemSelectedListener(new cidclass());
        taxspinner.setOnItemSelectedListener(new taxclass());
        String tag_string_req = "req_login";
        //pDialog.setMessage("Adding Order...");
        //showDialog();
        String url = "https://simplifiedsolutions.in/test-billing/api/products";
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.i("tagconvertstr", "[" + response + "]");
                Log.d(TAG, " Response: " + response);
                // hideDialog();
                //Toast.makeText(getApplicationContext(),"Order added", Toast.LENGTH_LONG).show();
                try {
                    JSONObject jObj = new JSONObject(response);
                    data = jObj.getJSONArray("data");

                    for (int i = 0; i < data.length(); i++) {
                        try {
                            //Getting json object
                            JSONObject json = data.getJSONObject(i);

                            //Adding the name of the student to array list
                            product.add(json.getString("name"));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(AddOrderSem.this,
                            android.R.layout.simple_spinner_dropdown_item, product) {
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
                    pidspinner.setAdapter(adapter2);


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
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

        String tag_string_req3 = "req_login";
        //pDialog.setMessage("Adding Order...");
        //showDialog();
        String url3 = "https://simplifiedsolutions.in/test-billing/api/taxes";
        StringRequest strReq3 = new StringRequest(Request.Method.GET,
                url3, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.i("tagconvertstr", "[" + response + "]");
                Log.d(TAG, " Response: " + response);
                // hideDialog();
                //Toast.makeText(getApplicationContext(),"Order added", Toast.LENGTH_LONG).show();
                try {
                    JSONObject jObj = new JSONObject(response);
                    data2 = jObj.getJSONArray("data");

                    for (int i = 0; i < data2.length(); i++) {
                        try {
                            //Getting json object
                            JSONObject json = data2.getJSONObject(i);

                            //Adding the name of the student to array list
                            taxlist.add(json.getString("rate"));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(AddOrderSem.this,
                            android.R.layout.simple_spinner_dropdown_item, taxlist) {
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
                    taxspinner.setAdapter(adapter2);


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
        AppController.getInstance().addToRequestQueue(strReq3, tag_string_req3);

        String tag_string_req2 = "req_login";
        //pDialog.setMessage("Adding Order...");
        //showDialog();
        String url2 = "http://suhrid1theinceptor.000webhostapp.com/userlogin/customer_details_api.php";
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
                            custid.add(json.getString("customername"));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddOrderSem.this,
                            android.R.layout.simple_spinner_dropdown_item, custid) {
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
                    cidspinner.setAdapter(adapter);


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

        btn_sign.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                register();
            }
        });

    }

    //private void registerUser() {

    //   register(oid, username, email, password);
    //}

    class pidclass implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            edit_username.setText(getdesc(position - 1));
            oname=parent.getSelectedItem().toString();
            oid=getorderid(position-1);
            //edit_eid.setText(getid(position-1));
            avquan=getquantity(position-1);
            edit_quanavailable.setText("Available: "+ getquantity(position-1));
            sellingprice = getsellingprice(position - 1);
            String am = String.valueOf(c * sellingprice);
            tax2=c * sellingprice;
            prevamount.setText("Amount: "+ am);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    class cidclass implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            cname=parent.getSelectedItem().toString();
            cid=getcust(position-1);

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    class taxclass implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            //edit_username.setText(getdesc(position - 1));
            taxrate= gettax(position-1);

            //oid=getorderid(position-1);
            //edit_eid.setText(getid(position-1));
            //edit_quanavailable.setText("Available: "+ getquantity(position-1));
            //sellingprice = getsellingprice(position - 1);
            tm = String.valueOf(tax2+((taxrate * tax2)/100) );
            edit_pass.setText("Total Amount:  "+ tm);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private int gettax(int position) {
        int desc=0 ;

        try {
            //Getting json object
            //JSONObject json = data1.getJSONObject(i);

            JSONObject json1 = data2.getJSONObject(position);

            desc = json1.getInt("rate");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return desc;
    }

    private String getcust(int position) {
        String desc="" ;

        try {
            //Getting json object
            //JSONObject json = data1.getJSONObject(i);

            JSONObject json1 = data1.getJSONObject(position);

            desc = json1.getString("cid");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return desc;
    }

    private String getorderid(int position) {
        String desc="" ;

        try {
            //Getting json object
            //JSONObject json = data1.getJSONObject(i);

            JSONObject json1 = data.getJSONObject(position);

            desc = json1.getString("id");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return desc;
    }

    private int getsellingprice(int position) {
        int desc =0;

        try {
            //Getting json object
            //JSONObject json = data1.getJSONObject(i);

            JSONObject json1 = data.getJSONObject(position);

            desc = json1.getInt("sell_price");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return desc;
    }
    private String getdesc(int position) {
        String desc = "";

        try {
            //Getting json object
            //JSONObject json = data1.getJSONObject(i);

            JSONObject json1 = data.getJSONObject(position);

            desc = json1.getString("brand");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return desc;
    }


    private String getid(int position) {
        String desc = "";

        try {
            //Getting json object
            //JSONObject json = data1.getJSONObject(i);

            JSONObject json1 = data.getJSONObject(position);

            desc = json1.getString("id");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return desc;
    }

    private int getquantity(int position) {
        int desc = 0;

        try {
            //Getting json object
            //JSONObject json = data1.getJSONObject(i);

            JSONObject json1 = data.getJSONObject(position);

            desc = json1.getInt("quantity");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return desc;
    }



    private void register(){

        if(oname.equalsIgnoreCase("Order List") || cname.equalsIgnoreCase("Customer List") || String.valueOf(taxrate).equalsIgnoreCase("Tax List")){

            Toast.makeText(getApplicationContext(),
                    "Please fill all the values", Toast.LENGTH_LONG).show();

        }
        else {

            final String username = edit_username.getText().toString().trim();
            //final String email = edit_email.getText().toString().trim().toLowerCase();
            final String password = edit_pass.getText().toString().trim().toLowerCase();
            String tag_string_req = "req_login";
            pDialog.setMessage("Adding Order...");
            showDialog();
            String url = "http://suhrid1theinceptor.000webhostapp.com/userlogin/add_order_api.php";
            StringRequest strReq = new StringRequest(Request.Method.POST,
                    url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.i("tagconvertstr", "[" + response + "]");
                    Log.d(TAG, " Response: " + response);
                    hideDialog();
                    Toast.makeText(getApplicationContext(),
                            "Order added successfully", Toast.LENGTH_LONG).show();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, " Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),
                            "Wrong Credentials!", Toast.LENGTH_LONG).show();
                    hideDialog();
                }
            })

            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    // Posting parameters to login url
                    Map<String, String> params = new HashMap<>();
                    params.put("oid", oid);
                    params.put("order_desc", username);
                    params.put("cid", cid);
                    params.put("customername", cname);
                    params.put("amount", tm);
                    params.put("isapproved","0");
                    params.put("emp_initiated",u_id);
                    //params.put("token", SharedPreference.getInstance(getApplicationContext()).getToken());
                    return params;
                }
            };

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

        } }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(sToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                                Intent intent = new Intent(AddOrderSem.this, LoginActivity.class);
                                startActivity(intent);
                                finish();

                            }

                        })
                        .setNegativeButton("No", null)
                        .show();

                // Launching the login activity

                return true;

            case R.id.addordersem:

                // Launching the login activity
                Intent i = new Intent(AddOrderSem.this, AddOrderSem.class);
                startActivity(i);
                return true;

            case R.id.dashboard:

                // Launching the login activity
                Intent id = new Intent(AddOrderSem.this, EmpSalesDash.class);
                startActivity(id);
                return true;

            case R.id.tprofile:

                // Launching the login activity
                Intent ip = new Intent(AddOrderSem.this, EmpProfile.class);
                startActivity(ip);
                return true;
            default:
                sDrawerLayout =(DrawerLayout)findViewById(R.id.adrawerlayout);
                sDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
        }

    }
    @Override
    public void onBackPressed() {
        if (this.sDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.sDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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





}
