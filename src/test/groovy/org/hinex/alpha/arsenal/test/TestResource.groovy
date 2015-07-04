package org.hinex.alpha.arsenal.test

class TestResource {
    
    static File getFile(String fileName) {
        def file = new File("${new File('').getAbsolutePath()}/src/test/resources/$fileName")
        file.getParentFile().mkdirs()
        file
    }

}
