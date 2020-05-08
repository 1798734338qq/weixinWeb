package cn.net.comsys.weixin.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cn.net.comsys.weixin.po.PublicAccountPo;

public class ReadWechatPublicAccountUtil {

	static List<PublicAccountPo> publicAccountPos = new ArrayList<PublicAccountPo>();
	
	static List<String> publicAccountName = new ArrayList<String>();
	
	public static List<String> parseDocument(String path) {
        try{
        	File file = new File(path);
            // 初始化一个XML解析工厂
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			
            // 创建一个DocumentBuilder实例
            DocumentBuilder builder = factory.newDocumentBuilder();
			
            // 创建一个解析XML的Document实例
            Document doc = builder.parse(file);
			
            NodeList nodes = doc.getElementsByTagName("weChatPublicAccount");
            for (int i = 0; i < nodes.getLength(); i++) {
            	Node node = nodes.item(i);
            	publicAccountName.add(node.getAttributes().getNamedItem("nickname").getNodeValue());
			}
            
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return publicAccountName;
    }
	
	
	public static List<PublicAccountPo> getPublicAccountPosCache() {
		
		return publicAccountPos;
	}

	public static void clearPublicAccountPosCache() {
		
		publicAccountPos.clear();
	}
	
	public static void setAllPublicAccountPo(List<PublicAccountPo> pos) {
		
		publicAccountPos = pos;
		
	}

	public static void addPublicAccountPo(PublicAccountPo po) {
		
		publicAccountPos.add(po);
		
	}
}
