package pl.edu.agh.semantic.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.fileupload.FileItem;

import pl.edu.agh.semantic.service.OntologyService;

public class OntologyServiceImpl implements OntologyService {
	// TODO: ontologies should be stored in separated folder or in some other
	// structure than file system
	@Override
	public Set<String> add(List<FileItem> fileItemsList, String destinationDir)
			throws Exception {
		Set<String> ontologyAdded = new HashSet<String>();
		for (FileItem item : fileItemsList) {
			// out.println("item: "+ item.getName());
			// out.flush();
			if (!item.isFormField()) {
				// actual file
				InputStream inputStream = item.getInputStream();
				File file = new File(destinationDir, item.getName());
				item.write(file);
				ontologyAdded.add(file.getName());
				// out.println("<h1>Successful ontology upload: </h1>" +
				// file.getName());
			}
		}
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
