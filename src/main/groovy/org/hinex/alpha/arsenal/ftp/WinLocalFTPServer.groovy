package org.hinex.alpha.arsenal.ftp

import org.apache.ftpserver.FtpServerFactory

class WinLocalFTPServer {
    
    static main(args) {
        def serverFactory = new FtpServerFactory()
        serverFactory.setUserManager(new UserManager())
        def server = serverFactory.createServer()
        server.start()
    }

}
