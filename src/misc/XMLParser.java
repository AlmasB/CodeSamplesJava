package com.almasb.common.parsing;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
//import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;


public class XMLParser {

    private Document doc;

    /**
     * Constructs an xml parser from given InputStream
     * Caller may close the stream after this ctor
     *
     * @param is
     * @throws Exception
     */
    public XMLParser(InputStream is) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        doc = dBuilder.parse(is);
        doc.getDocumentElement().normalize();
    }

    public String[] getDataByTag(String tag) {
        return getDataByTag(tag, 0);
    }
    
    public String[] getDataByTag(String tag, int start) {
        NodeList list = doc.getElementsByTagName(tag);
        
        int length = list.getLength() - start;
        if (length < 1)
            return new String[0];
        
        String[] res = new String[length];
        for (int i = start; i < list.getLength(); i++)
            res[i-start] = list.item(i).getTextContent();

        return res;
    }
    
    public String[] getAttributeByTag(String attr, String tag) {
        NodeList list = doc.getElementsByTagName(tag);
        String[] res = new String[list.getLength()];
        for (int i = 0; i < list.getLength(); i++) {
            Element e = (Element) list.item(i);
            res[i] = e.getAttribute(attr);
        }
        
        return res;
    }
}
