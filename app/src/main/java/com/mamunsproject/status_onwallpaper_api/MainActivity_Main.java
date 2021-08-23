package com.mamunsproject.status_onwallpaper_api;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mamunsproject.status_onwallpaper_api.Adapter.AdapterClass;
import com.mamunsproject.status_onwallpaper_api.Api.ImageModel;
import com.mamunsproject.status_onwallpaper_api.Api.SearchModel;
import com.mamunsproject.status_onwallpaper_api.Utilities.ApiUtilities;

import java.util.ArrayList;

import io.reactivex.annotations.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity_Main extends AppCompatActivity {

    private ArrayList<ImageModel> modelsClassList;
    private RecyclerView recyclerView;
    Adapter adapter;
    EditText editText;
    ImageButton search;
    CardView mNature, mBus, mCar, mTrain, mTrending;
    ProgressBar progressBar;
    int currentItems, totalItems, scrollOutItems;
    Boolean isScrolling = false;
    int pageNumber = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_main);
        getSupportActionBar().hide();


        recyclerView = findViewById(R.id.recyclerView);
        mNature = findViewById(R.id.nature);
        mBus = findViewById(R.id.bus);
        mCar = findViewById(R.id.car);
        mTrain = findViewById(R.id.train);
        mTrending = findViewById(R.id.tranding);
        editText = findViewById(R.id.editText);
        search = findViewById(R.id.search);
        progressBar = findViewById(R.id.mainProgressBar);

        modelsClassList = new ArrayList<>();
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.setHasFixedSize(true);
        adapter = new AdapterClass(MainActivity_Main.this, modelsClassList);
        recyclerView.setAdapter(adapter);


        //=================================FOR FETCHING UNLIMITED PHOTOS===============================================

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                currentItems = gridLayoutManager.getChildCount();
                totalItems = gridLayoutManager.getItemCount();
                scrollOutItems = gridLayoutManager.findFirstVisibleItemPosition();

                Log.d("TAG", "currentItems: "+currentItems+"     totalItems "+totalItems+"     scrollOutItems "+scrollOutItems);
                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
                    isScrolling = false;
                    pageNumber++;
                    findPhotos();
                    Log.d("PAGE", "onScrolled: " + pageNumber);
                }


            }
        });


        //=================================FOR FETCHING UNLIMITED PHOTOS===============================================


        mNature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = "nature";
                getSearchImage(query);
            }
        });

        mCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = "car";
                getSearchImage(query);
            }
        });

        mTrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = "train";
                getSearchImage(query);
            }
        });

        mBus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String query = "bus";
                getSearchImage(query);
            }
        });

        mTrending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findPhotos();
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId,
                                          KeyEvent keyEvent) { //triggered when done editing (as clicked done on keyboard)
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Check if no view has focus:
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    editText.clearFocus();
                    return true;

                }
                return false;
            }
        });


        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String query = editText.getText().toString().trim().toLowerCase();

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    getSearchImage(query);

                }
                return false;
            }
        });


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = editText.getText().toString().trim().toLowerCase();

                if (query.isEmpty()) {
                    Toast.makeText(MainActivity_Main.this, "Enter key!", Toast.LENGTH_SHORT).show();
                } else {
                    getSearchImage(query);
                }

                // Check if no view has focus:
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }
        });


        findPhotos();


    }

    private void findPhotos() {

        progressBar.setVisibility(View.VISIBLE);

        ApiUtilities.getApiInterface().getImage(pageNumber, 80).enqueue(new Callback<SearchModel>() {
            @Override
            public void onResponse(Call<SearchModel> call, Response<SearchModel> response) {

                progressBar.setVisibility(View.GONE);

                modelsClassList.clear();
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    modelsClassList.addAll(response.body().getPhotos());
                //    adapter.notifyDataSetChanged();

                    Log.d("PageNUMBER", "onResponse: "+pageNumber);

                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity_Main.this, "No Image Available TO FIND!", Toast.LENGTH_SHORT).show();

                }
                adapter.notifyDataSetChanged();
                pageNumber++;
                Log.d("PageNUMBER2", "onResponse: "+pageNumber);

            }

            @Override
            public void onFailure(Call<SearchModel> call, Throwable t) {
                progressBar.setVisibility(View.VISIBLE);
            }


        });
    }


    private void getSearchImage(String query) {

        progressBar.setVisibility(View.VISIBLE);
        ApiUtilities.getApiInterface().getSearchImage(query, pageNumber, 80).enqueue(new Callback<SearchModel>() {
            @Override
            public void onResponse(Call<SearchModel> call, Response<SearchModel> response) {

                progressBar.setVisibility(View.GONE);

                modelsClassList.clear();
                if (response.isSuccessful()) {
                    modelsClassList.addAll(response.body().getPhotos());
                    adapter.notifyDataSetChanged();
                    pageNumber++;
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity_Main.this, "No Image Available!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<SearchModel> call, Throwable t) {
                progressBar.setVisibility(View.GONE);

            }
        });
    }

}