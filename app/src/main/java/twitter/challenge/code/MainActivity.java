package twitter.challenge.code;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import twitter.challenge.code.Tools.SerializerListener;
import twitter.challenge.code.Tools.Utils;

public class MainActivity extends FragmentActivity implements TrendsRecyclerAdapter.ItemClickListener {

    //TODO can be restore from shared preferences
    public static final String INIT_VALUE = "638242";//Berlin area
    private TrendsViewModel viewModel;
    private ConnectivityManager connectivityManager;
    private RecyclerView trendsList;
    private TrendsRecyclerAdapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getLifecycle().addObserver(new TwitterTrendsLifeCycleObserver());
        connectivityManager =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        TrendsViewModelFactory factory = new TrendsViewModelFactory(new TwitterDataLoaderListener() {
            @Override
            public void onDataLoaded(String jsonResult) {
                Utils.getTrendsArrayFromJson(jsonResult, new SerializerListener() {
                    @Override
                    public void serialisationComplete(ArrayList<Trend> trends) {
                        viewModel.refreshTrendsList(trends);
                        showTrendsInRecyclerView(trends);
                    }

                    @Override
                    public void serialisationError(String errorMessage) {
                        Log.e(DownloadTask.TAG, errorMessage);
                    }
                });
            }

            @Override
            public void onDataFailed(String message) {

            }
        });
        viewModel = ViewModelProviders.of(this, factory).get(TrendsViewModel.class);
        //search area region
        final SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        recyclerAdapter = new TrendsRecyclerAdapter(viewModel.getTrends(INIT_VALUE, connectivityManager), this);
        trendsList.setAdapter(recyclerAdapter);

        findViewById(R.id.requestLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO request data from twitter
                //viewModel.getWOEId(twitterDataLoaderListener);
                //if ((woeId != null) && !woeId.isEmpty()) {
                //}
            }
        });
        //search area end
    }

    private void showTrendsInRecyclerView(final ArrayList<Trend> trends) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recyclerAdapter.updateTrendsList(trends);
//                recyclerAdapter.notifyDataSetChanged();
//                recyclerAdapter.setTrends(trends);
//                recyclerAdapter.notifyDataSetChanged();
            }
        });
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
