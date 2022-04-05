package main.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Worker {

	public void execute() {
		// TODO Auto-generated method stub

		loadProperties();

		readFiles();

	}


	String currFile = null;

	private void loadProperties() {
		System.out.println(":::: Loading application properties ::::");

		InputStream inStream;

		try {

			inStream = getClass().getResourceAsStream("/resources/config.properties");

			Properties p = new Properties();

			if (inStream != null) {
				p.load(inStream);
			} else {
				throw new FileNotFoundException();
			}
			// p.load(reader);

			setInputPath(p.getProperty("UploaderInputPath"));
			setOutputPath(p.getProperty("UploaderOutputPath"));
			setDelay(Integer.parseInt(p.getProperty("Delay")));
			setParentNode(p.getProperty("ParentNode"));

//			System.out.println(getInputPath() + "\n" + getOutputPath() + "\n" + getDelay());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void readFiles() {

		File inputFile = new File(getInputPath());
		File[] files = inputFile.listFiles();

		try {
			if (files.length > 0) {
				for (File file : files) {
					String fileName = file.getName();
					if (fileName.contains(".xml")) {
						System.out.println("File found in input directory:::: " + fileName);
						currFile = fileName;
						Document doc = parseXML(file);
						dropFiles(doc);
						deleteFile(fileName);
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private Document parseXML(File file) {

		Document doc = null;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(file);
			doc.getDocumentElement().normalize();

			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			NodeList nList = doc.getElementsByTagName(getParentNode());
			System.out.println("----------------------------");

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				System.out.println("\nCurrent Element : " + nNode.getNodeName());

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					System.out.println("TTCS Reference : "
							+ eElement.getElementsByTagName("ExternalRef").item(0).getTextContent());

					String calypRef = eElement.getElementsByTagName("CalypsoTradeId").item(0).getTextContent();
					System.out.println("Calypso Reference : " + calypRef);

					if (eElement.getElementsByTagName("Error") != null) {

						NodeList errorList = eElement.getElementsByTagName("Error");
						for (int x = 0; x < errorList.getLength(); x++) {
							Node errorNode = errorList.item(x);
							if (errorNode.getNodeType() == Node.ELEMENT_NODE) {
								Element errorElement = (Element) errorNode;
								errorElement.appendChild(
										eElement.getElementsByTagName("ExternalRef").item(0).cloneNode(true));
//								
								System.out.println("Message : "
										+ errorElement.getElementsByTagName("Message").item(0).getTextContent());

								System.out.println("New Reference : "
										+ errorElement.getElementsByTagName("ExternalRef").item(0).getTextContent());

							}

						}
						System.out.println(eElement.getElementsByTagName("Error").item(0).getTextContent());

					}
					System.out.println("----------------------------");

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}

	private void dropFiles(Document doc) {

		try {
			TransformerFactory transformerfactory = TransformerFactory.newInstance();
			Transformer transformer = transformerfactory.newTransformer();
			DOMSource domSource = new DOMSource(doc);
			StreamResult streamResult = new StreamResult(new File(getOutputPath() + currFile));

			transformer.transform(domSource, streamResult);

			System.out.println("Done creating XML");
			System.out.println("----------------------------");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void deleteFile(String fileName) {

		File f = new File(getInputPath() + fileName);

		f.delete();

		System.out.println("----------------------------");

		System.out.println("Deleted File ::: " + fileName + " after processing content");

	}

	String inputPath, outputPath, parentNode;

	public String getParentNode() {
		return parentNode;
	}

	public void setParentNode(String parentNode) {
		this.parentNode = parentNode;
	}

	int delay;

	public String getInputPath() {
		return inputPath;
	}

	public void setInputPath(String inputPath) {
		this.inputPath = inputPath;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}


}
