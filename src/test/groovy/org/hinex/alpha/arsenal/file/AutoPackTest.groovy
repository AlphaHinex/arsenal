package org.hinex.alpha.arsenal.file;

import static org.junit.Assert.*

import org.hinex.alpha.arsenal.autopack.AutoPack
import org.hinex.alpha.arsenal.autopack.AutoPack.FileType
import org.hinex.alpha.arsenal.autopack.AutoPack.PackStyle
import org.junit.Test

class AutoPackTest {

    @Test
    public void test() {
        def fileListPath = '/Users/alphahinex/workspace/1.log'
        def srcPath = '/Users/alphahinex/workspace/portal/'
        def desPath = '/Users/alphahinex/Desktop/update_portal/'
        def fileType = FileType.TYPE_SOURCE
        def packStyle = PackStyle.PACK_HIERARCHICAL
        
        new AutoPack(fileListPath, srcPath, desPath, fileType, packStyle).pack()
        println 'done'
    }

}
