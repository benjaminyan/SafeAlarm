package applications.rpartha.com.safealarm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by tillu on 4/23/2017.
 */

public class HappyActivity extends AppCompatActivity {
    public static String username, privateKey, serialNumber;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_happy);
        username = getIntent().getExtras().getString("username").toString();
        privateKey = getIntent().getExtras().getString("privateKey").toString();
        serialNumber = getIntent().getExtras().getString("serialNumber").toString();
    }
}
