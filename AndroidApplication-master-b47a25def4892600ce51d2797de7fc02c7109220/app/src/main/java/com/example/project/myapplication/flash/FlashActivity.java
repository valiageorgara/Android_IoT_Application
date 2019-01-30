package com.example.project.myapplication.flash;

import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;


public class FlashActivity extends AppCompatActivity {

    private Camera camera;
    private Camera.Parameters parameters;

    public boolean isFlashOn=false;
    public static Button onoffBtn;


    public void getCamera(){
        if(camera==null){
            try{
                camera= Camera.open();
                parameters=camera.getParameters();
            }
            catch (Exception ex){
                Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }
    public void turnOnFlash(){
        try {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(parameters);
            camera.startPreview();
            isFlashOn = true;
            onoffBtn.setText("Off");
        }
        catch(Exception x){
            Toast.makeText(this,x.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void turnOffFlash() {
        try {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(parameters);
            camera.startPreview();
            isFlashOn = false;
            onoffBtn.setText("On");
        } catch (Exception x) {
            Toast.makeText(this, x.toString(), Toast.LENGTH_LONG).show();
        }

    }

    public void onStop(){
        super.onStop();
        if(camera!=null){
            camera.release();
            camera=null;
            parameters=null;
        }
    }

}
