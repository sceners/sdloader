package sdloader.tool.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import sdloader.util.IOUtil;
import sdloader.util.ResourceUtil;

public class ProjectTemplateHelper {

	public static void execute( String webContentDir,InputStream webXml,InputStream indexHtml) {
		try {
			File baseDir = new File(".");
			ProjectTemplateHelper.createProjectTemplate(baseDir, webContentDir,
					webXml,indexHtml);
			File classPathFile = ProjectTemplateHelper
					.getEclipseClassPathFile(baseDir);
			if (classPathFile != null) {
				boolean modify = ProjectTemplateHelper.modifyEclipseClassPath(
						baseDir, classPathFile, webContentDir);
				if (modify) {
					System.out.println("Class file output folder modified.");
				}
			}
			System.out.println("Create success.Reflesh the project.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void createProjectTemplate(File baseDir,
			String webContentName, InputStream webXmlStream,
			InputStream indexHtmlStream) throws IOException {

		File webContent;
		if (webContentName == null) {
			webContent = baseDir;
		} else {
			webContent = new File(baseDir, webContentName);
			webContent.mkdir();
		}
		File indexHtml = new File(webContent, "index.html");
		if (indexHtml.exists()) {
			System.out.println("index.html already exist.");
		} else {
			indexHtml.createNewFile();
			FileOutputStream fout = new FileOutputStream(indexHtml);
			ResourceUtil.copyStream(indexHtmlStream, fout);
			fout.flush();
			fout.close();
		}

		File webInf = new File(webContent, "WEB-INF");
		webInf.mkdir();

		File lib = new File(webInf, "lib");
		lib.mkdir();
		File classes = new File(webInf, "classes");
		classes.mkdir();

		File webxml = new File(webInf, "web.xml");
		if (webxml.exists()) {
			System.out.println("web.xml already exist.");
		} else {
			webxml.createNewFile();
			FileOutputStream fout = new FileOutputStream(webxml);
			ResourceUtil.copyStream(webXmlStream, fout);
			fout.flush();
			fout.close();
		}
	}

	public static File getEclipseClassPathFile(File projectDir) {
		File classPathFile = new File(projectDir, ".classpath");
		return (classPathFile.exists()) ? classPathFile : null;
	}

	public static boolean modifyEclipseClassPath(File projectDir,
			File classPathFile, String WebContentPath) {
		// eclipse
		try {
			String classesPath = "";
			if (WebContentPath != null) {
				classesPath += WebContentPath + "/";
			}
			classesPath += "WEB-INF/classes";

			Document classPathDocument = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().parse(classPathFile);
			NodeList entryList = classPathDocument.getFirstChild()
					.getChildNodes();
			for (int i = 0; i < entryList.getLength(); i++) {
				Node node = entryList.item(i);
				if (node instanceof Element) {
					Node kindNode = node.getAttributes().getNamedItem("kind");
					String kind = kindNode.getNodeValue();
					if ("output".equals(kind)) {
						Node pathNode = node.getAttributes().getNamedItem(
								"path");
						String oldPath = pathNode.getNodeValue();
						if (!oldPath.equals(classesPath)) {
							pathNode.setNodeValue(classesPath);
							DOMSource source = new DOMSource(classPathDocument);
							FileOutputStream fout = new FileOutputStream(
									classPathFile);
							StreamResult result = new StreamResult(fout);
							TransformerFactory.newInstance().newTransformer()
									.transform(source, result);
							IOUtil.rmdir(new File(projectDir, oldPath));
							return true;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
