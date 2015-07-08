package org.hinex.alpha.arsenal.test.fixtures.file

class TestResource {
    
    private static def rootDir = "${new File('').getAbsolutePath()}/src/test/resources"
    
    static File getFile(String fileName, boolean dir = false) {
        File file = new File("$rootDir/$fileName")
        dir ? file.mkdirs() : file.getParentFile().mkdirs()
        file
    }
    
    private String baseDir
    
    public TestResource(String baseDir) {
        this.baseDir = baseDir
    }
    
    def create(Closure cl) {
        cl.delegate = this
        cl.resolveStrategy = Closure.DELEGATE_FIRST
        cl()
    }
    
    def file(String name) {
        getFile("$baseDir/$name")
    }

}