package com.aditya.ilovenoughat.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.aditya.ilovenoughat.R;
import com.aditya.ilovenoughat.adapter.ItemsRecyclerAdapter;
import com.aditya.ilovenoughat.model.Item;
import com.aditya.ilovenoughat.utils.Utils;

import java.util.ArrayList;

/**
 * Created by aditya on 9/11/16.
 */
public class ComparedResultsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compared);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initUI();

    }

    private void initUI()
    {
        Bundle extras=getIntent().getExtras();
        Item selectedItem=extras.getParcelable(Utils.KEY_COMPARE_ITEM);
        ArrayList<Item> itemsList= extras.getParcelableArrayList(Utils.KEY_COMPARE_LIST);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(" Comparision Results ");
        }
        TextView compareText=(TextView)findViewById(R.id.text_compare_results);
        int itemsSize=itemsList.size();
        if(itemsSize==1)
        compareText.setText(itemsList.size()+" Item "+getString(R.string.comparision_message));
        else
            compareText.setText(itemsList.size()+" Items "+getString(R.string.comparision_message));
        RecyclerView compareResultsRecyclerView=(RecyclerView)findViewById(R.id.results_recycler);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        compareResultsRecyclerView.setLayoutManager(gridLayoutManager);
        compareResultsRecyclerView.setHasFixedSize(true);

        ItemsRecyclerAdapter itemsAdapter = new ItemsRecyclerAdapter(this, itemsList);
        compareResultsRecyclerView.setAdapter(itemsAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}