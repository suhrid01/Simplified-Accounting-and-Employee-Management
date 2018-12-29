package com.suhrid.simplified.activity;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.shockwave.pdfium.PdfDocument;
import com.suhrid.simplified.R;
import com.suhrid.simplified.helper.SQLiteHandler;
import com.suhrid.simplified.helper.SessionManager;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by Abhro on 04-05-2018.
 */
public class Invoice extends Activity implements Response.Listener<byte[]>, Response.ErrorListener,OnPageChangeListener,OnLoadCompleteListener {

    Button downloadss,downloadcw;
    PDFView pdfView;
    Integer pageNumber = 0;
    String pdfFileName;
    public String file;
    public String u_id,date1;
    TextView header;
    private DatePickerDialog datePickerDialog1,datePickerDialog2;
    private DrawerLayout tDrawerLayout;
    private ActionBarDrawerToggle tToggle;
    InputStreamVolleyRequest request;
    int count;
    private SQLiteHandler db;

    private SessionManager session;
    private EditText datepickss,datepickcw,stdid;
    public String filename;
    public String date2;
    private MaterialSpinner clss,secs;
    private ArrayList<String> data = new ArrayList<String>();
    private ProgressDialog pDialog;

    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(23)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpdf);


        if (shouldAskPermissions()) {
            askPermissions();
        }

        db = new SQLiteHandler(getApplicationContext());
        //TextView selectsingle=(TextView) findViewById(R.id.selectsingle);
        //TextView selectclass=(TextView) findViewById(R.id.selectclass);
        //selectsingle.setVisibility(View.GONE);
        //selectclass.setVisibility(View.VISIBLE);
//        downloadcw.setVisibility(View.VISIBLE);
        final HashMap<String, String> user = db.getUserDetails();

        pdfView= (PDFView)findViewById(R.id.pdfView);
        displayFromAsset(Environment.getExternalStorageDirectory() +  "/Download" + "/" + filename);
        // session manager
        session = new SessionManager(getApplicationContext());
        u_id = user.get("u_id");
        isNetworkConnectionAvailable();
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);



                //Change your url below
                pDialog.setMessage("Downloading PDF...");
                showDialog();

                String mUrl="https://simplifiedsolutions.in/test-billing/invoices/salesPDF/36";
                try {
                    request = new InputStreamVolleyRequest(Request.Method.GET, mUrl, Invoice.this, Invoice.this, (HashMap<String, String>) getParams());
                } catch (AuthFailureError authFailureError) {
                    authFailureError.printStackTrace();
                }
                RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext(),
                        new HurlStack());
                mRequestQueue.add(request);
            }




    private void displayFromAsset(String assetFileName) {
        pdfFileName = assetFileName;

        pdfView.fromUri(Uri.parse(pdfFileName))
                .defaultPage(pageNumber)
                .enableSwipe(true)

                .swipeHorizontal(false)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();
    }


    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
    }


    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        printBookmarksTree(pdfView.getTableOfContents(), "-");

    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

          //  Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
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
    public void onResponse(byte[] response) {
        hideDialog();
        HashMap<String, Object> map = new HashMap<String, Object>();
        try {
            if (response!=null) {

                //Read file name from headers
                //String content =request.responseHeaders.get("Content-Disposition").toString();
                //StringTokenizer st = new StringTokenizer(content, "=");
                //String[] arrTag = st.toArray();

                filename = u_id+date2+".pdf";
                filename = filename.replace(":", ".");
                Log.d("DEBUG::RESUME FILE NAME", filename);
                Toast.makeText(Invoice.this, "PDF file downloaded at:"+filename , Toast.LENGTH_LONG).show();

                try{
                    long lenghtOfFile = response.length;

                    //covert reponse to input stream
                    InputStream input = new ByteArrayInputStream(response);
                    File path = Environment.getExternalStorageDirectory() ;
                    File file = new File(path+"/Download", filename);

                    map.put("resume_path", file.toString());
                    BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file));
                    byte data[] = new byte[1024];

                    long total = 0;

                    while ((count = input.read(data)) != -1) {
                        total += count;
                        output.write(data, 0, count);
                    }

                    output.flush();

                    output.close();
                    input.close();
                }catch(IOException e){
                    e.printStackTrace();

                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d("KEY_ERROR", "UNABLE TO DOWNLOAD FILE");
            e.printStackTrace();
        }

    }
    @Override
    public void onErrorResponse(VolleyError error) {
        // Log.e(TAG, "Login Err: " + error.getMessage());
        Toast.makeText(getApplicationContext(),
                error.getMessage(), Toast.LENGTH_LONG).show();
        hideDialog();
    }
    //@Override
    protected Map<String, String> getParams() throws AuthFailureError {
        // Posting parameters to login url
        Map<String, String> params = new HashMap<>();


        return params;
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
    public void onBackPressed() {
        if (this.tDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.tDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
