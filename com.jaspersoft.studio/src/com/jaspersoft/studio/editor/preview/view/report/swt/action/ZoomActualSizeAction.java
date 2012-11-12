/*
 * Jaspersoft Open Studio - Eclipse-based JasperReports Designer.
 * Copyright (C) 2005 - 2010 Jaspersoft Corporation. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of Jaspersoft Open Studio.
 *
 * Jaspersoft Open Studio is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Jaspersoft Open Studio is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Jaspersoft Open Studio. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.studio.editor.preview.view.report.swt.action;

import java.util.EventObject;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.editor.preview.view.report.swt.IReportViewer;

public class ZoomActualSizeAction extends AReportAction {

	public ZoomActualSizeAction(IReportViewer viewer) {
		super(viewer);

		setText("Actual Size"); //$NON-NLS-1$
		setToolTipText("Zoom actual size"); //$NON-NLS-1$
		setImageDescriptor(JaspersoftStudioPlugin.getImageDescriptor("icons/resources/zoomactual.gif"));
		setDisabledImageDescriptor(JaspersoftStudioPlugin.getImageDescriptor("icons/resources/zoomactuald.gif"));
		update();
	}

	private void update() {
		setChecked(rviewer.getZoomMode() == IReportViewer.ZOOM_MODE_ACTUAL_SIZE);
	}

	@Override
	public void viewerStateChanged(EventObject evt) {
		super.viewerStateChanged(evt);
		update();
	}

	@Override
	public void run() {
		rviewer.setZoomMode(IReportViewer.ZOOM_MODE_ACTUAL_SIZE);
		update();
	}

	public boolean isActionEnabled() {
		return rviewer.canChangeZoom();
	}

}
