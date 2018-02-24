package twitter.challenge.code.Tools;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import twitter.challenge.code.Trend;

public class Utils {
    public static void getTrendsArrayFromJson(@NonNull final String jsonResult,
                                              @NonNull final SerializerListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<Trend> trendsArray = new ArrayList<>();
                    JSONArray trendsMainObject = new JSONArray(jsonResult);
                    JSONArray trends = trendsMainObject.getJSONObject(0).optJSONArray("trends");
                    for (int i = 0; i < trends.length(); i++) {
                        JSONObject trend = trends.getJSONObject(i);
                        trendsArray.add(new Trend(trend.optString("name", "Empty"),
                                trend.optString("url", "Empty"),
                                trend.optBoolean("promoted_content", false),
                                trend.optString("query", "Empty"),
                                trend.optBoolean("tweet_volume", false)));
                    }
                    listener.serialisationComplete(trendsArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.serialisationError("Serialisation process failed");
                }
            }
        }).start();
    }
}
