package org.hinex.alpha.arsenal.ftp

import org.apache.ftpserver.ftplet.Authentication
import org.apache.ftpserver.ftplet.AuthenticationFailedException
import org.apache.ftpserver.ftplet.Authority
import org.apache.ftpserver.ftplet.FtpException
import org.apache.ftpserver.ftplet.User
import org.apache.ftpserver.usermanager.Md5PasswordEncryptor
import org.apache.ftpserver.usermanager.impl.AbstractUserManager
import org.apache.ftpserver.usermanager.impl.BaseUser
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission
import org.apache.ftpserver.usermanager.impl.TransferRatePermission
import org.apache.ftpserver.usermanager.impl.WritePermission
import org.hinex.alpha.arsenal.test.fixtures.file.TestResource

class UserManager extends AbstractUserManager {
    
    public UserManager() {
        super("admin", new Md5PasswordEncryptor())
    }

    @Override
    public User authenticate(Authentication arg0) throws AuthenticationFailedException {
        getUserByName('AlphaHinex')
    }

    @Override
    public void delete(String arg0) throws FtpException {
        // do nothing
    }

    @Override
    public boolean doesExist(String arg0) throws FtpException {
        // do nothing
        return false;
    }

    @Override
    public String[] getAllUserNames() throws FtpException {
        // do nothing
        return null;
    }

    @Override
    public User getUserByName(String arg0) throws FtpException {
        def user = new BaseUser()
        user.setName('AlphaHinex')
        user.setPassword('123');
        user.setHomeDirectory(TestResource.getFile('').getAbsolutePath())
        user.setEnabled(true)
        user.setMaxIdleTime(0)

        List<Authority> authorities = new ArrayList<Authority>();
        authorities.add(new WritePermission());
        authorities.add(new ConcurrentLoginPermission(0, 0));
        authorities.add(new TransferRatePermission(0, 0));
        user.setAuthorities(authorities);
        user
    }

    @Override
    public void save(User arg0) throws FtpException {
        // do nothing
    }

}
