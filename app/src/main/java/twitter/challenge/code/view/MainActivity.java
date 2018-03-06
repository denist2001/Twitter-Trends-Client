package twitter.challenge.code.view;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;

import java.util.ArrayList;

import twitter.challenge.code.R;
import twitter.challenge.code.Trend;
import twitter.challenge.code.TrendsRecyclerAdapter;
import twitter.challenge.code.viewmodel.TrendsViewModel;

public class MainActivity extends FragmentActivity implements TrendsRecyclerAdapter.ItemClickListener {

    //TODO can be restore from shared preferences
    public static final String INIT_VALUE = "638242";//Berlin area
    private TrendsViewModel viewModel;
    private ConnectivityManager connectivityManager;
    private TrendsRecyclerAdapter recyclerAdapter;
    private final Observer<ArrayList<Trend>> trendsObserver = new Observer<ArrayList<Trend>>() {
        @Override
        public void onChanged(@Nullable final ArrayList<Trend> newValue) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (newValue != null) {
                        recyclerAdapter.updateTrendsList(newValue);
                    }
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectivityManager =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        viewModel = TrendsViewModel.getInstance();
        //search area region
        ((SearchView) findViewById(R.id.search_view))
                .setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(@NonNull final String query) {
                        startDownload(query);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(@NonNull final String newText) {
                        return false;
                    }
                });

        final RecyclerView trendsList = findViewById(R.id.trendsList);
        trendsList.setHasFixedSize(true);
        final DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                trendsList.getContext(), DividerItemDecoration.VERTICAL);
        trendsList.addItemDecoration(dividerItemDecoration);
        trendsList.setLayoutManager(
                new LinearLayoutManager(trendsList.getContext()));
        final MutableLiveData<ArrayList<Trend>> liveData = startDownload(INIT_VALUE);
        if (liveData != null) {
            subscribeTrendsList(liveData);
            recyclerAdapter = new TrendsRecyclerAdapter(liveData.getValue(), this);
            trendsList.setAdapter(recyclerAdapter);
        }

        findViewById(R.id.requestLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO request current WOEID from Yahoo
                showAlertDialog("This function doesn't finished. Twitter registration needs.");
            }
        });
        //search area end
    }

    private void showAlertDialog(@NonNull final String message) {
        final AlertDialog.Builder alertDialog =
                new AlertDialog.Builder(MainActivity.this);
        alertDialog.setMessage(message);
        alertDialog.create().show();
    }

    private void subscribeTrendsList(@NonNull final MutableLiveData<ArrayList<Trend>> liveData) {
        liveData.observe(this, trendsObserver);
    }

    private MutableLiveData<ArrayList<Trend>> startDownload(@NonNull final String queryBody) {
        if (viewModel != null && !queryBody.isEmpty()) {
            return viewModel.getTrends(queryBody, connectivityManager);
        }
        return null;
    }

    //TrendsRecyclerAdapter.ItemClickListener region
    @Override
    public void onItemClicked(final String url) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });
    }
    //TrendsRecyclerAdapter.ItemClickListener end
}
