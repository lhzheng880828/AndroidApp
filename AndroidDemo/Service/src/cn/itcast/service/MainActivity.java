package cn.itcast.service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
    private MyServiceConnection conn;
    private IService myservice;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        conn = new MyServiceConnection();
        Intent intent = new Intent(this, MyService.class);
        bindService(intent, conn, BIND_AUTO_CREATE);
        Button button = (Button) this.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				TextView resultView = (TextView) findViewById(R.id.result);
				resultView.setText(myservice.getName(56));
			}
		});
    }
    
    private final class MyServiceConnection implements ServiceConnection{
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			myservice = (IService)service;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			myservice = null;
		}
    }

	@Override
	protected void onDestroy() {
		unbindService(conn);
		super.onDestroy();
	}
    
}