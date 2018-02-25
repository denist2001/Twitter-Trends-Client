package twitter.challenge.code.network;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.io.IOException;

import twitter.challenge.code.TwitterDataLoaderListener;

public class DownloadTask extends AsyncTask<String, Integer, DownloadTask.Result> {

    private final TwitterDataLoaderListener listener;
    private final ConnectivityManager manager;

    public DownloadTask(TwitterDataLoaderListener listener, ConnectivityManager manager) {
        this.listener = listener;
        this.manager = manager;
    }

    /**
     * Wrapper class that serves as a union of a result value and an exception. When the download
     * task has completed, either the result value or exception can be a non-null value.
     * This allows you to pass exceptions to the UI thread that were thrown during doInBackground().
     */
    static class Result {
        String mResultValue;
        Exception mException;

        Result(String resultValue) {
            mResultValue = resultValue;
        }

        Result(Exception exception) {
            mException = exception;
        }
    }

    /**
     * Cancel background network operation if we do not have network connectivity.
     */
    @Override
    protected void onPreExecute() {
        if (listener != null) {
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected() ||
                    (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                            && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
                // If no connectivity, cancel task and update Callback with null data.
                listener.onDataFailed("Wifi or mobile connection should be available.");
                cancel(true);
            }
        }
    }

    /**
     * Defines work to perform on the background thread.
     */
    @Override
    protected DownloadTask.Result doInBackground(String... params) {
        Result result = null;
        if (!isCancelled() && params != null && params.length > 0) {
            String param = params[0];
            try {
                String resultString = downloadUrl(param);
                if (resultString != null) {
                    result = new Result(resultString);
                } else {
                    throw new IOException("No response received.");
                }
            } catch (Exception e) {
                result = new Result(e);
            }
        }
        return result;
    }

    /**
     * Updates the DownloadCallback with the result.
     */
    @Override
    protected void onPostExecute(Result result) {
        if (result != null && listener != null) {

            if (result.mException != null) {
                listener.onDataFailed(result.mException.getMessage());
            } else if (result.mResultValue != null) {
                listener.onDataLoaded(result.mResultValue);
            }
        }
    }

    /**
     * Override to add special behavior for cancelled AsyncTask.
     */
    @Override
    protected void onCancelled(Result result) {
        this.cancel(true);
    }

    //https://api.gemini.yahoo.com/v3/rest/dictionary/woeid?location=california&type=state
    private String downloadUrl(String param) throws IOException {
        return TwitterConnection.getTrendsInWOEID(param);
    }
}
