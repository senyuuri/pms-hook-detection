/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.hellojni;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class PmsHookDetection extends AppCompatActivity {
    static {
        System.loadLibrary("hello-jni");
    }

    public native String  nativePmsDetection(Context c);
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_jni);
        tv = (TextView)findViewById(R.id.textView);

        printBanner();
        performJavaCheck();
        performNativeCheck();
    }

    private void printBanner(){
        tv.setText("/* \n" +
                " * .-. .-. .-. .   .-. .-. .-. .-. .-. .-. \n"+
                " * `-. |-  |-| |   |-| |(  `-. `-. |-  |   \n"+
                " * `-' `-' ` ' `-' ` ' `-' `-' `-' `-' `-' \n"+
                " * \n" +
                " * Security @ Sea Labs 2020\n"+
                " */\n\n");
    }

    private void performJavaCheck(){
        tv.append("Performing hook detection at Java level\n");
        try {
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
            Object currentActivityThread = currentActivityThreadMethod.invoke(null);
            tv.append("Obtain ActicityThread object ........... Done\n");

            Field sPackageManagerField = activityThreadClass.getDeclaredField("sPackageManager");
            sPackageManagerField.setAccessible(true);
            Object sPackageManager = sPackageManagerField.get(currentActivityThread);
            tv.append("Obtain sPackageManager object .......... Done\n");
            tv.append(sPackageManager.toString() + "\n");
            checkPMSHook(sPackageManager);

            PackageManager pm = getApplicationContext().getPackageManager();
            Field mPmField = pm.getClass().getDeclaredField("mPM");
            mPmField.setAccessible(true);
            Object mPmObject = mPmField.get(pm);
            tv.append("Obtain mPM object from Context .......... Done\n");
            tv.append(mPmObject.toString() + "\n");
            checkPMSHook(mPmObject);

        } catch(Exception e){
            tv.append(e.toString());
        }
    }

    private void checkPMSHook(Object o){
        if (o instanceof Proxy) {
            tv.append("########  PMS Hook Detected ######## \n\n");
        }
    }

    private void performNativeCheck(){
        tv.append(nativePmsDetection(getApplicationContext()));
    }

}
