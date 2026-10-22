namespace nova.operator
{
    export function drawTraceRateGraph(id:string,counters:number[],maxCounter:number,averageColor:string,maximumColor:string)
    {
        const canvas=document.getElementById(id) as HTMLCanvasElement;
        const context=canvas.getContext("2d");

        const countersPerPixel=counters.length/canvas.width;
        console.log("rates="+counters.length+",countersPerPixel="+countersPerPixel+",canvas.width="+canvas.width+",canvas.height="+canvas.height);
        for(let i=0;i<canvas.width;i++)
        {
            const endIndex=(i+1)*countersPerPixel;
            var maxCounterInPixel=0;
            var totalCountsInPixel=0;
            var indices=0;
            for (let beginIndex=i*countersPerPixel; beginIndex<endIndex; beginIndex++)
            {
                const index=Math.floor(beginIndex);
                const counter=counters[index];
                maxCounterInPixel=Math.max(maxCounterInPixel,counter);
                totalCountsInPixel+=counter;
                indices++;
            }
            const averageCounts=totalCountsInPixel/indices;

            const averageHeight=averageCounts/maxCounter*canvas.height;
            const maxHeight=maxCounterInPixel/maxCounter*canvas.height;
            context.fillStyle = maximumColor;
            context.fillRect(i, canvas.height-maxHeight, 1, maxHeight);
            context.fillStyle = averageColor;
            context.fillRect(i, canvas.height-averageHeight, 1, averageHeight);
        }
    }
}
