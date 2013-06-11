package pl.edu.agh.semantic.service;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * SPARQL Mapping service.
 * <p/>
 * Creation date: 2012.05.29
 *
 * @author Tomasz Zdybał
 */
public interface MappingService {
	String transformQuery(String query);

	void setMapping(String fileOrURI);

	void mapResult(String query, InputStream originalResult, OutputStream mappedResult);
}
