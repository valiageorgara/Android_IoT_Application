package com.example.project.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.myapplication.dialog.Dialog;
import com.example.project.myapplication.dialog.JavaDialog;
import com.example.project.myapplication.flash.FlashActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import static com.example.project.myapplication.flash.FlashActivity.onoffBtn;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Dialog.DialogListener, JavaDialog.JavaDialogListener{
    public static MediaPlayer mySound;
    static FlashActivity myFlash = new FlashActivity();
    public static Button playstopBtn;
    public static boolean isMusicOn=false;

    public Button connectBtn;
    public Button publishBtn;
    final int qos = 0;
    public static TextView subText;
    TextView textViewBroker;
    TextView textViewPort;
    TextView textViewFrequency;

    String topicStr   = "";
    String subTopic   = "ServerSubscribe_ClientPublish";
    String pubTopic   = "ServerPublish_ClientSubscribe";
    MqttAndroidClient client;
    String protocol = "tcp://";

    Handler mHandler =new Handler();
    boolean isRunning =true;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        onoffBtn = (Button) findViewById(R.id.FlashButton);
        onoffBtn.setOnClickListener(this);

        playstopBtn = (Button) findViewById(R.id.MusicButton);
        playstopBtn.setOnClickListener(this);

        textViewBroker = (TextView)findViewById(R.id.BrokerTextView);
        textViewPort = (TextView)findViewById(R.id.PortTextView);
        textViewFrequency = (TextView)findViewById(R.id.FrequencyTextView);

        connectBtn = (Button) findViewById(R.id.ConnectButton);
        connectBtn.setOnClickListener(this);

        publishBtn = (Button) findViewById(R.id.PublishButton);
        publishBtn.setOnClickListener(this);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.CAMERA}, 1);
            }
        }

       /* mySound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                turnOffMusic();
            }
        });*/



        new Thread(new Runnable() {
            @Override
            public void run() {
                while(isRunning){
                    try{
                        Thread.sleep(10000);

                        mHandler.post(new Runnable(){
                            @Override
                            public void run() {
                                displayData();

                            }
                        });
                    }catch(Exception e){

                    }
                }
            }
        }).start();
    }   //End of onCreate
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void turnOnMusic(){
        try {
            if(mySound==null){
                mySound = MediaPlayer.create(this, R.raw.jungle);
                mySound.start();
                isMusicOn = true;
                playstopBtn.setText("Stop");
            }
        }
        catch(Exception x){
            Toast.makeText(this,x.toString(), Toast.LENGTH_SHORT).show();
        }
    }
    public void turnOffMusic() {
        try {

            mySound.release();
            mySound = null;
            isMusicOn = false;
            playstopBtn.setText("Play");



        } catch (Exception x) {
            Toast.makeText(this, x.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void onClick(View view) {

       // turnOffMusic();

        switch (view.getId()) {
        ////////////////////////////
        case R.id.FlashButton:
            if (myFlash.isFlashOn )
                myFlash.turnOffFlash();

            else{
                myFlash.getCamera();
                myFlash.turnOnFlash();
            }
            break;
        ////////////////////////////////
        case R.id.MusicButton:


            if (isMusicOn) {
                turnOffMusic();

            }
            else{
                turnOnMusic();
            }
            break;

            case R.id.ConnectButton:
            subText = (TextView)findViewById(R.id.subText);
            String broker = textViewBroker.getText().toString();
            String port = textViewPort.getText().toString();
            String url = protocol + broker + ":" + port;
            /////////////////////////////////////////////////////////////
            String clientId = MqttClient.generateClientId();
            /////////////////////////////////////////////////////////////
            MqttConnectOptions options = new MqttConnectOptions();
            try {
                client = new MqttAndroidClient(this.getApplicationContext(), url,clientId);
                IMqttToken token = client.connect(options);
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Toast.makeText(MainActivity.this, "Connected!!", Toast.LENGTH_LONG).show();
                        setSubscription();
                    }
                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Toast.makeText(MainActivity.this, "Connection failed..", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
            /////////////////////CALLBACK METHODS////////////////////////////////////////////////////
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    //	This	method	is	called	when	the	connection	to	the	server	is	lost.
                    System.out.println("Connection	lost!"	+	cause);
                    System.exit(1);
                }
                @Override
                public void messageArrived(String s, MqttMessage message) throws Exception {
                    subText.setText(new String(message.getPayload()));
                    String command = String.valueOf(message);
                    if(command.equals("Execute EyesClosed") && isMusicOn==false &&myFlash.isFlashOn == false){
                        Toast.makeText(MainActivity.this,"Music and Flash already off!", Toast.LENGTH_SHORT).show();
                    }
                    else if(command.equals("Execute EyesOpened") && isMusicOn==true &&myFlash.isFlashOn == true){
                        Toast.makeText(MainActivity.this,"Music and Flash already on!", Toast.LENGTH_SHORT).show();
                    }
                    else if(command.equals("Execute EyesOpened")){
                        myFlash.getCamera();
                        myFlash.turnOnFlash();
                        turnOnMusic();
                    }
                    else if(command.equals("Execute EyesClosed")) {
                        myFlash.turnOffFlash();
                        turnOffMusic();

                    }
                }
                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {}
                ///////////////////////////////////////////////////////////////////////
            });
            break;
        case R.id.PublishButton:
            setPublication();
        default:
            break;
    }
}//end of onCLick
    public void setPublication(){
        String frequency = textViewFrequency.getText().toString();
        try{
            MqttMessage message = new MqttMessage(frequency.getBytes());
            IMqttToken subToken = client.publish(subTopic, message);   //frequency is published
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // successfully subscribed
                    Toast.makeText(MainActivity.this, "Successfully published to: " + subTopic, Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards
                    Toast.makeText(MainActivity.this, "Couldn't publish to: " + subTopic, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }
    public void setSubscription(){
        //topicStr = pubTopic;
        try{
            IMqttToken subToken = client.subscribe(pubTopic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // successfully subscribed
                    Toast.makeText(MainActivity.this, "Successfully subscribed to: " + pubTopic, Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards
                    Toast.makeText(MainActivity.this, "Couldn't subscribe to: " + pubTopic, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    boolean twice= false;
    @Override
    public void onBackPressed(){
        Runnable back = new Runnable(){
            @Override
            public void run() {
                if(twice == true){
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    System.exit(0);
                }
                twice = true;
            }
        };


        Thread pisw = new Thread(back);
        pisw.start();

        Toast.makeText(MainActivity.this, "Please press BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                twice = false;
            }
        },3000);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

       return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.AndroidSettings:
                //add the function to perform here
                openAndroidDialog();
                return(true);
            case R.id.Exit:
                //add the function to perform here
                finish();
                System.exit(0);
                return(true);
            case R.id.JavaSettings:
                //add the function to perform here
                openJavaDialog();
                return(true);
        }


        return(super.onOptionsItemSelected(item));
    }
    public void openAndroidDialog(){
        Dialog dialog = new Dialog();
        dialog.show(getSupportFragmentManager(), "com/example/project/myapplication/dialog");

    }

    public void openJavaDialog(){
        JavaDialog dialog2= new JavaDialog();
        dialog2.show(getSupportFragmentManager(), "dialog2");

    }
    @Override
    public void onRestart(){
        super.onRestart();
    }
    @Override
    public void applyText(final String broker, final String port) {
        textViewBroker.setText(broker);
        textViewPort.setText(port);

    }
    public void applyFrequency(final String frequency) {
        textViewFrequency.setText(frequency);

    }

    private void displayData(){

        if(!InternetConnectionClass.isConnected(this))
            //Toast.makeText(this, "Network Available", Toast.LENGTH_SHORT).show();
       // else
            Toast.makeText(this, "Check your Network Connection", Toast.LENGTH_SHORT).show();

    }
}
