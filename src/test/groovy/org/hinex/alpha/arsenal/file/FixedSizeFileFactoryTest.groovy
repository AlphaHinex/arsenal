package org.hinex.alpha.arsenal.file;

import static org.junit.Assert.*

import org.hinex.alpha.arsenal.test.TestResource
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test

class FixedSizeFileFactoryTest {

    static File testFile
    
    @BeforeClass
    public static void setUp() {
        testFile = TestResource.getFile('testFile')
    }
    
    @AfterClass
    public static void tearDown() {
        testFile.delete()
    }
    
    @Test
    public void tinyFile() {
        ProduceFile.generate(testFile, '10b')
        assert testFile.length() == 10
        
        ProduceFile.generate(testFile, '5')
        assert testFile.length() == 5
    }
    
    @Test
    public void kb() {
        ProduceFile.generate(testFile, '1,024')
        assert testFile.length() == 1024
        
        ProduceFile.generate(testFile, '2 k')
        assert testFile.length() == 2 * 1024
        
        ProduceFile.generate(testFile, '2.5Kb')
        assert testFile.length() == 2.5 * 1024
    }
    
    @Test
    public void mb() {
        ProduceFile.generate(testFile, '2 MB')
        assert testFile.length() == 2 * 1024 * 1024
        
        ProduceFile.generate(testFile, '1 048 576')
        assert testFile.length() == 1 * 1024 * 1024
    }
    
    @Test
    public void gb() {
        ProduceFile.generate(testFile, '0.5g')
        assert testFile.length() == 0.5 * 1024 * 1024 * 1024
    }
    
    @Test
    public void twogb() {
        ProduceFile.setMaterialLen 1024*1024*10L
        ProduceFile.generate(testFile, '2g')
        assert testFile.length() == 2L * 1024 * 1024 * 1024
    }

}
