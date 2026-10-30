package org.nova.json;

import java.io.OutputStream;

import org.nova.tracing.Trace;

public class MicroDomainPayload
{
    public static final String TEST_SAMPLE="{\"media\":{\"uri\":\"http://javaone.com/keynote.mpg\",\"title\":\"JavaOne Keynote\",\"width\":640,\"height\":480,\"format\":\"video/mpg\",\"duration\":18000000,\"size\":58982400,\"bitrate\":262144,\"persons\":[\"Bill Gates\",\"Steve Jobs\"],\"player\":\"JAVA\",\"copyright\":\"None\"},\"images\":[{\"uri\":\"http://javaone.com/keynote_large.jpg\",\"title\":\"JavaOne Keynote\",\"width\":1024,\"height\":768,\"size\":\"LARGE\"},{\"uri\":\"http://javaone.com/keynote_small.jpg\",\"title\":\"JavaOne Keynote\",\"width\":320,\"height\":240,\"size\":\"SMALL\"}]}";

    public static class Media
    {
        public String uri;
        public String title;
        public int width;
        public int height;
        public String format;
        public long duration;
        public long size;
        public int bitrate;
        public String[] persons;
        public String player;
        public String copyright;
    }

    public static class Image
    {
        public String uri;
        public String title;
        public int width;
        public int height;
        public String size;
    }

    public Media media;
    public Image[] images;

    public static MicroDomainPayload testSample()
    {
        MicroDomainPayload payload=new MicroDomainPayload();

        Media media=new Media();
        media.uri="http://javaone.com/keynote.mpg";
        media.title="JavaOne Keynote";
        media.width=640;
        media.height=480;
        media.format="video/mpg";
        media.duration=18000000;
        media.size=58982400;
        media.bitrate=262144;
        media.persons=new String[] { "Bill Gates", "Steve Jobs" };
        media.player="JAVA";
        media.copyright="None";
        payload.media=media;

        Image largeImage=new Image();
        largeImage.uri="http://javaone.com/keynote_large.jpg";
        largeImage.title="JavaOne Keynote";
        largeImage.width=1024;
        largeImage.height=768;
        largeImage.size="LARGE";

        Image smallImage=new Image();
        smallImage.uri="http://javaone.com/keynote_small.jpg";
        smallImage.title="JavaOne Keynote";
        smallImage.width=320;
        smallImage.height=240;
        smallImage.size="SMALL";

        payload.images=new Image[] { largeImage, smallImage };

        return payload;
    }
    
    static public Trace benchmarkRead(Trace parent,int count) throws Throwable
    {
        long length=0;
        for (int i=0;i<100000;i++)
        {
            var object=ObjectMapper.readObject(TEST_SAMPLE, MicroDomainPayload.class);
            length+=object.media.size;
        }
        try (Trace trace=new Trace(parent, "benchMarkRead"))
        {
            for (int i=0;i<count;i++)
            {
                var object=ObjectMapper.readObject(TEST_SAMPLE, MicroDomainPayload.class);
                length+=object.media.size;
            }
            trace.close();
            trace.setDetails("length="+length);
            return trace;
        }
    }
    static public Trace benchmarkWrite(Trace parent,int count) throws Throwable
    {
        long length=0;
        var object=testSample();
        for (int i=0;i<100000;i++)
        {
            var text=ObjectMapper.writeObjectToString(object);
            length+=text.length();
        }
        OutputStream stream=OutputStream.nullOutputStream();
        try (Trace trace=new Trace(parent, "benchMarkRead"))
        {
            for (int i=0;i<count;i++)
            {
                var text=ObjectMapper.writeObjectToString(object);
                length+=text.length();
            }
            trace.close();
            trace.setDetails("length="+length);
            return trace;
        }
    }
}