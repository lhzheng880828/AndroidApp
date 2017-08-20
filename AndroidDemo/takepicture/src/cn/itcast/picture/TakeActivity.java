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
    	requestWindowFeature(Window.FEATURE_NO_TITLE);//没有标题
    	window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
    	window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//高亮

        setContentView(R.layout.main);
        
        surfaceView =(SurfaceView)this.findViewById(R.id.surfaceView);
        surfaceView.getHolder().setFixedSize(176, 144);	//设置分辨率
        /*下面设置Surface不维护自己的缓冲区，而是等待屏幕的渲染引擎将内容推送到用户面前*/
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
				parameters.setPreviewSize(display.getWidth(), display.getHeight());//设置预览照片的大小
				parameters.setPreviewFrameRate(3);//每秒3帧
				parameters.setPictureFormat(PixelFormat.JPEG);//设置照片的输出格式
				parameters.set("jpeg-quality", 85);//照片质量
				parameters.setPictureSize(display.getWidth(), display.getHeight());//设置照片的大小
				camera.setParameters(parameters);
				camera.setPreviewDisplay(surfaceView.getHolder());//通过SurfaceView显示取景画面
				camera.startPreview();//开始预览
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
				camera.autoFocus(null);//自动对焦
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
				camera.startPreview();//开始预览
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}
		}
	}
    
}