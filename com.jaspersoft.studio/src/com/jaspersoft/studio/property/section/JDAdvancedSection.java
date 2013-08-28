/*******************************************************************************
 * Copyright (C) 2010 - 2013 Jaspersoft Corporation. All rights reserved. http://www.jaspersoft.com
 * 
 * Unless you have purchased a commercial license agreement from Jaspersoft, the following license terms apply:
 * 
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Jaspersoft Studio Team - initial API and implementation
 ******************************************************************************/
package com.jaspersoft.studio.property.section;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.runtime.Assert;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPart;

import com.jaspersoft.studio.editor.report.EditorContributor;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.APropertyNode;
import com.jaspersoft.studio.properties.view.AdvancedPropertySection;
import com.jaspersoft.studio.properties.view.TabbedPropertySheetPage;
import com.jaspersoft.studio.property.JRPropertySheetEntry;

public class JDAdvancedSection extends AdvancedPropertySection implements PropertyChangeListener {
	private EditDomain editDomain;
	private APropertyNode element;
	protected TabbedPropertySheetPage atabbedPropertySheetPage;


	public JDAdvancedSection() {
		super();
	}

	@Override
	public void createControls(Composite parent, final TabbedPropertySheetPage atabbedPropertySheetPage) {
		super.createControls(parent, atabbedPropertySheetPage);

		FormData data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, -20);
		data.top = new FormAttachment(0, 0);
		data.bottom = new FormAttachment(100, 0);
		page.getControl().setLayoutData(data);
		UpdatePageContent();
	}
	
	private void UpdatePageContent(){
		if (page != null && element != null && getEditDomain() != null){
			page.selectionChanged(getPart(), new StructuredSelection(element));
			JRPropertySheetEntry propertySheetEntry = new JRPropertySheetEntry(getEditDomain().getCommandStack(), (ANode) element);
			page.setRootEntry(propertySheetEntry);			
		}
	}


	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		setPart(part);
		setSelection(selection);
		if (selection instanceof IStructuredSelection) {
			EditorContributor provider = (EditorContributor) part.getAdapter(EditorContributor.class);
			if (provider != null)
				setEditDomain(provider.getEditDomain());

			Assert.isTrue(selection instanceof IStructuredSelection);
			Object input = ((IStructuredSelection) selection).getFirstElement();
			Assert.isTrue(input instanceof EditPart);
			Object model = ((EditPart) input).getModel();
			Assert.isTrue(model instanceof APropertyNode);
			this.element = (APropertyNode) model;
			UpdatePageContent();
		}
	}

	public EditDomain getEditDomain() {
		return editDomain;
	}

	public void setEditDomain(EditDomain editDomain) {
		this.editDomain = editDomain;
	}

	/**
	 * @see org.eclipse.ui.views.properties.tabbed.view.ITabbedPropertySection#aboutToBeShown()
	 */
	public void aboutToBeShown() {
		if (getElement() != null)
			getElement().getPropertyChangeSupport().addPropertyChangeListener(this);
		if (atabbedPropertySheetPage != null && atabbedPropertySheetPage.getSite() != null) {
			IActionBars actionBars = atabbedPropertySheetPage.getSite().getActionBars();
			if (actionBars != null)
				actionBars.getToolBarManager().removeAll();
			page.makeContributions(actionBars.getMenuManager(), actionBars.getToolBarManager(),
					actionBars.getStatusLineManager());
			actionBars.updateActionBars();
		}
	}

	/**
	 * @see org.eclipse.ui.views.properties.tabbed.view.ITabbedPropertySection#aboutToBeHidden()
	 */
	public void aboutToBeHidden() {
		if (getElement() != null)
			getElement().getPropertyChangeSupport().removePropertyChangeListener(this);
		if (atabbedPropertySheetPage != null && atabbedPropertySheetPage.getSite() != null) {
			IActionBars actionBars = atabbedPropertySheetPage.getSite().getActionBars();
			if (actionBars != null) {
				actionBars.getToolBarManager().removeAll();
				actionBars.updateActionBars();
			}
		}
	}

	/**
	 * Get the element.
	 * 
	 * @return the element.
	 */
	public APropertyNode getElement() {
		return element;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (getElement() != evt.getSource()) {
			getElement().getPropertyChangeSupport().removePropertyChangeListener(this);
			refresh();
			getElement().getPropertyChangeSupport().addPropertyChangeListener(this);
		}
	}

	private boolean isRefreshing = false;

	@Override
	public void refresh() {
		if (isRefreshing)
			return;
		isRefreshing = true;
		if (page != null)
			page.refresh();
		isRefreshing = false;
	}

}
