package twitter.challenge.code;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;

import twitter.challenge.code.Tools.SerializerListener;
import twitter.challenge.code.Tools.Utils;
import twitter.challenge.code.network.DownloadTask;

class TrendsViewModel extends ViewModel {

    private DownloadTask mDownloadTask;
    private MutableLiveData<ArrayList<Trend>> trends = new MutableLiveData<>();
    private TwitterDataLoaderListener listener;

    TrendsViewModel() {
        trends.setValue(new ArrayList<Trend>());
        this.listener = new TwitterDataLoaderListener() {
            @Override
            public void onDataLoaded(String jsonResult) {
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
            public void onDataFailed(String message) {

            }
        };
    }

    MutableLiveData<ArrayList<Trend>> getTrends(String queryBody, ConnectivityManager manager) {
        startDownload(queryBody, manager);
        return trends;
    }

    /**
     * Start non-blocking execution of DownloadTask.
     *
     * @param queryBody - expression for twitter query
     */
    private void startDownload(String queryBody, ConnectivityManager manager) {
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
