package twitter.challenge.code;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements TrendsRecyclerAdapter.ItemClickListener {

    //TODO can be restore from shared preferences
    public static final String INIT_VALUE = "638242";//Berlin area
    private TrendsViewModel viewModel;
    private ConnectivityManager connectivityManager;
    private RecyclerView trendsList;
    private TrendsRecyclerAdapter recyclerAdapter;
    private final Observer<ArrayList<Trend>> trendsObserver = new Observer<ArrayList<Trend>>() {
        @Override
        public void onChanged(@Nullable final ArrayList<Trend> newValue) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recyclerAdapter.updateTrendsList(newValue);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getLifecycle().addObserver(new TwitterTrendsLifeCycleObserver());
        connectivityManager =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        viewModel = ViewModelProviders.of(this).get(TrendsViewModel.class);
        //search area region
        ((SearchView)findViewById(R.id.search_view))
                .setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                startDownload(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        trendsList = findViewById(R.id.trendsList);
        trendsList.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                trendsList.getContext(), DividerItemDecoration.VERTICAL);
        trendsList.addItemDecoration(dividerItemDecoration);
        trendsList.setLayoutManager(
                new LinearLayoutManager(trendsList.getContext()));
        MutableLiveData<ArrayList<Trend>> liveData = viewModel.getTrends(INIT_VALUE, connectivityManager);
        subscribeTrendsList(liveData);
        recyclerAdapter = new TrendsRecyclerAdapter(liveData.getValue(), this);
        trendsList.setAdapter(recyclerAdapter);

        findViewById(R.id.requestLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO request data from twitter
                AlertDialog.Builder alertDialog =
                        new AlertDialog.Builder(MainActivity.this);
                alertDialog.setMessage("This function doesn't finished. Twitter registration needs.");
                alertDialog.create().show();
            }
        });
        //search area end
    }

    private void subscribeTrendsList(MutableLiveData<ArrayList<Trend>> liveData) {
        liveData.observe(this, trendsObserver);
    }

    private void startDownload(String queryBody) {
        if (viewModel != null) {
            // Execute the async download.
            viewModel.getTrends(queryBody, connectivityManager);
        }
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
