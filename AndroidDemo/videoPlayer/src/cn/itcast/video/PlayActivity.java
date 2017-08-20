package cn.itcast.video;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class PlayActivity extends Activity {
	private static final String TAG = "PlayActivity";
    private EditText filenameText;
    private MediaPlayer mediaPlayer;
    private SurfaceView surfaceView;
    private String filename;
    private int position;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        filenameText = (EditText)this.findViewById(R.id.filename);
        
        surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView);
        surfaceView.getHolder().setFixedSize(176, 144);//设置分辨率
        surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceView.getHolder().addCallback(new SurfaceCallback());

        mediaPlayer = new MediaPlayer();
        ButtonClickListener listener = new ButtonClickListener();
        ImageButton playButton = (ImageButton)this.findViewById(R.id.play);
        ImageButton pauseButton = (ImageButton)this.findViewById(R.id.pause);
        ImageButton resetButton = (ImageButton)this.findViewById(R.id.reset);
        ImageButton stopButton = (ImageButton) this.findViewById(R.id.stop);
        playButton.setOnClickListener(listener);
        pauseButton.setOnClickListener(listener);
        resetButton.setOnClickListener(listener);
        stopButton.setOnClickListener(listener);
    }
    
    private final class SurfaceCallback implements SurfaceHolder.Callback{
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			if(position>0 && filename!=null){
				try {
					play();
					mediaPlayer.seekTo(position);
					position = 0;
				} catch (IOException e) {
					Log.e(TAG, e.toString());
				}
			}
		}
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
		}
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			if(mediaPlayer.isPlaying()){
				position = mediaPlayer.getCurrentPosition();
				mediaPlayer.stop();
			}
		}    	
    }

	private final class ButtonClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				Toast.makeText(PlayActivity.this, R.string.sdcarderror, 1).show();
				return;
			}
			filename = filenameText.getText().toString();
			try {
				switch (v.getId()) {
				case R.id.play:
					play();					
					break;

				case R.id.pause:
					if(mediaPlayer.isPlaying()){
						mediaPlayer.pause();
					}else{
						mediaPlayer.start();
					}
					break;
				case R.id.reset:
					if(mediaPlayer.isPlaying()){
						mediaPlayer.seekTo(0);
					}else{
						play();
					}
					break;
				case R.id.stop:
					if(mediaPlayer.isPlaying()) mediaPlayer.stop();
					break;
				}
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}
		}
    }

	private void play() throws IOException {
		File videoFile = new File(Environment.getExternalStorageDirectory(), filename);
		mediaPlayer.reset();//重置为初始状态
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		/* 设置Video影片以SurfaceHolder播放 */
		mediaPlayer.setDisplay(surfaceView.getHolder());
		mediaPlayer.setDataSource(videoFile.getAbsolutePath());
		mediaPlayer.prepare();//缓冲				
		mediaPlayer.start();//播放
	}
}