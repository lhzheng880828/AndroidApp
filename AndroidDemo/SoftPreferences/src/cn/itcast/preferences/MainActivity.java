package cn.itcast.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
    private EditText nameText;
    private EditText ageText;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        nameText = (EditText)findViewById(R.id.name);
		ageText = (EditText)findViewById(R.id.age);
        Button button = (Button)this.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				String name = nameText.getText().toString();
				String age = ageText.getText().toString();
				SharedPreferences preferences = getSharedPreferences("itcast", Context.MODE_WORLD_READABLE);
				Editor editor = preferences.edit();
				editor.putString("name", name);
				editor.putInt("age", new Integer(age));
				editor.commit();
				Toast.makeText(MainActivity.this, R.string.success, 1).show();
			}
		});
        
        Button resumebutton = (Button)this.findViewById(R.id.resume);
        resumebutton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {				
				SharedPreferences preferences = getSharedPreferences("itcast", Context.MODE_PRIVATE);
				String name = preferences.getString("name", "");
				int age = preferences.getInt("age", 20);
				nameText.setText(name);
				ageText.setText(String.valueOf(age));
			}
		});
    }
}