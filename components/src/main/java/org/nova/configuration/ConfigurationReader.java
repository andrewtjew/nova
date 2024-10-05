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
package org.nova.configuration;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.nova.parsing.scan.Lexeme;
import org.nova.parsing.scan.LineAndColumn;
import org.nova.parsing.scan.Scanner;
import org.nova.parsing.scan.TextSource;
import org.nova.utils.FileUtils;

public class ConfigurationReader
{
	final Configuration configuration;
	final FileReader reader;
	final String fileName;

	private ConfigurationReader(String fileName, Configuration configuration) throws FileNotFoundException
	{
		this.fileName = fileName;
		this.reader = new FileReader(FileUtils.toNativePath(fileName));
		this.configuration = configuration;
	}

	private void checkError(Lexeme lexeme) throws Exception
	{
	    if (lexeme.isError())
	    {
            LineAndColumn lc=new LineAndColumn(lexeme.getSnippet().getBuffer(), lexeme.getSnippet().getTargetAbsolutePosition());
            throw new Exception("Error at " + this.fileName + "(" + (lc.getLine()+1)+":"+(lc.getColumn()+1)+")");
	    }
	}
	private void read() throws Throwable
	{
	    String text=FileUtils.readTextFile(this.fileName);
	    TextSource source=new TextSource(text);
	    Scanner scanner=new Scanner(source);
//	    scanner.skipWhiteSpace();
	    String description=null;
		for (;;)
		{
		    char character=scanner.skipWhiteSpaceAndBegin();
			if (character==0)
			{
				return;
			}
			else if (character=='#') //comment 
			{
			    Lexeme lexeme=scanner.produceTerminatedText('\r','\n');
			    checkError(lexeme);
			}
			else if (character=='!') //description 
            {
                Lexeme lexeme=scanner.produceTerminatedText('\r','\n');
                checkError(lexeme);
                description=lexeme.getValue().substring(1);
            }
			else if (character==';') //optional terminator
            {
                continue;
            }
			else if (character=='/') //comment
            {
                Lexeme lexeme=scanner.produceSlashSlashComment();
                checkError(lexeme);
            }
            else if (character=='@') //include
            {
                Lexeme lexeme=scanner.produceTerminatedText('\r','\n');
                String file=lexeme.getValue().substring(1).trim();
                int index=this.fileName.lastIndexOf('/');
                if (index<0)
                {
                    index=this.fileName.lastIndexOf('\\');
                }
                if (index>=0)
                {
                    file=this.fileName.substring(0, index+1)+file;
                } 
                try
                {
                    read(file,this.configuration);
                }
                catch (Throwable t)
                {
                    int line=new LineAndColumn(text,lexeme.getSnippet().getTargetAbsolutePosition()).getLine()+1;
                    throw new Exception("File Error: "+lexeme.getValue()+"("+this.fileName+":"+line+")",t);
                }
            }
            else if (character=='%') //optional include
            {
                Lexeme lexeme=scanner.produceTerminatedText('\r','\n');
                String file=lexeme.getValue().substring(1).trim();
                int index=this.fileName.lastIndexOf('/');
                if (index<0)
                {
                    index=this.fileName.lastIndexOf('\\');
                }
                if (index>=0)
                {
                    file=this.fileName.substring(0, index+1)+file;
                }
                try
                {
                    read(file,this.configuration);
                }
                catch (Throwable t)
                {
                    //optional include
                }
            }
			else
			{
    			Lexeme lexeme=scanner.produceTerminatedText('=');
                checkError(lexeme);
    			String name=lexeme.getValue().trim();
    
    			lexeme=scanner.expectPunctuator('=');
                checkError(lexeme);
    
                character=scanner.skipWhiteSpaceAndBegin();
                if ((character == '{')||(character=='['))
                {
                    scanner.revert();
                    lexeme=scanner.produceJSONLikeText();
                }
//                else if (character == '[')
//                {
//                    scanner.revert();
//                    lexeme=scanner.produceEnclosedJSONText();
//                }
                else if (character==0)
                {
                    lexeme=scanner.produceEnd();
                }
                else
                {
                    lexeme=scanner.produceTerminatedTextAndSkipTerminator('\r','\n',';');
                }
                int line=new LineAndColumn(text,lexeme.getSnippet().getTargetAbsolutePosition()).getLine()+1;
                if (lexeme.isError()==false)
                {
                    this.configuration.add(new ConfigurationItem(name, lexeme.getValue().trim(), ConfigurationSource.FILE,fileName+"("+line+")", description));
//                    System.out.println("Configuration: "+name+"="+lexeme.getValue().trim());
                }
                else
                {
                    throw new Exception(lexeme.getValue()+"("+this.fileName+":"+line+")");
                }
    			description=null;
			}
		}

	}

	public static void read(String fileName,Configuration configuration) throws Throwable
	{
		ConfigurationReader reader=new ConfigurationReader(fileName, configuration);
		reader.read();
	}
	
    public static Configuration read(String fileName) throws Throwable 
	{
        Configuration configuration=new Configuration();
//        try
        {
    		ConfigurationReader reader=new ConfigurationReader(fileName, configuration);
    		reader.read();
        }
//        catch (Throwable t)
//        {
//            System.out.println("No config file: "+fileName);
//        }
		return configuration;
	}

    public static Configuration read(String args[],String configurationFileKey,String defaultConfigurationFileName) throws Throwable
    {
        Configuration configuration=new Configuration();
        configuration.addArgs(args);
        String fileName=configuration.getValue(configurationFileKey,null);
        if (fileName==null)
        {
            fileName=defaultConfigurationFileName;
        }
        read(fileName,configuration);
        configuration.addArgs(args);
        return configuration;
    }

}
