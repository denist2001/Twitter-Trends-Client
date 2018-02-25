package twitter.challenge.code;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import twitter.challenge.code.Tools.YahooUrlData;

class DownloadTask extends AsyncTask<String, Integer, DownloadTask.Result> {

    static final String TAG = "DownloadTask";
    private final TwitterDataLoaderListener listener;
    private final ConnectivityManager manager;

    DownloadTask(TwitterDataLoaderListener listener, ConnectivityManager manager) {
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
        return getTrendsInWOEID(param);
    }

    private static String appAuthentication() {

        HttpURLConnection httpConnection = null;
        OutputStream outputStream = null;
        BufferedReader bufferedReader;
        StringBuilder response = null;

        try {
            URL url = new URL(YahooUrlData.URL_ROOT_TWITTER_API + YahooUrlData.GET_TOKEN_URL);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("POST");
            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);

            String accessCredential = YahooUrlData.CLIENT_ID + ":" + YahooUrlData.CLIENT_SECRET;
            String authorization = "Basic "
                    + Base64.encodeToString(accessCredential.getBytes(),
                    Base64.NO_WRAP);
            String param = "grant_type=client_credentials";//credentials  client_authorization_code
            httpConnection.addRequestProperty("client_id", YahooUrlData.CLIENT_ID);
            httpConnection.addRequestProperty("redirect_uri", "oob");
            httpConnection.addRequestProperty("response_type", "id_token");
            httpConnection.addRequestProperty("grant_type", "client_credentials");//authorization_code
            httpConnection.addRequestProperty("Authorization", authorization);
            httpConnection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            httpConnection.connect();

            outputStream = httpConnection.getOutputStream();
            outputStream.write(param.getBytes());

            InputStream inputStream;
            if (httpConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                inputStream = httpConnection.getErrorStream();
            } else {
                inputStream = httpConnection.getInputStream();
            }

            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            response = new StringBuilder();

            while ((line = bufferedReader.readLine()) != null) {
                response.append(line);
            }

            Log.d(TAG,
                    "POST response code: "
                            + String.valueOf(httpConnection.getResponseCode()));
            Log.d(TAG, "JSON response: " + response.toString());

        } catch (Exception e) {
            Log.e(TAG, "POST error: " + Log.getStackTraceString(e));

        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    private static String getTrendsInWOEID(String woeid) {
        HttpURLConnection httpConnection = null;
        BufferedReader bufferedReader = null;
        StringBuilder response = new StringBuilder();

        try {
            URL url = new URL(YahooUrlData.URL_BERLIN_TRENDING + woeid);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("GET");
            String getTrends = appAuthentication();

            JSONObject jsonObjectDocument = new JSONObject(getTrends);
            String token = jsonObjectDocument.getString("token_type") + " "
                    + jsonObjectDocument.getString("access_token");

            httpConnection.setRequestProperty("Authorization", token);
            httpConnection.setRequestProperty("Content-Type",
                    "application/json");
            httpConnection.connect();

            bufferedReader = new BufferedReader(new InputStreamReader(
                    httpConnection.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                response.append(line);
            }

            Log.d(TAG,
                    "GET response code: "
                            + String.valueOf(httpConnection
                            .getResponseCode()));
            Log.d(TAG, "JSON response: " + response.toString());

        } catch (Exception e) {
            Log.e(TAG, "GET error: " + Log.getStackTraceString(e));

        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }
}
