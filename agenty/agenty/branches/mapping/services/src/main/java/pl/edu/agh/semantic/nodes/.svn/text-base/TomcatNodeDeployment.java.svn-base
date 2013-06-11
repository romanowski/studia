
package pl.edu.agh.semantic.nodes;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.params.HttpParams;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.agh.semantic.utils.Result;




public class TomcatNodeDeployment {
	
	private static final Logger log = LoggerFactory.getLogger(TomcatNodeDeployment.class);
	private String indexNodeWarPath = URLDecoder.decode(TomcatNodeDeployment.class.getClassLoader().getResource("index-node.war").getFile());
	private String storageNodeWarPath = URLDecoder.decode(TomcatNodeDeployment.class.getClassLoader().getResource("storage-node.war").getFile());
	private String ontologyNodeWarPath = URLDecoder.decode(TomcatNodeDeployment.class.getClassLoader().getResource("ontology-node.war").getFile());
	String josekiZipEntryPath = "WEB-INF/classes/storage-node-joseki-config.ttl";
	String webZipEntryPath = "WEB-INF/web.xml";
	
	
	public Result deployIndexNode(String serverUrl, String nodeName, String username, String password,StringBuilder nodeUrl) throws Exception
	{
		return deployNode(serverUrl, nodeName, null, username, password, indexNodeWarPath, nodeUrl);
	}
	
	public Result deployStorageNode(String serverUrl, String nodeName, String SPARQLname, String username, String password,StringBuilder nodeUrl ) throws Exception
	{
		return deployNode(serverUrl, nodeName,SPARQLname, username, password, storageNodeWarPath, nodeUrl);
	}
	
	public Result deployOntologyNode(String serverUrl, String nodeName, String username, String password,StringBuilder nodeUrl ) throws Exception
	{
		return deployNode(serverUrl, nodeName, null, username, password, ontologyNodeWarPath, nodeUrl);
	}
	
	//TODO: change retrun to entity.getContent(), this method may not add node and return success
	private Result deployNode(String serverUrl, String nodeName, String SPARQLname, String username, String password, String warPath, StringBuilder nodeUrl) throws Exception
	{
			
	        DefaultHttpClient httpclient = new DefaultHttpClient();
	        nodeUrl.append( "http://" + serverUrl + "/" + nodeName);
	        String resultText;
	        try {
        		URI url = new URI("http://" + serverUrl + "/manager/text/deploy?path=/" + nodeName);
	            httpclient.getCredentialsProvider().setCredentials(
	                   new AuthScope(url.getHost(), url.getPort()),
	                    new UsernamePasswordCredentials(username, password));

	            HttpPut httpPut = new HttpPut(url);
	            log.info("url of PUT deployment command: {}", url.toString());
	            
	            // load file to PUT request
	            File file = new File(warPath);
	            if (warPath == storageNodeWarPath)
	            {
	            	file = editZip(warPath, nodeUrl.toString(), SPARQLname);
	            }
	            else
	            	file = new File(warPath);
	            FileInputStream fin = new FileInputStream(file);
	            
	            byte fileContent[] = new byte[(int)file.length()];
	            fin.read(fileContent);
	            ByteArrayEntity requestEntity = new ByteArrayEntity( fileContent );
	            

	            httpPut.setEntity(requestEntity);

	            HttpResponse response = httpclient.execute(httpPut);
	            HttpEntity entity = response.getEntity();
	            
	            StatusLine statusLine = response.getStatusLine();
	            if (statusLine.getStatusCode() == 401)
	            	return new Result(false, "Not authorized: " + statusLine.toString());
	            log.info("status of deploy command: {}", statusLine.toString());
	            resultText = EntityUtils.toString(entity);
	            log.info("response of deploy command: {}", resultText);
	            //?
	            EntityUtils.consume(entity);
	        	}
	            catch (Exception e){
	            	log.info("Deployng war error:", e);
	            	return new Result(false, e.getMessage());
	            }
	         finally {
	            httpclient.getConnectionManager().shutdown();
	        }
	    
		if (resultText.startsWith("OK"))
			return new Result(true, resultText);
		else
			return new Result(false, resultText);
	}
	
	public Result undeployNode(String serverUrl, String nodeName, String username, String password)
	{			
	        DefaultHttpClient httpclient = new DefaultHttpClient();
	        String resultText;
	        try {
        		URI url = new URI("http://" + serverUrl + "/manager/text/undeploy?path=" + nodeName);
	            httpclient.getCredentialsProvider().setCredentials(
	                   new AuthScope(url.getHost(), url.getPort()),
	                    new UsernamePasswordCredentials(username, password));

	            HttpGet httpPut = new HttpGet(url);
	            log.info("url of PUT undeployment command: {}", url.toString());
	            
	            HttpResponse response = httpclient.execute(httpPut);
	            HttpEntity entity = response.getEntity();
	            
	            StatusLine statusLine = response.getStatusLine();
	            if (statusLine.getStatusCode() == 401)
	            	return new Result(false, "Not authorized: " + statusLine.toString());
	            log.info("status of undeploy command: {}", statusLine.toString());
	            resultText = EntityUtils.toString(entity);
	            log.info("response of undeploy command: {}", resultText);
	            
	            
	            //?
	            EntityUtils.consume(entity);
	        	}
	            catch (Exception e){
	            	log.error("Deployng war error:", e);
	            	return new Result(false, e.getMessage());
	            }
	         finally {
	            httpclient.getConnectionManager().shutdown();
	        }
	    
		
	        if (resultText.startsWith("OK"))
				return new Result(true, resultText);
			else
				return new Result(false, resultText);
	}
	
	static final int BUFFER = 2048;
	private File editZip(String warFilePath, String nodeUrl, String sPARQLname) {
      try {

         ZipFile zipfile = new ZipFile(warFilePath);
         log.info("Zip file found in path: {} zipfile {}:", warFilePath, zipfile.getName());
         Enumeration<? extends ZipEntry> entries2 = zipfile.entries();
         entries2.nextElement();
         entries2.nextElement();
         log.info("Zip file first ENTRY NAME: {} number of entries: {}", entries2.nextElement().getName(), zipfile.size());
         
         //get joseki file 
         ZipEntry josekiZipEntry = zipfile.getEntry(josekiZipEntryPath);
         BufferedInputStream is = new BufferedInputStream(zipfile.getInputStream(josekiZipEntry));
		 StringWriter writer = new StringWriter();
         IOUtils.copy(is, writer);
         String josekiFileContent = writer.toString();
         is.close();
         
         //get web file 
         ZipEntry webZipEntry = zipfile.getEntry(webZipEntryPath);
         is = new BufferedInputStream(zipfile.getInputStream(webZipEntry));
		 StringWriter writer2 = new StringWriter();
         IOUtils.copy(is, writer2);
         String webFileContent = writer2.toString();
         is.close();
         
         //TODO: NOT REPLACING BUT MORE COMPLEX PROCESSING OF FILE
         //change file
         josekiFileContent = josekiFileContent.replace("b1", sPARQLname.toLowerCase());
         josekiFileContent = josekiFileContent.replace("B1", sPARQLname.toUpperCase());
         //josekiFileContent = josekiFileContent.replace("http://localhost:8080/storage-node1", nodeUrl);
         
         webFileContent = webFileContent.replace("http://localhost:8080/storage-node", nodeUrl);
         webFileContent = webFileContent.replace("b1", sPARQLname.toLowerCase());
         //save file
         
         //StringReader stringReader = new StringReader(fileContent);
         File tempZip = File.createTempFile("storage-node", "tmp");
         log.info("temp zip location:{}", tempZip.getPath());
         ZipOutputStream out = new ZipOutputStream(new FileOutputStream(tempZip));
         Enumeration<? extends ZipEntry> entries = zipfile.entries();
         while (entries.hasMoreElements())
         {
        	 ZipEntry zipEntry = entries.nextElement();
        	 log.info("Rewriting zip entry:{}", zipEntry.getName());
        	 byte[] data;
        	 
        	 if (zipEntry.getName().equals(webZipEntryPath)){
        		 data = webFileContent.getBytes();
        	 }
        	 else if (zipEntry.getName().equals(josekiZipEntryPath)){
        		 data = josekiFileContent.getBytes();
        	 }
        	 else
        		 data = IOUtils.toByteArray(zipfile.getInputStream(zipEntry)); 
        	 //IOUtils.copy(data);
        	 ZipEntry newZipEntry = new ZipEntry(zipEntry.getName());
        	 newZipEntry.setSize(data.length);
        	 CRC32 crc = new CRC32();
        	 crc.update(data);
        	 newZipEntry.setCrc(crc.getValue());
        	 out.putNextEntry(newZipEntry/*zipEntry*/);
        	 out.write(data);
        	 out.closeEntry();
         }
         out.flush();
         out.close();
         //out.write(b)
         //out.w
         //   dest.flush();
         //   dest.close();
          //  is.close();
            //return compressed zip input stream
         return tempZip;
      } catch(Exception e) {
         //e.printStackTrace();
    	  log.error("Error when editing war: ", e);
    	  return null;
      }
      
   }
}
