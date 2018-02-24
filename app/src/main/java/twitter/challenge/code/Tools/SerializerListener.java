package twitter.challenge.code.Tools;

import java.util.ArrayList;

import twitter.challenge.code.Trend;

public interface SerializerListener {
    void serialisationComplete(ArrayList<Trend> trends);
    void serialisationError(String errorMessage);
}
