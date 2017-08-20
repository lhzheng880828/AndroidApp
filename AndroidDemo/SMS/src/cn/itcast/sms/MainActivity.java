package cn.itcast.sms;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button button = (Button)this.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				EditText mobileText = (EditText)findViewById(R.id.mobile);
				EditText contentText = (EditText)findViewById(R.id.content);
				String mobile = mobileText.getText().toString();
				String content = contentText.getText().toString();
				SmsManager smsManager = SmsManager.getDefault();
				ArrayList<String> texts = smsManager.divideMessage(content);//²ð·Ö¶ÌÐÅ
				for(String text : texts){
					smsManager.sendTextMessage(mobile, null, text, null, null);
				}
				Toast.makeText(MainActivity.this, R.string.success, Toast.LENGTH_LONG).show();
			}
		});
    }
}