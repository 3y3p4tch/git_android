package saurav.outlab9;

import android.content.Intent;
import android.database.MatrixCursor;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
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
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        class download extends AsyncTask<String, Void, JSONObject> {
            protected JSONObject doInBackground(String[] names) {
                if (names.length != 0) {
                    String name = names[0];
                    URL url = null;
                    HttpsURLConnection conn;
                    try {
                        url = new URL("https://api.github.com/users/" + name);
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
                            return resp;
                        }
                        else {
                            conn.disconnect();
                            return new JSONObject();
                        }
                    }
                    catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
                return new JSONObject();
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                String name = null, company = null, location = null;
                try {
                    name = jsonObject.getString("login");
                    company = jsonObject.getString("company");
                    location = jsonObject.getString("location");
                }
                catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),  "You have exceeded your search limit. Try again after some time", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                ((TextView) findViewById(R.id.name)).setText(name);
                ((TextView) findViewById(R.id.company)).setText(company);
                ((TextView) findViewById(R.id.location)).setText(location);
            }
        }



        class repo_download extends AsyncTask<String, Void, JSONArray> {
            protected JSONArray doInBackground(String[] names) {
                if (names.length != 0) {
                    String name = names[0];
                    URL url = null;
                    HttpsURLConnection conn;
                    try {
                        url = new URL("https://api.github.com/users/" + name + "/repos");
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
                            JSONArray resp = new JSONArray(str);
                            conn.disconnect();
                            return resp;
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
                    Toast.makeText(getApplicationContext(), "User does not have any repositories", Toast.LENGTH_SHORT).show();
                    return;
                }
                String[] columnNames = new String[]{"_id","name","desc","age"};
                MatrixCursor contents = new MatrixCursor(columnNames, 30);
                int[] resourceIds = new int[] {R.id.name, R.id.repo_name, R.id.repo_Description, R.id.repo_age};
                for (int i =0;i<jsonArray.length(); i++) {
                    String name = null, desc = null, age = null;
                    try {
                        name = jsonArray.getJSONObject(i).getString("name");
                        desc = jsonArray.getJSONObject(i).getString("description");
                        String time_date = jsonArray.getJSONObject(i).getString("created_at").substring(0,10);

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-M-dd");
                        long curr = new Date().getTime();
                        long prev = simpleDateFormat.parse(time_date).getTime();
                        long diff = curr - prev;
                        Calendar c = Calendar.getInstance();
                        c.setTimeInMillis(diff);
                        age = c.get(Calendar.YEAR) - 1970 + " years, " + c.get(Calendar.MONTH) + " months, " + c.get(Calendar.DAY_OF_MONTH) + " days";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String[] something = new String[4];
                    something[0] = null;
                    something[1] = name;
                    something[2] = desc;
                    something[3] = age;
                    contents.addRow(something);
                }
                SimpleCursorAdapter adapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.layout, contents, columnNames, resourceIds, 0);
                ListView lstview = findViewById(R.id.repos);
                lstview.setAdapter(adapter);
            }
        }
        new download().execute(intent.getStringExtra("name"));
        new repo_download().execute(intent.getStringExtra("name"));
    }
}
