package com.u1fukui.exoplayer_demos;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;

public class PlayerFragment extends Fragment {

    public static PlayerFragment newInstance() {
        return new PlayerFragment();
    }

    private static final String TAG = "PlayerFragment";

    // https://developer.apple.com/streaming/examples/
    private static final String CONTENT_URL = "https://devstreaming-cdn.apple.com/videos/streaming/examples/img_bipbop_adv_example_ts/master.m3u8";

    private SimpleExoPlayer player;

    private SurfaceView surfaceView;

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach");
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        initPlayer();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        surfaceView = view.findViewById(R.id.surface_view);
        player.setVideoSurfaceView(surfaceView);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        preparePlayer(Uri.parse(CONTENT_URL));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView");
        if (player != null) {
            player.setVideoSurfaceView(null);
        }
        surfaceView = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        if (player != null) {
            player.release();
            player = null;
        }
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");
        super.onDetach();
    }

    private void initPlayer() {
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        player = ExoPlayerFactory.newSimpleInstance(getContext(), new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter)));
    }

    private void preparePlayer(Uri contentUri) {
        AdaptiveMediaSourceEventListener eventListener = new AdaptiveMediaSourceEventListener() {
            @Override
            public void onLoadStarted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs) {
            }

            @Override
            public void onLoadCompleted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {
                player.setPlayWhenReady(true);
            }

            @Override
            public void onLoadCanceled(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {
            }

            @Override
            public void onLoadError(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded, IOException error, boolean wasCanceled) {
            }

            @Override
            public void onUpstreamDiscarded(int trackType, long mediaStartTimeMs, long mediaEndTimeMs) {
            }

            @Override
            public void onDownstreamFormatChanged(int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaTimeMs) {
            }
        };

        Handler handler = new Handler();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getContext(), Util.getUserAgent(getContext(), "ExoPlayerDemo"));

        player.prepare(new HlsMediaSource(contentUri, dataSourceFactory, handler, eventListener));
    }
}
