package saurav.outlab9;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;


public class Display_users extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_users);
        Intent intent = getIntent();
        try {
            JSONArray arr = new JSONArray(intent.getStringExtra("array"));
            String[] myStringArray = new String[arr.length()];
            for(int i = 0; i < arr.length(); i++) {
                myStringArray[i] = arr.getJSONObject(i).getString("login");
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.users, myStringArray);
            ListView listView = (ListView) findViewById(R.id.list);
            listView.setAdapter(adapter);
            Toast.makeText(getApplicationContext(), getPackageResourcePath(), Toast.LENGTH_SHORT);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void user_click(View v) {
        Intent intent = new Intent(getApplicationContext(), UserActivity.class);
        intent.putExtra("name", ((TextView) v).getText());
        startActivity(intent);
    }
}
