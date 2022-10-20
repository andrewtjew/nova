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
package org.nova.frameworks;


//public class CoreEnvironmentApplicationRunner //
//{
//    public static interface CoreEnvironmentApplicationInstantiator
//    {
//        CoreEnvironmentApplication instantiate(CoreEnvironment coreEnvironment) throws Throwable;
//    }
//    
//    
//    private void showNotice(Logger logger,String message)
//    {
//        System.out.println(message);
//        logger.log(Level.NOTICE, message);
//    }
//    
//    private void showException(Logger logger,Throwable t)
//    {
//        t.printStackTrace(System.err);
//        logger.log(t);
//    }
//    
//    public void run(Configuration configuration,CoreEnvironmentApplicationInstantiator instantiator)
//    {
//        CoreEnvironment coreEnvironment=null;
//        try
//        {
//            System.out.println("Starting CoreEnvironment...");
//            coreEnvironment=new CoreEnvironment(configuration);
//        }
//        catch (Throwable t)
//        {
//            t.printStackTrace(System.err);
//            return;
//        }
//        
//        CoreEnvironmentApplication application=null;
//        try
//        {
//            showNotice(coreEnvironment.getLogger(),"New Application...");
//            application=instantiator.instantiate(coreEnvironment);
//        }
//        catch (Throwable t)
//        {
//            showException(coreEnvironment.getLogger(),t);
//            return;
//        }
//
//        try (Trace trace=new Trace(coreEnvironment.getTraceManager(),"run"))
//        {
//            try
//            {
////                application.stop();
//            }
//            catch (Throwable t)
//            {
//                showException(coreEnvironment.getLogger(),t);
//            }
//            finally
//            {
//                coreEnvironment.stop();
//            }
//        }
//    }
//    
//    public void run(String[] args,CoreEnvironmentApplicationInstantiator instantiator)
//    {
//        Configuration configuration=null;
//        try
//        {
//            configuration=ConfigurationReader.read(args,"config",FileUtils.toNativePath("./resources/application.cnf"));
//        }
//        catch (Throwable t)
//        {
//            System.err.println("Cannot locate configuration files.");
//            return;
//        }
//        run(configuration,instantiator);
//    }
//
//}
