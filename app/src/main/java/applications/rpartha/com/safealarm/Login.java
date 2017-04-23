package applications.rpartha.com.safealarm;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Intent;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;

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
        Log.d(TAG, "login success");
        //Intent intent = new Intent(Login.this, MainScreen.class);
        //startActivity(intent);
        String loginJson = "{\"username\":\""+mailId+"\",\"password\":\""+pass+"\"}";
        IntentService intentService = new IntentService(loginJson){
            @Override
            protected void onHandleIntent(@Nullable Intent intent) {
                StringBuilder result = new StringBuilder();
                int status = 200;
                String loginJson = intent.getDataString();
                Log.d(TAG, loginJson);
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL("http://benjamin-Q504UA:3000/login");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedOutputStream out = new BufferedOutputStream(urlConnection.getOutputStream()); /* generate login json */
                    out.write(loginJson.getBytes());
                    status = urlConnection.getResponseCode();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                }catch( Exception e) {
                    e.printStackTrace();
                }
                finally {
                    if (urlConnection != null) urlConnection.disconnect();
                }
                if (status != 200) {
                    onLoginFailed();
                } else {
                    // make a JsonReader, pass in the result, get privateKey, pass both privateKey and username to main activity
                    try {
                        JSONObject jsonObject = new JSONObject(loginJson);
                        String privateKey = jsonObject.getString("privateKey");
                        Toast.makeText(this, "private key is: " + privateKey, Toast.LENGTH_SHORT).show();

                    } catch (final JSONException e) {
                        Log.e(TAG, "Json parsing error: " + e.getMessage());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        "Json parsing error: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        };

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

        else if(!mailId.equals("test.user@mail.com")){
            emailText.setError("wrong email address");
            isValid = false;
        }

        else{
            emailText.setError(null);
        }


        if(!pass.equals("test_password")){
            isValid = false;
        }

        else{
            passwordText.setError(null);
        }

        return isValid;
    }

}