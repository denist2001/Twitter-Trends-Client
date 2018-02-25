package twitter.challenge.code.listeners;

import java.util.ArrayList;

import twitter.challenge.code.Trend;

public interface SerializerListener {
    void serialisationComplete(ArrayList<Trend> trends);
    void serialisationError(String errorMessage);
}
