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
 *
 */
#include <string.h>
#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_example_hellojni_PmsHookDetection_nativePmsDetection( JNIEnv* env, jobject thiz, jobject context)
{
    // function signatures derived from frameworks/base/core/java/android/app/ActivityThread.java
    jclass activityThread = (*env)->FindClass(env, "android/app/ActivityThread");
    jfieldID sPackageManagerField = (*env)->GetStaticFieldID(env, activityThread, "sPackageManager", "Landroid/content/pm/IPackageManager;");
    jobject sPackageManager = (*env)->GetStaticObjectField(env, activityThread, sPackageManagerField);

    // Check PMS hook at current thread's sPackageManager
    jclass proxyClazz = (*env)->FindClass(env, "java/lang/reflect/Proxy");
    if((*env)->IsInstanceOf(env, sPackageManager, proxyClazz)){
        //return (*env)->NewStringUTF(env, "########  PMS Hook Detected ########");
    }

    // Check PMS hook at mPm field in ContextWrapper
    jclass contextWrapperCls = (*env)->FindClass(env, "android/content/ContextWrapper");
    jmethodID getPackageManagerId = (*env)->GetMethodID(env, contextWrapperCls, "getPackageManager", "()Landroid/content/pm/PackageManager;");
    jobject applicationPackageManager =  (*env)->CallObjectMethod(env, context, getPackageManagerId);

    jclass packageManagerCls = (*env)->FindClass(env, "android/app/ApplicationPackageManager");
    jfieldID mPmField = (*env)->GetFieldID(env, packageManagerCls, 'mPm', "Landroid/content/pm/IPackageManager;");
    jobject mPmObj = (*env)->GetObjectField(env, applicationPackageManager, mPmField);
    if((*env)->IsInstanceOf(env, mPmObj, proxyClazz)){
        return (*env)->NewStringUTF(env, "########  PMS Hook Detected ########");
    }
    return (*env)->NewStringUTF(env, "PMS hook not detected");
}
