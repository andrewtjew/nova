package org.nova.usps;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.nova.http.client.JSONClient;
import org.nova.http.client.PathAndQuery;
import org.nova.http.client.TextResponse;
import org.nova.logging.Logger;
import org.nova.tracing.Trace;
import org.nova.tracing.TraceManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class AddressInformation
{
    final private String userName;
    final private JSONClient client;
    final DocumentBuilder builder;
    public AddressInformation(String userName,TraceManager traceManager,Logger logger,String endPoint) throws Throwable
    {
        this.userName=userName;
        this.client=new JSONClient(traceManager, logger, endPoint);
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        this.builder= factory.newDocumentBuilder();
    }
    
//    http://production.shippingapis.com/ShippingAPI.dll?API=Verify
//
//        &XML=<AddressValidateRequest USERID="xxxxxxx"><Address ID="0"><Address1></Address1>
//
//        <Address2>6406 Ivy Lane</Address2><City>Greenbelt</City><State>MD</State>
//
//        <Zip5></Zip5><Zip4></Zip4></Address></AddressValidateRequest>    
    public AddressInformation(String userName,TraceManager traceManager,Logger logger) throws Throwable
    {
        this(userName,traceManager,logger,"http://production.shippingapis.com");
    }
    public boolean verify(Trace parent,String address1,String address2,String city,String state,String zip) throws Throwable
    {
        if (this.userName==null)
        {
            return true;
        }
        Document document=this.builder.newDocument();
        Element root=document.createElement("AddressValidateRequest");
        root.setAttribute("USERID", this.userName);

        Element addressElement=document.createElement("Address");
        addressElement.setAttribute("ID", "0");

        document.appendChild(root);
        root.appendChild(addressElement);
        
        {
            Element element=document.createElement("Address1");
            element.setTextContent(address1);
            addressElement.appendChild(element);
        }
        {
            Element element=document.createElement("Address2");
            element.setTextContent(address2);
            addressElement.appendChild(element);
        }
        {
            Element element=document.createElement("City");
            element.setTextContent(city);
            addressElement.appendChild(element);
        }
        {
            Element element=document.createElement("State");
            element.setTextContent(state);
            addressElement.appendChild(element);
        }
        {
            Element element=document.createElement("Zip5");
            element.setTextContent(zip);
            addressElement.appendChild(element);
        }
        {
            Element element=document.createElement("Zip4");
            addressElement.appendChild(element);
        }
        
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
         
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        String xml=writer.getBuffer().toString();
        
        String pathAndQuery=new PathAndQuery("/ShippingAPI.dll").addQuery("API", "Verify").addQuery("XML", xml).toString();
//        System.out.println(pathAndQuery);
        
        TextResponse response=this.client.getText(parent, null, pathAndQuery);
        String responseText=response.get();
//        System.out.println(responseText);
        Document responseDocument=this.builder.parse(new ByteArrayInputStream(responseText.getBytes(StandardCharsets.UTF_8)));
        NodeList list=responseDocument.getElementsByTagName("AddressValidateResponse");
        if (list.getLength()==1)
        {
            Element addressValidateResponseElement=(Element)list.item(0);
            NodeList addressList=addressValidateResponseElement.getElementsByTagName("Address");
            if (addressList.getLength()==1)
            {
                Element addressResponseElement=(Element)addressList.item(0);
//                System.out.println(addressResponseElement.getTextContent());
                
                NodeList errorList=addressResponseElement.getElementsByTagName("Error");
                if (errorList.getLength()==1)
                {
                    return false;
                }
                NodeList cityList=addressResponseElement.getElementsByTagName("City");
                if (cityList.getLength()!=1)
                {
                    return false;
                }
                Element cityElement=(Element)cityList.item(0);
                String cityText=cityElement.getTextContent();
                if (city.equalsIgnoreCase(cityText)==false)
                {
                    return false;
                }

                
                NodeList zipList=addressResponseElement.getElementsByTagName("Zip5");
                if (zipList.getLength()!=1)
                {
                    return false;
                }
                Element zipElement=(Element)zipList.item(0);
                String zipText=zipElement.getTextContent();
                if (zip.equals(zipText)==false)
                {
                    return true;
                }
                return true;
            }
        }
        return false;
    }
    
}
