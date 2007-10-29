package sdloader.internal.resource;

import java.net.MalformedURLException;
import java.net.URL;

public class WarProtocolBuilder {

	public static final String WAR_PROTOCOL = "war:";

	public static final String RESOURCE_SEPARATOR = "!";

	public static URL classPathToUrl(final URL rootUrl, final String originalPath) {
		assertBothNotNull(rootUrl, originalPath);
		final String s = originalPath.replace(ClassTypeResource.WEBINF_PREFIX,
				"");
		final String urlStr = rootUrl.toExternalForm();
		StringBuilder builder = new StringBuilder();
		builder.append(WAR_PROTOCOL);
		builder.append(urlStr);
		builder.append(RESOURCE_SEPARATOR);
		if(!originalPath.startsWith("/")) {
			builder.append("/");
		}
		builder.append(s);
		return createURL(builder);
	}
	
	public static URL resourcePathToUrl(final URL rootUrl, final String originalPath) {
		assertBothNotNull(rootUrl, originalPath);
		final String urlStr = rootUrl.toExternalForm();
		StringBuilder builder = new StringBuilder();
		builder.append(WAR_PROTOCOL);
		builder.append(urlStr);
		builder.append(RESOURCE_SEPARATOR);
		if(!originalPath.startsWith("/")) {
			builder.append("/");
		}
		builder.append(originalPath);
		return createURL(builder);
	}

	public static URL innerjarUrl(final URL rootUrl, final String originalPath) {
		assertBothNotNull(rootUrl, originalPath);
		final String urlStr = rootUrl.toExternalForm();
		StringBuilder builder = new StringBuilder();
		builder.append(JarArchiveTypeResourceImpl.PROTOCOL);
		builder.append(urlStr);
		builder.append(RESOURCE_SEPARATOR);
		if(!originalPath.startsWith("/")) {
			builder.append("/");
		}
		builder.append(originalPath);
		return createURL(builder);
	}

	private static URL createURL(StringBuilder builder) {
		try {
			return new URL(null, builder.toString());
		} catch (MalformedURLException ignore) {
			return null;
		}
	}
	
	private static void assertBothNotNull(final URL rootUrl, final String originalPath) {
		if (rootUrl == null || originalPath == null) {
			throw new IllegalArgumentException();
		}
	}

}
