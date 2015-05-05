/*******************************************************************************
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 * 
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 * 
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package com.jaspersoft.studio.kpi;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import net.sf.jasperreports.eclipse.ui.util.UIUtils;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;

import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.studio.server.model.server.MServerProfile;
import com.jaspersoft.studio.server.protocol.IConnection;

public class KPIDefinitionPanelController extends Wizard {

	KPIDefinitionPanel kpiDefinitionPanel = null;
	
	public KPIDefinitionPanelController(IConnection client, ResourceDescriptor parentReportUnit, ResourceDescriptor kpiReportUnit, MServerProfile serverProfile)
	{
		kpiDefinitionPanel = new KPIDefinitionPanel();
		kpiDefinitionPanel.setWSClient(client);
		kpiDefinitionPanel.setParentReportUnit(parentReportUnit);
		kpiDefinitionPanel.setKpiReportUnit(kpiReportUnit);
		kpiDefinitionPanel.setMServerProfile(serverProfile);
	}
	
	
	@Override
	public boolean performFinish() {
		try {
			
			final String jrxmlFile = kpiDefinitionPanel.getJrxmlFile();
			final String datasetUri = kpiDefinitionPanel.getDatasourceUri();
			final ResourceDescriptor parentReportUnit = kpiDefinitionPanel.getParentReportUnit();
			final IConnection client = kpiDefinitionPanel.getWSClient();
			
			getContainer().run(true, true, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					monitor.beginTask("Create/Update KPI",
							IProgressMonitor.UNKNOWN);
					try {
						
						// 1. If exists, remove the old KPI...
						
						if (!KPIUtils.deleteReportUnitKPI(client, parentReportUnit.getUriString()))
						{
							UIUtils.showInformation(":-( I'm unable to delete the existing KPI.");
							throw new InvocationTargetException(new Exception());
						}
						
						
						// 2. Deploy the new KPI...
						// Verifichiamo che il file sia buono...
						if ( !(new File(jrxmlFile)).exists() )
						{
							UIUtils.showInformation("The file does not exist");
						}
						
						// 3. Ready to deploy the KPI
						if (!KPIUtils.createReportUnitKPI(client, parentReportUnit.getUriString(), jrxmlFile, datasetUri))
						{
							UIUtils.showInformation(":-( I'm not able to publish this KPI...an error has occurred while publishing it.");
							throw new InvocationTargetException(new Exception());
						}
						
						
					} finally {
						monitor.done();
					}
				}

				
			});
		} catch (InvocationTargetException e) {
			// Default exception... we will show an appropriate message in this case
			// directly from the job...
			//UIUtils.showError(e.getCause());
			return false;
		} catch (InterruptedException e) {
			UIUtils.showError(e);
			return false;
		} 
		
		return true;
		
	}
	
	@Override
	public void addPages() {
		super.addPages();
		addPage(kpiDefinitionPanel);
	}

	
	
	
	
}