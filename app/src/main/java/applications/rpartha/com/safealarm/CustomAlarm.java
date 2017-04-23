package applications.rpartha.com.safealarm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;
import pojos.LoginServerRequest;
import pojos.RegisterDeviceServerRequest;

/**
 * Created by tillu on 4/22/2017.
 */

public class CustomAlarm extends AppCompatActivity {


    @Bind(R.id.input_alarm) EditText alarmText;
    @Bind(R.id.submit_alarm_btn) Button alarmButton;

    private static String username;
    private static String privateKey;
    private static String serialNumber;
    private static String type;

    private static final String TAG = "CustomAlarm";

    public static class CustomAlarmOutput {
        String result;
        int status;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_alarm);
        ButterKnife.bind(this);

        username = getIntent().getExtras().getString("username").toString();
        privateKey = getIntent().getExtras().getString("privateKey").toString();
        serialNumber = getIntent().getExtras().getString("serialNumber").toString();

        alarmButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
            register();
            }
        });
    }

    public void register() {
        type = alarmText.getText().toString();
        if(!hasEnteredText()){
            Toast.makeText(getApplicationContext(), "Must enter at least 1 character", Toast.LENGTH_LONG).show();
            alarmButton.setEnabled(true);
        }
        else {
            // Toast.makeText(getApplicationContext(), "Replace with Message Activity", Toast.LENGTH_LONG).show();
            final ProgressDialog progressDialog = new ProgressDialog(CustomAlarm.this, R.style.AppTheme_Dark_Dialog);//this is cool
            alarmButton.setEnabled(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Registering Device...");
            progressDialog.show();

            new android.os.Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Got here");
                        new AsyncTask<Void,Void,CustomAlarmOutput>() {
                            public CustomAlarmOutput doInBackground(Void... args) {
                                HttpURLConnection conn = null;
                                CustomAlarmOutput output = null;
                                try {
                                    URL url = new URL("http://adapter.cs.rutgers.edu:3000/ownership");
                                    conn = (HttpURLConnection) url.openConnection();
                                    conn.setRequestProperty("Content-Type","application/json; charset=UTF-8");
                                    conn.setRequestProperty("Accept","text/plain; charset=UTF-8");
                                    conn.setRequestProperty("keep-alive","true");
                                    conn.setConnectTimeout(10000);
                                    conn.setDoInput(true);
                                    conn.setDoOutput(true);
                                    conn.setRequestMethod("POST");
                                    RegisterDeviceServerRequest req = new RegisterDeviceServerRequest();
                                    req.username = username;
                                    req.serialNumber = serialNumber;
                                    req.privateKey = privateKey;
                                    req.type = type;
                                    Gson gson = new Gson();
                                    String body = gson.toJson(req);
                                    Log.d(TAG,"!!!BODY!!! " + body);
                                    conn.connect();
                                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                                    wr.write(body);
                                    wr.flush();
                                    InputStream is = conn.getInputStream();
                                    InputStreamReader isr = new InputStreamReader(is);
                                    BufferedReader reader = new BufferedReader(isr);
                                    // BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                                    StringBuilder builder = new StringBuilder();
                                    String str = reader.readLine();
                                    while (str != null) {
                                        builder.append(str);
                                        str = reader.readLine();
                                    }
                                    String result = builder.toString();
                                    // String result = conn.getResponseMessage();
                                    output = new CustomAlarmOutput();
                                    output.status = conn.getResponseCode();
                                    output.result = result;
                                } catch(MalformedURLException e) {
                                    Log.d(TAG, "!!!ERROR!!! Malformed URL");
                                } catch (IOException e) {
                                    Log.d(TAG, "!!!ERROR!!! IO Exception");
                                } finally {
                                    if (conn != null) conn.disconnect();
                                }
                                return output;
                            }

                            public void onPostExecute(CustomAlarmOutput result) {
                                if (result != null && result.status == 200) {
                                    Log.d(TAG, "!!!RESULT!!!" + result.result);
                                    Intent intent = new Intent(CustomAlarm.this, HappyActivity.class); // REPLACE
                                    intent.putExtra("username",username);
                                    intent.putExtra("privateKey",privateKey);
                                    intent.putExtra("serialNumber",serialNumber);
                                    startActivity(intent);
                                    // pass result.result as privateKey to the next activity along with username
                                } else {
                                    Log.d(TAG, "!!!RESULT!!!: Error.");
                                    alarmButton.setEnabled(true);
                                }
                            }
                        }.execute();
                        progressDialog.dismiss();
                    }
                }, 3000
            );
        }
    }

    public boolean hasEnteredText(){

        if(!(alarmText.getText().toString().length() >= 1)){
            return false;
        }
        return true;
    }

    public String getEnteredText() {
        return alarmText.getText().toString();
    }

}
