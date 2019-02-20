//
// Created by dkdk6 on 2018-11-29.
//

#include <jni.h>
#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/ioctl.h>
#include <android/log.h>
#include <memory.h>


typedef struct {
    unsigned char data[6];
} ioctl_hseg_data;

#define LCD_MAGIC 0XBC
#define LCD_SET_CURSOR_POS  _IOW(LCD_MAGIC, 0, int)
#define LCD_CLEAR   _IO(LCD_MAGIC, 1)
#define BUTTON_MAGIC  0XBD
#define BUTTON_PUSH _IOR(BUTTON_MAGIC,0,int)

JNIEXPORT jint JNICALL
Java_com_example_dkdk6_blackpinkchess_Activity_PlayGameActivity_SSegmentWrite(JNIEnv *jenv, jobject self, jint data) {
    int dev;
    if ((dev = open("/dev/7segment", O_WRONLY | O_SYNC)) < 0) {
        __android_log_print(ANDROID_LOG_ERROR, "SSegment", "failed to open /dev/7segment\n");
        return 1;

        write(dev, &data, sizeof(int));
        sleep(1);
        close(dev);
        return 0;
    }
}

JNIEXPORT jint JNICALL
Java_com_example_dkdk6_blackpinkchess_Activity_PlayGameActivity_DotWrite(JNIEnv *jenv, jobject self, jint data)
{
    int dev;
    if((dev = open("/dev/dotmatrix", O_WRONLY | O_SYNC)) < 0) {
        __android_log_print(ANDROID_LOG_ERROR, "Dot", "failed to open /dev/dotmatrix\n");
        return 1;
    }
    write(dev, &data, sizeof(int));
    close(dev);
    return 0;
}

JNIEXPORT jstring JNICALL Java_com_example_dkdk6_blackpinkchess_Activity_PlayGameActivity_LcdWrite
        (JNIEnv *jenv, jobject self, jstring data1, jstring data2)
{
    int dev, pos;
    if((dev=open("/dev/lcd", O_WRONLY | O_SYNC)) < 0) {
        __android_log_print(ANDROID_LOG_ERROR, "LCD", "failed to open /dev/lcd\n");
        return 1;
    }
    const char *nativeString1 = (*jenv)->GetStringUTFChars(jenv,data1, 0);
    const char *nativeString2 = (*jenv)->GetStringUTFChars(jenv,data2, 0);
    pos=0;
    ioctl(dev, LCD_CLEAR, &pos, _IOC_SIZE(LCD_CLEAR));
    ioctl(dev, LCD_SET_CURSOR_POS, &pos, _IOC_SIZE(LCD_SET_CURSOR_POS));
    write(dev, nativeString1, strlen(nativeString1));
    pos=16;
    ioctl(dev, LCD_SET_CURSOR_POS, &pos, _IOC_SIZE(LCD_SET_CURSOR_POS));
    write(dev, nativeString2, strlen(nativeString2));
    (*jenv)->ReleaseStringUTFChars(jenv,data1,nativeString1);
    (*jenv)->ReleaseStringUTFChars(jenv,data2,nativeString2);
    close(dev);
    return 0;
}

JNIEXPORT jint JNICALL
Java_com_example_dkdk6_blackpinkchess_Activity_PlayGameActivity_MotorWrite(JNIEnv *jenv, jobject self, jint data)
{
    int dev;
    if((dev = open("/dev/motor", O_WRONLY | O_SYNC)) < 0) {
        __android_log_print(ANDROID_LOG_ERROR, "MOTOR", "failed to open /dev/motor\n");
        return 1;
    }
    write(dev, &data, sizeof(int));
    close(dev);
    return 0;
}

JNIEXPORT jint JNICALL
Java_com_example_dkdk6_blackpinkchess_Activity_PlayGameActivity_ButtonRead(JNIEnv *jenv, jobject self)
{
    int dev, num=0, i=0;
    int result[9];
    for(i=0;i<9;i++) result[i]=0;
    if((dev=open("/dev/button", O_WRONLY | O_SYNC)) < 0) {
        __android_log_print(ANDROID_LOG_ERROR, "BUTTON", "failed to open /dev/button\n");
        return 0;
    }
    ioctl(dev,BUTTON_PUSH,&num,_IOC_SIZE(BUTTON_PUSH));
    close(dev);
    return num;
}
