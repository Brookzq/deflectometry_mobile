package com.quanzhang.camera2SecretPictureTaker.util;

import android.hardware.camera2.params.RggbChannelVector;

/** this is to convert pictures format into bmp format
 *
 * @author quan zhang
 */
public class CameraUtils {

    // bmp
    public static byte[] addBMP_RGB_888(int[] b, int w, int h) {
        int len = b.length;
        System.out.println(b.length);
        byte[] buffer = new byte[w * h * 4];
        int offset = 0;
        for (int i = len - 1; i >= w; i -= w) {
            int end = i, start = i - w + 1;
            for(int j = start; j <= end; j++) {
                buffer[offset] = (byte)(b[j] >> 0);
                buffer[offset + 1] = (byte)(b[j] >> 8);
                buffer[offset + 2] = (byte)(b[j] >> 16);
                buffer[offset + 3] = (byte)(b[j] >> 24);
                offset += 4;
            }
        }
        return buffer;
    }

    //BMP文件信息头
    public static byte[] addBMPImageInfosHeader(int w, int h) {
        byte[] buffer = new byte[40];
        //这个是固定的 BMP 信息头要40个字节
        buffer[0] = 0x28;
        buffer[1] = 0x00;
        buffer[2] = 0x00;
        buffer[3] = 0x00;
        //宽度 地位放在序号前的位置 高位放在序号后的位置
        buffer[4] = (byte) (w >> 0);
        buffer[5] = (byte) (w >> 8);
        buffer[6] = (byte) (w >> 16);
        buffer[7] = (byte) (w >> 24);
        //长度 同上
        buffer[8] = (byte) (h >> 0);
        buffer[9] = (byte) (h >> 8);
        buffer[10] = (byte) (h >> 16);
        buffer[11] = (byte) (h >> 24);
        //总是被设置为1
        buffer[12] = 0x01;
        buffer[13] = 0x00;
        //比特数 像素 32位保存一个比特 这个不同的方式(ARGB 32位 RGB24位不同的!!!!)
        buffer[14] = 0x20;
        buffer[15] = 0x00;
        //0-不压缩 1-8bit位图
        //2-4bit位图 3-16/32位图
        //4 jpeg 5 png
        buffer[16] = 0x00;
        buffer[17] = 0x00;
        buffer[18] = 0x00;
        buffer[19] = 0x00;
        //说明图像大小
        buffer[20] = 0x00;
        buffer[21] = 0x00;
        buffer[22] = 0x00;
        buffer[23] = 0x00;
        //水平分辨率
        buffer[24] = 0x00;
        buffer[25] = 0x00;
        buffer[26] = 0x00;
        buffer[27] = 0x00;
        //垂直分辨率
        buffer[28] = 0x00;
        buffer[29] = 0x00;
        buffer[30] = 0x00;
        buffer[31] = 0x00;
        //0 使用所有的调色板项
        buffer[32] = 0x00;
        buffer[33] = 0x00;
        buffer[34] = 0x00;
        buffer[35] = 0x00;
        //不开颜色索引
        buffer[36] = 0x00;
        buffer[37] = 0x00;
        buffer[38] = 0x00;
        buffer[39] = 0x00;
        return buffer;
    }

    //BMP文件头
    public static byte[] addBMPImageHeader(int size) {
        byte[] buffer = new byte[14];
        //magic number 'BM'
        buffer[0] = 0x42;
        buffer[1] = 0x4D;
        //记录大小
        buffer[2] = (byte) (size >> 0);
        buffer[3] = (byte) (size >> 8);
        buffer[4] = (byte) (size >> 16);
        buffer[5] = (byte) (size >> 24);
        buffer[6] = 0x00;
        buffer[7] = 0x00;
        buffer[8] = 0x00;
        buffer[9] = 0x00;
        buffer[10] = 0x36;
        buffer[11] = 0x00;
        buffer[12] = 0x00;
        buffer[13] = 0x00;
        return buffer;
    }

    public static RggbChannelVector colorTemperature(int whiteBalance) {
        float temperature = whiteBalance / 100;
        float red;
        float green;
        float blue;

        //Calculate red
        if (temperature <= 66)
            red = 255;
        else {
            red = temperature - 60;
            red = (float) (329.698727446 * (Math.pow((double) red, -0.1332047592)));
            if (red < 0)
                red = 0;
            if (red > 255)
                red = 255;
        }


        //Calculate green
        if (temperature <= 66) {
            green = temperature;
            green = (float) (99.4708025861 * Math.log(green) - 161.1195681661);
            if (green < 0)
                green = 0;
            if (green > 255)
                green = 255;
        } else {
            green = temperature - 60;
            green = (float) (288.1221695283 * (Math.pow((double) green, -0.0755148492)));
            if (green < 0)
                green = 0;
            if (green > 255)
                green = 255;
        }

        //calculate blue
        if (temperature >= 66)
            blue = 255;
        else if (temperature <= 19)
            blue = 0;
        else {
            blue = temperature - 10;
            blue = (float) (138.5177312231 * Math.log(blue) - 305.0447927307);
            if (blue < 0)
                blue = 0;
            if (blue > 255)
                blue = 255;
        }

//        Log.v(TAG, "red=" + red + ", green=" + green + ", blue=" + blue);
        return new RggbChannelVector((red / 255) * 2, (green / 255), (green / 255), (blue / 255) * 2);
    }

}
