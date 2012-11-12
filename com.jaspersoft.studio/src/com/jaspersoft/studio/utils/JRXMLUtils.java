package com.jaspersoft.studio.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

import com.jaspersoft.studio.compatibility.JRXmlWriterHelper;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

/**
 * This class maintains a list of utilities methods to manage JRXML files and streams.
 * 
 * @author Massimo Rabbi
 * 
 */
public class JRXMLUtils {

	public static final String JASPER_FILE_EXTENSION = "jasper";
	public static final String JRXML_FILE_EXTENSION = "jrxml";

	/**
	 * Gets a JRXML input stream from an existing one, that can be either a .jasper file or .jrxml.
	 * <p>
	 * Others file extension are not meant to return a valid JRXML input stream.
	 * 
	 * @param in
	 *          the original input stream
	 * @param fileExtension
	 *          the file extension
	 * @param encoding
	 *          the file encoding
	 * @param version
	 *          the JR version
	 * @return a valid JRXML input stream, <code>null</code> if not possible
	 * @throws JRException
	 */
	public static InputStream getJRXMLInputStream(JasperReportsConfiguration jrContext, InputStream in,
			String fileExtension, String encoding, String version) throws JRException {
		if (fileExtension.equals(JASPER_FILE_EXTENSION)) {
			// get JRXML from the .jasper
			JasperReport report = (JasperReport) JRLoader.loadObject(in);
			String str;
			try {
				str = JRXmlWriterHelper.writeReport(jrContext, report, JRXmlWriterHelper.fixencoding(encoding), version);
				return new ByteArrayInputStream(str.getBytes());
			} catch (Exception e) {
				UIUtils.showError("Something goes wrong while trying to create a JRXML input stream from a .jasper one.", e);
			}
		} else if (fileExtension.equals(JRXML_FILE_EXTENSION)) {
			// the original one is already ok
			return in;
		}
		return null;
	}

}
