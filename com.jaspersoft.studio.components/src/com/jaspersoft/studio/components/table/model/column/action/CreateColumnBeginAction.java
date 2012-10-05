/*
 * JasperReports - Free Java Reporting Library. Copyright (C) 2001 - 2011 Jaspersoft Corporation. All rights reserved.
 * http://www.jaspersoft.com
 * 
 * Unless you have purchased a commercial license agreement from Jaspersoft, the following license terms apply:
 * 
 * This program is part of JasperReports.
 * 
 * JasperReports is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * JasperReports is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with JasperReports. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.studio.components.table.model.column.action;

import java.util.List;
import java.util.Map;

import org.eclipse.ui.IWorkbenchPart;

import com.jaspersoft.studio.components.Activator;
import com.jaspersoft.studio.components.table.model.column.MColumn;
import com.jaspersoft.studio.editor.gef.util.CreateRequestUtil;
import com.jaspersoft.studio.editor.outline.actions.ACreateAction;
import com.jaspersoft.studio.editor.palette.JDPaletteCreationFactory;

/*
 * The Class CreateGroupAction.
 */
public class CreateColumnBeginAction extends ACreateAction {

	/** The Constant ID. */
	public static final String ID = "create_table_column_begin"; //$NON-NLS-1$

	/**
	 * Constructs a <code>CreateAction</code> using the specified part.
	 * 
	 * @param part
	 *            The part for this action
	 */
	public CreateColumnBeginAction(IWorkbenchPart part) {
		super(part);
		setCreationFactory(new JDPaletteCreationFactory(MColumn.class));
	}

	/**
	 * Initializes this action's text and images.
	 */
	@Override
	protected void init() {
		super.init();
		setText(Messages.CreateColumnBeginAction_title);
		setToolTipText(Messages.CreateColumnBeginAction_desc);
		setId(CreateColumnBeginAction.ID);
		setImageDescriptor(Activator
				.getImageDescriptor("icons/table-insert-column.png")); //$NON-NLS-1$
		setDisabledImageDescriptor(Activator
				.getImageDescriptor("icons/table-insert-column.png")); //$NON-NLS-1$
		setEnabled(false);
	}

	@Override
	protected boolean setExtendedData(Map<Object, Object> map, List<?> objects) {
		super.setExtendedData(map, objects);
		map.put(CreateRequestUtil.NEWINDEX, 0);
		return true;
	}
}
