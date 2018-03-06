package twitter.challenge.code;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import twitter.challenge.code.databinding.TrendViewBinding;

public class TrendsRecyclerAdapter extends RecyclerView.Adapter<TrendsRecyclerAdapter.TrendsViewHolder> {
    @NonNull
    private ArrayList<Trend> trends;
    @NonNull
    private final ItemClickListener clickListener;

    public TrendsRecyclerAdapter(@NonNull final ArrayList<Trend> trends,
                                 @NonNull final ItemClickListener clickListener) {
        this.trends = trends;
        this.clickListener = clickListener;
    }

    @Override
    public TrendsViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final TrendViewBinding suggestionInListBinding = TrendViewBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false);
        final DisplayMetrics displaymetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            parent.getDisplay().getMetrics(displaymetrics);
        }
        suggestionInListBinding.getRoot().getLayoutParams().width = displaymetrics.widthPixels;
        return new TrendsViewHolder(suggestionInListBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final TrendsViewHolder holder, final int position) {
        final Trend object = trends.get(position);
        holder.bind(object);
    }

    @Override
    public int getItemCount() {
        return trends.size();
    }

    public void updateTrendsList(@NonNull final ArrayList<Trend> trends) {
        if (trends.isEmpty()) {
            return;
        }
        if (!this.trends.isEmpty()) {
            this.trends.clear();
        }
        this.trends = trends;
        notifyDataSetChanged();
    }

    class TrendsViewHolder extends RecyclerView.ViewHolder {
        @NonNull
        private final TrendViewBinding trendViewBinding;

        TrendsViewHolder(@NonNull final TrendViewBinding bindingElement) {
            super(bindingElement.getRoot());
            this.trendViewBinding = bindingElement;
        }

        void bind(Trend item) {
            trendViewBinding.setVariable(BR.trend, item);
            trendViewBinding.urlView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClicked(((TextView) v).getText().toString());
                }
            });
            trendViewBinding.executePendingBindings();
        }
    }

    public interface ItemClickListener {
        void onItemClicked(final String url);
    }
}
