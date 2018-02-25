package twitter.challenge.code.listeners;

public interface TwitterDataLoaderListener {
    void onDataLoaded(String jsonResult);
    void onDataFailed(String errorMessage);
}
