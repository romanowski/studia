

package pl.edu.agh.semantic.servlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.mortbay.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.semantic.impl.AutomaticIndexingServiceImpl;
import pl.edu.agh.semantic.impl.OntologyServiceImpl;
import pl.edu.agh.semantic.service.AutomaticIndexingService;
import pl.edu.agh.semantic.service.OntologyService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Set;

/**
 * Handle ontology add request submitted by HTML form.
 *
 * @author Jakub Cwajna
 */
@SuppressWarnings("serial")
public class OntologyServlet extends HttpServlet {
	private static final Logger log = LoggerFactory.getLogger(OntologyServlet.class);

	//TODO: initalize by service factory?
	protected OntologyService ontologyService = new OntologyServiceImpl();
	
	//TODO: some file input validation: security problems
	//TODO: storing ontologies in more sofisticated way
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		StringWriter servletResult =  new StringWriter();
		PrintWriter out = new PrintWriter(servletResult);

		String destinationDir = req.getSession().getServletContext().getRealPath("/");
		try {
		if (ServletFileUpload.isMultipartContent(req)) {
			
				ServletFileUpload servletFileUpload = new ServletFileUpload(new DiskFileItemFactory());
				List<FileItem> fileItemsList = servletFileUpload.parseRequest(req);
				
				ontologyService.add(fileItemsList, destinationDir);
				
				out.println("Ontologies successfully uploaded");
		} else if (req.getParameter("remove")!=null){
			String ontologyToRemove = req.getParameter("remove_ontology_url");
			ontologyService.remove(ontologyToRemove, destinationDir);
			out.println("Ontology " + ontologyToRemove+ " successfully removed");
		}
		} catch (FileUploadBase.SizeLimitExceededException e) {
			log.debug("Upload file size limit exceeded!", e);
			throw new ServletException(e);
		} catch (FileUploadException e) {
			log.debug("Upload error!", e);
			throw new ServletException(e);
		} catch (Exception e) {
			log.debug("Save upload file error!", e);
			throw new ServletException(e);
		}
		out.flush();
		req.setAttribute("servlet-result", servletResult.toString());
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
	}
}
