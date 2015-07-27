package com.wanda.fangke.instagram.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedVignetteBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.wanda.fangke.instagram.R;
import com.wanda.fangke.instagram.Utils;

import org.jinstagram.entity.users.feed.MediaFeedData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by fangke on 2015/6/12.
 */
public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private static final int ANIMATED_ITEMS_COUNT = 2;

    private Context context;
    private List<MediaFeedData> feeds = new ArrayList<>();
    private ImageLoader imageLoader;
    private int lastAnimatedPosition = -1;
    private int itemsCount = 0;
    private OnFeedItemClickListener onFeedItemClickListener;

    private final ArrayList<Integer> likedPositions = new ArrayList<>();

    public FeedAdapter(Context context) {
        this.context = context;
        configureUIL();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_feed, parent, false);
        return new CellFeedViewHolder(view);
    }

    private void runEnterAnimation(View view, int position) {
        if (position >= ANIMATED_ITEMS_COUNT - 1) {
            return;
        }

        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            view.setTranslationY(Utils.getScreenHeight(context));
            view.animate()
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator(3.f))
                    .setDuration(700)
                    .start();
        }
    }

    private void configureUIL() {
        // Create global configuration and initialize ImageLoader with this config
        DisplayImageOptions displayOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .resetViewBeforeLoading(true)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(displayOptions)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .writeDebugLogs()
                .build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        //runEnterAnimation(viewHolder.itemView, position);
        MediaFeedData feedData = feeds.get(position);
        final CellFeedViewHolder holder = (CellFeedViewHolder) viewHolder;
        imageLoader.displayImage(feedData.getImages().getLowResolution().getImageUrl(), holder.ivFeedCenter, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
//                holder.ivProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//                holder.ivProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                holder.ivProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
//                holder.ivProgressBar.setVisibility(View.GONE);
            }
        });
        holder.ivFeedBottom.setImageResource(R.mipmap.img_feed_bottom_1);

        holder.ivFeedBottom.setOnClickListener(this);
        holder.btnMore.setOnClickListener(this);
        holder.ivFeedBottom.setTag(position);
        holder.btnMore.setTag(position);
        holder.btnLike.setOnClickListener(this);
        holder.btnLike.setTag(holder);
        holder.btnComments.setOnClickListener(this);
        holder.btnComments.setTag(position);
        int currentLikesCount = feedData.getLikes().getCount();
        holder.tsLikesCounter.setCurrentText(context.getResources().getQuantityString(R.plurals.likes_count, currentLikesCount, currentLikesCount));

        holder.tvUsername.setText(feedData.getUser().getUserName());

        imageLoader.displayImage(feedData.getUser().getProfilePictureUrl(), holder.ivProfilePhoto, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });


    }


    @Override
    public int getItemCount() {
        return feeds.size();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnComments) {
            if (onFeedItemClickListener != null) {
                onFeedItemClickListener.onCommentsClick(v, (Integer) v.getTag());
            }
        } else if (v.getId() == R.id.ivFeedBottom) {
            if (onFeedItemClickListener != null) {
                onFeedItemClickListener.onCommentsClick(v, (Integer) v.getTag());
            }
        } else if (v.getId() == R.id.btnMore) {
            if (onFeedItemClickListener != null) {
                onFeedItemClickListener.onMoreClick(v, (Integer) v.getTag());
            }
        } else if (v.getId() == R.id.btnLike) {
            CellFeedViewHolder holder = (CellFeedViewHolder) v.getTag();
            updateLikesCounter(holder, true);
        }

    }

    public static class CellFeedViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.ivFeedCenter)
        ImageView ivFeedCenter;
        @InjectView(R.id.ivFeedBottom)
        ImageView ivFeedBottom;
        @InjectView(R.id.btnMore)
        ImageButton btnMore;
        @InjectView(R.id.btnLike)
        ImageButton btnLike;
        @InjectView(R.id.vBgLike)
        View vBgLike;
        @InjectView(R.id.ivLike)
        ImageView ivLike;
        @InjectView(R.id.tsLikesCounter)
        TextSwitcher tsLikesCounter;
        @InjectView(R.id.ivProgressBar)
        ProgressBar ivProgressBar;
        @InjectView(R.id.btnComments)
        ImageButton btnComments;
        @InjectView(R.id.ivProfilePhoto)
        ImageView ivProfilePhoto;
        @InjectView(R.id.tvUsername)
        TextView tvUsername;

        public CellFeedViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }


    public void setOnFeedItemClickListener(OnFeedItemClickListener onFeedItemClickListener) {
        this.onFeedItemClickListener = onFeedItemClickListener;
    }

    public interface OnFeedItemClickListener {
        void onCommentsClick(View v, int position);

        void onMoreClick(View v, int position);
    }

    private void updateLikesCounter(CellFeedViewHolder holder, boolean animated) {
        int currentLikesCount = feeds.get(holder.getPosition()).getLikes().getCount() + 1;
        String likesCountText = context.getResources().getQuantityString(R.plurals.likes_count, currentLikesCount, currentLikesCount);
        if (animated) {
            holder.tsLikesCounter.setText(likesCountText);
        } else {
            holder.tsLikesCounter.setCurrentText(likesCountText);
        }

        //todo instagram like action here
        feeds.get(holder.getPosition()).getLikes().setCount(currentLikesCount);
    }

    public void setFeeds(List<MediaFeedData> feeds) {
        this.feeds = feeds;
//        fillLikesWithRandomValues();
    }
}
