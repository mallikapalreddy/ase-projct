package com.hackathon.myyoutube;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.google.android.youtube.player.YouTubeThumbnailView.OnInitializedListener;
import com.hackathon.myyoutube.helper.YoutubeConnector;

public class PlayerActivity extends YouTubeBaseActivity implements
		OnInitializedListener, com.google.android.youtube.player.YouTubePlayer.OnInitializedListener {

	private YouTubePlayerView playerView;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		setContentView(R.layout.activity_player);

		playerView = (YouTubePlayerView) findViewById(R.id.player_view);
		playerView.initialize(YoutubeConnector.KEY, this);
	}

	@Override
	public void onInitializationFailure(Provider provider,
			YouTubeInitializationResult result) {
		Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_LONG)
				.show();
	}

	@Override
	public void onInitializationSuccess(Provider provider,
			YouTubePlayer player, boolean restored) {
		if (!restored) {
			player.cueVideo(getIntent().getStringExtra("VIDEO_ID"));
		}
	}

	@Override
	public void onInitializationFailure(YouTubeThumbnailView arg0,
			YouTubeInitializationResult arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onInitializationSuccess(YouTubeThumbnailView arg0,
			YouTubeThumbnailLoader arg1) {
		// TODO Auto-generated method stub
		
	}
}