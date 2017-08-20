package cn.itcast.file;

import cn.itcast.service.FileService;
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
    private FileService fileService;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
     
        fileService = new FileService(this);
        
        Button button = (Button) this.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText filenameText = (EditText) findViewById(R.id.filename);
				EditText contentText = (EditText) findViewById(R.id.filecontent);
				String filename = filenameText.getText().toString();
				String content = contentText.getText().toString();
				try {
					//判断sdcard是否存在于手机上，并且可以进行读写访问
					if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
						fileService.saveToSDCard(filename, content);
						Toast.makeText(MainActivity.this, R.string.success, 1).show();
					}else{
						Toast.makeText(MainActivity.this, R.string.sdcarderror, 1).show();
					}
				} catch (Exception e) {
					Log.e(TAG, e.toString());
					Toast.makeText(MainActivity.this, R.string.error, 1).show();
				}
			}
		});
    }
}