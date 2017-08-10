package nl.inl.blacklab.server.requesthandlers;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;

import nl.inl.blacklab.index.DocumentFormats;
import nl.inl.blacklab.index.config.ConfigInputFormat;
import nl.inl.blacklab.server.BlackLabServer;
import nl.inl.blacklab.server.datastream.DataStream;
import nl.inl.blacklab.server.exceptions.BadRequest;
import nl.inl.blacklab.server.exceptions.BlsException;
import nl.inl.blacklab.server.jobs.User;
import nl.inl.blacklab.server.util.FileUploadHandler;
import nl.inl.blacklab.server.util.FileUploadHandler.UploadedFileTask;

/**
 * Add or update an input format configuration.
 */
public class RequestHandlerAddFormat extends RequestHandler {

	public RequestHandlerAddFormat(BlackLabServer servlet,
			HttpServletRequest request, User user, String indexName,
			String urlResource, String urlPathPart) {
		super(servlet, request, user, indexName, urlResource, urlPathPart);
	}

	@Override
	public int handle(DataStream ds) throws BlsException {
		debug(logger, "REQ add format: " + indexName);

		FileUploadHandler.handleRequest(ds, request, "data", new UploadedFileTask() {
			@Override
			public void handle(FileItem fi) throws Exception {
				// Get the uploaded file parameters
				String fileName = fi.getName();
				if (!fileName.matches("[\\w_\\-]+(\\.blf)?\\.(ya?ml|json)"))
				    throw new BadRequest("ILLEGAL_INDEX_NAME", "Format configuration name may only contain letters, digits, underscore and dash, and must end with .yaml or .json (or .blf.yaml/.blf.json)");
				String formatIdentifier = ConfigInputFormat.stripExtensions(fileName);
				boolean isJson = fileName.endsWith(".json");
                File userFormatDir = indexMan.getUserFormatDir(user.getUserId());
				File formatFile = new File(userFormatDir, formatIdentifier + ".blf." + (isJson ? "json" : "yaml"));
				fi.write(formatFile);
                ConfigInputFormat f = new ConfigInputFormat(formatFile);
                f.setName(user.getUserId() + ":" + f.getName()); // prefix with user id to avoid collisions
                DocumentFormats.register(f);
			}
		});
		return Response.success(ds, "Format added.");
	}

}