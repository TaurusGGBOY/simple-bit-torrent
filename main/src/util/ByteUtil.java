package util;

import java.nio.ByteBuffer;

public class ByteUtil {
    /**
     * 传入一个字节 返回这一个字节的二进制字符串表示
     *
     * @param by
     * @return
     */
    public static String getBit(byte by) {
        StringBuffer sb = new StringBuffer();
        sb.append((by >> 7) & 0x1)
                .append((by >> 6) & 0x1)
                .append((by >> 5) & 0x1)
                .append((by >> 4) & 0x1)
                .append((by >> 3) & 0x1)
                .append((by >> 2) & 0x1)
                .append((by >> 1) & 0x1)
                .append((by >> 0) & 0x1);
        return sb.toString();
    }

    /**
     * 传入一个比特串，转换成一个字节
     *
     * @param bit
     * @return
     */
    public static byte bitToByte(String bit) {
        int re, len;
        if (null == bit) {
            return 0;
        }
        len = bit.length();
        if (len != 4 && len != 8) {
            return 0;
        }
        if (len == 8) {// 8 bit处理
            if (bit.charAt(0) == '0') {// 正数
                re = Integer.parseInt(bit, 2);
            } else {// 负数
                re = Integer.parseInt(bit, 2) - 256;
            }
        } else {//4 bit处理
            re = Integer.parseInt(bit, 2);
        }
        return (byte) re;
    }

    /**
     * int到byte[] 由高位到低位
     *
     * @param i 需要转换为byte数组的整行值。
     * @return byte数组
     */
    public static byte[] intToByteArray(int i) {
        return ByteBuffer.allocate(4).putInt(i).array();
    }

    /**
     * byte数组中取int数值，本方法适用于(低位在后，高位在前)的顺序。和intToBytes2（）配套使用
     */
    public static int byteArrayToInt(byte[] src) {
        return ByteBuffer.wrap(src).getInt();
    }

    /**
     * 传入01字符串，返回比特数组，注意chars的长度必须是8的整数
     * @param chars
     * @return
     */
    public static byte[] bitStringToByteArr(char[] chars) {
        byte[] output = new byte[chars.length / 8];
        for (int i = 0; i < output.length; i++) {
            for (int b = 0; b <= 7; b++) {
                output[i] |= (byte) ((chars[i * 8 + b] == '1' ? 1 : 0) << (7 - b));
            }
        }
        return output;
    }

    /**
     * 连接两个比特数组
     * @param a
     * @param b
     * @return
     */
    public static byte[] concat(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

}