package examples.page;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lucy.annotation.core.Inject;

import org.t2framework.annotation.composite.GET;
import org.t2framework.annotation.core.ActionParam;
import org.t2framework.annotation.core.Default;
import org.t2framework.annotation.core.Page;
import org.t2framework.navigation.Direct;
import org.t2framework.navigation.Forward;
import org.t2framework.navigation.NoOperation;
import org.t2framework.spi.Navigation;

import commons.util.Assertion;
import commons.util.ResourceUtil;
import commons.util.StringUtil;

import examples.helper.DownloadHelper;

@Page("download")
public class DownloadPage {

	protected DownloadHelper downloadHelper;

	@Default
	public Navigation list() {
		return Forward.to("jsp/download.jsp");
	}

	@GET
	@ActionParam
	public Navigation simpleDownloadByGet(HttpServletRequest request,
			HttpServletResponse response) {
		String filename = request.getParameter("filename");
		if (StringUtil.isEmpty(filename)) {
			request.setAttribute("result", "filenameは必須です.");
			return list();
		} else if (!filename.endsWith(".csv")) {
			filename = filename + ".csv";
		}
		final File file = downloadHelper.getFile(filename);
		response.setContentType("application/octet-stream");
		response.setHeader("content-disposition", "attachment; filename=\""
				+ file.getName() + "\"");
		return Direct.from(file);
	}

	/**
	 * Just check if error handler works.
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@GET
	@ActionParam
	public Navigation errorByResourceNotFound(HttpServletRequest request,
			HttpServletResponse response) {
		ResourceUtil
				.getResourceAsFile("no_such_file_found_its_just_check_if_error_handling_works.zip");
		return NoOperation.INSTANCE;
	}

	@Inject
	public void inject(DownloadHelper downloadHelper) {
		this.downloadHelper = Assertion.notNull(downloadHelper);
	}
}
