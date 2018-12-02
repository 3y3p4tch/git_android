package saurav.outlab9;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            class download extends AsyncTask<String, Void, JSONArray> {
                protected JSONArray doInBackground(String[] names) {
                    if (names.length != 0) {
                        String name = names[0];
                        String query = null;
                        URL url = null;
                        HttpsURLConnection conn;
                        try {
                            query = URLEncoder.encode(name, "utf-8");
                        }
                        catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        try {
                            url = new URL("https://api.github.com/search/users?q=" + query + "&sort=repositories");
                        }
                        catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        try {
                            conn = (HttpsURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            conn.setRequestProperty("Content-type", "application/json");
                            if (conn.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                                InputStream in = new BufferedInputStream(conn.getInputStream());
                                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                                StringBuilder total = new StringBuilder();
                                String line;
                                while ((line = r.readLine()) != null) {
                                    total.append(line).append('\n');
                                }
                                String str = total.toString();
                                JSONObject resp = new JSONObject(str);
                                conn.disconnect();
                                return resp.getJSONArray("items");
                            }
                            else {
                                conn.disconnect();
                                return new JSONArray();
                            }
                        }
                        catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                   return new JSONArray();
                }

                @Override
                protected void onPostExecute(JSONArray jsonArray) {
                    if (jsonArray.length() == 0) {
                        Toast.makeText(getApplicationContext(), "No Results match your search", Toast.LENGTH_SHORT).show();
                        ((Button) findViewById(R.id.search)).setClickable(true);
                        return;
                    }
                    Toast.makeText(getApplicationContext(), "Completed Search", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), Display_users.class);
                    intent.putExtra("array", jsonArray.toString());
                    startActivity(intent);
                }
            }
            @Override
            public void onClick(View v) {
                v.setClickable(false);
                String username = ((EditText) findViewById(R.id.user)).getText().toString();
                download thread = new download();
                thread.execute(username);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((Button) findViewById(R.id.search)).setClickable(true);
    }
}
