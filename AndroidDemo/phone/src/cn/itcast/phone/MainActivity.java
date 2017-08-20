package cn.itcast.phone;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button button = (Button)this.findViewById(R.id.button);
        button.setOnClickListener(new ButtonListener());
        
    }
    
    private final class ButtonListener implements View.OnClickListener{
		public void onClick(View v) {
			EditText mobileText = (EditText)findViewById(R.id.mobile);
			String mobile = mobileText.getText().toString();
			Intent intent = new Intent();
			intent.setAction("android.intent.action.CALL");
			intent.setData(Uri.parse("tel:"+ mobile));
			startActivity(intent);
		}
    }
}