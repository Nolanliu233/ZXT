package cn.tongyuankeji.common.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
public class XmlUtils
{
	// Node type:
	private final static int ELEMENT_NODE = 1;
	@SuppressWarnings("unused")
	private final static int ATTRIBUTE_NODE = 2;
	private final static int TEXT_NODE = 3;
	@SuppressWarnings("unused")
	private final static int CDATA_SECTION_NODE = 4;
	@SuppressWarnings("unused")
	private final static int ENTITY_REFERENCE_NODE = 5;
	@SuppressWarnings("unused")
	private final static int ENTITY_NODE = 6;
	@SuppressWarnings("unused")
	private final static int PROCESSING_INSTRUCTION_NODE = 7;
	@SuppressWarnings("unused")
	private final static int COMMENT_NODE = 8;
	@SuppressWarnings("unused")
	private final static int DOCUMENT_NODE = 9;
	@SuppressWarnings("unused")
	private final static int DOCUMENT_TYPE_NODE = 10;
	@SuppressWarnings("unused")
	private final static int DOCUMENT_FRAGMENT_NODE = 11;
	@SuppressWarnings("unused")
	private final static int NOTATION_NODE = 12;

	/**
	 * 将xdoc转换成String，去掉所有无效空格等（目前只转ELEMENT_NODE、TEXT_NODE、ATTRIBUTE_NODE）
	 * 
	 * @param xdoc
	 *            被转换的xml document
	 * @param includesAttr
	 *            保留节点上哪些attributes。传入null时，保存全部
	 * @param addProcInstrcution
	 *            是否添加头上的process instruction
	 * @param encoding
	 *            编码。传入null时，内部添加utf-8
	 */
	public static String xdoc2String(Document xdoc, String[] includesAttr, boolean addProcInstrcution, String encoding)
	{
		assert xdoc != null : "xdoc2String(Document, String[], boolean, String)参数xdoc不能为空！";

		StringBuffer sb = new StringBuffer();

		if (addProcInstrcution)
			sb.append("<?xml version=\"1.0\" encoding=\"");
		sb.append(Utils.isBlank(encoding) ? "utf-8" : encoding);
		sb.append("\"?>");
		node2String(xdoc.getDocumentElement(), sb, includesAttr);

		return sb.toString();
	}

	/**
	 * 将节点node分支树转成String，去掉所有无效空格等（目前只支持ELEMENT_NODE、TEXT_NODE、ATTRIBUTE_NODE）
	 * 
	 * @param xnode
	 *            被转换的xml node
	 * @param includesAttr
	 *            保留节点上哪些attributes。传入null时，保存全部
	 */
	public static void node2String(Node xnode, StringBuffer sb, String[] includesAttr)
	{
		assert xnode != null : "node2String(Node, StringBuffer, String[])参数xnode不能为空！";
		assert sb != null : "node2String(Node, StringBuffer, String[])参数sb不能为空！";

		NamedNodeMap attrs = null;
		Element xele = null;
		NodeList lstChild = null;

		switch (xnode.getNodeType())
		{
			case ELEMENT_NODE: // element
				xele = (Element) xnode;
				sb.append("<");
				sb.append(xele.getNodeName());

				attrs = xele.getAttributes();
				for (int j = 0; j < attrs.getLength(); j++)
				{
					if (includesAttr != null && includesAttr.length > 0)
					{
						if (!Utils.isContainString(attrs.item(j).getNodeName(), true, includesAttr))
							continue;
					}

					sb.append(" ");
					sb.append(attrs.item(j).getNodeName());
					sb.append("=\"");
					sb.append(attrs.item(j).getNodeValue());
					sb.append("\"");
				}
				sb.append(">");

				lstChild = xele.getChildNodes();
				for (int i = 0; i < lstChild.getLength(); i++)
					node2String(lstChild.item(i), sb, includesAttr);

				sb.append("</");
				sb.append(xele.getNodeName());
				sb.append(">");
				break;

			case TEXT_NODE: // #text
				if (!Utils.isBlank(xnode.getNodeValue()))
					sb.append(xnode.getNodeValue().trim());
				break;

			default:
				break;
		}
	}

	/**
	 * 从filename装载xml文档，成为Xml document
	 * 
	 * @param filename
	 *            文件名，含路径
	 */
	public static Document loadXmlFile(String filename) throws Exception
	{
		assert !Utils.isBlank(filename) : "loadXmlFile(String)参数filename不能为空！";

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		return dBuilder.parse(new File(filename));
	}

	public static List<Element> getElementChild(Node parent)
	{
		ArrayList<Element> result = new ArrayList<Element>();

		if (parent.getNodeType() == ELEMENT_NODE)
		{
			NodeList lst = parent.getChildNodes();
			for (int i = 0; i < lst.getLength(); i++)
			{
				if (lst.item(i).getNodeType() == ELEMENT_NODE)
					result.add((Element) lst.item(i));
			}
		}

		return result;
	}

	public static Element getElementChildByName(Node parent, String childKey, String childVal)
	{
		if (parent.getNodeType() == ELEMENT_NODE)
		{
			NodeList lst = parent.getChildNodes();
			for (int i = 0; i < lst.getLength(); i++)
			{
				if (lst.item(i).getNodeType() == ELEMENT_NODE
						&& ((Element) lst.item(i)).getAttribute(childKey).equals(childVal))
					return (Element) lst.item(i);
			}
		}

		return null;
	}
}
