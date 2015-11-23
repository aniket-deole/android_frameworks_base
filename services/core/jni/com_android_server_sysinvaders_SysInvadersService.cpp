/*
 * Copyright (C) 2009 The Android Open Source Project
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

#define LOG_TAG "SysInvadersJNI"

#include "jni.h"
#include "JNIHelp.h"
#include "android_runtime/AndroidRuntime.h"

#include <utils/misc.h>
#include <utils/Log.h>
#include <hardware/hardware.h>

#include <stdio.h>

namespace android
{

/**
 * JNI Layer init function;
 * Load HAL and its method table for later use;
 * return a pointer to the same structure
 * (not mandatory btw, we can use a global variable)
 */
/**
 * SysInvaders wrapper function;
 * Call the native SysInvaders function from Framework layer
 */
static jlong init_native(JNIEnv *env, jobject clazz) {
  ALOGV ("Entering initNative");
  return 0;
}
static JNINativeMethod method_table[] = {
    { "init_native", "()J", (void*)init_native}
};
int register_android_server_sysinvadersService(JNIEnv *env)
{
    return jniRegisterNativeMethods(env, "com/android/server/sysinvaders/SysInvadersService",
            method_table, NELEM(method_table));
}
};
