package com.example.allen.allenaudio;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Allen on 2016/11/16.
 */
public class MusicService extends MediaBrowserServiceCompat implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private static final String TAG = MusicService.class.getSimpleName();
    private ArrayList<MediaSessionCompat.QueueItem> mPlayingQueue;
    private MediaSessionCompat mMediaSession;
    private int mCurrentQueueIndex;
    private MediaPlayer mMediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();

        mCurrentQueueIndex = 0;
        mPlayingQueue = new ArrayList<>();

        // 建立MediaSession
        mMediaSession = new MediaSessionCompat(this, TAG);

        mMediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlayFromMediaId(String mediaId, Bundle extras) {
                super.onPlayFromMediaId(mediaId, extras);
                Log.d(TAG, "onPlayFromMediaId, mediaId = " + mediaId
                        + ", index = " + extras.getInt("index")
                        + ", path = " + extras.getString("path"));
                for(MediaSessionCompat.QueueItem queueItem :mPlayingQueue){
                    MediaDescriptionCompat des = queueItem.getDescription();
                    if(mediaId.equals(des.getMediaId())){
                        mCurrentQueueIndex = des.getExtras().getInt("index");
                        Log.d(TAG, "mCurrentQueueIndex = " + mCurrentQueueIndex);
                        break;
                    }
                }
                playMusic();
            }

            @Override
            public void onPlayFromUri(Uri uri, Bundle extras) {
                super.onPlayFromUri(uri, extras);
                Log.d(TAG, "onPlayFromUri");
            }

            @Override
            public void onCommand(String command, Bundle extras, ResultReceiver cb) {
                super.onCommand(command, extras, cb);
                Log.d(TAG, "onCommand");
            }

            @Override
            public void onPlay() {
                super.onPlay();
                Log.d(TAG, "onPlay");
                resumeMusic();
            }

            @Override
            public void onPause() {
                super.onPause();
                Log.d(TAG, "onPause");
                pauseMusic();
            }

            @Override
            public void onStop() {
                super.onStop();
                Log.d(TAG, "onStop");
            }

            @Override
            public void onSkipToQueueItem(long id) {
                super.onSkipToQueueItem(id);
                // 點擊queue中的歌曲，會到這裡
                Log.d(TAG, "onSkipToQueueItem, id = " + id);
                for(MediaSessionCompat.QueueItem item :mPlayingQueue){
                    if (id == item.getQueueId()) {
                        mCurrentQueueIndex = item.getDescription().getExtras().getInt("index");
                        Log.d(TAG, "mCurrentQueueIndex = " + mCurrentQueueIndex);
                        break;
                    }
                }
                playMusic();
            }
        });

        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setActive(true);
        setSessionToken(mMediaSession.getSessionToken());

    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        Log.d(TAG, "onGetRoot");
        return new BrowserRoot("xxx", null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        Log.d(TAG, "onLoadChildren, parentId = " + parentId);
        ArrayList<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();

        //
        Bundle bundle1 = new Bundle();
        bundle1.putInt("index", 0);
        bundle1.putString("path", "android.resource://" + getPackageName() + "/" + R.raw.lcmw_0);
        MediaDescriptionCompat.Builder builder1 = new MediaDescriptionCompat.Builder();
        builder1.setMediaId("ID_1");
        builder1.setTitle("Title_1");
        builder1.setSubtitle("Subtitle_1");
        builder1.setExtras(bundle1);

        Bitmap bmp1 = BitmapFactory.decodeResource(getResources(), R.drawable.pic_album1);
        builder1.setIconBitmap(bmp1);

        MediaBrowserCompat.MediaItem item1 = new MediaBrowserCompat.MediaItem(builder1.build(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);
        mediaItems.add(item1);

        //
        Bundle bundle2 = new Bundle();
        bundle2.putInt("index", 1);
        bundle2.putString("path", "android.resource://" + getPackageName() + "/" + R.raw.lcmw_1);
        MediaDescriptionCompat.Builder builder2 = new MediaDescriptionCompat.Builder();
        builder2.setMediaId("ID_2");
        builder2.setTitle("Title_2");
        builder2.setSubtitle("Subtitle_2");
        builder2.setExtras(bundle2);

        Bitmap bmp2 = BitmapFactory.decodeResource(getResources(), R.drawable.pic_album2);
        builder2.setIconBitmap(bmp2);

        MediaBrowserCompat.MediaItem item2 = new MediaBrowserCompat.MediaItem(builder2.build(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);
        mediaItems.add(item2);

        //
        Bundle bundle3 = new Bundle();
        bundle3.putInt("index", 2);
        bundle3.putString("path", "android.resource://" + getPackageName() + "/" + R.raw.lcmw_2);
        MediaDescriptionCompat.Builder builder3 = new MediaDescriptionCompat.Builder();
        builder3.setMediaId("ID_3");
        builder3.setTitle("Title_3");
        builder3.setSubtitle("Subtitle_3");
        builder3.setExtras(bundle3);

        Bitmap bmp3 = BitmapFactory.decodeResource(getResources(), R.drawable.pic_album3);
        builder3.setIconBitmap(bmp3);

        MediaBrowserCompat.MediaItem item3 = new MediaBrowserCompat.MediaItem(builder3.build(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);
        mediaItems.add(item3);

        //
        result.detach();
        result.sendResult(mediaItems);

        mPlayingQueue.add(new MediaSessionCompat.QueueItem(builder1.build(), 0));
        mPlayingQueue.add(new MediaSessionCompat.QueueItem(builder2.build(), 1));
        mPlayingQueue.add(new MediaSessionCompat.QueueItem(builder3.build(), 2));
        mMediaSession.setQueue(mPlayingQueue);
        mMediaSession.setQueueTitle("待播放清單");
    }

    private void playMusic() {
        if(mMediaPlayer == null){
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnErrorListener(this);
        }else{
            mMediaPlayer.reset();
        }

        MediaSessionCompat.QueueItem queueItem = mPlayingQueue.get(mCurrentQueueIndex);
        String path = queueItem.getDescription().getExtras().getString("path");

        try{
            mMediaPlayer.setDataSource(this, Uri.parse(path));
            mMediaPlayer.prepareAsync();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void pauseMusic() {
        if(mMediaPlayer != null){
            mMediaPlayer.pause();
            mMediaSession.setPlaybackState(new PlaybackStateCompat.Builder().setState(PlaybackStateCompat.STATE_PAUSED, mMediaPlayer.getCurrentPosition(), 1).setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS).build());
        }
    }

    private void resumeMusic() {
        if(mMediaPlayer!=null){
            mMediaPlayer.start();
            mMediaSession.setPlaybackState(new PlaybackStateCompat.Builder().setState(PlaybackStateCompat.STATE_PLAYING, mMediaPlayer.getCurrentPosition(), 1).setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS).build());
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "onPrepared");
        if(mMediaPlayer!=null){
            mMediaPlayer.start();

            // update playback state
            mMediaSession.setPlaybackState(new PlaybackStateCompat.Builder().setState(PlaybackStateCompat.STATE_PLAYING, mMediaPlayer.getCurrentPosition(), 1).setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS).build());

            // set meta data
            MediaMetadataCompat.Builder bob = new MediaMetadataCompat.Builder();
            bob.putString(MediaMetadataCompat.METADATA_KEY_TITLE, (String) mPlayingQueue.get(mCurrentQueueIndex).getDescription().getTitle());
            bob.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, (String) mPlayingQueue.get(mCurrentQueueIndex).getDescription().getSubtitle());
            bob.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, mPlayingQueue.get(mCurrentQueueIndex).getDescription().getIconBitmap());
//            bob.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, mCurrentSongItem != null ? mCurrentSongItem.getSingerName() : null);
//            bob.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, mCurrentSongItem != null ? mCurrentSongItem.getSingerName() : null);
            bob.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mp.getDuration());
            mMediaSession.setMetadata(bob.build());

            // show notification
            Intent intent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setLargeIcon(b)
                    .setContentIntent(pendingIntent)
                    .setContentTitle((String) mPlayingQueue.get(mCurrentQueueIndex).getDescription().getTitle())
                    .setContentText((String) mPlayingQueue.get(mCurrentQueueIndex).getDescription().getSubtitle())
                    .setTicker((String) mPlayingQueue.get(mCurrentQueueIndex).getDescription().getTitle())
//                    .setShowWhen(false)
//                    .setWhen(0);
                    .setWhen(System.currentTimeMillis() - mMediaPlayer.getCurrentPosition())
                    .setShowWhen(true)
                    .setUsesChronometer(true);

                builder.setStyle(new NotificationCompat.MediaStyle()
                        .setMediaSession(mMediaSession.getSessionToken())
//                        .setShowActionsInCompactView(0, 1, 2)
                );

            startForeground(99, builder.build());
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "onCompletion");
        mMediaSession.setPlaybackState(new PlaybackStateCompat.Builder().setState(PlaybackStateCompat.STATE_NONE, 0, 0.5f).setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS).build());
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        mMediaSession.setCallback(null);
        mMediaSession.release();

        if(mMediaPlayer!=null){
            mMediaPlayer.setOnPreparedListener(null);
            mMediaPlayer.setOnCompletionListener(null);
            mMediaPlayer.setOnErrorListener(null);
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
