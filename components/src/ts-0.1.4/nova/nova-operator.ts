namespace nova.operator
{
    interface TraceRate
    {
        rate:number;
        totalDuration:number;
        maximumDuration:number;
    }

    interface TraceRateResult
    {
        rates:TraceRate[];

        averageDuration:number;
        maximumDuration:number;
        maximumDurationEpochSecond:number;

        averageRate:number;
        maximumRate:number;
        maximumRateEpochSecond:number;
    }

    export function drawTraceRateGraph(id:string,result:TraceRateResult,averageCountColor:string,averageDurationColor,maximumColor:string,centerColor:string)
    {
        const canvas=document.getElementById(id) as HTMLCanvasElement;
        const context=canvas.getContext("2d");

        context.beginPath();
        context.strokeStyle = centerColor;
        context.lineWidth = 1;
        context.moveTo(0, canvas.height/2);
        context.lineTo(canvas.width, canvas.height/2);
        context.stroke();

        const countersPerPixel=result.rates.length/canvas.width;
        for(let i=0;i<canvas.width;i++)
        {
            const beginIndex=i*countersPerPixel;
            const endIndex=(i+1)*countersPerPixel;
            var maximumRateInPixel=0;
            var totalRateInPixel=0;

            var totalDurationInPixel=0;
            var maximumDurationInPixel=0;
            var samples=0;
            for (let j=beginIndex; j<endIndex; j++)
            {
                const index=Math.floor(j);
                const traceRate=result.rates[index];
                if (traceRate==null)
                {
                    continue;
                }
                const rate=traceRate.rate;
                totalRateInPixel+=rate;
                maximumRateInPixel=Math.max(maximumRateInPixel,rate);

                totalDurationInPixel+=traceRate.totalDuration;
                maximumDurationInPixel=Math.max(maximumDurationInPixel,traceRate.maximumDuration);
                samples++;
            }
            const averageRate=totalRateInPixel/samples;
            const averageRateHeight=averageRate/result.maximumRate*canvas.height/2;
            const maxRateHeight=maximumRateInPixel/result.maximumRate*canvas.height/2;

            const averageDuration=totalDurationInPixel/totalRateInPixel;
            const averageDurationHeight=averageDuration/result.maximumDuration*canvas.height/2;
            const maxDurationHeight=maximumDurationInPixel/result.maximumDuration*canvas.height/2;

            context.fillStyle = maximumColor;
            context.fillRect(i, canvas.height/2-maxRateHeight, 1, maxRateHeight+maxDurationHeight);


            context.fillStyle = averageCountColor;
            context.fillRect(i, canvas.height/2-averageRateHeight, 1, averageRateHeight);

            context.fillStyle = averageDurationColor;
            context.fillRect(i, canvas.height/2, 1, averageDurationHeight);
        }
    }



}
