/*
 * Jaspersoft Open Studio - Eclipse-based JasperReports Designer. Copyright (C) 2005 - 2010 Jaspersoft Corporation. All
 * rights reserved. http://www.jaspersoft.com
 * 
 * Unless you have purchased a commercial license agreement from Jaspersoft, the following license terms apply:
 * 
 * This program is part of Jaspersoft Open Studio.
 * 
 * Jaspersoft Open Studio is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * Jaspersoft Open Studio is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with Jaspersoft Open Studio. If not,
 * see <http://www.gnu.org/licenses/>.
 */
/*
 * Jaspersoft Open Studio - Eclipse-based JasperReports Designer. Copyright (C) 2005 - 2010 Jaspersoft Corporation. All
 * rights reserved. http://www.jaspersoft.com
 * 
 * Unless you have purchased a commercial license agreement from Jaspersoft, the following license terms apply:
 * 
 * This program is part of iReport.
 * 
 * iReport is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * iReport is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with iReport. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.studio.property.dataset.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.property.dataset.TLabelProvider;
import com.jaspersoft.studio.swt.widgets.table.ListContentProvider;
import com.jaspersoft.studio.swt.widgets.table.ListOrderButtons;
import com.jaspersoft.studio.swt.widgets.table.MoveT2TButtons;

public class WizardFieldsPage extends WizardPage {
	private List<Object> inFields;
	private List<Object> outFields;

	protected Table rightTable;
	private Table leftTable;
	protected TableViewer rightTView;
	private TableViewer leftTView;

	public void setFields(List<Object> inFields) {
		if (inFields == null || this.outFields == null)
			fillTables(inFields, new ArrayList<Object>());
		else {
			// add fields if not exists inside
			for (Object f : inFields) {
				if (this.inFields.contains(f) || this.outFields.contains(f))
					continue;
				this.inFields.add(f);
			}
			List<Object> tmp = new ArrayList<Object>();
			// remove fields from in
			for (Object f : this.inFields) {
				if (!inFields.contains(f))
					tmp.add(f);
			}
			this.inFields.removeAll(tmp);

			tmp.clear();
			// remove fields from out
			for (Object f : this.outFields) {
				if (!outFields.contains(f))
					tmp.add(f);
			}
			this.outFields.removeAll(tmp);

			rightTView.refresh();
			leftTView.refresh();
		}
	}

	public List<Object> getFields() {
		return outFields;
	}

	public List<Object> getInFields() {
		return inFields;
	}

	public WizardFieldsPage(String key) {
		super(key); //$NON-NLS-1$
		setTitle(Messages.common_fields);
		setDescription(Messages.WizardFieldsPage_description);
	}

	public WizardFieldsPage() {
		this("tablepage"); //$NON-NLS-1$
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		setControl(composite);

		leftTable = new Table(composite, SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_VERTICAL);
		gd.widthHint = 300;
		leftTable.setLayoutData(gd);
		leftTable.setHeaderVisible(true);

		TableColumn[] col = new TableColumn[1];
		col[0] = new TableColumn(leftTable, SWT.NONE);
		col[0].setText(Messages.WizardFieldsPage_dataset_fields);
		col[0].pack();

		TableLayout tlayout = new TableLayout();
		tlayout.addColumnData(new ColumnWeightData(100, false));
		leftTable.setLayout(tlayout);

		leftTView = new TableViewer(leftTable);
		leftTView.setContentProvider(new ListContentProvider());
		setLabelProvider(leftTView);

		Composite bGroup = new Composite(composite, SWT.NONE);
		bGroup.setLayout(new GridLayout(1, false));
		bGroup.setLayoutData(new GridData(GridData.FILL_VERTICAL));

		// -----------------------------------
		rightTable = new Table(composite, SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
		gd = new GridData(GridData.FILL_BOTH);
		gd.minimumWidth = 300;
		rightTable.setLayoutData(gd);
		rightTable.setHeaderVisible(true);

		createColumns();

		rightTView = new TableViewer(rightTable);
		rightTView.setContentProvider(new ListContentProvider());
		setLabelProvider(rightTView);
		attachCellEditors(rightTView, rightTable);

		createOrderButtons(composite);

		new MoveT2TButtons().createButtons(bGroup, leftTView, rightTView);
	}

	protected void attachCellEditors(TableViewer viewer, Composite parent) {

	}

	protected void setLabelProvider(TableViewer tableViewer) {
		tableViewer.setLabelProvider(new TLabelProvider());
	}

	protected void rightTView(TableViewer tableViewer) {
		tableViewer.setLabelProvider(new TLabelProvider());
	}

	protected void createColumns() {
		TableColumn[] col;
		TableLayout tlayout;
		col = new TableColumn[1];
		col[0] = new TableColumn(rightTable, SWT.NONE);
		col[0].setText(Messages.common_fields);
		col[0].pack();

		tlayout = new TableLayout();
		tlayout.addColumnData(new ColumnWeightData(100, false));
		rightTable.setLayout(tlayout);
	}

	private void createOrderButtons(Composite composite) {
		Composite bGroup = new Composite(composite, SWT.NONE);
		bGroup.setLayout(new GridLayout(1, false));
		bGroup.setLayoutData(new GridData(GridData.FILL_VERTICAL));

		new ListOrderButtons().createOrderButtons(bGroup, rightTView);
	}

	private void fillTables(List<Object> inlist, List<Object> outlist) {
		this.inFields = inlist;
		this.outFields = outlist;
		leftTView.setInput(inFields);
		rightTView.setInput(outFields);
	}

}
