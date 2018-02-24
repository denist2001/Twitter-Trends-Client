package twitter.challenge.code;

public interface TwitterDataLoaderListener {
    void onDataLoaded(String jsonResult);
    void onDataFailed(String errorMessage);
}
