package cn.itcast.audio;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PlayActivity extends Activity {
	private static final String TAG = "PlayActivity";
    private EditText filenameText;
    private MediaPlayer mediaPlayer;
    private String filename;
    private int position;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        filenameText = (EditText)this.findViewById(R.id.filename);
       
        mediaPlayer = new MediaPlayer();
        ButtonClickListener listener = new ButtonClickListener();
        Button playButton = (Button)this.findViewById(R.id.play);
        Button pauseButton = (Button)this.findViewById(R.id.pause);
        Button resetButton = (Button)this.findViewById(R.id.reset);
        Button stopButton = (Button) this.findViewById(R.id.stop);
        playButton.setOnClickListener(listener);
        pauseButton.setOnClickListener(listener);
        resetButton.setOnClickListener(listener);
        stopButton.setOnClickListener(listener);
        Log.i(TAG, "onCreate()");
    }
    
    @Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		this.filename = savedInstanceState.getString("filename");
		this.position = savedInstanceState.getInt("position");
		super.onRestoreInstanceState(savedInstanceState);
		Log.i(TAG, "onRestoreInstanceState()");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("filename", filename);
		outState.putInt("position", position);
		super.onSaveInstanceState(outState);
		Log.i(TAG, "onSaveInstanceState()");
	}

	@Override
	protected void onPause() {//���ͻȻ�绰������ֹͣ��������
		if(mediaPlayer.isPlaying()){
			position = mediaPlayer.getCurrentPosition();//���浱ǰ���ŵ�
			mediaPlayer.stop();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		if(position>0 && filename!=null){//����绰������������������
			try {
				play();
				mediaPlayer.seekTo(position);
				position = 0;
			} catch (IOException e) {
				Log.e(TAG, e.toString());
			}
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {
    	mediaPlayer.release();     
		super.onDestroy();
		Log.i(TAG, "onDestroy()");
	}

	private final class ButtonClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			filename = filenameText.getText().toString();//�ȵõ��ı����е�����
			Button button = (Button) v;//�õ�button 
			try {
				switch (v.getId()) {//ͨ����������Buttonid�����ж�Button������
				case R.id.play://����
					play();
					break;

				case R.id.pause:
					if(mediaPlayer.isPlaying()){
						mediaPlayer.pause();
						button.setText(R.string.continue1);//�������ť�ϵ�������ʾΪ����
					}else{
						mediaPlayer.start();//��������
						button.setText(R.string.pause);
					}
					break;
					
				case R.id.reset:
					if(mediaPlayer.isPlaying()){
						mediaPlayer.seekTo(0);//������0��ʼ����
					}else{
						play();//�����û�в��ţ���������ʼ����
					}
					break;
					
				case R.id.stop:
					if(mediaPlayer.isPlaying()) mediaPlayer.stop();//��������ڲ��ŵĻ���������ֹͣ
					break;
				}
			} catch (Exception e) {//�׳��쳣
				Log.e(TAG, e.toString());
			}
		}		
    }
	private void play() throws IOException {
		File audioFile = new File(Environment.getExternalStorageDirectory(),filename);
		mediaPlayer.reset();
		mediaPlayer.setDataSource(audioFile.getAbsolutePath());
		mediaPlayer.prepare();
		mediaPlayer.start();//����
	}
}