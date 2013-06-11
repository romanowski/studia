package pl.edu.agh.semantic.nodes;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class NodeManager {
	private static final Logger log = LoggerFactory.getLogger(NodeManager.class);
	private final ReadWriteLock lock = new ReentrantReadWriteLock(true);
	private final Lock read = lock.readLock();
	private final Lock write = lock.writeLock();
	
	private Set<String> nodes = new HashSet<String>();
	private String tagName;
	
	public NodeManager(String tagName){
		this.tagName = tagName;
	}
	public boolean addNode(String url) {
		log.info("Adding {} !!!!index!!!! node", url);

		write.lock();
		try {
			return nodes.add(url);
		} catch (RuntimeException e) {
			log.error("Runtime error", e);
			throw e;
		} finally {
			write.unlock();
		}
	}
	
	public boolean removeNode(String url) {
		log.info("Removing {} !!!index!! node", url);
		boolean status = false;

		write.lock();
		try {
			nodes.remove(url);
		} catch (RuntimeException e) {
			log.error("Runtime error", e);
			throw e;
		} finally {
			write.unlock();
		}

		return status;
	}
	
	public Set<String> getAllNodes() {
		log.debug("Returning all !!!index!!! nodes");
		read.lock();
		// TODO: try catch not needed?
		try {
			return nodes;
		} catch (RuntimeException e) {
			log.error("Runtime error", e);
			throw e;
		} finally {
			read.unlock();
		}
	}
	
	private void setNodes(Set<String> nodes) {
		write.lock();
		try {
			this.nodes = nodes;
		} catch (RuntimeException e) {
			log.error("Runtime error", e);
			throw e;
		} finally {
			write.unlock();
		}
	}
	
	synchronized public void readFromXML(Document doc) {
		log.info("Reading " + tagName + " from a file {}", doc.getDocumentURI());
		try {
			//DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			//DocumentBuilder db = dbf.newDocumentBuilder();
			//Document doc = db.parse(new File(filename));

			Element rootElement = doc.getDocumentElement();
			NodeList nodesElement = rootElement.getElementsByTagName(tagName);
			if (nodesElement.getLength() == 0)
				return;
			NodeList nodesElementList = nodesElement.item(0).getChildNodes();
			log.info("Number of " + tagName + " nodes: {}", nodesElementList.getLength());

			// get all index nodes
			Set<String> nodes = new HashSet<String>();
			for (int i = 0; i < nodesElementList.getLength(); i++) {
				Element nodeElement = (Element) nodesElementList.item(i);

				log.info(tagName + " element: {}", nodeElement.toString());
				
				String nodeUrl = nodeElement.getTextContent();
				log.info(tagName + " url: {}", nodeUrl);

				// add to index
				nodes.add(nodeUrl);
			}
			
			// update index 
			setNodes(nodes);

		} catch (Exception e) {
			log.error("Reading " + tagName + " from a file error", e);
		}
	}

	
	
	/**
	 * Writes index content to XML file.
	 *
	 * @param filename XML filename
	 */

	synchronized public void writeToXML(Document doc) {
		try {
			Element rootElement = (Element) doc.getElementsByTagName("nodes").item(0);

			// write all index nodes
			Element nodesElement = doc.createElement(tagName);
			rootElement.appendChild(nodesElement);
			for (String node : nodes) {

				Element nodeElement = doc.createElement(tagName);
				nodesElement.appendChild(nodeElement);
				nodeElement.appendChild(doc.createTextNode(node));
				
			}

		} catch (Exception e) {
			log.error("Writing " + tagName + " to a file error", e);
		}
	}
}
