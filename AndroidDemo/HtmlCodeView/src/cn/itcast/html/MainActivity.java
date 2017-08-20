package cn.itcast.html;

import cn.itcast.service.HtmlService;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        TextView textView = (TextView)this.findViewById(R.id.textView);
        try {
        	textView.setText(HtmlService.getHtml("http://www.sohu.com"));
		} catch (Exception e) {
			Log.e("MainActivity", e.toString());
			Toast.makeText(MainActivity.this, "Õ¯¬Á¡¨Ω” ß∞‹", 1).show();
		}
        
    }
}