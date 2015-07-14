
/* ##################### 
 * Created by AlphaHinex
 * 2011-3-31 
 * implement of message-digest 5 algorithm
 */

package org.hinex.alpha.arsenal.crypto;

import static java.lang.Math.abs;
import static java.lang.Math.floor;
import static java.lang.Math.pow;
import static java.lang.Math.sin;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.hinex.alpha.arsenal.util.CodecUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MD5 {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MD5.class);
    
    private static long aContainer = 0x67452301L;
    private static long bContainer = 0xefcdab89L;
    private static long cContainer = 0x98badcfeL;
    private static long dContainer = 0x10325476L;
    private static final int[] R = {7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22,
                                    5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20,
                                    4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23,
                                    6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21};
    private static final long[] T = new long[64];
    
    static {
        for(int i = 0; i < 64; i++) {
            T[i] = (long)floor(abs(sin(i + 1)) * pow(2, 32));
        }
    }
    
    private MD5() { }
    
    public static String hexdigest(String input) throws UnsupportedEncodingException {
        return hexdigest(input, Charset.defaultCharset().name());
    }
    
    public static String hexdigest(String input, String charsetName) throws UnsupportedEncodingException {
        byte[] readyBytes = getReadyBytes(input, charsetName);
        long[] m;
        
        for (int i = 0; i < readyBytes.length >>> 6; i++) {
            m = CodecUtil.decode(Arrays.copyOfRange(readyBytes, i << 6, (i + 1) << 6));
            LOGGER.debug("m[]:");
            LOGGER.debug(Arrays.toString(m));
            fourTrans(m);
        }
        
        byte[] result = CodecUtil.encode(new long[]{aContainer, bContainer, cContainer, dContainer});
        LOGGER.debug("after fourTurns:");
        LOGGER.debug(Arrays.toString(result));

        return CodecUtil.toHexString(result);
    }
    
    private static byte[] getReadyBytes(String input, String charsetName) throws UnsupportedEncodingException {
        // turn input string to bytes
        byte[] readyBytes;
        int readyBytesLen = 0;
        byte[] inputBytes = input.getBytes(charsetName);
        int inputLen = inputBytes.length;
        LOGGER.debug("inputBytes.length:" + inputLen);
        readyBytesLen += inputLen;
        
        // generate fill bytes to expand input bytes' length to 64*N + 56
        int mod = inputLen%64;
        int fillNum = mod < 56?(56 - mod):(120 - mod);
        LOGGER.debug("fillNum:" + fillNum);
        byte[] fillBytes = new byte[fillNum];
        Arrays.fill(fillBytes, (byte)0);
        fillBytes[0] = (byte)0x80;
        readyBytesLen += fillNum;
        
        // bytes of 8 length represent the length of input in binary mode 
        long[] inputLenL = new long[2];
        inputLenL[0] = (long)(inputLen<<3);
        inputLenL[1] = 0L;
        byte[] inputLenBytes = CodecUtil.encode(inputLenL);
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
        LOGGER.debug("readyBytes[" + readyBytes.length + "]:");
        LOGGER.debug(Arrays.toString(readyBytes));
        
        return readyBytes;
    }
    
    private static void fourTrans(long[] m) {
        int g;
        long a = aContainer;
        long b = bContainer;
        long c = cContainer;
        long d = dContainer; 
        for(int i = 0; i < 64; i++) {
            if(i < 16) {
                g = i;
                a = ff(a, b, c, d, m[g], R[i], T[i]);
            } else if(i < 32) {
                g = (5*i + 1)%16;
                a = gg(a, b, c, d, m[g], R[i], T[i]);
            } else if(i<48) {
                g = (3*i + 5)%16;
                a = hh(a, b, c, d, m[g], R[i], T[i]);
            } else {
                g = (7*i)%16;
                a = ii(a, b, c, d, m[g], R[i], T[i]);
            }
            
            long temp = d;
            d = c;
            c = b;
            b = a;
            a = temp;
        }
        
        aContainer += a;
        bContainer += b;
        cContainer += c;
        dContainer += d;
    }
    
    private static long ff(long a, long b, long c, long d, long m, long s, long t) {
        a += f(b, c, d) + m + t;
        a = ((int) a << s) | ((int) a >>> (32 - s));
        a += b;
        return a;
    }
    
    private static long f(long x, long y, long z) {
        return (x&y)|((~x)&z);
    }
    
    private static long gg(long a, long b, long c, long d, long m, long s, long t) {
        a += g(b, c , d) + m + t;
        a = ((int) a << s) | ((int) a >>> (32 - s));
        a += b;
        return a;
    }
    
    private static long g(long x, long y, long z) {
        return (x&z)|(y&(~z));
    }
    
    private static long hh(long a, long b, long c, long d, long m, long s, long t) {
        a += h(b, c, d) + m + t;
        a = ((int) a << s) | ((int) a >>> (32 - s));
        a += b;
        return a;
    }
    
    private static long h(long x, long y, long z) {
        return x^y^z;
    }
    
    private static long ii(long a, long b, long c, long d, long m, long s, long t) {
        a += i(b, c, d) + m + t;
        a = ((int) a << s) | ((int) a >>> (32 - s));
        a += b;
        return a;
    }
    
    private static long i(long x, long y, long z) {
        return y^(x|(~z));
    }
    
}
