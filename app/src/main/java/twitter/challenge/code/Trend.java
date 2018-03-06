package twitter.challenge.code;

import android.support.annotation.Nullable;

public class Trend {
    @Nullable
    private final String name;
    @Nullable
    private final String url;
    @Nullable
    private final Boolean promoted_content;
    @Nullable
    private final String query;
    @Nullable
    private final Boolean tweet_volume;

    public Trend(@Nullable final String name,
                 @Nullable final String url,
                 @Nullable final Boolean promoted_content,
                 @Nullable final String query,
                 @Nullable final Boolean tweet_volume) {
        this.name = name;
        this.url = url;
        this.promoted_content = promoted_content;
        this.query = query;
        this.tweet_volume = tweet_volume;
    }

    @Nullable
    public String getName() {
        return name;
    }

    @Nullable
    public String getUrl() {
        return url;
    }

    @Nullable
    public Boolean getPromoted_content() {
        return promoted_content;
    }

    @Nullable
    public String getQuery() {
        return query;
    }

    @Nullable
    public Boolean getTweet_volume() {
        return tweet_volume;
    }
}
