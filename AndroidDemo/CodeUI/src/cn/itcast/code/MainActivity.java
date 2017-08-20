package cn.itcast.code;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout linearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
        		ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        TextView textView = new TextView(this);
        textView.setText(R.string.hello);
        textView.setId(34);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
        		ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.addView(textView, textParams);
        
        setContentView(linearLayout, layoutParams);
       
    }
}