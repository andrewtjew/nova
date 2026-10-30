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

import org.nova.metrics.TopDoubleValues;
import org.nova.metrics.TopLongValues;
import org.nova.metrics.TraceSample;

public class SlowTraceSampleDetector
{
    final private TopLongValues topCounts;
    final private TopDoubleValues topAverageDurations;
    final private TopLongValues topTotalDurations;
    final private TopDoubleValues topMaxDurationOutliers;
    final private TopDoubleValues topAverageWaits;
    final private TopLongValues topTotalWaits;
    final private TopDoubleValues topWaitOutliers;
    
    public SlowTraceSampleDetector(int totalSamples)
    {
        int topCount=5;
        if (totalSamples<25)
        {
            topCount=totalSamples/5;
        }
        
        this.topCounts=new TopLongValues(topCount);
        this.topAverageDurations=new TopDoubleValues(topCount);
        this.topTotalDurations=new TopLongValues(topCount);
        this.topMaxDurationOutliers=new TopDoubleValues(topCount);

        this.topAverageWaits=new TopDoubleValues(topCount);
        this.topTotalWaits=new TopLongValues(topCount);
        this.topWaitOutliers=new TopDoubleValues(topCount);
    }
    
    public void update(TraceSample sample)
    {
        this.topCounts.update(sample.getCount());
        this.topAverageDurations.update(sample.getAverageDurationNs());
        this.topTotalDurations.update(sample.getTotalDurationNs());
        double out=0;
        if (sample.getStandardDeviationDurationNs()>0)
        {
            out=(sample.getMaxDurationTrace().getDurationNs()-sample.getAverageDurationNs())/sample.getStandardDeviationDurationNs();
        }
        this.topMaxDurationOutliers.update(out);
        
        
        this.topAverageWaits.update(sample.getAverageWaitNs());
        this.topTotalWaits.update(sample.getTotalWaitNs());
        out=0;
        if (sample.getStandardDeviationWaitNs()>0)
        {
            out=(sample.getMaxWaitTrace().getDurationNs()-sample.getAverageWaitNs())/sample.getStandardDeviationWaitNs();
        }
        this.topWaitOutliers.update(out);
    }
    public boolean isCountInsideTop(TraceSample sample)
    {
        return this.topCounts.isInsideTop(sample.getCount());
    }
    public boolean isAverageDurationInsideTop(TraceSample sample)
    {
        return this.topAverageDurations.isInsideTop(sample.getAverageDurationNs());
    }
    public boolean isTotalDurationInsideTop(TraceSample sample)
    {
        return this.topTotalDurations.isInsideTop(sample.getTotalDurationNs());
    }
    public boolean isMaxDurationAnOutlier(TraceSample sample)
    {
        double out=0;
        if (sample.getStandardDeviationDurationNs()>0)
        {
            out=(sample.getMaxDurationTrace().getDurationNs()-sample.getAverageDurationNs())/sample.getStandardDeviationDurationNs();
        }
        return this.topMaxDurationOutliers.isInsideTop(out);
    }
    public boolean isAverageWaitInsideTop(TraceSample sample)
    {
        return this.topAverageWaits.isInsideTop(sample.getAverageWaitNs());
    }
    public boolean isTotalWaitInsideTop(TraceSample sample)
    {
        return this.topTotalWaits.isInsideTop(sample.getTotalWaitNs());
    }
    public boolean isMaxWaitAnOutlier(TraceSample sample)
    {
        double out=0;
        if (sample.getStandardDeviationWaitNs()>0)
        {
            out=(sample.getMaxWaitTrace().getDurationNs()-sample.getAverageWaitNs())/sample.getStandardDeviationWaitNs();
        }
        return this.topWaitOutliers.isInsideTop(out);
    }
    public double getTotalDurationRatio(TraceSample sample)
    {
        var total=this.topTotalDurations.getTotal();
        if (total==0)
        {
            return 0;
        }
        return (double)sample.getTotalDurationNs()/total;
    }
    public double getTotalWaitRatio(TraceSample sample)
    {
        return (double)sample.getTotalWaitNs()/this.topTotalWaits.getTotal();
    }
}
