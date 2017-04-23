package applications.rpartha.com.safealarm;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;
import pojos.LoginServerRequest;
import pojos.LoginServerResponse;

/**
 * The type Login activity.
 */
public class Login extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    public static String pass = "", mailId = "";

    /**
     * The Email text.
     */
    @Bind(R.id.input_email) EditText emailText;
    /**
     * The Password text.
     */
    @Bind(R.id.input_password) EditText passwordText;
    /**
     * The Login button.
     */
    @Bind(R.id.btn_login) Button loginButton;
    /**
     * The Signup link.
     */
    /*@BindView(R.id.link_signup) TextView signupLink;*/

    /**
     * Allow user to login
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                login();
            }
        });

        /*signupLink.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                Toast.makeText(getApplicationContext(), "SignUp through the app is currently not available yet!", Toast.LENGTH_LONG);
            }
        });*/
    }

    /**
     * Login.
     */
    public void login(){
        Log.d(TAG, "MainScreen");

        if(!passwordChecked()){
            onLoginFailed();
            return;
        }

        loginButton.setEnabled(false); //find me1 sss

        final ProgressDialog progressDialog = new ProgressDialog(Login.this, R.style.AppTheme_Dark_Dialog);//this is cool
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating User...");
        progressDialog.show();

        mailId = emailText.getText().toString();
        pass = passwordText.getText().toString();

        //ADD AUTHENTICATION LOGIC HERE//
        //CHECK USERS AGAINST DATABASE//

        new android.os.Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Got here");
                        onLoginSuccess();
                        progressDialog.dismiss();
                    }
                }, 3000
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    /**
     * If login success, go to main screen
     */
    public void onLoginSuccess() {
        loginButton.setEnabled(true);
        new HTTPAsyncTask().execute("http://adapter.cs.rutgers.edu:3000");

    }

    /**
     * If login failed.
     */
    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed like due to incorrect email or password.", Toast.LENGTH_LONG).show();

        loginButton.setEnabled(true);
    }

    /**
     * Check user password
     *
     * @return whether or not password check was successful
     */
    public boolean passwordChecked(){
        boolean isValid = true;

        mailId = emailText.getText().toString();
        pass = passwordText.getText().toString();

        if(mailId.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(mailId).matches()){
            emailText.setError("please enter a valid email address");
            isValid = false;
        }


        else{
            emailText.setError(null);
        }


        if(!(pass.length() >= 8)){
            isValid = false;
        }

        else{
            passwordText.setError(null);
        }

        return isValid;
    }

    // check network connection
    public boolean checkNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        boolean isConnected = false;
        if (networkInfo != null && (isConnected = networkInfo.isConnected())) {
            // show "Connected" & type of network "WIFI or MOBILE"
            Log.d(TAG, "Connected "+networkInfo.getTypeName());

        } else {
            // show "Not Connected"
            Log.d(TAG, "Not connected");
        }

        return isConnected;
    }

    private class HTTPAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            HttpGet getter = new HttpGet("http://adapter.cs.rutgers.edu:3000/login");
            getter.setHeader("Content-Type","application/json");
            getter.setHeader("Expect","100-continue");
            HttpResponse resp = null;
            try {
                HttpClient httpClient = new DefaultHttpClient();
                resp = httpClient.execute(getter);
            } catch (ClientProtocolException e) {
                Log.e(getClass().getSimpleName(), "HTTP protocol error", e);
            } catch (IOException e) {
                Log.e(getClass().getSimpleName(), "Communication error", e);
            }
            if (resp != null) {
                return resp.toString();
            } else {
                return "AYE-PAPI";
            }

        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Gson gson = new Gson();
            if (!result.equals("AYE-PAPI")) {
                Log.d(TAG, "RESULT: " + result);
                LoginServerResponse response = gson.fromJson(result, LoginServerResponse.class);
                String privateKey = response.privateKey;
                Toast.makeText(getApplicationContext(), privateKey, Toast.LENGTH_LONG);
                // package username and privateKey into bundle and send in Intent to MainScreen activity
            } else {
                // prompt again
            }
        }
    }

    /*private String HttpGet(String myUrl) throws IOException{
        InputStream inputStream = null;
        String result = "";

        URL url = new URL(myUrl);

        // create HttpURLConnection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type","application/json");
        conn.setRequestProperty("Accept","application/json");
        //conn.setRequestProperty("Content-Length", "348");

        // make GET request to the given URL
        conn.connect();



        // receive response as inputStream
        inputStream = conn.getInputStream();
        Gson gson = new Gson();
        LoginServerRequest request = new LoginServerRequest();
        request.username = mailId;
        request.password = pass;
        String requestJson = gson.toJson(request);
        BufferedOutputStream outputStream = new BufferedOutputStream(conn.getOutputStream());
        outputStream.write(requestJson.getBytes());

        // convert inputstream to string
        if(inputStream != null && conn.getResponseCode() == 200) {
            result = convertInputStreamToString(inputStream);
            Log.d(TAG, "RESULT: " + result);
        } else
            result = "Did not work!";
        //conn.disconnect();
        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }*/

}