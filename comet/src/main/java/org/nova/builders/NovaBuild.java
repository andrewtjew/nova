package org.nova.builders;

import java.io.File;

import org.nova.comet.CometUtils;
import org.nova.comet.SshSession;

public class NovaBuild extends Script
{
    @Override
    public void run(String[] args) throws Throwable
    {
        String artifact = null;
        String sourcedir=null;
        String version = "0.0.1-SNAPSHOT";
        String mem="256M";
        boolean noLibs=false;
        boolean noBuilds=false;
        String aws=null;
        boolean restart=false;
        String exedir=null;
        String service=null;
        boolean gitPush=true;
        
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
                    case "service":
                    service = parts[1];
                    break;

                    case "sourcedir":
                    sourcedir = parts[1];
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
    
                    case "nolibs":
                    noLibs = Boolean.parseBoolean(parts[1]);
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
                        
                    case "gitpush":
                        gitPush = Boolean.parseBoolean(parts[1]);
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
            throw new Exception("artifact cannot be null");
        }
        if (sourcedir== null)
        {
            throw new Exception("sourceDir cannot be null");
        }
        if (restart)
        {
            if (aws==null)
            {
                throw new Exception("aws cannot be null when restart=true");
            }
        }
        sourcedir=CometUtils.toNativePath(sourcedir);
        if (sourcedir.endsWith(File.separator)==false)
        {
            sourcedir=sourcedir+File.separator;
        }
        //--- done parameter parsing ---
        
        String mvnJar = artifact + "-" + version + ".jar";
        String artifactJar=artifact+".jar";
        String package_=sourcedir+artifact+"\\"+artifact;
        String jarDest = sourcedir+artifact+"\\target\\"+mvnJar;

        if (restart==false)
        {
            if (noBuilds==false)
            {
                
                if (noLibs==false)
                {
                    CometUtils.exec("\\wd\\nova\\core","mvn clean install");
                    CometUtils.exec("\\wd\\nova\\components","mvn clean install");
//                    CometUtils.exec("\\wd\\nova\\services","mvn clean install");
//                    CometUtils.exec("\\wd\\nova\\xp","mvn clean install");
//                    CometUtils.exec("\\wd\\nova\\bootstrap\\bootstrap5.2.0","mvn clean install");
                }
                CometUtils.deleteFile(jarDest);
                CometUtils.exec(sourcedir+artifact,"mvn clean install");
                if (CometUtils.existsFile(jarDest)==false)
                {
                    throw new Exception("jar not found: "+jarDest);
                }
                System.out.println("building done");
        
                //Create package
                CometUtils.deleteDirectory(package_);
                CometUtils.createDirectory(package_);
                CometUtils.copyFile(jarDest,package_+"\\"+artifactJar);
                CometUtils.cloneDirectory(sourcedir+artifact+"\\resources",package_+"\\resources");
                if (CometUtils.existsFile(sourcedir+artifact+"\\client"))
                {
                    CometUtils.cloneDirectory(sourcedir+artifact+"\\client",package_+"\\client");
                }
                CometUtils.deleteFile(package_+"\\resources\\local.cnf");
                CometUtils.copyDirectory(sourcedir+artifact+"\\etc",package_+"\\etc");
                
                String buildVersion=CometUtils.exec(sourcedir,"git describe --tags --abbrev=0");
                
                buildVersion=CometUtils.incrementVersion(buildVersion,0,1);
                System.out.println("build version: "+buildVersion);
                
                String message="package: "+artifact+"\r\n"+"maven version: "+version+"\r\n"+"build version: "+buildVersion+"\r\n"+"built on: "+CometUtils.now_UTC_ISO()+"\r\n"+"built at: "+CometUtils.getLocalHostName()+"\r\n";
                CometUtils.writeTextFile(message,package_+"\\build-info.txt");           
                String start="java -XX:+UseCompressedOops -XX:+UseG1GC -XX:MaxGCPauseMillis=1000 -Xms"+mem+" -Xmx"+mem+" -jar "+artifactJar+" config=.\\resources\\application.cnf";
                CometUtils.writeTextFile(start,package_+"\\run.bat");         
        
                CometUtils.exec(sourcedir+artifact,"jar -cf "+artifactJar+" "+artifact);
                CometUtils.createDirectory(sourcedir+"\\packages");
                CometUtils.moveFile(sourcedir+artifact+"\\"+artifactJar,sourcedir+"\\packages\\"+artifactJar);
                CometUtils.deleteDirectory(package_);
        
                CometUtils.exec(sourcedir,"git add .");
                CometUtils.exec(sourcedir,"git tag -a "+buildVersion+" -m \"package build\"");
                CometUtils.exec(sourcedir,"git commit -m \""+artifact+" package build version: "+buildVersion+"\"");
                if (gitPush)
                {
                     CometUtils.exec(sourcedir,"git push");
                }
            }
        }
        if (exedir!=null)
        {
            CometUtils.exec(exedir,"jar -xf "+sourcedir+"\\packages\\"+artifactJar);
        }
            
        String javaCommand="sudo java -XX:+UseG1GC -Xms"+mem+" -Xmx"+mem+" -jar "+artifact+".jar config=./resources/application.cnf";
        if (aws!=null)
        {
            System.out.println("connecting...");
            SshSession session=new SshSession(aws,22,"ec2-user","c:\\users\\andrew\\Mira\\Singapore.pem",null);
            System.out.println("copying package"); 
            
            if (restart==false)
            {
                session.copy(sourcedir+"\\packages\\"+artifactJar,artifactJar);
                System.out.println("Unzipping...");    
    
                String execOutput=session.exec(true,".","unzip -o "+artifactJar+" -d .");
                System.out.println(execOutput);
            }
    
            System.out.println("Killing existing process: "+javaCommand);
            int killed=session.killMatching(javaCommand,1000);
            System.out.println("killed="+killed);
    
            System.out.println("Executing: "+javaCommand+" in "+artifact);
            session.execBackground(artifact,javaCommand);
            
            session.close();
        }
        if (service!=null)
        {
            var serviceDir=new File(sourcedir+"\\..\\services\\"+artifact).getCanonicalPath();

            System.out.println("removing service: "+service);    
            CometUtils.exec("\\wd\\nova\\os","nssm stop \""+service+"\"");
            CometUtils.exec("\\wd\\nova\\os","nssm remove \""+service+"\" confirm");
            
            if (restart==false)
            {
                System.out.println("copying package");
                CometUtils.createDirectory(serviceDir);
                CometUtils.copyFile(sourcedir+"\\packages\\"+artifactJar,serviceDir+"\\"+artifactJar);
                System.out.println("Unzipping...");    
                CometUtils.exec(serviceDir,"jar -xf "+artifactJar);
                
            }
            System.out.println("installing service: "+service);    
            CometUtils.exec("\\wd\\nova\\os","nssm install \""+service+"\" \""+serviceDir+"\\"+artifact+"\\run.bat\"");
            CometUtils.exec("\\wd\\nova\\os","nssm set \""+service+"\" Start SERVICE_DELAYED_AUTO_START");
            CometUtils.exec("\\wd\\nova\\os","nssm start \""+service);

            
//            CometUtils.exec("\\wd\\nova\\os","nssm install \""+service+"\" \""+sourcedir+"\\packages\\"+artifactJar+"\" java -XX:+UseG1GC -Xms"+mem+" -Xmx"+mem+" -jar "+artifact+".jar config=./resources/application.cnf");
        }
    }

}
