package cn.itcast.picture;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

public class TakeActivity extends Activity {
	private static final String TAG = "TakeActivity";
    private SurfaceView surfaceView;
    private Camera camera;
    private boolean preview;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
    	requestWindowFeature(Window.FEATURE_NO_TITLE);//û�б���
    	window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);// ����ȫ��
    	window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//����

        setContentView(R.layout.main);
        
        surfaceView =(SurfaceView)this.findViewById(R.id.surfaceView);
        surfaceView.getHolder().setFixedSize(176, 144);	//���÷ֱ���
        /*��������Surface��ά���Լ��Ļ����������ǵȴ���Ļ����Ⱦ���潫�������͵��û���ǰ*/
        surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceView.getHolder().addCallback(new SurfaceCallback());
    }
    
    private final class SurfaceCallback implements SurfaceHolder.Callback{

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			try {
				camera = Camera.open();
				WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
				Display display = wm.getDefaultDisplay();
				Camera.Parameters parameters = camera.getParameters();
				parameters.setPreviewSize(display.getWidth(), display.getHeight());//����Ԥ����Ƭ�Ĵ�С
				parameters.setPreviewFrameRate(3);//ÿ��3֡
				parameters.setPictureFormat(PixelFormat.JPEG);//������Ƭ�������ʽ
				parameters.set("jpeg-quality", 85);//��Ƭ����
				parameters.setPictureSize(display.getWidth(), display.getHeight());//������Ƭ�Ĵ�С
				camera.setParameters(parameters);
				camera.setPreviewDisplay(surfaceView.getHolder());//ͨ��SurfaceView��ʾȡ������
				camera.startPreview();//��ʼԤ��
				preview = true;
			} catch (IOException e) {
				Log.e(TAG, e.toString());
			}

		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			if(camera!=null){
				if(preview) camera.stopPreview();
				camera.release();
			}
		}
    	
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(camera!=null && event.getRepeatCount()==0){
			switch (keyCode) {
			case KeyEvent.KEYCODE_SEARCH:
				camera.autoFocus(null);//�Զ��Խ�
				break;

			case KeyEvent.KEYCODE_CAMERA:
			case KeyEvent.KEYCODE_DPAD_CENTER:
				camera.takePicture(null, null, new TakePictureCallback());
				break;
			}
		}
		return true;
	}
	
	private final class TakePictureCallback implements PictureCallback{
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			try {
				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
				File file = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis()+".jpg");
				FileOutputStream outStream = new FileOutputStream(file);
				bitmap.compress(CompressFormat.JPEG, 100, outStream);
				outStream.close();
				camera.stopPreview();
				camera.startPreview();//��ʼԤ��
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}
		}
	}
    
}