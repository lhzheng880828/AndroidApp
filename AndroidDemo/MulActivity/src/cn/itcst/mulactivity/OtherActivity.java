package cn.itcst.mulactivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class OtherActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.other);
		Intent intent = this.getIntent();//得到激活它的意图
		String name = intent.getStringExtra("name");
		int age = intent.getExtras().getInt("age");//另一种写法
		TextView textView = (TextView)this.findViewById(R.id.result);
		textView.setText("名称："+ name+"  年龄："+ age);
		
		Button button = (Button)this.findViewById(R.id.close);
	        button.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.putExtra("result", "这是处理结果");
					setResult(20, intent);//设置返回数据
					finish();//关闭activity
				}
			});      
	}

}
