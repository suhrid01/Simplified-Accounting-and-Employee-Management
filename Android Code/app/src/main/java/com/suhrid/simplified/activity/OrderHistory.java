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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

public class OrderHistory extends BaseActivity {

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
        setContentOfView(R.layout.activity_orderhistory,this);

        tDrawerLayout = (DrawerLayout) findViewById(R.id.adrawerlayout);
        tToggle = new ActionBarDrawerToggle(this, tDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        tDrawerLayout.addDrawerListener(tToggle);
        tToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new SessionManager(getApplicationContext());

        NavigationView navigation = (NavigationView) findViewById(R.id.snavigation);
        navigation.setNavigationItemSelectedListener(this);
        db = new SQLiteHandler(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        fname = (TextView) findViewById(R.id.welcome);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Ordinary.ttf");
        fname.setTypeface(typeface);
        fname.setText("Order History");
        recyclerView1 = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView1.setHasFixedSize(true);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));

        getdeatils();
    }

    private void getdeatils() {



        String tag_string_req = "req_login";
        pDialog.setMessage("Fetching Order History...");
        showDialog();
        data.clear();
        StringRequest strReq = new StringRequest("http://suhrid1theinceptor.000webhostapp.com/userlogin/order_history_api.php", new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                //  inputSearch.setVisibility(View.VISIBLE);
                //tool.setVisibility(View.VISIBLE);
                Log.i("tagconvertstr", "[" + response + "]");
                hideDialog();
                Log.d(TAG, "Attend Response: " + response);
                try {


                    JSONObject jObj = new JSONObject(response);

                    data1 = jObj.getJSONArray("respond");
                    for (i = 0; i < data1.length(); i++) {
                        try {
                            JSONObject json = data1.getJSONObject(i);
                            String o_id = json.getString("oid");
                            data.add(o_id);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    OrderHistory.ProductAdapter adapter = new OrderHistory.ProductAdapter(OrderHistory.this, data);


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
            id = json.getString("eid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return id;
    }
    private String getcname(int position) {
        String id = "";
        try {
            //Getting object of given index
            JSONObject json = data1.getJSONObject(position);

            //Fetching name from that object
            id = json.getString("customername");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return id;
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
    private String getemp_id(int position) {
        String eid = "";
        try {
            //Getting object of given index
            JSONObject json = data1.getJSONObject(position);

            //Fetching name from that object
            eid = json.getString("eid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return eid;
    }


    public class ProductAdapter extends RecyclerView.Adapter<OrderHistory.ProductAdapter.ProductViewHolder> {


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
        public OrderHistory.ProductAdapter.ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //inflating and returning our view holder
            LayoutInflater inflater = LayoutInflater.from(mCtx);
            View view = inflater.inflate(R.layout.list_item, null);
            return new OrderHistory.ProductAdapter.ProductViewHolder(view);
        }

        @Override
        public void onBindViewHolder(OrderHistory.ProductAdapter.ProductViewHolder holder, final int position) {
            //getting the product of the specified position
            //Product product = productList.get(position);


            //binding the data with the viewholder views
            SpannableString content = new SpannableString("\u2022 " + getname(position));
            content.setSpan(new UnderlineSpan(), 2, content.length(), 0);

            holder.textViewTitle.setText(
                    content);
             holder.textViewShortDesc.setText("Company: "+ getcname(position)+"    "+"Employee Collected: "+getemp_id(position));
            // holder.textViewRating.setText(String.valueOf(product.getRating()));
             holder.textViewPrice.setText("Amount left to collect: "+ getamount(position));
            holder.textViewTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(), "Invoice will be generated. Coming Soon", Toast.LENGTH_LONG).show();

                    Intent i = new Intent(OrderHistory.this, Invoice.class);
                    //Bundle bundle = new Bundle();
                    //Add your data from getFactualResults method to bundle
                    //bundle.putString("USER_ID", getid(position));
                    //bundle.putString("USER_AMOUNT", getamount(position));
                    //Add the bundle to the intent
                    //i.putExtras(bundle);

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

                textViewTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
                textViewShortDesc = (TextView) itemView.findViewById(R.id.textViewShortDesc);
                //textViewRating = itemView.findViewById(R.id.textViewRating);
                textViewPrice = (TextView) itemView.findViewById(R.id.textViewPrice);
                // imageView = (ImageView)itemView.findViewById(R.id.imageView);
            }
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



