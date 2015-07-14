package org.hinex.alpha.arsenal.crypto

import java.security.MessageDigest

import org.hinex.alpha.arsenal.util.CodecUtil

import spock.lang.Specification
import spock.lang.Unroll;

@Unroll
class MD5Spec extends Specification {
    
    def static inputs = [
        'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789',
        '中文'
    ]
    
    def static result
    
    def "Compare the MD5 results of #input with Java Message Digest"() {
        given:
        def md5Result = MD5.hexdigest(input)
        MessageDigest md = MessageDigest.getInstance('MD5')
        def jmdResult = CodecUtil.toHexString(md.digest(input.bytes))
        println "$input : $md5Result"
        
        expect:
        md5Result.equalsIgnoreCase jmdResult
        
        where:
        input << inputs
    }

}
