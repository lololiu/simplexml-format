package com.royll.xmlformat.util;

//import org.w3c.dom.Document;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.xml.sax.SAXParseException;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;


/**
 * Created by Dell on 2016/11/17.
 */
public class XmlUtils {

    //    public static void SaveXMLWithIndent(String xml, String fileName) {
//        try {
//            Document doc = getDocumentFromString(xml);
//            DOMSource src = new DOMSource(doc);
//            StreamResult sr = new StreamResult(new File(fileName));
//            TransformerFactory tf = TransformerFactory.newInstance();
//            Transformer t = tf.newTransformer();
//            t.setOutputProperty(OutputKeys.INDENT, "yes");
//            t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
//            t.setOutputProperty(OutputKeys.METHOD, "xml");
//            t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
//            t.transform(src, sr);
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    public static Document getDocumentFromString(String xml) throws Exception {
//        Document doc = null;
//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        factory.setNamespaceAware(true);
//        DocumentBuilder builder = factory.newDocumentBuilder();
//        doc = builder.parse(new InputSource(new StringReader(xml)));
//        return doc;
//    }
    public static String formatXml(String str) throws SAXParseException,DocumentException,IOException {
        SAXReader reader = new SAXReader();
        // System.out.println(reader);
        // 注释：创建一个串的字符输入流
        StringReader in = new StringReader(str);
        Document doc = reader.read(in);
        // System.out.println(doc.getRootElement());
        // 注释：创建输出格式
        OutputFormat formater = OutputFormat.createPrettyPrint();
        //formater=OutputFormat.createCompactFormat();
        // 注释：设置xml的输出编码
        formater.setEncoding("utf-8");
        // 注释：创建输出(目标)
        StringWriter out = new StringWriter();
        // 注释：创建输出流
        XMLWriter writer = new XMLWriter(out, formater);
        // 注释：输出格式化的串到目标中，执行后。格式化后的串保存在out中。
        writer.write(doc);

        writer.close();
        System.out.println(out.toString());
        // 注释：返回我们格式化后的结果
        return out.toString();
    }

}
