package pl.edu.agh.semantic.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jws.WebService;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.mortbay.log.Log;

import pl.edu.agh.semantic.service.OntologyService;
import pl.edu.agh.semantic.service.OntologyWebService;

@WebService(targetNamespace = "http://semantic.agh.edu.pl/",
	endpointInterface = "pl.edu.agh.semantic.service.OntologyWebService", serviceName = "OntologyWebService")
public class OntologyServiceImpl implements OntologyService, OntologyWebService {
	// TODO: ontologies should be stored in separated folder or in some other
	// structure than file system
	@Override
	public Set<String> add(List<FileItem> fileItemsList, String destinationDir)
			throws Exception {
		Set<String> ontologyAdded = new HashSet<String>();
		for (FileItem item : fileItemsList) {
			if (!item.isFormField()) {
				File file = new File(destinationDir, item.getName());
				Log.info("Ontology added at "+destinationDir+item.getName());
				item.write(file);
				ontologyAdded.add(file.getName());
			}
		}
		return ontologyAdded;
	}
	
	@Override
	public Set<String> add(String ontologyName, String fileData, String destinationDir)
			throws Exception {
		Set<String> ontologyAdded = new HashSet<String>();
		Log.info("trying add ontology at "+destinationDir+ontologyName);
		File file = new File(destinationDir, ontologyName);
		
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		out.write(fileData);
		out.close();
		ontologyAdded.add(file.getName());

		return ontologyAdded;
	}

	@Override
	public void remove(String ontology, String destinationDir) {
		File file = new File(destinationDir, ontology);
		file.delete();
	}

	@Override
	public Set<String> getAll(String destinationDir) {
		File ontologyDir = new File(destinationDir);
		File[] ontologiesFiles = ontologyDir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				if (name.endsWith("jsp") || name.endsWith("css") || name.equals("WEB-INF") || name.equals("META-INF"))
					return false;
				return true;
			}
		});
		Set<String> all = new HashSet<String>();
		for(File ontologyFile :ontologiesFiles){
			all.add(ontologyFile.getName());
		}
		return all;
		// TODO Auto-generated method stub

	}
}
