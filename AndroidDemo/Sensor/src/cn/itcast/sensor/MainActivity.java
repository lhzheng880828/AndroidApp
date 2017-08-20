package cn.itcast.sensor;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {
    private TextView accelerometerView;
    private TextView orientationView;
    private SensorManager sensorManager;
    private MySensorEventListener sensorEventListener;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        sensorEventListener = new MySensorEventListener();
        accelerometerView = (TextView) this.findViewById(R.id.accelerometerView);
        orientationView = (TextView) this.findViewById(R.id.orientationView);
        //��ȡ��Ӧ��������
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }
	@Override
	protected void onResume() {
		Sensor orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		sensorManager.registerListener(sensorEventListener, orientationSensor, SensorManager.SENSOR_DELAY_NORMAL);
		
		Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(sensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
		super.onResume();
	}
	private final class MySensorEventListener implements SensorEventListener{
		@Override
		public void onSensorChanged(SensorEvent event) {//���Եõ�������ʵʱ���������ı仯ֵ
			if(event.sensor.getType()==Sensor.TYPE_ORIENTATION){
				float x = event.values[SensorManager.DATA_X];      
				float y = event.values[SensorManager.DATA_Y];      
	        	float z = event.values[SensorManager.DATA_Z];  
				orientationView.setText("Orientation: " + x + ", " + y + ", " + z); 
			}else if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
				float x = event.values[SensorManager.DATA_X];      
				float y = event.values[SensorManager.DATA_Y];      
	        	float z = event.values[SensorManager.DATA_Z]; 
	        	accelerometerView.setText("Accelerometer: " + x + ", " + y + ", " + z); 
			}
		
		}
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	}
	
	@Override
	protected void onPause() {
		sensorManager.unregisterListener(sensorEventListener);
		super.onPause();
	}

    
}