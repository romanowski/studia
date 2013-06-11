/*
 * Copyright (c) 2011,2012, Krzysztof Styrc and Tomasz Zdybał
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the project nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE PROJECT AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE PROJECT OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

package pl.edu.agh.semantic.servlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.semantic.impl.StorageServiceImpl;
import pl.edu.agh.semantic.service.StorageService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * Stores data submitted with HTML form.
 *
 * @author Tomasz Zdybał
 */
@SuppressWarnings("serial")
public class StorageServiceServlet extends HttpServlet {
	private static final Logger log = LoggerFactory.getLogger(StorageServiceServlet.class);

	private StorageService storageService;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		storageService = new StorageServiceImpl();
		storageService.init(config.getInitParameter("configFile"), config.getInitParameter("endpointURI"),
				config.getInitParameter("graphURI"));
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		StringWriter servletResult =  new StringWriter();
		PrintWriter out = new PrintWriter(servletResult);
		if (ServletFileUpload.isMultipartContent(req)) {

			try {
				ServletFileUpload servletFileUpload = new ServletFileUpload(new DiskFileItemFactory());
				List<FileItem> fileItemsList = servletFileUpload.parseRequest(req);
				InputStream inputStream = null;
				String type = null;
				String graph = null;
				String graphName = null;
				String data = null;
				for (FileItem item : fileItemsList) {
					if (!item.isFormField()) {
						// actual file
						inputStream = item.getInputStream();
					} else {
						// other values from form
						if ("type".equals(item.getFieldName())) {
							type = item.getString();
						} else if ("graph".equals(item.getFieldName())) {
							graph = item.getString();
						} else if ("graph_name".equals(item.getFieldName())) {
							graphName = item.getString();
						} else if ("data".equals(item.getFieldName())) {
							data = item.getString();
						}
					}
				}

				if (data != null && data.trim().length() > 0) {
					inputStream = IOUtils.toInputStream(data);
				}

				if ("named".equals(graph) && graphName != null) {
					storageService.addDataToNamedGraph(inputStream, type, graphName);
				} else {
					storageService.addDataToDefaultGraph(inputStream, type);
				}

				out.println("Successful data upload and automatic indexing!");
			} catch (FileUploadBase.SizeLimitExceededException e) {
				log.debug("Upload file size limit exceeded!", e);
				throw new ServletException(e);
			} catch (FileUploadException e) {
				log.debug("Upload error!", e);
				throw new ServletException(e);
			}
			out.flush();
			req.setAttribute("servlet-result", servletResult.toString());
	        req.getRequestDispatcher("/index.jsp").forward(req, resp);
		}
	}
}
