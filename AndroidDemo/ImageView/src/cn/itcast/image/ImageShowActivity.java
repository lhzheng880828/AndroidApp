package cn.itcast.image;

import cn.itcast.service.ImageService;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ImageShowActivity extends Activity {
	private static final String TAG = "ImageShowActivity";
    private EditText pathText;
    private ImageView imageView;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        pathText = (EditText) this.findViewById(R.id.urlpath);
        imageView = (ImageView) this.findViewById(R.id.imageView);
        Button button = (Button)this.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String path = pathText.getText().toString();
				try {
					byte[] data = ImageService.getImage(path);
					Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);//生成位图
					imageView.setImageBitmap(bitmap);//显示图片					
				} catch (Exception e) {
					Toast.makeText(ImageShowActivity.this, R.string.error, 1).show();
					Log.e(TAG, e.toString());
				}
			}
		});
    }
}