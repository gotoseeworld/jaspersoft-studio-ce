/*******************************************************************************
 * Copyright (C) 2010 - 2013 Jaspersoft Corporation. All rights reserved.
 * http://www.jaspersoft.com
 * 
 * Unless you have purchased a commercial license agreement from Jaspersoft, 
 * the following license terms apply:
 * 
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Jaspersoft Studio Team - initial API and implementation
 ******************************************************************************/
package com.jaspersoft.studio.server.editor;

import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.design.JasperDesign;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.EditorPart;

import com.jaspersoft.studio.editor.IEditorContributor;
import com.jaspersoft.studio.editor.JrxmlEditor;
import com.jaspersoft.studio.server.JSFileResolver;
import com.jaspersoft.studio.server.export.JrxmlExporter;
import com.jaspersoft.studio.server.publish.action.JrxmlPublishAction;
import com.jaspersoft.studio.server.publish.wizard.SaveConfirmationDialog;
import com.jaspersoft.studio.utils.AContributorAction;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

public class JRSEditorContributor implements IEditorContributor {

	public void onLoad(final JasperDesign jd, final EditorPart editor) {
		if (!(editor instanceof JrxmlEditor))
			return;
		String prop = jd.getProperty(JrxmlExporter.PROP_SERVERURL);
		if (prop == null)
			return;

		Job job = new Job("Initialize JRS File Resolver") {
			protected IStatus run(IProgressMonitor monitor) {
				((JrxmlEditor) editor).addFileResolver(new JSFileResolver(jd, monitor));
				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.LONG);
		job.setSystem(true);
		job.schedule();

		// prop = jd.getProperty("com.jaspersoft.ji.adhoc");
		// if (prop != null && prop.equals("1")) {
		// UIUtils.showWarning("You have selected to edit an Ad Hoc report.\n"
		// + "If you continue, the report will lose its sorting and grouping.\n"
		// +
		// "Furthermore, any changes you make in Jaspersoft Studio will be lost\n"
		// + "next Time you edit it via the Ad Hoc report editor.\n"
		// + "Continue anyway?");
		// }

	}

	public static final String KEY_PUBLISH2JSS = "PUBLISH2JSS";
	public static final String KEY_PUBLISH2JSS_SILENT = "PUBLISH2JSS.SILENT";

	public void onSave(JasperReportsContext jrConfig, IProgressMonitor monitor) {
		JasperReportsConfiguration jConfig = (JasperReportsConfiguration) jrConfig;
		JasperDesign jd = jConfig.getJasperDesign();

		String prop = jd.getProperty(JrxmlExporter.PROP_SERVERURL);
		if (prop == null)
			return;

		boolean run = jConfig.get(KEY_PUBLISH2JSS, false);
		boolean allways = jConfig.get(KEY_PUBLISH2JSS_SILENT, false);
		if (!allways) {
			SaveConfirmationDialog dialog = new SaveConfirmationDialog(Display.getDefault().getActiveShell());
			run = (dialog.open() == Dialog.OK);
			jConfig.put(KEY_PUBLISH2JSS, run);
			jConfig.put(KEY_PUBLISH2JSS_SILENT, dialog.getAllways());
		}
		if (run)
			getAction(monitor, jConfig).run();
	}

	protected static JrxmlPublishAction getAction(IProgressMonitor monitor, JasperReportsConfiguration jrConfig) {
		JrxmlPublishAction publishAction = new JrxmlPublishAction(2, monitor);
		publishAction.setJrConfig(jrConfig);
		return publishAction;
	}

	public void onRun() {
		// TODO Auto-generated method stub

	}

	public AContributorAction[] getActions() {
		return new AContributorAction[] { new JrxmlPublishAction() };
	}

	@Override
	public String getTitleToolTip(JasperReportsContext jrConfig, String toolTip) {
		String s = toolTip;
		JasperDesign jd = ((JasperReportsConfiguration) jrConfig).getJasperDesign();
		if (jd != null) {
			String p = jd.getProperty(JrxmlExporter.PROP_SERVERURL);
			if (p != null)
				s += "\nServer: " + p;
			p = jd.getProperty(JrxmlExporter.PROP_REPORTUNIT);
			if (p != null)
				s += "\nReport Unit: " + p;
			p = jd.getProperty(JrxmlExporter.PROP_REPORTRESOURCE);
			if (p != null)
				s += "\nResource name: " + p;
		}
		return s;
	}

}
