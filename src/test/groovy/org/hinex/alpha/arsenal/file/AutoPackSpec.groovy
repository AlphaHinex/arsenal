package org.hinex.alpha.arsenal.file

import org.hinex.alpha.arsenal.test.fixtures.file.TestResource;

import spock.lang.Specification

class AutoPackSpec extends Specification {
    
    def "Pack with default setting"() {
        given:
        createTestFiles()
    }
    
    private def createTestFiles() {
        createDir('srcdir/src/main/java/org/hinex/alpha/arsenal/test', {
            (1..3).each { idx ->
                file("Test${idx}.java").text = "org.hinex.alpha.arsenal.test.Test$idx"
            }
        })
        createDir('srcdir/src/main/groovy/org/hinex/alpha/arsenal/test', {
            (1..5).each { idx ->
                file("GroovyTest${idx}.groovy").text = "org.hinex.alpha.arsenal.test.GroovyTest$idx"  
            }
        })
        createDir('srcdir/build/classes/main/org/hinex/alpha/arsenal/test', {
            (1..3).each { idx ->
                file("Test${idx}.class").text = "CAFE BABE org.hinex.alpha.arsenal.test.Test$idx"
            }
        })
        createDir('srcdir/build/classes/main/org/hinex/alpha/arsenal/test', {
            (1..5).each { idx ->
                file("GroovyTest${idx}.class").text = "CAFE BABE org.hinex.alpha.arsenal.test.GroovyTest$idx"
                (1..3).each { ci ->
                    file("GroovyTest${idx}\$_\$spock_closure${ci}.class").text = "CAFE BABE org.hinex.alpha.arsenal.test.GroovyTest$idx closure$ci"
                }
            }
        })
    }
    
    private def createDir(String dir, Closure cl) {
        TestResource root = new TestResource(dir)
        root.create cl
    }

}
