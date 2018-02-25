package twitter.challenge.code.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import twitter.challenge.code.Trend;
import twitter.challenge.code.listeners.TwitterDataLoaderListener;
import twitter.challenge.code.listeners.SerializerListener;
import twitter.challenge.code.tools.Utils;
import twitter.challenge.code.network.DownloadTask;

public class TrendsViewModel extends ViewModel {

    private DownloadTask mDownloadTask;
    @NonNull
    private final MutableLiveData<ArrayList<Trend>> trends = new MutableLiveData<>();
    private TwitterDataLoaderListener listener;

    public TrendsViewModel() {
        trends.setValue(new ArrayList<Trend>());
        this.listener = new TwitterDataLoaderListener() {
            @Override
            public void onDataLoaded(@NonNull final String jsonResult) {
                Utils.getTrendsArrayFromJson(jsonResult, new SerializerListener() {
                    @Override
                    public void serialisationComplete(final ArrayList<Trend> newTrends) {
                        Runnable runnable = new Runnable(){
                            @Override
                            public void run() {
                                trends.setValue(newTrends);
                            }
                        };
                        new Handler(Looper.getMainLooper()).post(runnable);
                    }

                    @Override
                    public void serialisationError(String errorMessage) {

                    }
                });
            }

            @Override
            public void onDataFailed(@NonNull final String message) {

            }
        };
    }

    @NonNull
    public MutableLiveData<ArrayList<Trend>> getTrends(@NonNull final String queryBody,
                                                       @NonNull final ConnectivityManager manager) {
        startDownload(queryBody, manager);
        return trends;
    }

    /**
     * Start non-blocking execution of DownloadTask.
     *
     * @param queryBody - expression for twitter query
     */
    private void startDownload(@NonNull final String queryBody,
                               @NonNull final ConnectivityManager manager) {
        cancelDownload();

        mDownloadTask = new DownloadTask(listener, manager);
        mDownloadTask.execute(queryBody);
    }

    /**
     * Cancel (and interrupt if necessary) any ongoing DownloadTask execution.
     */
    private void cancelDownload() {
        if (mDownloadTask != null) {
            mDownloadTask.cancel(true);
        }
    }
}
