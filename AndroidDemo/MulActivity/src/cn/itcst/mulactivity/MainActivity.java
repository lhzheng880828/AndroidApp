package cn.itcst.mulactivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button button = (Button)this.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//打开Other Activity
				Intent intent = new Intent(MainActivity.this, OtherActivity.class);//为Intent设置要激活的组件
				/*intent.putExtra("name", "传智播客");
				intent.putExtra("age", 4);*/
				Bundle bundle = new Bundle();
				bundle.putString("name", "传智播客");
				bundle.putInt("age", 4);
				intent.putExtras(bundle);
				
				//写法一 intent.setClass(MainActivity.this, OtherActivity.class);//设置要激活的组件
				//写法二 intent.setComponent(new ComponentName(MainActivity.this, OtherActivity.class));//设置要激活的组件
				//startActivity(intent);
				startActivityForResult(intent, 100);
			}
		});
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Toast.makeText(this, data.getStringExtra("result"), 1).show();//得到返回结果
		
		super.onActivityResult(requestCode, resultCode, data);
	}
    
}