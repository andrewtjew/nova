package org.nova.builders;

import java.io.File;

import org.nova.comet.CometUtils;
import org.nova.comet.SshSession;

import com.google.common.io.Files;

public class MiraBuild extends Script
{
    @Override
    public void run(String[] args) throws Throwable
    {
        String artifact = null;
        String sourcedir=null;
        String novadir=null;
        String version = "0.0.1-SNAPSHOT";
        String mem="256M";
        boolean noBuilds=false;
        String aws=null;
        boolean restart=false;
        String exedir=null;
        String packagedir=null;
        
        for (String arg : args)
        {
            String[] parts = arg.split("=");
            if (parts.length != 2)
            {
                System.err.println("invalid arg:" + arg);
                return;
            }
            try
            {
                switch (parts[0].toLowerCase())
                {
                    case "sourcedir":
                    sourcedir = parts[1];
                    break;

                    case "packagedir":
                        packagedir = parts[1];
                    break;

                    case "novadir":
                        novadir = parts[1];
                    break;

                    case "exedir":
                    exedir = parts[1];
                    break;

                    case "version":
                        version = parts[1];
                        break;
    
                    case "artifact":
                        artifact = parts[1];
                        break;
    
                    case "mem":
                        mem = parts[1];
                        break;
    
                    case "restart":
                    restart = Boolean.parseBoolean(parts[1]);
                    break;

                    case "nobuilds":
                        noBuilds = Boolean.parseBoolean(parts[1]);
                        break;
    
                    case "aws":
                        aws = parts[1];
                        break;
    
                    default:
                        System.err.println("invalid arg:" + arg);
                }
            }
            catch (Throwable t)
            {
                
            }
        }
        if (noBuilds==false)
        {
            if (artifact == null)
            {
                throw new Exception("artifact not specified");
            }
            if (sourcedir== null)
            {
                throw new Exception("sourceDir not specified");
            }
            sourcedir=CometUtils.toNativePath(sourcedir);
        }
        if (packagedir== null)
        {
            throw new Exception("packagedir not specified");
        }
        if (restart)
        {
            if (aws==null)
            {
                throw new Exception("aws cannot be null when restart=true");
            }
        }
        //--- done parameter parsing ---
        
        String mvnJar = artifact + "-" + version + ".jar";
        String artifactJar=artifact+".jar";
        String jarDest = packagedir+artifact+"\\target\\"+mvnJar;

        if (restart==false)
        {
            String packageWorkDir=sourcedir+"\\"+artifact+"\\"+artifact;
            String jarSource = sourcedir+"\\"+artifact+"\\target\\"+mvnJar;
            
            if (noBuilds==false)
            {
                if (novadir!=null)
                {
                    CometUtils.exec(novadir+"\\core","mvn clean install");
                    CometUtils.exec(novadir+"\\components","mvn clean install");
                    CometUtils.exec(novadir+"\\services","mvn clean install");
                    CometUtils.exec(novadir+"\\xp","mvn clean install");
                    CometUtils.exec(novadir+"\\bootstrap\\bootstrap5.2.0","mvn clean install");
                    System.out.println("nova building done");
                }
                CometUtils.deleteFile(jarSource);
                CometUtils.exec(sourcedir+"\\"+artifact,"mvn clean install");
                if (CometUtils.existsFile(jarSource)==false)
                {
                    throw new Exception("jar not found: "+jarSource);
                }
                System.out.println("building done");
        
                //Create package
                CometUtils.deleteDirectory(packageWorkDir);
                CometUtils.createDirectory(packageWorkDir);
                CometUtils.copyFile(jarSource,packageWorkDir+"\\"+artifactJar);
                CometUtils.cloneDirectory(sourcedir+"\\"+artifact+"\\resources",packageWorkDir+"\\resources");
                if (CometUtils.existsFile(sourcedir+"\\"+artifact+"\\client"))
                {
                    CometUtils.cloneDirectory(sourcedir+artifact+"\\client",packageWorkDir+"\\client");
                }
                CometUtils.deleteFile(packageWorkDir+"\\resources\\local.cnf");
                CometUtils.copyDirectory(sourcedir+"\\"+artifact+"\\etc",packageWorkDir+"\\etc");
                
                String buildVersion=CometUtils.exec(sourcedir,"git describe --tags --abbrev=0");
                
                buildVersion=CometUtils.incrementVersion(buildVersion,0,1);
                System.out.println("build version: "+buildVersion);
                
                String message="package: "+artifact+"\r\n"+"maven version: "+version+"\r\n"+"build version: "+buildVersion+"\r\n"+"built on: "+CometUtils.now_UTC_ISO()+"\r\n"+"built at: "+CometUtils.getLocalHostName()+"\r\n";
                CometUtils.writeTextFile(message,packageWorkDir+"\\build-info.txt");           
                String start="java -XX:+UseCompressedOops -XX:+UseG1GC -XX:MaxGCPauseMillis=1000 -Xms"+mem+" -Xmx"+mem+" -jar "+artifactJar+" config=.\\resources\\application.cnf";
                CometUtils.writeTextFile(start,packageWorkDir+"\\run.bat");         
        
                CometUtils.exec(sourcedir+"\\"+artifact,"jar -cf "+artifactJar+" "+artifact);
                CometUtils.createDirectory(sourcedir+"\\packages");
                CometUtils.moveFile(sourcedir+"\\"+artifact+"\\"+artifactJar,packagedir+"\\"+artifactJar);
                CometUtils.deleteDirectory(packageWorkDir);
        
                 CometUtils.exec(sourcedir,"git add .");
                 CometUtils.exec(sourcedir,"git tag -a "+buildVersion+" -m \"package build\"");
                 CometUtils.exec(sourcedir,"git commit -m \""+artifact+" package build version: "+buildVersion+"\"");
    //            CometUtils.exec(sourceDir,"git push");
            }
        }
            
        if (aws!=null)
        {
            String javaCommand="sudo java -XX:+UseG1GC -Xms"+mem+" -Xmx"+mem+" -jar "+artifact+".jar config=.\\resources\\application.cnf";
            System.out.println("connecting "+aws);
            SshSession session=new SshSession(aws,22,"ec2-user","c:\\users\\andrew\\Mira\\Singapore.pem",null);
            System.out.println("copying package"); 
            
            if (restart==false)
            {
                session.copy(packagedir+"\\"+artifactJar,artifactJar);
                System.out.println("Unzipping...");    
    
                String execOutput=session.exec(true,".","unzip -o "+artifactJar+" -d .");
                System.out.println(execOutput);
            }
    
            System.out.println("Killing existing process: "+javaCommand);
            int killed=session.killMatching(javaCommand,1000);
            System.out.println("killed="+killed);
    
            System.out.println("Executing: "+javaCommand+" in "+artifact);
            session.execBackground(artifact,javaCommand);
        }
    }

}
