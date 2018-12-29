package com.suhrid.simplified.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.suhrid.simplified.R;
import com.suhrid.simplified.app.AppConfig;
import com.suhrid.simplified.app.AppController;
import com.suhrid.simplified.helper.SQLiteHandler;
import com.suhrid.simplified.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.suhrid.simplified.app.AppController.TAG;

public class EmpManDash extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout tDrawerLayout;
    private ActionBarDrawerToggle tToggle;
    private SQLiteHandler db;
    private SessionManager session;
    private List<String> data = new ArrayList<String>();
    private ProgressDialog pDialog;
    private int i;
    private ImageView tool;
    private RecyclerView recyclerView1;
    private JSONArray data1;
    private TextView fname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_man_dash);

        tDrawerLayout = (DrawerLayout) findViewById(R.id.adrawerlayout);
        tToggle = new ActionBarDrawerToggle(this, tDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        tDrawerLayout.addDrawerListener(tToggle);
        tToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigation = (NavigationView) findViewById(R.id.snavigation);
        navigation.setNavigationItemSelectedListener(this);
        Menu nav=navigation.getMenu();
        nav.findItem(R.id.addordersem).setVisible(false);

        session = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        db = new SQLiteHandler(getApplicationContext());
        fname = (TextView) findViewById(R.id.welcome);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Ordinary.ttf");
        fname.setTypeface(typeface);
        fname.setText("Welcome Manager");
        recyclerView1 = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView1.setHasFixedSize(true);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));

        getdeatils();
    }

    private void getdeatils() {



        String tag_string_req = "req_login";
        pDialog.setMessage("Fetching Pending Orders...");
        showDialog();
        data.clear();
        StringRequest strReq = new StringRequest( "http://suhrid1theinceptor.000webhostapp.com/userlogin/pending_orders_api.php",new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                //  inputSearch.setVisibility(View.VISIBLE);
                //tool.setVisibility(View.VISIBLE);
                Log.i("tagconvertstr", "[" + response + "]");
                hideDialog();
                Log.d(TAG, "Attend Response: " + response);
                try {


                    JSONObject jObj = new JSONObject(response);

                    data1 = jObj.getJSONArray("name");
                    for (i = 0; i < data1.length(); i++) {
                        try {
                            JSONObject json = data1.getJSONObject(i);
                            String o_id = json.getString("oid");
                            data.add(o_id);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    EmpManDash.ProductAdapter adapter = new EmpManDash.ProductAdapter(EmpManDash.this, data);


                    recyclerView1.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }



        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                //Log.e(TAG, "Login Err: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Fill all the details", Toast.LENGTH_LONG).show();

            }
        }); /*{

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                //String clas="X";
                //String secs="A";
                // String date="03/05/18";
                params.put("eid", u_id);
                params.put("date", date);
                // params.put("h_date", date);
                return params;
            }
        };*/

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }



    public void checkNetworkConnection () {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    public boolean isNetworkConnectionAvailable () {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        if (isConnected) {
            Log.d("Network", "Connected");
            return true;
        } else {
            checkNetworkConnection();
            Log.d("Network", "Not Connected");
            return false;
        }
    }

    private String getid(int position) {
        String id = "";
        try {
            //Getting object of given index
            JSONObject json = data1.getJSONObject(position);

            //Fetching name from that object
            id = json.getString("oid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return id;
    }
    private String getcust_id(int position) {
        String cid = "";
        try {
            //Getting object of given index
            JSONObject json = data1.getJSONObject(position);

            //Fetching name from that object
            cid = json.getString("cid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return cid;
    }
    private String getcust_name(int position) {
        String cname = "";
        try {
            //Getting object of given index
            JSONObject json = data1.getJSONObject(position);

            //Fetching name from that object
            cname = json.getString("customername");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return cname;
    }
    private String getemp_name(int position) {
        String cname = "";
        try {
            //Getting object of given index
            JSONObject json = data1.getJSONObject(position);

            //Fetching name from that object
            cname = json.getString("emp_initiated");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return cname;
    }
    private String getamount(int position) {
        String am = "";
        try {
            //Getting object of given index
            JSONObject json = data1.getJSONObject(position);

            //Fetching name from that object
            am = json.getString("amount");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return am;
    }

    private String getname(int position) {
        String name = "";
        try {
            //Getting object of given index
            JSONObject json = data1.getJSONObject(position);

            //Fetching name from that object
            name = json.getString("order_desc");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return name;
    }



    public class ProductAdapter extends RecyclerView.Adapter<EmpManDash.ProductAdapter.ProductViewHolder> {


        //this context we will use to inflate the layout
        private Context mCtx;

        //we are storing all the products in a list
        private List<String> productList;

        //getting the context and product list with constructor
        public ProductAdapter(Context mCtx, List<String> productList) {
            //super(context, resource, objects);
            this.mCtx = mCtx;
            this.productList = productList;
        }


        @Override
        public EmpManDash.ProductAdapter.ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //inflating and returning our view holder
            LayoutInflater inflater = LayoutInflater.from(mCtx);
            View view = inflater.inflate(R.layout.list_item2, null);
            return new EmpManDash.ProductAdapter.ProductViewHolder(view);
        }

        @Override
        public void onBindViewHolder(EmpManDash.ProductAdapter.ProductViewHolder holder, final int position) {
            //getting the product of the specified position
            //Product product = productList.get(position);


            //binding the data with the viewholder views
            SpannableString content = new SpannableString("\u2022 " + getname(position));
            content.setSpan(new UnderlineSpan(), 2, content.length(), 0);

            holder.textViewTitle.setText(
                    content);
            holder.textViewShortDesc.setText("Company: "+getcust_name(position)+" "+ "Added by Employee: "+ getemp_name(position));
            // holder.textViewRating.setText(String.valueOf(product.getRating()));
            holder.textViewPrice.setText("Amount: "+ getamount(position));

            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        pDialog.setMessage("Approving..");
                        showDialog();
                        approve("1");
                    }
                    else{
                        pDialog.setMessage("Approve Cancellation..");
                        showDialog();
                        approve("0");
                    }
                }

                public void approve(final String isapprove) {

                    String tag_string_req = "req_login";
                    //pDialog.setMessage("Attendance Changed for " + getname(position));
                    //showDialog();

                    StringRequest strReq = new StringRequest(Request.Method.POST,
                            "http://suhrid1theinceptor.000webhostapp.com/userlogin/emporder_approve_api.php", new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            hideDialog();
                            //  Toast.makeText(getApplicationContext(), SharedPreference.getInstance(getApplicationContext()).getToken(), Toast.LENGTH_LONG).show();

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Log.e(TAG, "Login Err: " + error.getMessage());
                            Toast.makeText(getApplicationContext(),
                                    error.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    }) {

                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            // Posting parameters to login url
                            Map<String, String> params = new HashMap<>();
                            //String h_class = "X";
                            //String notif = "2";
                            //String h_date = "26/03/18";
                            params.put("isapproved", isapprove);
                            params.put("order_desc", getname(position));
                            params.put("cust_name", getcust_name(position));
                            params.put("emp_name", getemp_name(position));
                            return params;
                        }
                    };

                    // Adding request to request queue
                    AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
                }
            });
          /*  holder.textViewTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(), "Assign Orders for " + getid(position), Toast.LENGTH_LONG).show();

                    Intent i = new Intent(EmpManDash.this, AssignOrder.class);
                    Bundle bundle = new Bundle();
                    //Add your data from getFactualResults method to bundle
                    bundle.putString("ORDER_ID", getid(position));
                    bundle.putString("ORDER_NAME", getname(position));
                    bundle.putString("CUSTOMER_ID", getcust_id(position));
                    bundle.putString("CUSTOMER_NAME", getcust_name(position));
                    //Add the bundle to the intent
                    i.putExtras(bundle);

                    //Fire the second activity
                    startActivity(i);
                }
            });*/
            //holder.imageView.setImageDrawable(;

        }


        @Override
        public int getItemCount() {
            return productList.size();
        }


        class ProductViewHolder extends RecyclerView.ViewHolder {

            TextView textViewTitle, textViewShortDesc, textViewRating, textViewPrice;
            ImageView imageView;
            CheckBox checkBox;

            public ProductViewHolder(View itemView) {
                super(itemView);

                textViewTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
                textViewShortDesc = (TextView) itemView.findViewById(R.id.textViewShortDesc);
                //textViewRating = itemView.findViewById(R.id.textViewRating);
                textViewPrice = (TextView) itemView.findViewById(R.id.textViewPrice);
                checkBox = (CheckBox) itemView.findViewById(R.id.check);
                // imageView = (ImageView)itemView.findViewById(R.id.imageView);
            }
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        item.setChecked(true);

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
                                Intent intent = new Intent(EmpManDash.this, LoginActivity.class);
                                startActivity(intent);
                                finish();

                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;

            case R.id.tprofile:

                // Launching the login activity
                Intent ipr = new Intent(EmpManDash.this, EmpProfile.class);
                startActivity(ipr);
                return true;

            case R.id.addordersem:
                // Launching the login activity
                Intent i = new Intent(EmpManDash.this, AddOrderSem.class);
                startActivity(i);
                return true;

            case R.id.invoice:
                Toast.makeText(getBaseContext(), "Invoice Coming Soon", Toast.LENGTH_LONG).show();


            default:
                tDrawerLayout =(DrawerLayout)findViewById(R.id.adrawerlayout);
                tDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
        }

    }


    @Override
    public void onBackPressed() {

        if (this.tDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.tDrawerLayout.closeDrawer(GravityCompat.START);
            new android.app.AlertDialog.Builder(this)
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
        } else {
            this.tDrawerLayout.closeDrawer(GravityCompat.START);
            new android.app.AlertDialog.Builder(this)
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




 private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}



