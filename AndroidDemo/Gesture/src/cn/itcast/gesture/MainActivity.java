package cn.itcast.gesture;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {
    private boolean success;
    private GestureLibrary library;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //�ҵ����ƿ�
        library = GestureLibraries.fromRawResource(this, R.raw.gestures);
        //�������ƿ�
        success = library.load();
        GestureOverlayView gestureView = (GestureOverlayView)this.findViewById(R.id.gestures);
        gestureView.addOnGesturePerformedListener(new GestureListener());

    }
    
    private final class GestureListener implements OnGesturePerformedListener{
		@Override
		public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
			if(success){
				//�����ƿ��в���ƥ�������,��ƥ��ļ�¼�������ǰ��
				ArrayList<Prediction> predictions = library.recognize(gesture);
				if(!predictions.isEmpty()){
					Prediction prediction = predictions.get(0);
					Log.i("MainActivity", "score:"+ prediction.score);
					if(prediction.score>3){
						if("agree".equals(prediction.name)){
							android.os.Process.killProcess(android.os.Process.myPid());
						}else if("5556".equals(prediction.name)){
							Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:5556"));
							startActivity(intent);
						}
					}
				}
			}
		}
    	
    }
}