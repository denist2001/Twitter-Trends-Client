package twitter.challenge.code;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class TrendsViewModelFactory implements ViewModelProvider.Factory {

    private final TwitterDataLoaderListener listener;

    TrendsViewModelFactory(TwitterDataLoaderListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull final Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TrendsViewModel.class)) {
            return (T) new TrendsViewModel(listener);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
