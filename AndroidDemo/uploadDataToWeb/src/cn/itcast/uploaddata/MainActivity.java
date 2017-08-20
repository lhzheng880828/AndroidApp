package cn.itcast.uploaddata;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.itcast.net.HttpRequest;
import cn.itcast.utils.FormFile;
import cn.itcast.utils.SocketHttpRequester;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
    private EditText timelengthText;
    private EditText titleText;
    private EditText videoText;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
       Button button = (Button) this.findViewById(R.id.button);
       timelengthText = (EditText) this.findViewById(R.id.timelength);
       videoText = (EditText) this.findViewById(R.id.video);
       titleText = (EditText) this.findViewById(R.id.title);
       button.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				String title = titleText.getText().toString();
				String timelength = timelengthText.getText().toString();
				Map<String, String> params = new HashMap<String, String>();
				params.put("method", "save");
				params.put("title", title);
				params.put("timelength", timelength);
				try {
				//	HttpRequest.sendGetRequest("http://192.168.1.100:8080/videoweb/video/manage.do", params, "UTF-8");
					File uploadFile = new File(Environment.getExternalStorageDirectory(), videoText.getText().toString());
					FormFile formfile = new FormFile("02.mp3", uploadFile, "video", "audio/mpeg");
					SocketHttpRequester.post("http://192.168.1.100:8080/videoweb/video/manage.do", params, formfile);
					Toast.makeText(MainActivity.this, R.string.success, 1).show();
				} catch (Exception e) {
					Toast.makeText(MainActivity.this, R.string.error, 1).show();
					Log.e(TAG, e.toString());
				}
			}
		});
        
    }
}