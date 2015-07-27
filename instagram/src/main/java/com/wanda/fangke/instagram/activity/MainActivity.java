package com.wanda.fangke.instagram.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.wanda.fangke.instagram.R;
import com.wanda.fangke.instagram.Utils;
import com.wanda.fangke.instagram.adapter.FeedAdapter;
import com.wanda.fangke.instagram.utils.CommentsWrapper;
import com.wanda.fangke.instagram.view.FeedContextMenu;
import com.wanda.fangke.instagram.view.FeedContextMenuManager;

import org.jinstagram.Instagram;
import org.jinstagram.auth.model.Token;
import org.jinstagram.entity.common.Comments;
import org.jinstagram.entity.users.feed.MediaFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;
import org.jinstagram.exceptions.InstagramException;
import org.jinstagram.model.QueryParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;


public class MainActivity extends BaseActivity implements FeedAdapter.OnFeedItemClickListener, FeedContextMenu.onFeedContextMenuItemClickListener {

    @InjectView(R.id.rvFeed)
    RecyclerView rvFeed;
    @InjectView(R.id.btnCreate)
    ImageButton btnCreate;

    private FeedAdapter feedAdapter;
    private List<MediaFeedData> feeds;
    private Boolean pendingIntroAnimation = false;
    private static final int ANIM_DURATION_TOOLBAR = 300;
    private static final int ANIM_DURATION_FAB = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupFeed();
        if (savedInstanceState == null) {
            pendingIntroAnimation = true;
        }
    }

    private void retrieveFeed() {
        final Intent loadingIntent = new Intent(this, LoadingActivity.class);
        startActivity(loadingIntent); // will stay until mediaFeedData arrives
        new RetrieveFeedTask(this).execute();
    }

    private void setupFeed() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        rvFeed.setLayoutManager(linearLayoutManager);
        feedAdapter = new FeedAdapter(this);
        rvFeed.setAdapter(feedAdapter);
        feedAdapter.setOnFeedItemClickListener(this);
        rvFeed.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                FeedContextMenuManager.getManager().onScrolled(recyclerView, dx, dy);
            }
        });
        retrieveFeed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (pendingIntroAnimation) {
            pendingIntroAnimation = false;
            //startIntroAnimation();
        }
        return true;
    }

    private void startIntroAnimation() {

        btnCreate.setTranslationY(2 * getResources().getDimensionPixelOffset(R.dimen.btn_fab_size));
        int actionBarsSize = Utils.dpToPx(56);
        toolbar.setTranslationY(-actionBarsSize);
        ivLogo.setTranslationY(-actionBarsSize);
        getInboxMenuItem().getActionView().setTranslationY(-actionBarsSize);

        toolbar.animate().translationY(0).setDuration(ANIM_DURATION_TOOLBAR).setStartDelay(300);
        ivLogo.animate().translationY(0).setDuration(ANIM_DURATION_TOOLBAR).setStartDelay(400);
        getInboxMenuItem().getActionView().animate().translationY(0).setDuration(ANIM_DURATION_TOOLBAR).setStartDelay(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        startContentAnimation();
                    }
                }).start();
    }

    private void startContentAnimation() {

        btnCreate.animate().translationY(0)
                .setInterpolator(new OvershootInterpolator(1.0f))
                .setStartDelay(300).setDuration(ANIM_DURATION_FAB).start();
        //feedAdapter.updateItems();
    }

    @Override
    public void onCommentsClick(View v, int position) {
        final Intent intent = new Intent(this, CommentActivity.class);
        Comments comments = feeds.get(position).getComments();
        int[] startingLocation = new int[2];
        v.getLocationOnScreen(startingLocation);
        intent.putExtra(CommentActivity.ARG_DRAWING_START_LOCATION, startingLocation[1]);
        CommentsWrapper commentWrap = new CommentsWrapper();
        commentWrap.setComments(comments);
        intent.putExtra("COMMENT", commentWrap);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onMoreClick(View v, int itemPosition) {
        FeedContextMenuManager.getManager().toggleContextMenuFromView(v, itemPosition, this);
    }

    @Override
    public void onReportClick(int feedItem) {

    }

    @Override
    public void onSharePhotoClick(int feedItem) {

    }

    @Override
    public void onCopyShareUrlClick(int feedItem) {

    }

    @Override
    public void onCancelClick(int feedItem) {

    }

    private class RetrieveFeedTask extends AsyncTask<String, Void, String> {
        private Context mContext;

        public RetrieveFeedTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPostExecute(String result) {

        }

        @Override
        protected String doInBackground(String... params) {
            Token accessToken = (Token) getIntent().getSerializableExtra("ACCESS_TOKEN");
            Instagram instagram = new Instagram(accessToken);
            double latitude = 41.075425;
            double longitude = 29.034963;
            MediaFeed feed = null;
            Map<String, String> options = new HashMap<>();

            try {
                options.put(QueryParam.COUNT, "10");
                feed = instagram.searchMedia(latitude, longitude, options);
            } catch (InstagramException e) {
                e.printStackTrace();
            }
            feeds = feed.getData();
            feedAdapter.setFeeds(feeds);
            final Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(mainIntent);

            return null;
        }
    }
}
