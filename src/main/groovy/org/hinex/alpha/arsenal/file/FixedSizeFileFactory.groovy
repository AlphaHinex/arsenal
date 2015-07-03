package org.hinex.alpha.arsenal.file

class FixedSizeFileFactory {
    
    static private byte[] material = '0123456789abcdefghijklmnopqrstuvwxyz'.bytes
    
    static File produce(File file, String fileLen) {
        fileLen = fileLen.replaceAll(/[\s|,]/, '')
        def len = fileLen.find(/\d+(.\d+)?/).toBigDecimal()
        def unit = fileLen.replace("$len", '')
        if(unit) {
            switch(unit.toLowerCase()) {
                case 'kb':
                case 'k': len *= 1024; break
                case 'mb':
                case 'm': len *= 1024 * 1024; break
                case 'gb':
                case 'g': len *= 1024 * 1024 * 1024; break
            }
        }
        
        file.withOutputStream { os ->
            def ml = material.length
            if(len <= ml) {
                os.write((byte[])(material[0..len-1]))
            } else {
                long pos = 0
                while(pos + ml <= len) {
                    os.write(material)
                    pos += ml
                }
                if(len > pos) {
                    os.write((byte[])(material[0..(len-pos-1)]))
                }
            }
        }
        file
    }
    
    static void setMaterialLen(long len) {
        material = ('h'*len).bytes
    }

}
