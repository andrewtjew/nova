namespace barvian.number_flow
{
    interface NumberFormatOptions
    {
        style:string;
        currency:string;
    }
    
    interface Properties
    {
        format: NumberFormatOptions;
        locales: String;
        numberPrefix: String;
        opacityTiming: Number;
        transformTiming: Number;
        spinTiming: Number;
        trend: Number;
        animated: Boolean;
        respectMotionPreference: Boolean;
    }

    export function set(id:string,value:string,properties:string=null):void
    {
        const flow = document.getElementById(id) as any;
        if (properties!==null)
        {
            const props=JSON.parse(properties) as Properties;
            if (props.format!==undefined)
            {
                flow.format={style: props.format.style, currency: props.format.currency};
            }
            if (props.locales!==undefined)
            {
                flow.locales=props.locales;
            }
            if (props.numberPrefix!==undefined)
            {
                flow.numberPrefix=props.numberPrefix;
            }
            if (props.opacityTiming!==undefined)
            {
                flow.opacityTiming=props.opacityTiming;
            }
            if (props.transformTiming!==undefined)
            {
                flow.transformTiming=props.transformTiming;
            }
            if (props.spinTiming!==undefined)
            {
                flow.spinTiming={duration: props.spinTiming};
            }
            if (props.trend!==undefined)
            {
                flow.trend=props.trend;
            }
            if (props.animated!==undefined)
            {
                flow.animated=props.animated;
            }
            if (props.respectMotionPreference!==undefined)
            {
                flow.respectMotionPreference=props.respectMotionPreference;
            }
        }
        flow.update(value);
        console.log("update: "+id+"="+value+",flow="+flow);
   }

}