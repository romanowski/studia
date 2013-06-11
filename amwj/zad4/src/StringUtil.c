#include "edu_amwj_StringUtil.h"
#include "edu_amwj_StringUtilStats.h"
#include <string.h>
#include <stdio.h>


jfieldID getUseCount(JNIEnv *env, jobject jo){
   return (*env)->GetFieldID(env, (*env)->GetObjectClass(env, jo),"useCount",  "I");
}

jfieldID getReplCount(JNIEnv *env, jobject jo){
   return (*env)->GetFieldID(env, (*env)->GetObjectClass(env, jo),
   "replCount", "I");
}


JNIEXPORT jint JNICALL Java_edu_amwj_StringUtilStats_getNumberOfReplacements
  (JNIEnv *env, jobject jo){
     return (*env)->GetIntField(env, jo, getUseCount(env, jo));
  };

JNIEXPORT jint JNICALL Java_edu_amwj_StringUtilStats_getNumberOfCalls
    (JNIEnv * env, jobject jo){
      return (*env)->GetIntField(env, jo, getReplCount(env, jo));
    };

JNIEXPORT jobject JNICALL Java_edu_amwj_StringUtil_getStats
      (JNIEnv * env, jobject jo){
         return (*env)->GetObjectField(env, jo,
            (*env)->GetFieldID(env, (*env)->GetObjectClass(env, jo),"stats", "Ledu/amwj/StringUtilStats;"));
      };

JNIEXPORT jstring JNICALL Java_edu_amwj_StringUtil_replace
    (JNIEnv *env, jobject jo, jstring ja, jstring jb, jstring jc){

    //check args
    if(ja == NULL || jb == NULL || jc == NULL){
        return (*env)->ThrowNew(env, (*env)->FindClass(env, "Ljava/lang/IllegalArgumentException;"),"null param");
    }


    /*
    in pure c we use this code:
    const char *a = (*env)->GetStringUTFChars(env, ja, 0);
    const char *b = (*env)->GetStringUTFChars(env, jb, 0);
    const char *c = (*env)->GetStringUTFChars(env, jc, 0);

    //replacing and calling nextUse() and nextRepl() on stats field using
    GetFieldID GetField, CallObjectMethod

    then transfering it back to java:

     (*env)->ReleaseStringUTFChars(env, ja, a);
         return (*env)->NewStringUTF(env,  cap);

         or use simpler way:*/

    jmethodID inner = (*env)->GetMethodID(env, (*env)->GetObjectClass(env, jo), "inner_replace",
    "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");

    return (jstring)((*env)->CallObjectMethod(env, jo, inner, ja,jb,jc));
}

