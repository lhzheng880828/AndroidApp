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
		Intent intent = this.getIntent();//�õ�����������ͼ
		String name = intent.getStringExtra("name");
		int age = intent.getExtras().getInt("age");//��һ��д��
		TextView textView = (TextView)this.findViewById(R.id.result);
		textView.setText("���ƣ�"+ name+"  ���䣺"+ age);
		
		Button button = (Button)this.findViewById(R.id.close);
	        button.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.putExtra("result", "���Ǵ�����");
					setResult(20, intent);//���÷�������
					finish();//�ر�activity
				}
			});      
	}

}
