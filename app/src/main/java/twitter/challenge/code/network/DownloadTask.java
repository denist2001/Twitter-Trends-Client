package twitter.challenge.code.network;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;

import twitter.challenge.code.listeners.TwitterDataLoaderListener;

public class DownloadTask extends AsyncTask<String, Integer, DownloadTask.Result> {

    @NonNull
    private final TwitterDataLoaderListener listener;
    @NonNull
    private final ConnectivityManager manager;

    public DownloadTask(@NonNull final TwitterDataLoaderListener listener,
                        @NonNull final ConnectivityManager manager) {
        this.listener = listener;
        this.manager = manager;
    }

    /**
     * Wrapper class that serves as a union of a result value and an exception. When the download
     * task has completed, either the result value or exception can be a non-null value.
     * This allows you to pass exceptions to the UI thread that were thrown during doInBackground().
     */
    static class Result {
        @Nullable
        String mResultValue;
        @Nullable
        Exception mException;

        Result(@NonNull final String resultValue) {
            mResultValue = resultValue;
        }

        Result(@NonNull final Exception exception) {
            mException = exception;
        }
    }

    /**
     * Cancel background network operation if we do not have network connectivity.
     */
    @Override
    protected void onPreExecute() {
        final NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        // If no connectivity, cancel task and update Callback with null data.
        if (networkInfo == null || !networkInfo.isConnected()
                || (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            listener.onDataFailed("Wifi or mobile connection should be available.");
            cancel(true);
        }
    }

    /**
     * Defines work to perform on the background thread.
     */
    @Override
    protected DownloadTask.Result doInBackground(final String... params) {
        Result result = null;
        if (!isCancelled() && params != null && params.length > 0) {
            try {
                final String resultString = downloadUrl(params[0]);
                if (!resultString.isEmpty()) {
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
    protected void onPostExecute(final Result result) {
        if (result != null) {
            if (result.mResultValue != null) {
                listener.onDataLoaded(result.mResultValue);
                return;
            }
            if (result.mException != null) {
                listener.onDataFailed(result.mException.getMessage());
                return;
            }
        }
        listener.onDataFailed("Unknown result.");
    }

    /**
     * Override to add special behavior for cancelled AsyncTask.
     */
    @Override
    protected void onCancelled(@NonNull final Result result) {
        cancel(true);
    }

    @NonNull
    private String downloadUrl(@NonNull final String param) throws IOException {
        return TwitterConnection.getTrendsInWOEID(param);
    }
}
