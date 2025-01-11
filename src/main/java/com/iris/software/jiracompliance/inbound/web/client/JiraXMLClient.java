package com.iris.software.jiracompliance.inbound.web.client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.iris.software.jiracompliance.application.model.CustomIssue;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@NoArgsConstructor
@Profile("JiraXMLClient")
public class JiraXMLClient implements JiraClient {

	private static final String XML_FILE_NAME = "export.xml";

	private static final String XPATH_EXPRESSION = "/rss/channel/item";

	@Override
	public Map<String, List<CustomIssue>> getJiraCustomIssuesMap() {
		return getJiraCustomIssues().stream().collect(Collectors.groupingBy(CustomIssue::getIssueType));
	}

	@Override
	public List<CustomIssue> getJiraCustomIssues() {
		List<CustomIssue> customIssues = null;
		try {
			customIssues = new ArrayList<>();
			// Get XML file from resource folder
			File xmlFile = getFileFromResources(XML_FILE_NAME);

			// Create Document instance
			Document document = buildDocument(xmlFile);

			// Create XPath instance
			XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xpath = xPathFactory.newXPath();

			// Compile and evaluate an XPath expression
			NodeList nodeList = (NodeList) xpath.evaluate(XPATH_EXPRESSION, document, XPathConstants.NODESET);

			for (int i = 0; i < nodeList.getLength(); i++) {
				NodeList childNodes = nodeList.item(i).getChildNodes();
				CustomIssue customIssue = new CustomIssue();
				Map<String, Object> issueFieldMap = new HashMap<>();

				for (int j = 0; j < childNodes.getLength(); j++) {
					Node childNode = childNodes.item(j);
					if (childNode.getNodeType() == Node.ELEMENT_NODE) {
						if ("key".equals(childNode.getNodeName())) {
							customIssue
									.setId(Long.valueOf(childNode.getAttributes().getNamedItem("id").getTextContent()));
						}

						if ("type".equals(childNode.getNodeName())) {
							customIssue.setIssueType(childNode.getTextContent().trim());
						}

						issueFieldMap.put(childNode.getNodeName(), childNode.getTextContent().trim());
					}
				}
				customIssue.setIssueFieldMap(issueFieldMap);
				customIssues.add(customIssue);
			}
		} catch (IOException | ParserConfigurationException | SAXException | XPathExpressionException e) {
			e.printStackTrace();
		}
		return customIssues;
	}

	private File getFileFromResources(String fileName) throws IOException {
		ClassPathResource resource = new ClassPathResource(fileName);
		return resource.getFile();
	}

	private Document buildDocument(File file) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(file);
	}

}
