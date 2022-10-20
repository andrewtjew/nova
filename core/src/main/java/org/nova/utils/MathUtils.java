/*******************************************************************************
 * Copyright (C) 2016-2019 Kat Fung Tjew
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
package org.nova.utils;

public class MathUtils
{
    public static int mod(int value,int mod)
    {
        int result=value%mod;
        return result<0?result+mod:result;
    }
    public static long mod(long value,long mod)
    {
        long result=value%mod;
        return result<0?result+mod:result;
    }
    
    
    
    public static double average(double[] values)
    {
        double total=0;
        for (double value:values)
        {
            total+=value;
        }
        return total/values.length;
    }
    public static double sumDeviationSquared(double[] values,double average)
    {
        double sumDeviationSquared=0;
        for (double value:values)
        {
            double diff=value-average;
            sumDeviationSquared+=diff*diff;
        }
        return sumDeviationSquared;
    }
    public static double sampleStandardDeviation(double[] values,double average)
    {
        double sumDeviationSquared=sumDeviationSquared(values,average);
        return Math.sqrt(sumDeviationSquared/(values.length-1));
    }
    public static double sampleStandardDeviation(double[] values)
    {
        double average=average(values);
        double sumDeviationSquared=sumDeviationSquared(values,average);
        return Math.sqrt(sumDeviationSquared/(values.length-1));
    }
    public static double populationStandardDeviation(double[] values,double average)
    {
        double sumDeviationSquared=sumDeviationSquared(values,average);
        return Math.sqrt(sumDeviationSquared/(values.length));
    }
    public static double populationStandardDeviation(double[] values)
    {
        double average=average(values);
        double sumDeviationSquared=sumDeviationSquared(values,average);
        return Math.sqrt(sumDeviationSquared/(values.length));
    }
    
}



