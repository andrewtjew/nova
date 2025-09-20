package org.nova.builders;

import org.nova.comet.CometUtils;
import org.nova.comet.SshSession;

public class AzureBuild extends Script
{
    @Override
    public void run(String[] args) throws Throwable
    {
        String artifact = null;
        String libDir = "c:\\evolve\\repo\\services2\\";
        String sourceDir = "c:\\repos\\azure\\services2\\";
        String version = "0.0.1-SNAPSHOT";
        String mem="256M";
        boolean noLibs=false;
        boolean noBuild=false;
        String bootstrapVersion="5.2.0";
        String aws=null;

        for (String arg : args)
        {
            String[] parts = arg.split("=");
            if (parts.length != 2)
            {
                System.err.println("invalid arg:" + arg);
                return;
            }
            switch (parts[0].toLowerCase())
            {
                case "sourcedir":
                    libDir = parts[1];
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

                case "bootstrapversion":
                    bootstrapVersion = parts[1];
                    break;

                case "aws":
                    aws = parts[1];
                    break;

                default:
                    System.err.println("invalid arg:" + arg);
            }
        }
        if (artifact == null)
        {
            System.err.println("no artificat");
            return;
        }
        //---- End of args processing -----------------------------------------------------------
        
        String mvnJar = artifact + "-" + version + ".jar";
        String destJar=artifact+".jar";
        String package_=sourceDir+artifact+"\\"+artifact;
        String destDir = sourceDir+artifact+"\\target\\"+mvnJar;
        if (noBuild==false)
        {
            if (noLibs==false)
            {
                CometUtils.exec("c:\\dependencies\\nova\\core","mvn clean install");
                CometUtils.exec("c:\\dependencies\\nova\\components","mvn clean install");
                CometUtils.exec("c:\\dependencies\\nova\\services","mvn clean install");
                CometUtils.exec("c:\\dependencies\\nova\\bootstrap\\bootstrap"+bootstrapVersion,"mvn clean install");
                CometUtils.exec(libDir+"messaging","mvn clean install");
                CometUtils.exec(libDir+"common","mvn clean install");
                CometUtils.exec(libDir+"common-ui-bootstrap"+bootstrapVersion,"mvn clean install");
            }
            CometUtils.deleteFile(destDir);
            CometUtils.exec(sourceDir+artifact,"mvn clean install");
            if (CometUtils.existsFile(destDir)==false)
            {
                System.err.println("jar not found: "+destDir);
                return;
            }
    
            //Create package
            CometUtils.deleteDirectory(package_);
            CometUtils.createDirectory(package_);
            CometUtils.copyFile(destDir,package_+"\\"+destJar);
            CometUtils.copyDirectory(sourceDir+artifact+"\\client",package_+"\\client");

            CometUtils.cloneDirectory(sourceDir+"\\resources",package_+"\\resources");
            CometUtils.copyDirectory(sourceDir+artifact+"\\resources",package_+"\\resources");
//            CometUtils.deleteDirectory(package_+"\\resources\\client\\games");
            CometUtils.copyDirectory(sourceDir+artifact+"\\etc",package_+"\\etc");
            CometUtils.copyDirectory(sourceDir+"\\testcerts",package_+"\\testcerts");
            String buildVersion=CometUtils.exec(sourceDir,"git describe --tags --abbrev=0");
            buildVersion=CometUtils.incrementVersion(buildVersion,0,1);
            System.out.println("build version: "+buildVersion);
            
            String message="package: "+artifact+"\r\n"+"maven version: "+version+"\r\n"+"build version: "+buildVersion+"\r\n"+"built on: "+CometUtils.now_UTC_ISO()+"\r\n"+"built at: "+CometUtils.getLocalHostName()+"\r\n";
            CometUtils.writeTextFile(message,package_+"\\build-info.txt");           
            String start="java -XX:+UseCompressedOops -XX:+UseG1GC -XX:MaxGCPauseMillis=1000 -Xms"+mem+" -Xmx"+mem+" -jar "+destJar+" config=.\\resources\\svr.cnf";
            CometUtils.writeTextFile(start,package_+"\\run-test-all-local.bat");          
            CometUtils.writeTextFile(start,package_+"\\run-svr.bat");         
    
            CometUtils.exec(sourceDir+artifact,"jar -cf "+destJar+" "+artifact);
            CometUtils.createDirectory(sourceDir+"\\packages");
            CometUtils.moveFile(sourceDir+artifact+"\\"+destJar,sourceDir+"\\packages\\"+destJar);
            CometUtils.deleteDirectory(package_);
    
            CometUtils.exec(sourceDir,"git add .");
            CometUtils.exec(sourceDir,"git tag -a "+buildVersion+" -m \"package build\"");
            CometUtils.exec(sourceDir,"git commit -m \""+artifact+" package build version: "+buildVersion+"\"");
            CometUtils.exec(sourceDir,"git push");
        }
            
        if (aws!=null)
        {
            String javaCommand="sudo java -XX:+UseG1GC -Xms"+mem+" -Xmx"+mem+" -jar "+artifact+".jar config=./resources/"+" config=.\\resources\\application.cnf";
            System.out.println("connecting...");
            SshSession session=new SshSession(aws,22,"ec2-user");
            System.out.println("copying package"); 
            
            session.copy(libDir+"\\packages\\"+destJar,destJar);
            System.out.println("Unzipping...");    

            String execOutput=session.exec(true,".","unzip -o "+destJar+" -d .");
            System.out.println(execOutput);
    
            System.out.println("Killing existing process: "+javaCommand);
            int killed=session.killMatching(javaCommand,1000);
            System.out.println("killed="+killed);
    
            System.out.println("Executing: "+javaCommand+" in "+destDir);
            session.execBackground(destDir,javaCommand);
        }
        
        
    }

}
