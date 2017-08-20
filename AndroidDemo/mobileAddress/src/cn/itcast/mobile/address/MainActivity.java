package cn.itcast.mobile.address;

import java.io.InputStream;

import cn.itcast.service.MobileInfoService;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private EditText mobileText;
    private TextView addressView;
    private static final String TAG = "MainActivity";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mobileText = (EditText)this.findViewById(R.id.mobile);
        addressView = (TextView)this.findViewById(R.id.address);
        Button button = (Button)this.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				String mobile = mobileText.getText().toString();
				InputStream inStream = this.getClass().getClassLoader().getResourceAsStream("mobilesoap.xml");
				try {
					addressView.setText(MobileInfoService.getMobileAddress(inStream, mobile));
				} catch (Exception e) {
					Log.e(TAG, e.toString());
					Toast.makeText(MainActivity.this, "≤È—Ø ß∞‹", 1).show();
				}
			}
		});
    }
}