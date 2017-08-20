package cn.itcast.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.itcast.domain.Person;
import cn.itcast.service.PersonService;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {
    private PersonService personService;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        this.personService = new PersonService(this);
       
        ListView listView = (ListView) this.findViewById(R.id.listView);
        Cursor cursor = personService.getCursorScrollData(0, 5);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.item, cursor,
        		new String[]{"_id", "name", "amount"}, new int[]{R.id.id, R.id.name, R.id.amount});
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ListView lView = (ListView)parent;
				Cursor data = (Cursor)lView.getItemAtPosition(position);
				int personid = data.getInt(data.getColumnIndex("_id"));
				Toast.makeText(MainActivity.this, personid+"", 1).show();
			}
		});
        /*
        List<Person> persons = personService.getScrollData(0, 5);
        List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        for(Person person : persons){
        	HashMap<String, Object> item = new HashMap<String, Object>();
        	item.put("id", person.getId());
        	item.put("name", person.getName());
        	item.put("amount", person.getAmount());
        	data.add(item);
        }
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.item,
        		new String[]{"id", "name", "amount"}, new int[]{R.id.id, R.id.name, R.id.amount});
        listView.setAdapter(adapter);
        
        listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ListView lView = (ListView)parent;
				HashMap<String, Object> item = (HashMap<String, Object>)lView.getItemAtPosition(position);
				Toast.makeText(MainActivity.this, item.get("id").toString(), 1).show();
			}
		});
		*/
        
        Button button = (Button) this.findViewById(R.id.insertbutton);
        button.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				ContentResolver contentResolver = getContentResolver();
				Uri insertUri = Uri.parse("content://cn.itcast.providers.personprovider/person");
				ContentValues values = new ContentValues();
				values.put("name", "itcastliming");
				values.put("amount", 100);
				Uri uri = contentResolver.insert(insertUri, values);
				Toast.makeText(MainActivity.this, "ÃÌº”ÕÍ≥…", 1).show();
			}
		});
    }
}