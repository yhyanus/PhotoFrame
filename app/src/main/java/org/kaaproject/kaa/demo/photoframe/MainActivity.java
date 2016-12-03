/**
 * Copyright 2014-2016 CyberVision, Inc.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kaaproject.kaa.demo.photoframe;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import org.kaaproject.kaa.demo.photoframe.fragment.DevicesFragment;
import org.kaaproject.kaa.demo.photoframe.fragment.LoginFragment;
import org.kaaproject.kaa.demo.photoframe.fragment.WaitFragment;
import org.kaaproject.kaa.demo.photoframe.kaa.KaaManager;

/**
 * The implementation of the {@link AppCompatActivity} class.
 * Manages fragments transition depending on the current application state.
 */
public class MainActivity extends AppCompatActivity {
private static String TAG ="MainActivity" ;
    private KaaManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_frame);
        if(isPermissionGranted(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE},1 ) ) {
            manager = new KaaManager();
            manager.start(this);

            if (!manager.isKaaStarted()) {
                new WaitFragment().move(this);
            } else if (!manager.isUserAttached()) {
                new LoginFragment().move(this);
            } else {
                new DevicesFragment().move(this);
            }
        }
    }

    public KaaManager getManager() {
        return manager;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        manager.stop();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setLightsOutMode(boolean enabled) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            if (enabled) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            }
        } else {
            getWindow().getDecorView().setSystemUiVisibility(enabled ? View.SYSTEM_UI_FLAG_FULLSCREEN : 0);
        }
    }
    public  boolean isPermissionGranted(String[] whats ,int code ) {
        boolean ok = true;
        if (Build.VERSION.SDK_INT >= 23) {
            for( String str : whats) {
                if (checkSelfPermission(str) //Manifest.permission.WRITE_EXTERNAL_STORAGE )
                        == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "Permission "+ str+ " is granted" );
                } else {
                    Log.v(TAG, "Permission is revoked");
                    ActivityCompat.requestPermissions(this, whats,code);
                    return  false;
                }
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
        return ok;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            manager = new KaaManager();
            manager.start(this);

            if (!manager.isKaaStarted()) {
                new WaitFragment().move(this);
            } else if (!manager.isUserAttached()) {
                new LoginFragment().move(this);
            } else {
                new DevicesFragment().move(this);
            }
        }else if(requestCode==2 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);

        }
    }
}
