package com.suhrid.simplified.activity;


import android.app.DatePickerDialog;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.suhrid.simplified.R;
import com.suhrid.simplified.app.AppConfig;
import com.suhrid.simplified.app.AppController;
import com.suhrid.simplified.helper.SQLiteHandler;
import com.suhrid.simplified.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmpSalesDash extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener, MaterialSpinner.OnItemSelectedListener {

    private DrawerLayout tDrawerLayout;
    private ActionBarDrawerToggle tToggle;
    private SQLiteHandler db;
    private SessionManager session;
    public String u_id;
    public TextView listname;
    public TextView listintime;
    public TextView listouttime;
    private List<String> data = new ArrayList<String>();
    private ListView lv;
    private JSONArray data1;
    private JSONArray data2;
    private Switch mySwitch = null;
    private static final String TAG = LoginActivity.class.getSimpleName();
    public String clas, secs, date, date1, date2,time,cust_name;
    private String[] status = new String[10];
    private EditText datepicktd, inputSearch;
    private MaterialSpinner s1, s2;
    private DatePickerDialog datePickerDialog3;
    private Button getattend;
    private ProgressDialog pDialog;
    private int i;
    private ImageView tool;
    private RecyclerView recyclerView;


    ProductAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empsalesdash);

        tDrawerLayout = (DrawerLayout) findViewById(R.id.adrawerlayout);
        tToggle = new ActionBarDrawerToggle(this, tDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        tDrawerLayout.addDrawerListener(tToggle);
        tToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigation = (NavigationView) findViewById(R.id.snavigation);
        navigation.setNavigationItemSelectedListener(this);
        //inputSearch = (EditText) findViewById(R.id.inputSearch);
        // listname = (TextView) findViewById(R.id.list_item_text);
        //listintime = (TextView) findViewById(R.id.intimelist);
        //listouttime = (TextView) findViewById(R.id.outtimelist);
        datepicktd = (EditText) findViewById(R.id.datePickertd);
        getattend = (Button) findViewById(R.id.getattendance);
        //mySwitch = (Switch) findViewById(R.id.switch1);

        //tool=(ImageView) findViewById(R.id.tooltip);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat it = new SimpleDateFormat("HH:mm:ss");
        time = it.format(c.getTime());
        // mySwitch.setOnCheckedChangeListener(TDashboard.this);


        db = new SQLiteHandler(getApplicationContext());

        final HashMap<String, String> user = db.getUserDetails();





        // session manager
        session = new SessionManager(getApplicationContext());
        u_id = user.get("u_id");

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));




        //generateListContent();
        //lv.setAdapter(new MyListAdaper(this, R.layout.list_item, data));
        //Doing the same with this method as we did with getName()



        setViewActions();

        prepareDatePickerDialog();

        isNetworkConnectionAvailable();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date123 = new Date();
        String crdate=formatter.format(date123);
        getattendance(crdate);
        datepicktd.setText(crdate);
        //Toast.makeText(getApplicationContext(), crdate, Toast.LENGTH_LONG).show();
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

                date1 = mt + "/" + formattedDatey;
                date = dt + "/" + mt + "/" + formattedDatey;
                date2 = formattedDatey+"-"+mt+"-"+dt;
                //datePickerDialog.dismiss();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));


    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.datePickertd:
                datePickerDialog3.show();
                break;

        }
    }


    public void onclick2(View view) {
        // Toast.makeText(getApplicationContext(), date2, Toast.LENGTH_LONG).show();
        getattendance(date2);
    }


    public void getattendance(final String date) {

        String tag_string_req = "req_login";
        pDialog.setMessage("Fetching Companies...");
        showDialog();
        data.clear();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_CREATEPWD, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //  inputSearch.setVisibility(View.VISIBLE);
                //tool.setVisibility(View.VISIBLE);
                Log.i("tagconvertstr", "["+response+"]");
                hideDialog();
                Log.d(TAG, "Attend Response: " + response);
                try {


                    JSONObject jObj = new JSONObject(response);

                    data1 = jObj.getJSONArray("respond");
                    data2 = jObj.getJSONArray("name");
                    if (data1.length() == 0) {
                        //inputSearch.setVisibility(View.GONE);
                        //tool.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "No customer allocated for " + date + ". Holiday or Wrong Date Input", Toast.LENGTH_LONG).show();
                        prepareDatePickerDialog();
                    }///String msg="";
                    //String u_id = jObj.getString("uid");


                    for (i = 0; i < data1.length(); i++) {
                        try {
                            //Getting json object
                            //JSONObject json = data1.getJSONObject(i);

                            JSONObject json= data1.getJSONObject(i);
                            String c_id = json.getString("cid");
                            for (int j = 0; j < data2.length(); j++) {

                                JSONObject json1 = data2.getJSONObject(j);


                                String c_id2 = json1.getString("cid");


                                // data.add(json1.getString("customername"));
                                if (c_id.equals(c_id2)) {
                                    cust_name = json1.getString("customername");
                                    data.add(cust_name);
                                }


                                //status[i] = json1.getString("status");
                                //Log.d(TAG, "Array Response: " + status[i]);

                                //Toast.makeText(getApplicationContext(), status, Toast.LENGTH_LONG).show();



                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    ProductAdapter adapter = new ProductAdapter(EmpSalesDash.this, data);
                    recyclerView.setAdapter(adapter);

                } catch(JSONException e)
                {
                    // JSON error
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
        }) {

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
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }



    private String getdesc(int position) {
        String order_desc = "";

        try {
            //Getting json object
            //JSONObject json = data1.getJSONObject(i);
            JSONObject json1 = data1.getJSONObject(position);

            order_desc = json1.getString("order_desc");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return order_desc;
    }

    private String getamount(int position) {
        String amount = "";

        try {
            //Getting json object
            //JSONObject json = data1.getJSONObject(i);
            JSONObject json1 = data1.getJSONObject(position);

            amount = json1.getString("amount");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return amount;
    }

    private String getid(int position) {
        String id = "";
        try {
            //Getting object of given index
            JSONObject json = data1.getJSONObject(position);

            //Fetching name from that object
            id = json.getString("cid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return id;
    }

    private String getname(int position){
        String customername="";

        try {
            //Getting json object
            //JSONObject json = data1.getJSONObject(i);

            JSONObject json= data1.getJSONObject(position);
            String c_id = json.getString("cid");
            for (int j = 0; j < data2.length(); j++) {

                JSONObject json1 = data2.getJSONObject(j);


                String c_id2 = json1.getString("cid");


                // data.add(json1.getString("customername"));
                if (c_id.equals(c_id2)) {
                    customername = json1.getString("customername");
                    //data.add(cust_name);
                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return customername;
    }

    @Override
    public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {

    }

    public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {


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
        public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //inflating and returning our view holder
            LayoutInflater inflater = LayoutInflater.from(mCtx);
            View view = inflater.inflate(R.layout.list_item, null);
            return new ProductViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ProductViewHolder holder, final int position) {
            //getting the product of the specified position
            //Product product = productList.get(position);


            //binding the data with the viewholder views
            SpannableString content = new SpannableString("\u2022 " + getname(position));
            content.setSpan(new UnderlineSpan(), 2, content.length(), 0);

            holder.textViewTitle.setText(
                    content);
            // holder.textViewShortDesc.setText("Address: ");
            // holder.textViewRating.setText(String.valueOf(product.getRating()));
            holder.textViewPrice.setText("Amount: "+ getamount(position));
            holder.textViewTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(), "Details for " + getid(position), Toast.LENGTH_LONG).show();

                    Intent i = new Intent(EmpSalesDash.this, CompanyDetails.class);
                    Bundle bundle = new Bundle();
                    //Add your data from getFactualResults method to bundle
                    bundle.putString("USER_ID", getid(position));
                    bundle.putString("USER_NAME", getname(position));
                    bundle.putString("USER_AMOUNT", getamount(position));
                    bundle.putString("USER_DESCRIPTION", getdesc(position));
                    //Add the bundle to the intent
                    i.putExtras(bundle);

                    //Fire the second activity
                    startActivity(i);
                }
            });
            //holder.imageView.setImageDrawable(;

        }


        @Override
        public int getItemCount() {
            return productList.size();
        }


        class ProductViewHolder extends RecyclerView.ViewHolder {

            TextView textViewTitle, textViewShortDesc, textViewRating, textViewPrice;
            ImageView imageView;

            public ProductViewHolder(View itemView) {
                super(itemView);

                textViewTitle = (TextView)itemView.findViewById(R.id.textViewTitle);
                textViewShortDesc = (TextView)itemView.findViewById(R.id.textViewShortDesc);
                //textViewRating = itemView.findViewById(R.id.textViewRating);
                textViewPrice = (TextView)itemView.findViewById(R.id.textViewPrice);
                // imageView = (ImageView)itemView.findViewById(R.id.imageView);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (tToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (this.tDrawerLayout.isDrawerOpen(GravityCompat.START))
        {
            this.tDrawerLayout.closeDrawer(GravityCompat.START);
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
            this.tDrawerLayout.closeDrawer(GravityCompat.START);
            new AlertDialog.Builder(this)
                   // .setIcon(R.drawable.dialog_warning)
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                session.setLogin(false);

                db.deleteUsers();
                new AlertDialog.Builder(this)
                      //  .setIcon(R.drawable.dialog_warning)
                        .setTitle("Logging Out")
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(EmpSalesDash.this, LoginActivity.class);
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
                Intent i = new Intent(EmpSalesDash.this, AddOrderSem.class);
                startActivity(i);
                return true;
           /* case R.id.thome:

                // Launching the login activity
                Intent ih = new Intent(TDashboard.this, TDashboard.class);
                startActivity(ih);
                return true;

            case R.id.new_registration:

                // Launching the login activity
                Intent ic = new Intent(TDashboard.this, TContactUs.class);
                startActivity(ic);
                return true;*/


            default:
                tDrawerLayout = (DrawerLayout) findViewById(R.id.adrawerlayout);
                tDrawerLayout.closeDrawer(GravityCompat.START);
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

}
