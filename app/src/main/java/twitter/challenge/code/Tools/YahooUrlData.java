package twitter.challenge.code.Tools;

import twitter.challenge.code.BuildConfig;

//https://api.gemini.yahoo.com/v3/rest/dictionary/woeid?location=california&type=state
//Berlin	Berlin	Germany	638242
//Berlin	Berlin	Germany	12596838
public class YahooUrlData {
    public static final String URL_ROOT_TWITTER_API = "https://api.twitter.com";
    public static final String GET_TOKEN_URL = "/oauth2/token";
    public static final String URL_BERLIN_TRENDING ="https://api.twitter.com/1.1/trends/place.json?id=";
    public static final String CLIENT_ID = BuildConfig.CONSUMER_KEY;
    public static final String CLIENT_SECRET = BuildConfig.CONSUMER_SECRET;

    private String street;
    private String city;
    private String state;
    private String zip;
    private String latitude;
    private String longitude;

    YahooUrlData(String street, String city, String state, String zip, String latitude, String longitude) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZip() {
        return zip;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getYahooUrlParams() {
        //        result.append("street=");
//        result.append(street);
//        result.append("&city=");
//        result.append(city);
//        result.append("&state=");
//        result.append(state);
//        result.append("&zip=");
//        result.append(zip);
        return latitude +
                "," +
                longitude +
                "')?appid=Twitter-Trends-Client&format=json";
    }
}
