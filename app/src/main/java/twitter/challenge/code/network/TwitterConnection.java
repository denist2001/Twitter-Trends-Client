package twitter.challenge.code.network;

import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import twitter.challenge.code.BuildConfig;

class TwitterConnection {

    private static final String TAG = "DownloadTask";
    private static final String URL_BERLIN_TRENDING = "https://api.twitter.com/1.1/trends/place.json?id=";
    private static final String URL_ROOT_TWITTER_API = "https://api.twitter.com";
    private static final String CLIENT_ID = BuildConfig.CONSUMER_KEY;
    private static final String CLIENT_SECRET = BuildConfig.CONSUMER_SECRET;
    private static final String GET_TOKEN_URL = "/oauth2/token";
    private static final String PARAM = "grant_type=client_credentials";

    @NonNull
    static String getTrendsInWOEID(@NonNull final String woeid) {
        HttpURLConnection httpConnection = null;
        BufferedReader bufferedReader = null;
        final StringBuilder response = new StringBuilder();

        try {
            final URL url = new URL(URL_BERLIN_TRENDING + woeid);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Authorization", getFormattedToken());
            httpConnection.setRequestProperty("Content-Type", "application/json");
            httpConnection.connect();

            bufferedReader = new BufferedReader(new InputStreamReader(
                    httpConnection.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                response.append(line);
            }

            Log.d(TAG, "GET response code: "
                    + String.valueOf(httpConnection.getResponseCode()));
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

    @NonNull
    private static String getFormattedToken() throws JSONException {
        final JSONObject jsonToken = new JSONObject(appAuthentication());
        return jsonToken.getString("token_type") + " "
                + jsonToken.getString("access_token");
    }

    private static String appAuthentication() {

        HttpURLConnection httpConnection = null;
        OutputStream outputStream = null;
        StringBuilder response = null;

        try {
            final URL url = new URL(URL_ROOT_TWITTER_API + GET_TOKEN_URL);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("POST");
            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);

            httpConnection.addRequestProperty("client_id", CLIENT_ID);
            httpConnection.addRequestProperty("redirect_uri", "oob");
            httpConnection.addRequestProperty("response_type", "id_token");
            httpConnection.addRequestProperty("grant_type", "client_credentials");
            httpConnection.addRequestProperty("Authorization", getConvertedCredential());
            httpConnection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            httpConnection.connect();

            outputStream = httpConnection.getOutputStream();
            outputStream.write(PARAM.getBytes());

            InputStream inputStream;
            if (httpConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                inputStream = httpConnection.getErrorStream();
            } else {
                inputStream = httpConnection.getInputStream();
            }

            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            response = new StringBuilder();

            while ((line = bufferedReader.readLine()) != null) {
                response.append(line);
            }

            Log.d(TAG, "POST response code: "
                    + String.valueOf(httpConnection.getResponseCode()));
            Log.d(TAG, "JSON response: "
                    + response.toString());

        } catch (Exception e) {
            Log.e(TAG, "POST error: "
                    + Log.getStackTraceString(e));

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

    @NonNull
    private static String getConvertedCredential() {
        String accessCredential = CLIENT_ID + ":" + CLIENT_SECRET;
        return "Basic "
                + Base64.encodeToString(accessCredential.getBytes(),
                Base64.NO_WRAP);
    }
}
