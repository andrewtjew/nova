/*******************************************************************************
 * Copyright (C) 2017-2019 Kat Fung Tjew
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.nova.comet;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

import org.nova.utils.Utils;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.IdentityRepository;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.agentproxy.AgentProxyException;
import com.jcraft.jsch.agentproxy.RemoteIdentityRepository;
import com.jcraft.jsch.agentproxy.connector.PageantConnector;

public class SshSession
{
    static enum CopyDirectoryResult
    {
        SUCCESS, SOURCE_NOT_DIRECTORY, DESTINATION_CHECK_FAILED,
    }

    final JSch jsch;
    Session session;
    ChannelSftp channel;

    public SshSession(String host, int port, String user, String keyFile, String passphrase) throws JSchException, IOException
    {
        this.jsch = new JSch();
        this.jsch.addIdentity(keyFile, passphrase);
        this.session = jsch.getSession(user, host, port);
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        this.session.connect();
        this.channel = (ChannelSftp) session.openChannel("sftp");
        this.channel.connect();
    }

    public SshSession(String host, int port, String user) throws JSchException, IOException, AgentProxyException
    {
        PageantConnector connector=new PageantConnector();
        
        this.jsch = new JSch();
        IdentityRepository identityRepository=new RemoteIdentityRepository(connector);
        this.jsch.setIdentityRepository(identityRepository);
        
        this.session = jsch.getSession(user, host, port);
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        this.session.connect();
        this.channel = (ChannelSftp) session.openChannel("sftp");
        this.channel.connect();
    }

    public String exec(boolean captureOutput,String command) throws IOException, JSchException
    {
        ChannelExec channel = (ChannelExec) this.session.openChannel("exec");
        if (captureOutput)
        {
            StringBuilder sb = new StringBuilder();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            channel.setCommand(command);
            channel.connect();
            try
            {
                for (String line = reader.readLine(); line != null; line = reader.readLine())
                {
                    sb.append(line);
                    sb.append("\r\n");
                }
            }
            finally
            {
                channel.disconnect();
            }
            return sb.toString();
        }
        else
        {
            channel.setCommand(command);
            channel.connect();
            channel.disconnect();
            return null;
        }
    }
    public String exec(boolean captureOutput,String directory,String command) throws IOException, JSchException
    {
        return exec(captureOutput,"cd \""+directory+"\";"+command);
    }

    /*
    public void execQuiet(String command) throws IOException, JSchException
    {
        ChannelExec channel = (ChannelExec) this.session.openChannel("exec");
        channel.setCommand(command);
        channel.connect();
        channel.disconnect();
    }
    */

    public int killMatching(String commandPattern,int delay) throws Throwable
    {
        String command="ps -C \""+commandPattern+"\" -f";
        String output=exec(true,command);
        String[] lines=Utils.split(output, '\n');
        for (String line:lines)
        {
            if (line.contains(commandPattern))
            {
                String[] parts=Utils.splitUsingWhiteSpace(line);
                String id=parts[1];
                exec(true,"kill "+id);
            }
        }
        if (delay>0)
        {
            Thread.sleep(delay);
        }
        String afterOutput=exec(true,command);
        String[] afterLines=Utils.split(afterOutput, '\n');
        int killed=lines.length-afterLines.length;
        return killed;
    }
    
    public void execBackground(String directory,String command) throws IOException, JSchException
    {
        exec(false,"cd \""+directory+"\";nohup "+command+" &>/dev/null &");
    }

    public boolean exists(String path)
    {
        try
        {
            channel.lstat(path);
            return true;
        }
        catch (Throwable t)
        {
        }
        return false;
    }

    public boolean isFile(String path)
    {
        try
        {
            SftpATTRS attributes = channel.lstat(path);
            return !attributes.isDir();
        }
        catch (Throwable t)
        {
        }
        return false;
    }

    public boolean isDirectory(String path)
    {
        try
        {
            SftpATTRS attributes = channel.lstat(path);
            return attributes.isDir();
        }
        catch (Throwable t)
        {
        }
        return false;
    }

    public boolean createDirectoryIfNotExists(String path) throws JSchException, SftpException
    {
        try
        {
            SftpATTRS attributes = channel.lstat(path);
            if (attributes.isDir() == false)
            {
                throw new Exception("Not a directory: " + path);
            }
            return false;
        }
        catch (Throwable t)
        {
        }
        channel.mkdir(path);
        return true;
    }

    /*
    public boolean copyIfOlder(String source, String destination, int seconds) throws JSchException, SftpException
    {
        try
        {
            SftpATTRS attributes = channel.lstat(destination);
            if (attributes.isDir() == true)
            {
                throw new Exception("Not a file: " + destination);
            }
            File file = new File(source);
            long localTime=file.lastModified();
            long remoteTime=attributes.getMTime()*1000L;
            int span = (int)((localTime-remoteTime)/1000);
            if (span < seconds)
            {
                return false;
            }
        }
        catch (Throwable t)
        {
        }
        channel.put(source, destination);
        return true;
    }
    */
    
    public boolean copy(String source, String destination) throws JSchException, SftpException
    {
        try
        {
            SftpATTRS attributes = channel.lstat(destination);
            if (attributes.isDir() == true)
            {
                throw new Exception("Not a file: " + destination);
            }
        }
        catch (Throwable t)
        {
        }
        channel.put(source, destination);
        return true;
    }

    public CopyDirectoryResult copyDirectoryRecursively(String sourceDirectory, String destinationDirectory) throws JSchException, SftpException, IOException
    {
        File directory = new File(sourceDirectory);
        if (directory.isDirectory() == false)
        {
            return CopyDirectoryResult.SOURCE_NOT_DIRECTORY;
        }
        if (checkDirectory(directory, destinationDirectory) == false)
        {
            return CopyDirectoryResult.DESTINATION_CHECK_FAILED;
        }
        copyDirectoryRecursively(directory, destinationDirectory);
        return CopyDirectoryResult.SUCCESS;
    }

    private void copyDirectoryRecursively(File sourceDirectory, String destinationDirectory) throws JSchException, SftpException, IOException
    {
        try
        {
            channel.lstat(destinationDirectory);
        }
        catch (Throwable t)
        {
            channel.mkdir(destinationDirectory);
        }
        File[] files = sourceDirectory.listFiles();
        for (File file : files)
        {
            if (file.isDirectory() == false)
            {
                String name = file.getName();
                channel.put(file.getCanonicalPath(), destinationDirectory + "/" + name);
            }
        }
        for (File file : files)
        {
            if (file.isDirectory())
            {
                String name = file.getName();
                copyDirectoryRecursively(file, destinationDirectory + "/" + name);
            }
        }
    }

    private boolean checkDirectory(File directory, String destination) throws JSchException, SftpException, IOException
    {
        try
        {
            SftpATTRS attributes = channel.lstat(destination);
            if (attributes.isDir() == false)
            {
                return false;
            }
        }
        catch (Throwable t)
        {
        }
        File[] files = directory.listFiles();
        for (File file : files)
        {
            if (file.isDirectory())
            {
                String name = file.getName();
                try
                {
                    String destinationDirectory = destination + "/" + name;
                    SftpATTRS attributes = channel.lstat(destinationDirectory);
                    if (attributes.isDir() == false)
                    {
                        return false;
                    }
                    if (checkDirectory(file, destinationDirectory) == false)
                    {
                        return false;
                    }
                }
                catch (Throwable t)
                {
                }
            }
        }
        return true;
    }

    public Object[] ls(String path) throws JSchException, SftpException
    {
        Vector vector = channel.ls(path);
        Object[] array = vector.toArray(new Object[vector.size()]);
        return array;
    }

    public void close() throws JSchException, SftpException
    {
        if (channel != null)
        {
            channel.disconnect();
            channel = null;
        }
        if (session != null)
        {
            session.disconnect();
            session = null;
        }
    }

    public String pwd() throws JSchException, SftpException
    {
        return channel.pwd();
    }

    public String readlink(String path) throws JSchException, SftpException
    {
        return channel.readlink(path);
    }

    public String realpath(String path) throws JSchException, SftpException
    {
        return channel.realpath(path);
    }

    public void get(String source, String destination) throws JSchException, SftpException
    {
        channel.get(source, destination);
    }

    public void put(String source, String destination) throws JSchException, SftpException
    {
        channel.put(source, destination);
    }

    public void rm(String path) throws JSchException, SftpException
    {
        channel.rm(path);
    }

    public void rmdir(String path) throws JSchException, SftpException
    {
        channel.rmdir(path);
    }

    public void rename(String oldPath, String newPath) throws JSchException, SftpException
    {
        channel.rename(oldPath, newPath);
    }

    public void cd(String path) throws JSchException, SftpException
    {
        channel.cd(path);
    }

    public void lcd(String path) throws JSchException, SftpException
    {
        channel.lcd(path);
    }

    public void chgrp(int gid, String path) throws JSchException, SftpException
    {
        channel.chgrp(gid, path);
    }

    public void chmod(int permissions, String path) throws JSchException, SftpException
    {
        channel.chmod(permissions, path);
    }

    public void chown(int uid, String path) throws JSchException, SftpException
    {
        channel.chown(uid, path);
    }

    public void symlink(String oldPath, String newPath) throws JSchException, SftpException
    {
        channel.symlink(oldPath, newPath);
    }

    public void hardlink(String oldPath, String newPath) throws JSchException, SftpException
    {
        channel.hardlink(oldPath, newPath);
    }

    public void mkdir(String path) throws JSchException, SftpException
    {
        channel.mkdir(path);
    }

    public String lpwd() throws JSchException, SftpException
    {
        return channel.lpwd();
    }
    
    
}
