package org.hinex.alpha.arsenal.file

import org.hinex.alpha.arsenal.test.TestResource

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Unroll

@Unroll
@Stepwise
class FixedSizeFileFactorySpec extends Specification {
    
    @Shared File testFile
    
    def setupSpec() {
        testFile = TestResource.getFile('testFile')
    }
    
    def cleanupSpec() {
        testFile.delete()
    }
    
    def "Produce files with fixed size: #size"() {
        setup:
        FixedSizeFileFactory.produce(testFile, size)
        
        expect:
        testFile.length() == length
        
        where:
        size        || length
        '5'         || 5
        '10b'       || 10
        '1,024'     || 1024
        '2 k'       || 2 * 1024
        '2.5Kb'     || 2.5 * 1024
        '2 MB'      || 2 * 1024 * 1024
        '1 048 576' || 1 * 1024 * 1024
    }
    
    def "Produce large files with fixed size: #size"() {
        setup:
        FixedSizeFileFactory.setMaterialLen 1024*1024*10L
        FixedSizeFileFactory.produce(testFile, size)
        
        expect:
        testFile.length() == length
        
        where:
        size        || length
        '0.5g'      || 0.5 * 1024 * 1024 * 1024
        '2g'        || 2L * 1024 * 1024 * 1024
    }

}
