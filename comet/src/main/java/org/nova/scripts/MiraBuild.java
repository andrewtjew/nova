package org.nova.scripts;

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
        String sourceDir=null;
        String version = "0.0.1-SNAPSHOT";
        String mem="256M";
        boolean noLibs=false;
        boolean noBuilds=false;
        String aws=null;

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
                        sourceDir = parts[1];
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
    
                    case "nolibs":
                        noLibs = Boolean.parseBoolean(parts[1]);
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
        if (artifact == null)
        {
            System.err.println("no artificat");
            return;
        }
        if (sourceDir== null)
        {
            System.err.println("no sourceDir");
            return;
        }
        sourceDir=CometUtils.toNativePath(sourceDir);
        if (sourceDir.endsWith(File.separator)==false)
        {
            sourceDir=sourceDir+File.separator;
        }
        String mvnJar = artifact + "-" + version + ".jar";
        String artifactJar=artifact+".jar";
        String package_=sourceDir+artifact+"\\"+artifact;
        String jarDest = sourceDir+artifact+"\\target\\"+mvnJar;
        if (noBuilds==false)
        {
            
            if (noLibs==false)
            {
                CometUtils.exec("c:\\wd\\nova\\core","mvn clean install");
                CometUtils.exec("c:\\wd\\nova\\components","mvn clean install");
                CometUtils.exec("c:\\wd\\nova\\services","mvn clean install");
                CometUtils.exec("c:\\wd\\nova\\xp","mvn clean install");
            }
            CometUtils.deleteFile(jarDest);
            CometUtils.exec(sourceDir+artifact,"mvn clean install");
            if (CometUtils.existsFile(jarDest)==false)
            {
                System.err.println("jar not found: "+jarDest);
                return;
            }
            System.out.println("building done");
    
            //Create package
            CometUtils.deleteDirectory(package_);
            CometUtils.createDirectory(package_);
            CometUtils.copyFile(jarDest,package_+"\\"+artifactJar);
            CometUtils.cloneDirectory(sourceDir+artifact+"\\resources",package_+"\\resources");
            if (CometUtils.existsFile(sourceDir+artifact+"\\client"))
            {
                CometUtils.cloneDirectory(sourceDir+artifact+"\\client",package_+"\\client");
            }
            CometUtils.deleteFile(package_+"\\resources\\local.cnf");
            CometUtils.copyDirectory(sourceDir+artifact+"\\etc",package_+"\\etc");
            String buildVersion=CometUtils.exec(sourceDir,"git describe --tags --abbrev=0");
            buildVersion=CometUtils.incrementVersion(buildVersion,0,1);
            System.out.println("build version: "+buildVersion);
            
            String message="package: "+artifact+"\r\n"+"maven version: "+version+"\r\n"+"build version: "+buildVersion+"\r\n"+"built on: "+CometUtils.now_UTC_ISO()+"\r\n"+"built at: "+CometUtils.getLocalHostName()+"\r\n";
            CometUtils.writeTextFile(message,package_+"\\build-info.txt");           
            String start="java -XX:+UseCompressedOops -XX:+UseG1GC -XX:MaxGCPauseMillis=1000 -Xms"+mem+" -Xmx"+mem+" -jar "+artifactJar+" config=.\\resources\\application.cnf";
            CometUtils.writeTextFile(start,package_+"\\run.bat");         
    
            CometUtils.exec(sourceDir+artifact,"jar -cf "+artifactJar+" "+artifact);
            CometUtils.createDirectory(sourceDir+"\\packages");
            CometUtils.moveFile(sourceDir+artifact+"\\"+artifactJar,sourceDir+"\\packages\\"+artifactJar);
//            CometUtils.deleteDirectory(package_);
    
//            CometUtils.exec(sourceDir,"git add .");
//            CometUtils.exec(sourceDir,"git tag -a "+buildVersion+" -m \"package build\"");
//            CometUtils.exec(sourceDir,"git commit -m \""+artifact+" package build version: "+buildVersion+"\"");
//            CometUtils.exec(sourceDir,"git push");
        }
            
        if (aws!=null)
        {
            String javaCommand="sudo java -XX:+UseG1GC -Xms"+mem+" -Xmx"+mem+" -jar "+artifact+".jar config=./resources/"+" config=.\\resources\\application.cnf";
            System.out.println("connecting...");
//            SshSession session=new SshSession(aws,22,"ec2-user");
//            SshSession session=new SshSession("18.211.84.163",22,"ec2-user");
            
            SshSession session=new SshSession(aws,22,"ec2-user","c:\\users\\andrew\\Mira\\Singapore.pem","");
            
            
            
            System.out.println("copying package"); 
            
            session.copy(sourceDir+"\\packages\\"+artifactJar,artifactJar);
            System.out.println("Unzipping...");    

            String execOutput=session.exec(true,".","unzip -o "+artifactJar+" -d .");
            System.out.println(execOutput);
    
            System.out.println("Killing existing process: "+javaCommand);
            int killed=session.killMatching(javaCommand,1000);
            System.out.println("killed="+killed);
    
            System.out.println("Executing: "+javaCommand+" in "+jarDest);
            session.execBackground(jarDest,javaCommand);
        }
        
        
    }

}
