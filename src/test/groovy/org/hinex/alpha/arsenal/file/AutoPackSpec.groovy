package org.hinex.alpha.arsenal.file

import org.hinex.alpha.arsenal.autopack.AutoPack
import org.hinex.alpha.arsenal.test.fixtures.file.TestResource

import spock.lang.Specification

class AutoPackSpec extends Specification {
    
    private static final String PACKAGE = 'org/hinex/alpha/arsenal/test'
    private static final int JAVA_SRC_COUNT = 3
    private static final int GROOVY_SRC_COUNT = 5
    private static final int GROOVY_CLOSURE_COUNT = 3
    
    private static File fileList = TestResource.getFile('autopack/fileList')
    private static def fileListPath = TestResource.getFile('autopack/filelist').getAbsolutePath()
    private static def srcPath = TestResource.getFile('autopack/srcdir', true).getAbsolutePath()
    private static def desPath = TestResource.getFile('autopack/desdir', true).getAbsolutePath()
    
    def setupSpec() {
        createTestFiles()
    }
    
    def cleanup() {
        new File(desPath).deleteDir()
    }
    
    def cleanupSpec() {
        TestResource.getFile('autopack').deleteDir()
    }
    
    def "Pack with default setting (FileType.TYPE_SOURCE, PackStyle.PACK_HIERARCHICAL)"() {
        when:
        new AutoPack(fileListPath, srcPath, desPath).pack()
        
        then:
        new File(desPath).list().length == 2
        new File("$desPath/src/main/java/$PACKAGE").list(filter('.java')).length == JAVA_SRC_COUNT
        new File("$desPath/src/main/groovy/$PACKAGE").list(filter('.groovy')).length == GROOVY_SRC_COUNT
    }
    
    private def createTestFiles() {
        createDir("autopack/srcdir/src/main/java/$PACKAGE", {
            (1..JAVA_SRC_COUNT).each { idx ->
                file("Test${idx}.java").text = "org.hinex.alpha.arsenal.test.Test$idx"
            }
        })
        createDir("autopack/srcdir/src/main/groovy/$PACKAGE", {
            (1..GROOVY_SRC_COUNT).each { idx ->
                file("GroovyTest${idx}.groovy").text = "org.hinex.alpha.arsenal.test.GroovyTest$idx"  
            }
        })
        createDir("autopack/srcdir/build/classes/main/$PACKAGE", {
            (1..JAVA_SRC_COUNT).each { idx ->
                file("Test${idx}.class").text = "CAFE BABE org.hinex.alpha.arsenal.test.Test$idx"
            }
        })
        createDir("autopack/srcdir/build/classes/main/$PACKAGE", {
            (1..GROOVY_SRC_COUNT).each { idx ->
                file("GroovyTest${idx}.class").text = "CAFE BABE org.hinex.alpha.arsenal.test.GroovyTest$idx"
                (1..GROOVY_CLOSURE_COUNT).each { ci ->
                    file("GroovyTest${idx}\$_\$spock_closure${ci}.class").text = "CAFE BABE org.hinex.alpha.arsenal.test.GroovyTest$idx closure$ci"
                }
            }
        })
        
        File fileList = TestResource.getFile('autopack/filelist')
        (1..JAVA_SRC_COUNT).each { idx ->
            fileList << "src/main/java/$PACKAGE/Test${idx}.java\r\n"
        }
        (1..GROOVY_SRC_COUNT).each { idx ->
            fileList << "src/main/groovy/$PACKAGE/GroovyTest${idx}.groovy\r\n"
        }
    }
    
    private def createDir(String dir, Closure cl) {
        TestResource root = new TestResource(dir)
        root.create cl
    }
    
    private def filter(String suffix) {
        new FilenameFilter() {
            boolean accept(File file, String name) {
                name.endsWith(suffix)
            }
        }
    }

}
