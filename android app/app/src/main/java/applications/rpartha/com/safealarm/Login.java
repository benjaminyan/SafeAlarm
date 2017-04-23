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
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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
    }

    public static class LoginOutput {
        int status;
        String result;
    }
    /**
     * Login.
     */
    public void login() {
        if (passwordChecked()) {
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
                            new AsyncTask<Void,Void,LoginOutput>() {
                                public LoginOutput doInBackground(Void... args) {
                                    HttpURLConnection conn = null;
                                    LoginOutput output = null;
                                    try {
                                        URL url = new URL("http://adapter.cs.rutgers.edu:3000/login");
                                        conn = (HttpURLConnection) url.openConnection();
                                        conn.setRequestProperty("Content-Type","application/json; charset=UTF-8");
                                        conn.setRequestProperty("Accept","application/json; charset=UTF-8");
                                        conn.setRequestProperty("keep-alive","true");
                                        conn.setConnectTimeout(5000);
                                        conn.setDoInput(true);
                                        conn.setDoOutput(true);
                                        conn.setRequestMethod("POST");
                                        LoginServerRequest req = new LoginServerRequest();
                                        req.password = pass;
                                        req.username = mailId;
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
                                        output = new LoginOutput();
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

                                public void onPostExecute(LoginOutput result) {
                                    if (result != null && result.status == 200) {
                                        Log.d(TAG, "!!!RESULT!!!" + result.result);
                                        Intent intent = new Intent(Login.this, MainScreen.class);
                                        intent.putExtra("username",mailId);
                                        intent.putExtra("privateKey",result.result);
                                        startActivity(intent);
                                        // pass result.result as privateKey to the next activity along with username
                                    } else {
                                        Log.d(TAG, "!!!RESULT!!!: Error.");
                                        loginButton.setEnabled(true);
                                    }
                                }
                            }.execute();
                            progressDialog.dismiss();
                        }
                    }, 3000
            );
        } else {
            // error message
        }
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

}