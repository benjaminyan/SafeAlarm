package applications.rpartha.com.safealarm;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import applications.rpartha.com.safealarm.barcode.BarcodeCaptureActivity;

/**
 * Created by tillu on 4/22/2017.
 */

public class MainScreen extends AppCompatActivity {
    private static final String LOG_TAG = MainScreen.class.getSimpleName();
    private static final int BARCODE_READER_REQUEST_CODE = 1;
    private static String username;
    private static String privateKey;

    private TextView mResultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        privateKey = getIntent().getExtras().getString("privateKey").toString();
        username = getIntent().getExtras().getString("username").toString();

        mResultTextView = (TextView) findViewById(R.id.result_textview);

        Button scanBarcodeButton = (Button) findViewById(R.id.scan_barcode_button);
        scanBarcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);
                startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
            }
        });

        FloatingActionButton floatingActionButton = (FloatingActionButton)findViewById(R.id.continueFab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), CustomAlarm.class);
                intent.putExtra("username",username);
                intent.putExtra("privateKey",privateKey);
                intent.putExtra("serialNumber",mResultTextView.getText());
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Point[] p = barcode.cornerPoints;
                    mResultTextView.setText(barcode.displayValue);
                } else mResultTextView.setText(R.string.no_barcode_captured);
            } else Log.e(LOG_TAG, String.format(getString(R.string.barcode_error_format),
                    CommonStatusCodes.getStatusCodeString(resultCode)));
        } else super.onActivityResult(requestCode, resultCode, data);
    }
}
