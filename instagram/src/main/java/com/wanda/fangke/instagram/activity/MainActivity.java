package com.wanda.fangke.instagram.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.wanda.fangke.instagram.R;
import com.wanda.fangke.instagram.Utils;
import com.wanda.fangke.instagram.adapter.FeedAdapter;
import com.wanda.fangke.instagram.view.FeedContextMenu;
import com.wanda.fangke.instagram.view.FeedContextMenuManager;

import java.util.ArrayList;

import butterknife.InjectView;


public class MainActivity extends BaseActivity implements FeedAdapter.OnFeedItemClickListener, FeedContextMenu.onFeedContextMenuItemClickListener {

    @InjectView(R.id.rvFeed)
    RecyclerView rvFeed;
    @InjectView(R.id.btnCreate)
    ImageButton btnCreate;

    private FeedAdapter feedAdapter;
    private ArrayList<Bitmap> bitmaps;
    private boolean isOk = false;
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

    private void setupFeed() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        convertUrlToBitMap();
        rvFeed.setLayoutManager(linearLayoutManager);
        feedAdapter = new FeedAdapter(this, bitmaps);
        rvFeed.setAdapter(feedAdapter);
        feedAdapter.setOnFeedItemClickListener(this);
        rvFeed.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                FeedContextMenuManager.getManager().onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void convertUrlToBitMap() {
        ArrayList<String> urlList = getIntent().getStringArrayListExtra("URLLIST");
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .build();
        ImageLoader il = ImageLoader.getInstance();
        il.init(config);

        final Intent mainIntent = new Intent(this, MainActivity.class);
        bitmaps = new ArrayList<>();

        for (String url : urlList) {
            il.loadImage(url, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {


                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                    bitmaps.add(loadedImage);
                    if (isOk == false) {
                        startActivity(mainIntent);
                        isOk = true;
                    }
                    //feedAdapter.notifyDataSetChanged();

                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
            final Intent loadingIntent = new Intent(this, LoadingActivity.class);

            startActivity(loadingIntent);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (pendingIntroAnimation) {
            pendingIntroAnimation = false;
            startIntroAnimation();
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
        feedAdapter.updateItems();
    }

    @Override
    public void onCommentsClick(View v, int position) {
        final Intent intent = new Intent(this, CommentActivity.class);

        int[] startingLocation = new int[2];
        v.getLocationOnScreen(startingLocation);
        intent.putExtra(CommentActivity.ARG_DRAWING_START_LOCATION, startingLocation[1]);
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
}
