package org.hinex.alpha.arsenal.autopack

import org.hinex.alpha.arsenal.file.CopyFile

class AutoPack {
    
    private String fileListPath
    private String srcPath
    private String desPath
    private FileType fileType
    private PackStyle packStyle
    private String readmePath
    
    public AutoPack(String fileListPath, 
                    String srcPath, 
                    String desPath,
                    FileType fileType = FileType.TYPE_SOURCE,
                    PackStyle packStyle = PackStyle.PACK_HIERARCHICAL) {
        this.fileListPath = fileListPath
        this.srcPath = srcPath
        this.desPath = desPath
        this.fileType = fileType
        this.packStyle = packStyle
    }
    
    public void pack() {
        checkAndInitPath()
        genReadme()
        copyFiles()
    }
    
    private void checkAndInitPath() {
        assert fileListPath > ''
        assert srcPath > ''
        assert desPath > ''
        
        assert new File(fileListPath).exists()
        assert new File(srcPath).exists()
         
        def file = new File(desPath)
        if (!file.exists()) {
            file.mkdirs()
        }
    }
    
    private void genReadme() {
        readmePath = "$desPath/README.md"
        
        new File(readmePath).withWriter { writer ->
            writer.writeLine '### Change file list \r\n'
            
            def fileCount = 0
            new File(fileListPath).withReader { reader ->
                writer.writeLine('```')
                reader.readLines().each { path ->
                    path = path.trim().replace('/', File.separator)
                    if (fileType == FileType.TYPE_CLASS) {
                        if (path.startsWith('src')) {
                            path = path.replace('src', "build${File.separator}classes${File.separator}main")
                        }
                        if (path.endsWith('.java') || path.endsWith('.groovy')) {
                            path = path.replaceAll(/(\.java|\.groovy)/, '.class')
                        }
                        writer.writeLine(path)
                        fileCount++
                        
                        if (path.endsWith('.class')) {
                            def container = new File("$srcPath$path")
                            container.getParentFile().listFiles().each { file ->
                                if (file.getName().startsWith(container.getName().replace('.class', '$'))) {
                                    writer.writeLine(file.getPath().replace(srcPath, ''))
                                    fileCount++
                                }
                            }
                        }
                    } else {
                        writer.writeLine(path)
                        fileCount++
                    }
                }
                writer.writeLine('```')
            }
            
            writer.writeLine("\r\nTotal file: $fileCount")
        }
    }
    
    private void copyFiles() {
        new File(readmePath).withReader { reader ->
            reader.readLines().each { filePath ->
                if (filePath ==~ /(?i)^(.*[\/\\\\]){1,}.*.[a-z]{2,5}$/) {
                    def fileNameOrPath = filePath
                    if (packStyle == PackStyle.PACK_FLAT) {
                        fileNameOrPath = filePath.substring(filePath.lastIndexOf(File.separatorChar))
                    }
                    CopyFile.copy(srcPath + filePath, desPath + fileNameOrPath)
                }
            }
        }
    }
    
    public enum FileType {
        TYPE_SOURCE,
        TYPE_CLASS;
    }
    
    public enum PackStyle {
        PACK_FLAT,
        PACK_HIERARCHICAL;
    }

}
