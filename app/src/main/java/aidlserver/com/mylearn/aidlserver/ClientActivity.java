package aidlserver.com.mylearn.aidlserver;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import mylearn.com.aidlserver.AIDLCalc;


/**
 * Author: Prasanna B
 * Created On: 29-March-2019
 * To test AIDL Calculator functionality
 *
 * Note: Server and client apps need to have same aidl file with same package structure.
 *       You will get compile time error for referring services(AIDLCalc) if package name mismatch
 */
public class ClientActivity extends AppCompatActivity {

    private static final String ADD = "ADD";
    private static final String SUBTRACT = "SUBTRACT";
    private static final String MULTIPLY = "MULTIPLY";

    AIDLCalc myService;
    EditText firstno, secondno;
    TextView tv_answer;
    Button add, subtract, multiply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        firstno = (EditText) findViewById(R.id.ed_firstno);
        secondno = (EditText) findViewById(R.id.ed_secondno);

        add = (Button) findViewById(R.id.btn_add);
        subtract = (Button) findViewById(R.id.btn_subtract);
        multiply = (Button) findViewById(R.id.btn_multiply);

        tv_answer = (TextView) findViewById(R.id.tv_answer);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executeAction(ADD);
            }
        });

        subtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executeAction(SUBTRACT);
            }
        });

        multiply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executeAction(MULTIPLY);
            }
        });

        // make connection to service
        initAIDLServiceConnection();
    }

    private boolean validateFields() {
        String first = firstno.getText().toString();
        String second = secondno.getText().toString();
        if (TextUtils.isEmpty(first)) {
            Toast.makeText(getBaseContext(), "Enter first no", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(second)) {
            Toast.makeText(getBaseContext(), "Enter second no", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private void executeAction(String action) {
        if (validateFields()) {
            int first = Integer.parseInt(firstno.getText().toString());
            int second = Integer.parseInt(secondno.getText().toString());
            try {
                if (action.equalsIgnoreCase(ADD)) {
                    tv_answer.setText("Result : " + myService.addNumbers(first, second));
                } else if (action.equalsIgnoreCase(SUBTRACT)) {
                    tv_answer.setText("Result : " + myService.subtractNumbers(first, second));
                } else if (action.equalsIgnoreCase(MULTIPLY)) {
                    tv_answer.setText("Result : " + myService.multiplyNumbers(first, second));
                }
            } catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getBaseContext(), "Sorry, Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initAIDLServiceConnection() {
        Intent i = new Intent();
        // Note: Set the below fields correctly to avoid runtime exception

        // Serverside AIDL service mobile app's package name
        i.setPackage("mylearn.com.aidlserver");
        // In Serverside AIDL service mobile app's manifest file, i have mentioned below action in intent filter for the service tag
        i.setAction("mylearn.com.aidlserver.prasanna");

        bindService(i, aidlServiceConn, Service.BIND_AUTO_CREATE);
    }

    ServiceConnection aidlServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            myService = AIDLCalc.Stub.asInterface(iBinder);
            Toast.makeText(ClientActivity.this, "AIDL Service Connected", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            myService = null;
            Toast.makeText(ClientActivity.this, "AIDL Service Disconnected", Toast.LENGTH_SHORT).show();
        }
    };

    private void releaseAIDLServiceConnection() {
        // release/disconnect aidl service
        unbindService(aidlServiceConn);
        aidlServiceConn = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseAIDLServiceConnection();
    }
}
