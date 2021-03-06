/*******************************************************************************
 * Copyright (C) 2010 - 2016. TIBCO Software Inc. 
 * All Rights Reserved. Confidential & Proprietary.
 ******************************************************************************/
package com.jaspersoft.studio.server.wizard.permission;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class PermissionDialog extends WizardDialog {

	List<SelectionListener> listeners = new ArrayList<SelectionListener>();
	Button applyButton = null;

	public PermissionDialog(Shell parentShell, IWizard newWizard) {
		super(parentShell, newWizard);
	}

	public void addApplyListener(SelectionListener listener) {
		if (!listeners.contains(listener))
			listeners.add(listener);
	}

	public void removeApplyListener(SelectionListener listener) {
		listeners.remove(listener);
	}

	private void fireApplyPressed(SelectionEvent e) {
		for (SelectionListener listener : listeners)
			listener.widgetSelected(e);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		((GridLayout) parent.getLayout()).numColumns = 1;
		applyButton = new Button(parent, SWT.NONE);
		applyButton.setFont(parent.getFont());
		applyButton.setText(com.jaspersoft.studio.server.messages.Messages.PermissionDialog_0);
		setButtonLayoutData(applyButton);
		applyButton.setEnabled(false);
		applyButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				fireApplyPressed(event);
			}

		});
		super.createButtonsForButtonBar(parent);
	}

	@Override
	public void updateButtons() {
		super.updateButtons();
		applyButton.setEnabled(true);
	}
	// public void setApplyButtonEnabled(boolean b) {
	//
	// applyButton.setEnabled(b);
	// }
}
