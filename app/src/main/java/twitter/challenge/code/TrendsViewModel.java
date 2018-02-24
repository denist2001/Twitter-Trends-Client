package twitter.challenge.code;

import android.arch.lifecycle.ViewModel;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;

import java.util.ArrayList;

class TrendsViewModel extends ViewModel {

    private DownloadTask mDownloadTask;
    private ArrayList<Trend> trends = new ArrayList<>();
    private TwitterDataLoaderListener listener;

    TrendsViewModel(@NonNull final TwitterDataLoaderListener listener) {
        this.listener = listener;
    }

    ArrayList<Trend> getTrends(String queryBody, ConnectivityManager manager) {
        if (trends.isEmpty()) {
            startDownload(queryBody, manager);
        }
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

    void refreshTrendsList(ArrayList<Trend> trends) {
        this.trends = trends;
    }
}
