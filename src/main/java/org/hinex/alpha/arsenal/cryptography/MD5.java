
/* ##################### 
 * Created by AlphaHinex
 * 2011-3-31 
 * implement of message-digest 5 algorithm
 */

package org.hinex.alpha.arsenal.cryptography;

import static java.lang.Math.abs;
import static java.lang.Math.floor;
import static java.lang.Math.pow;
import static java.lang.Math.sin;

import java.util.Arrays;

public class MD5 {
//  private static Logger log = Logger.getLogger(GenMD5.class);
    
    private static long A = 0x67452301L;
    private static long B = 0xefcdab89L;
    private static long C = 0x98badcfeL;
    private static long D = 0x10325476L;
    private static final int[] r = {7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22,
                                    5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20,
                                    4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23,
                                    6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21};
    private static final long[] t = new long[64];
    static {
        for(int i = 0; i < 64; i++) {
            t[i] = (long)floor(abs(sin(i + 1)) * pow(2, 32));
        }
    }
    
    public static String getMD5(String input) {
        byte[] readyBytes = getReadyBytes(input);
        
        for(int i = 0; i < readyBytes.length>>>6; i++) {
            long[] m = new long[16];
            m = CodeUtil.decode(Arrays.copyOfRange(readyBytes, i<<6, (i+1)<<6));
//          log.debug("m[]:");
//          log.debug(Arrays.toString(m));
            fourTrans(m);
        }
        
        byte[] result = CodeUtil.encode(new long[]{A, B, C, D});
//      log.debug("after fourTurns:");
//      log.debug(Arrays.toString(result));

        return CodeUtil.toHexString(result);
    }
    
    private static byte[] getReadyBytes(String input) {
        // turn input string to bytes
        byte[] readyBytes;
        int readyBytesLen = 0;
        byte[] inputBytes = input.getBytes();
        int inputLen = inputBytes.length;
//      log.debug("inputBytes.length:" + inputLen);
        readyBytesLen += inputLen;
        
        // generate fill bytes to expand input bytes' length to 64*N + 56
        int mod = inputLen%64;
        int fillNum = mod < 56?(56 - mod):(120 - mod);
//      log.debug("fillNum:" + fillNum);
        byte[] fillBytes = new byte[fillNum];
        Arrays.fill(fillBytes, (byte)0);
        fillBytes[0] = (byte)0x80;
        readyBytesLen += fillNum;
        
        // bytes of 8 length represent the length of input in binary mode 
        long[] inputLenL = new long[2];
        inputLenL[0] = (long)(inputLen<<3);
        inputLenL[1] = 0L;
        byte[] inputLenBytes = CodeUtil.encode(inputLenL);
        readyBytesLen += inputLenBytes.length;
        
        // add the three parts above to one byte array
        readyBytes = new byte[readyBytesLen];
        int i = 0;
        for(byte temp:inputBytes) {
            readyBytes[i++] = temp;
        }
        for(byte temp:fillBytes) {
            readyBytes[i++] = temp;
        }
        for(byte temp:inputLenBytes) {
            readyBytes[i++] = temp;
        }
//      log.debug("readyBytes[" + readyBytes.length + "]:");
//      log.debug(Arrays.toString(readyBytes));
        
        return readyBytes;
    }
    
    private static void fourTrans(long[] m) {
        int g;
        long a = A, b = B, c = C, d = D; 
        for(int i = 0; i < 64; i++) {
            if(i < 16) {
                g = i;
                a = FF(a, b, c, d, m[g], r[i], t[i]);
            }
            else if(i < 32) {
                g = (5*i + 1)%16;
                a = GG(a, b, c, d, m[g], r[i], t[i]);
            }
            else if(i<48) {
                g = (3*i + 5)%16;
                a = HH(a, b, c, d, m[g], r[i], t[i]);
            }
            else {
                g = (7*i)%16;
                a = II(a, b, c, d, m[g], r[i], t[i]);
            }
            
            long temp = d;
            d = c;
            c = b;
            b = a;
            a = temp;
        }
        
        A += a;
        B += b;
        C += c;
        D += d;
    }
    
    private static long FF(long a, long b, long c, long d, long m, long s, long t) {
        a += F(b,c,d) + m + t;
        a = ((int) a << s) | ((int) a >>> (32 - s));
        a += b;
        return a;
    }
    
    private static long F(long x, long y, long z) {
        return (x&y)|((~x)&z);
    }
    
    private static long GG(long a, long b, long c, long d, long m, long s, long t) {
        a += G(b,c,d) + m + t;
        a = ((int) a << s) | ((int) a >>> (32 - s));
        a += b;
        return a;
    }
    
    private static long G(long x, long y, long z) {
        return (x&z)|(y&(~z));
    }
    
    private static long HH(long a, long b, long c, long d, long m, long s, long t) {
        a += H(b,c,d) + m + t;
        a = ((int) a << s) | ((int) a >>> (32 - s));
        a += b;
        return a;
    }
    
    private static long H(long x, long y, long z) {
        return x^y^z;
    }
    
    private static long II(long a, long b, long c, long d, long m, long s, long t) {
        a += I(b,c,d) + m + t;
        a = ((int) a << s) | ((int) a >>> (32 - s));
        a += b;
        return a;
    }
    
    private static long I(long x, long y, long z) {
        return y^(x|(~z));
    }
    
    public static class Test {
        public static void main(String[] args) {
            String input = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            
            long starttime = System.currentTimeMillis();
            System.out.println(getMD5(input));
            long endtime = System.currentTimeMillis();
            System.out.println(endtime - starttime);
            
            try {
                java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
                starttime = System.currentTimeMillis();
                md.update(input.getBytes());
                System.out.println(CodeUtil.toHexString(md.digest()));
                endtime = System.currentTimeMillis();
                System.out.println(endtime - starttime);
            } catch (java.security.NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }
}
