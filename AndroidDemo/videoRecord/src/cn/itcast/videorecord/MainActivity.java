package cn.itcast.videorecord;

import java.io.File;

import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
    private SurfaceView surfaceView;
    private MediaRecorder mediaRecorder;
    private boolean record;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mediaRecorder = new MediaRecorder();
        surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView);
        /*��������Surface��ά���Լ��Ļ����������ǵȴ���Ļ����Ⱦ���潫�������͵��û���ǰ*/
        this.surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        this.surfaceView.getHolder().setFixedSize(320, 240);//���÷ֱ���

        ButtonClickListener listener = new ButtonClickListener();
        Button stopButton = (Button) this.findViewById(R.id.stop);
        Button recordButton = (Button) this.findViewById(R.id.record);
        stopButton.setOnClickListener(listener);
        recordButton.setOnClickListener(listener);        
    }
    
    @Override
	protected void onDestroy() {
    	mediaRecorder.release();
		super.onDestroy();
	}

	private final class ButtonClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				Toast.makeText(MainActivity.this, R.string.sdcarderror, 1).show();
				return ;
			}
			try {
				switch (v.getId()) {
				case R.id.record:
					mediaRecorder.reset();
					mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA); //��������ɼ���Ƶ
					mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); 
					mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
					mediaRecorder.setVideoSize(320, 240);
					mediaRecorder.setVideoFrameRate(3); //ÿ��3֡
					mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H263); //������Ƶ���뷽ʽ
					mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
					File videoFile = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis()+".3gp");
					mediaRecorder.setOutputFile(videoFile.getAbsolutePath());
					mediaRecorder.setPreviewDisplay(surfaceView.getHolder().getSurface());
					mediaRecorder.prepare();//Ԥ��׼��
					mediaRecorder.start();//��ʼ��¼
					record = true;
					break;

				case R.id.stop:
					if(record){
						mediaRecorder.stop();
						record = false;
					}
					break;
				}
			} catch (Exception e) {
				Toast.makeText(MainActivity.this, R.string.error, 1).show();
				Log.e(TAG, e.toString());
			}
		}
    	
    }
}