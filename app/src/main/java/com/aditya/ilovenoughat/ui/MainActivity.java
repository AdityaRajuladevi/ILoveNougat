package com.aditya.ilovenoughat.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.aditya.ilovenoughat.R;
import com.aditya.ilovenoughat.adapter.ItemsRecyclerAdapter;
import com.aditya.ilovenoughat.callbacks.ItemClickSupport;
import com.aditya.ilovenoughat.model.Item;
import com.aditya.ilovenoughat.utils.Logger;
import com.aditya.ilovenoughat.utils.Utils;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {


    private ItemsRecyclerAdapter itemsAdapter;
    private TextView txt_no_items;
    private RecyclerView searchResultsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchResultsRecyclerView = (RecyclerView) findViewById(R.id.results_recycler);
        int orientation=getScreenOrientation();
        GridLayoutManager gridLayoutManager=null;
        if(orientation==Configuration.ORIENTATION_PORTRAIT)
        {
           gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        }
        else if (orientation==Configuration.ORIENTATION_LANDSCAPE)
        {
            gridLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        }

        searchResultsRecyclerView.setLayoutManager(gridLayoutManager);
        searchResultsRecyclerView.setHasFixedSize(true);

        txt_no_items = (TextView) findViewById(R.id.text_no_items);
        txt_no_items.setText(getString(R.string.search_instruction));

        final ArrayList<Item> itemsList;
        itemsList = new ArrayList<>();
        itemsAdapter = new ItemsRecyclerAdapter(this, itemsList);
        searchResultsRecyclerView.setAdapter(itemsAdapter);

        if (savedInstanceState != null && savedInstanceState.containsKey(Utils.KEY_LIST_STATE)) {
            ArrayList<Item> tempList =savedInstanceState.getParcelableArrayList(Utils.KEY_LIST_STATE);
            checkForResultsMessage(tempList);
        }

        // adding recycler view on click listener
        ItemClickSupport.addTo(searchResultsRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Item searchedItem= itemsList.get(position);
                //  validInputAndSearch(false);
                get6PMItemsForKey(searchedItem);
            }
        });

    }

    public int getScreenOrientation()
    {
        Display screenOrientation = getWindowManager().getDefaultDisplay();
        int orientation = Configuration.ORIENTATION_UNDEFINED;
        if(screenOrientation.getWidth()==screenOrientation.getHeight()){
            orientation = Configuration.ORIENTATION_SQUARE;
            //Do something

        } else{
            if(screenOrientation.getWidth() < screenOrientation.getHeight()){
                orientation = Configuration.ORIENTATION_PORTRAIT;
                //Do something

            }else {
                orientation = Configuration.ORIENTATION_LANDSCAPE;
                //Do something

            }
        }
        return orientation;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        Logger.v(query);


        if (getCurrentFocus().getWindowToken() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    0);

        }
        validateInputAndSearch(query);

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Utils.KEY_LIST_STATE, itemsAdapter.getItemArrayList());
    }

    private void validateInputAndSearch(String query) {

        if (TextUtils.isEmpty(query)) {
            Toast.makeText(MainActivity.this, " Search Item cannot be empty!!!", Toast.LENGTH_SHORT).show();
        } else {
            getZapoosItemsForKey(query);
        }
    }

    private void getZapoosItemsForKey(String key) {
        RequestQueue queue = Volley.newRequestQueue(this);

        // final ProgressDialog progressDialog = ProgressDialog.show(this, getString(R.string.app_name), " Searching items in Zapoo.com");

        // Request a string response from the provided URL.
        String url = Utils.ZAPOOS_URL_PART1 + key + Utils.ZAPOOS_URL_PART2;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                generateItemsListFromZapoosResponse(response);
//                if (progressDialog != null && progressDialog.isShowing())
//                    progressDialog.dismiss();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                if (progressDialog != null && progressDialog.isShowing())
//                    progressDialog.dismiss();

            }
        });
        // Add the request to the RequestQueue.
        queue.add(request);
    }

    private void generateItemsListFromZapoosResponse(JSONObject responseObject) {
        ArrayList<Item> currentSearchList = new ArrayList<>();
        try {
            String originalTerm = responseObject.getString("originalTerm");
            int currentResultCount = responseObject.getInt("currentResultCount");
            int totalResultCount = responseObject.getInt("totalResultCount");
            String term = responseObject.getString("term");
            JSONArray results = responseObject.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject eachObject = results.getJSONObject(i);
                Item eachItem = new Item();
                eachItem.setBrandName(eachObject.getString("brandName"));
                eachItem.setThumbnailImageUrl(eachObject.getString("thumbnailImageUrl"));
                eachItem.setProductId(eachObject.getInt("productId"));
                eachItem.setOriginalPrice(eachObject.getString("originalPrice"));
                eachItem.setStyleId(eachObject.getInt("styleId"));
                eachItem.setColorId(eachObject.getInt("colorId"));
                eachItem.setPrice(eachObject.getString("price"));
                eachItem.setPercentOff(eachObject.getString("percentOff"));
                eachItem.setProductUrl(eachObject.getString("productUrl"));
                eachItem.setProductName(eachObject.getString("productName"));
                currentSearchList.add(eachItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        checkForResultsMessage(currentSearchList);

    }

    private void checkForResultsMessage(ArrayList<Item> itemsList) {
        if (itemsList != null && itemsList.size() > 0) {
            searchResultsRecyclerView.setVisibility(View.VISIBLE);
            if (itemsAdapter != null)
                itemsAdapter.refreshAdapterData(itemsList);
            txt_no_items.setVisibility(View.GONE);
        } else {
            txt_no_items.setText(getString(R.string.no_items_message));
            txt_no_items.setVisibility(View.VISIBLE);
            searchResultsRecyclerView.setVisibility(View.GONE);
        }
    }


    private void get6PMItemsForKey(final Item selectedItem) {
        RequestQueue queue = Volley.newRequestQueue(this);
        final ProgressDialog pDialog = ProgressDialog.show(this, getString(R.string.app_name), "Comparing Prices with 6PM products..");
        // Request a string response from the provided URL.
        String url = Utils.PM_URL_PART1 + selectedItem.getProductName() + Utils.PM_URL_PART2;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                generateItemsListFrom6PMResponse(response, selectedItem);
                if (pDialog != null && pDialog.isShowing())
                    pDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (pDialog != null && pDialog.isShowing())
                    pDialog.dismiss();
            }
        });
// Add the request to the RequestQueue.
        queue.add(request);
    }

    private void generateItemsListFrom6PMResponse(JSONObject responseObject, Item selectedItem) {
        ArrayList<Item> currentSearchList = new ArrayList<>();
        try {
            String originalTerm = responseObject.getString("originalTerm");
            int currentResultCount = responseObject.getInt("currentResultCount");
            int totalResultCount = responseObject.getInt("totalResultCount");
            String term = responseObject.getString("term");
            JSONArray results = responseObject.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject eachObject = results.getJSONObject(i);
                Item eachItem = new Item();
                eachItem.setBrandName(eachObject.getString("brandName"));
                eachItem.setThumbnailImageUrl(eachObject.getString("thumbnailImageUrl"));
                eachItem.setProductId(eachObject.getInt("productId"));
                eachItem.setOriginalPrice(eachObject.getString("originalPrice"));
                eachItem.setStyleId(eachObject.getInt("styleId"));
                eachItem.setColorId(eachObject.getInt("colorId"));
                eachItem.setPrice(eachObject.getString("price"));
                eachItem.setPercentOff(eachObject.getString("percentOff"));
                eachItem.setProductUrl(eachObject.getString("productUrl"));
                eachItem.setProductName(eachObject.getString("productName"));
                if (selectedItem.getProductId() == eachItem.getProductId()) {
                    currentSearchList.add(eachItem);
                   // Logger.v("App 6PM ",eachItem.getProductName()+" , "+ eachItem.getPrice()+" , "+eachItem.getStyleId()+" O Style "+ selectedItem.getStyleId() );
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

         if( currentSearchList.size()>0) {
            Intent compareActivityIntent=new Intent(this,ComparedResultsActivity.class);
            compareActivityIntent.putExtra(Utils.KEY_COMPARE_ITEM,selectedItem);
            compareActivityIntent.putParcelableArrayListExtra(Utils.KEY_COMPARE_LIST,currentSearchList);
            startActivity(compareActivityIntent);
        }
        else
         {
            CoordinatorLayout coordinatorLayout=(CoordinatorLayout)findViewById(R.id.coordinator_layout) ;
             Snackbar snackbar = Snackbar
                     .make(coordinatorLayout, getString(R.string.comparision_no_results_message), Snackbar.LENGTH_LONG);

             snackbar.show();
         }
    }
}
