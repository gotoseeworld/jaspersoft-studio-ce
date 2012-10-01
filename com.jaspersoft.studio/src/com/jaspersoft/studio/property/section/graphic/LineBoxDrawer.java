/*******************************************************************************
 * ---------------------------------------------------------------------
 * Copyright (C) 2005 - 2012 Jaspersoft Corporation. All rights reserved.
 * http://www.jaspersoft.com.
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
 * ---------------------------------------------------------------------
 ******************************************************************************/
package com.jaspersoft.studio.property.section.graphic;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRPen;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.export.draw.BoxDrawer;
import net.sf.jasperreports.engine.export.legacy.BorderOffset;
import net.sf.jasperreports.engine.type.LineStyleEnum;
import net.sf.jasperreports.engine.util.JRPenUtil;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Canvas;

/**
 * Paint the border of an element in the box viewer and make them selectable to easily change the property of one or more 
 * of them
 * @author Orlandin Marco
 *
 */
class LineBoxDrawer extends BoxDrawer {
	
	/**
	 * List of the area where there is a selectable borders
	 */
	private List<Border> clickablesElements = new ArrayList<Border>();
	
	/**
	 * Area where the borders will be painted
	 */
	private Canvas paintingSquare;
	
	/**
	 * The last border selected
	 */
	private Border lastSelected = null;
	
	/**
	 * Location of a border
	 *
	 */
	public static enum Location{LEFT,RIGHT,BOTTOM,TOP};
	
	/**
	 * Stroke used to do the selection effect of a border
	 */
	private static Stroke dashedStroke = null;
	
	/**
	 * The color used for the guidelines
	 */
	private static Color guideColor = null;
	
	/**
	 * Describe the position of a border and store if it is or not selected
	 * @author Orlandin Marco
	 *
	 */
	class Border{
		
		/**
		 * The border selection area
		 */
		private Rectangle rect;
		
		/**
		 * The border position
		 */
		private Location border;
		
		/**
		 * True if selected, otherwise false
		 */
		private boolean selected;

		
		public Border(Rectangle rect, Location border){
			this.rect = rect;
			this.border = border;
			selected = false;
		}
		
		public Location getLocation(){
			return border;
		}
		
		/**
		 * Used to check a point is inside the border area
		 * @param x coordinate x of the point
		 * @param y coordinate y of the point
		 * @return true if the point is inside the border area
		 */
		public boolean isIntersecting(int x, int y){
			return rect.contains(new Point(x, y));
		}
		
		/**
		 * Set the selection area of a border
		 * @param rect a rectangle where the bounds are the selection area
		 */
		public void setRectangle(Rectangle rect){
			this.rect = rect;
		}
		
		public boolean getSelected(){
			return selected;
		}
		
		/**
		 * Complement the selection value of a border
		 * @return True if the border is selected after the change, otherwise false
		 */
		public boolean changeSelected(){
			selected = !selected;
			if (selected)
				lastSelected = this;
			return selected;
		}
		
		/**
		 * Return the selection area of a border
		 * @return a rectangle where the bounds are the selection area
		 */
		public Rectangle getRect(){
			return rect;
		}
		
		/**
		 * Set the selection value
		 * @param newValue the value to assign to the selection
		 */
		public void setSelected(boolean newValue){
			selected = newValue;
		}
		
	}
	
	/**
	 * Return the last border selected
	 * @return A reference to the last selected border, could be null if no border
	 * was selected from the creation time
	 */
	public Border getLastSelected(){
		return lastSelected;
	}
	
	/**
	 * Build the class and add a Listener to the canvas to check the mouse click on the area. When a mouse click
	 * is detected will be checked if it's position is inside the area of a border. In that case the border selection value
	 * will be complemented and the canvas area will be redrawn
	 * @param jasperReportsContext
	 * @param square the canvas painting area
	 */
	public LineBoxDrawer(JasperReportsContext jasperReportsContext, Canvas square) {
		super(jasperReportsContext);
		if (dashedStroke == null){
			//Create the dashed stroke to paint the selection
			dashedStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[]{2,2}, 0f);
			//Create the guideColoros
			guideColor = new Color(192,192,192);
		}
		paintingSquare = square;
		paintingSquare.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {}
			
			@Override
			public void mouseDown(MouseEvent e) {
				for(Border bord : clickablesElements){
					if (bord.isIntersecting(e.x, e.y)){
						bord.changeSelected();
						paintingSquare.redraw();
					}
				}
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {}
		});
	}
	
	/**
	 * Request the repaint of the painting area
	 */
	public void refresh(){
		paintingSquare.redraw();
	}
	
	/**
	 * Check if the left border is selected, if there isn't a left 
	 * border it count as unselected
	 * @return True if the left border is selected, otherwise false
	 */
	public boolean isLeftSelected(){
		Border border = getBorder(Location.LEFT);
		return border != null ? border.getSelected() : false;
	}
	
	/**
	 * Check if the right border is selected, if there isn't a right 
	 * border it count as unselected
	 * @return True if the right border is selected, otherwise false
	 */
	public boolean isRightSelected(){
		Border border = getBorder(Location.RIGHT);
		return border != null ? border.getSelected() : false;
	}
	
	/**
	 * Check if the top border is selected, if there isn't a top 
	 * border it count as unselected
	 * @return True if the top border is selected, otherwise false
	 */
	public boolean isTopSelected(){
		Border border = getBorder(Location.TOP);
		return border != null ? border.getSelected() : false;
	}
	
	/**
	 * Check if the bottom border is selected, if there isn't a bottom 
	 * border it count as unselected
	 * @return True if the bottom border is selected, otherwise false
	 */
	public boolean isBottomSelected(){
		Border border = getBorder(Location.BOTTOM);
		return border != null ? border.getSelected() : false;
	}

	/**
	 * Draw the border and the guide line
	 * @param graphics2d
	 * @param box
	 * @param element
	 */
	public void drawBox(Graphics2D graphics2d, JRLineBox box, JRPrintElement element) {
		drawGuideLines(graphics2d, box.getTopPen(), box.getLeftPen(), box.getBottomPen(), box.getRightPen(), element, 0, 0);
		drawLeftPen(graphics2d, box.getTopPen(), box.getLeftPen(), box.getBottomPen(), element, 0, 0);
		drawTopPen(graphics2d, box.getTopPen(), box.getLeftPen(), box.getRightPen(), element, 0, 0);
		drawBottomPen(graphics2d, box.getLeftPen(), box.getBottomPen(), box.getRightPen(), element, 0, 0);
		drawRightPen(graphics2d, box.getTopPen(), box.getBottomPen(), box.getRightPen(), element, 0, 0);
	}
	
	/**
	 * Set a specific border selected
	 * @param borderPosition the border
	 */
	public void setBorderSelected(Location borderPosition){
		setBorderSelected(borderPosition, true);
	}
	
	/**
	 * Set the selection value of a specific border
	 * @param borderPosition the border
	 * @param selectedValue the new selection value
	 */
	public void setBorderSelected(Location borderPosition, boolean selectedValue){
		Border selected = getBorder(borderPosition);
		if (selected!=null){
			selected.setSelected(selectedValue);
		} else {
			selected = new Border(new Rectangle(), borderPosition);
			selected.setSelected(selectedValue);
			clickablesElements.add(selected);
		}
		lastSelected = selected;
	}
	
	/**
	 * Unselect all the borders
	 */
	public void unselectAll(){
		for(Border bord : clickablesElements)
			bord.setSelected(false);
	}
	
	/**
	 * Return s specific border
	 * @param borderPosition the wanted border
	 * @return the requested border or null if it doesn't exist
	 */
	private Border getBorder(Location borderPosition){
		Iterator<Border> it = clickablesElements.iterator();
		Border result = null;
		while(it.hasNext() && (result == null)){
			Border actBorder = it.next();
			if (actBorder.getLocation().equals(borderPosition)){
				result = actBorder;
			}
		}
		return result;
	}
	
	/**
	 * Update the selection area of a border, if it doesn't exist will be created
	 * @param borderPosition the border to update
	 * @param newSize the new selection area
	 * @return the updated border
	 */
	private Border updateBorder(Location borderPosition, Rectangle newSize){
		Iterator<Border> it = clickablesElements.iterator();
		boolean notFound = true;
		Border actualElement = null;
		while(it.hasNext() && notFound){
			actualElement = it.next();
			if (actualElement.getLocation().equals(borderPosition)){
				notFound = false;
				actualElement.setRectangle(newSize);
			}
		}
		if (notFound){
			actualElement = new Border(newSize, borderPosition);
			clickablesElements.add(actualElement);
		}
		return actualElement;
	}


	/**
	 * Draw the guide lines at every corner of the painting area
	 * @param grx
	 * @param topPen
	 * @param leftPen
	 * @param bottomPen
	 * @param rightPen
	 * @param element
	 * @param offsetX
	 * @param offsetY
	 */
	protected void drawGuideLines(Graphics2D grx, JRPen topPen, JRPen leftPen, JRPen bottomPen, JRPen rightPen, JRPrintElement element, int offsetX, int offsetY){
		Stroke oldStroke = grx.getStroke();
		Color oldColor = grx.getColor();
		grx.setStroke(new BasicStroke(1));
		grx.setColor(guideColor);
		float topOffset = topPen.getLineWidth().floatValue() / 2 - BorderOffset.getOffset(topPen);
		int width = element.getWidth();
		int height = element.getHeight();
		
		float translationLeftUpperX = element.getX() + offsetX + BorderOffset.getOffset(leftPen);
		float translationLeftUpperY= element.getY() + offsetY - topOffset;
		
		float translationRightUpperX = element.getX() + offsetX + width - BorderOffset.getOffset(rightPen);
		float translationRightUpperY=  element.getY() + offsetY - topOffset;
		
		float translationLeftBottomY=  element.getY() + offsetY + height - BorderOffset.getOffset(bottomPen);
		
		//Left Upper corner
		grx.drawLine(0, Math.round(translationLeftUpperY), Math.round(translationLeftUpperX), Math.round(translationLeftUpperY));
		grx.drawLine(Math.round(translationLeftUpperX),  0, Math.round(translationLeftUpperX),  Math.round(translationRightUpperY));
		
		//Right upper corner
		grx.drawLine(Math.round(translationRightUpperX),  Math.round(translationRightUpperY), paintingSquare.getBounds().width,  Math.round(translationRightUpperY));
		grx.drawLine(Math.round(translationRightUpperX),  0,Math.round(translationRightUpperX),  Math.round(translationRightUpperY));
		
		//Left bottom corner
		grx.drawLine(0, Math.round(translationLeftBottomY), Math.round(translationLeftUpperX), Math.round(translationLeftBottomY));
		grx.drawLine(Math.round(translationLeftUpperX), Math.round(translationLeftBottomY), Math.round(translationLeftUpperX), paintingSquare.getBounds().height);
		
		//Right bottom corner
		grx.drawLine(Math.round(translationRightUpperX), Math.round(translationLeftBottomY), paintingSquare.getBounds().width, Math.round(translationLeftBottomY));
		grx.drawLine(Math.round(translationRightUpperX), Math.round(translationLeftBottomY), Math.round(translationRightUpperX), paintingSquare.getBounds().height);
		
		grx.setStroke(oldStroke);
		grx.setColor(oldColor);
	}
	
	/**
	 * Paint the left border
	 */
	protected void drawLeftPen(Graphics2D grx, JRPen topPen, JRPen leftPen, JRPen bottomPen, JRPrintElement element, 
																int offsetX, int offsetY)
	{
		Stroke leftStroke = JRPenUtil.getStroke(leftPen, BasicStroke.CAP_BUTT);
		int height = element.getHeight();
		float topOffset = topPen.getLineWidth().floatValue() / 2 - BorderOffset.getOffset(topPen);
		float bottomOffset = bottomPen.getLineWidth().floatValue() / 2 - BorderOffset.getOffset(bottomPen);
		float leftPenWidth = 7.0f;
		float selectionWidth = 7.0f;
		LineStyleEnum lineStyle;
		if (leftStroke != null && height > 0)
		{
			leftPenWidth = leftPen.getLineWidth().floatValue();
			//If the line is to thin a bigger value will be used for the slection
			if (leftPenWidth>7)
				selectionWidth = leftPenWidth;
			lineStyle = leftPen.getLineStyleValue();
			grx.setStroke(leftStroke);
			grx.setColor(leftPen.getLineColor());
		} else {
			//If the border is not present will be used a white transparent border as placeholder for the  sleection
			grx.setStroke(new BasicStroke(7));
			grx.setColor(new Color(255,255,255,0));
			lineStyle = LineStyleEnum.SOLID;
		}
		AffineTransform oldTx = grx.getTransform();
		if (lineStyle == LineStyleEnum.DOUBLE)
		{
			float translationX = element.getX() + offsetX - leftPenWidth / 3;
			float translationY= element.getY() + offsetY - topOffset;
			grx.translate(translationX, translationY);
			float scaleX = 1;
			float scaleY = (height + (topOffset + bottomOffset))/ height; 
			grx.scale(scaleX, scaleY);
			grx.drawLine(0, 0, 0, height);
			grx.setTransform(oldTx);
			Rectangle newRect = new Rectangle(Math.round((translationX-selectionWidth/2)*scaleX),Math.round(translationY*scaleY), Math.round(selectionWidth+selectionWidth/3),height);
			Border updatedBorder = updateBorder(Location.LEFT, newRect);
			if (updatedBorder.getSelected()){
				Color oldColor = grx.getColor();
				Stroke oldStroke = grx.getStroke();
				grx.setColor(Color.black);
				grx.setStroke(dashedStroke);
				grx.drawRect(newRect.x-1, newRect.y-1, newRect.width+5, newRect.height+2);
				grx.setStroke(oldStroke);
				grx.setColor(oldColor);
			}
			grx.translate(element.getX() + offsetX + leftPenWidth / 3, element.getY() + offsetY + topOffset / 3);
			if(height > (topOffset + bottomOffset) / 3)
			{
				grx.scale(1, (height - (topOffset + bottomOffset) / 3)/ height);
			}
			grx.drawLine(0,0,0,	height);
			grx.setTransform(oldTx);

		} else {
			float translationX = element.getX() + offsetX + BorderOffset.getOffset(leftPen);
			float translationY= element.getY() + offsetY - topOffset;
			grx.translate(translationX, translationY);
			float scaleX = 1;
			float scaleY = (height + topOffset + bottomOffset)/ height; 
			grx.scale(scaleX, scaleY);
			grx.drawLine(0,	0, 0,	height);
			grx.setTransform(oldTx);
			Rectangle newRect = new Rectangle(Math.round((translationX-selectionWidth/2)*scaleX),Math.round(translationY*scaleY), Math.round(selectionWidth),height);
			Border updatedBorder = updateBorder(Location.LEFT, newRect);
			if (updatedBorder.getSelected()){
				Stroke oldStroke = grx.getStroke();
				Color oldColor = grx.getColor();
				grx.setColor(Color.black);
				grx.setStroke(dashedStroke);
				grx.drawRect(newRect.x-3, newRect.y-2, newRect.width+6, newRect.height+4);
				grx.setStroke(oldStroke);
				grx.setColor(oldColor);
			}
		}
	}
	
	/**
	 * Paint the right border
	 */
	protected void drawRightPen(Graphics2D grx, JRPen topPen, JRPen bottomPen, JRPen rightPen, JRPrintElement element, int offsetX, int offsetY)
		{
			Stroke rightStroke = JRPenUtil.getStroke(rightPen, BasicStroke.CAP_BUTT);
			int height = element.getHeight();
			int width = element.getWidth();
			float topOffset = topPen.getLineWidth().floatValue() / 2 - BorderOffset.getOffset(topPen);
			float bottomOffset = bottomPen.getLineWidth().floatValue() / 2 - BorderOffset.getOffset(bottomPen);
			float rightPenWidth = 7.0f;
			float selectionWidth = 7.0f;
			LineStyleEnum lineStyle;
			if (rightStroke != null && height > 0)
			{
				rightPenWidth = rightPen.getLineWidth().floatValue();
				if (rightPenWidth>7)
					selectionWidth = rightPenWidth;
				lineStyle = rightPen.getLineStyleValue();
				grx.setStroke(rightStroke);
				grx.setColor(rightPen.getLineColor());
			} else {
				grx.setStroke(new BasicStroke(7));
				grx.setColor(new Color(255,255,255,0));
				lineStyle = LineStyleEnum.SOLID;
			}
			AffineTransform oldTx = grx.getTransform();
			if (lineStyle == LineStyleEnum.DOUBLE)
			{
				float translationX = element.getX() + offsetX + width + rightPenWidth / 3;
				float translationY=  element.getY() + offsetY - topOffset;
				grx.translate(translationX, translationY);
				float scaleX = 1;
				float scaleY = (height + topOffset + bottomOffset)/ height; 
				grx.scale(scaleX, scaleY);
				grx.drawLine(0, 0, 0, height);
				grx.setTransform(oldTx);
				Rectangle newRect = new Rectangle(Math.round((translationX-selectionWidth)*scaleX),Math.round(translationY*scaleY), Math.round(selectionWidth+selectionWidth/3),height);
				Border updatedBorder = updateBorder(Location.RIGHT, newRect);
				if (updatedBorder.getSelected()){
					Color oldColor = grx.getColor();
					Stroke oldStroke = grx.getStroke();
					grx.setColor(Color.black);
					grx.setStroke(dashedStroke);
					grx.drawRect(newRect.x-1, newRect.y-1, newRect.width+5, newRect.height+2);
					grx.setStroke(oldStroke);
					grx.setColor(oldColor);
				}
				grx.translate(element.getX() + offsetX + width - rightPenWidth / 3, element.getY() + offsetY + topOffset / 3);
				if(height > (topOffset + bottomOffset) / 3)
				{
					grx.scale(1, (height - (topOffset + bottomOffset) / 3)/ height);
				}
				grx.drawLine(0, 0, 0, height);
				grx.setTransform(oldTx);
			}
			else
			{
				float translationX = element.getX() + offsetX + width - BorderOffset.getOffset(rightPen);
				float translationY=  element.getY() + offsetY - topOffset;
				grx.translate(translationX, translationY);
				float scaleX = 1;
				float scaleY = (height + topOffset + bottomOffset)/ height; 
				grx.scale(scaleX, scaleY);
				grx.drawLine(0,	0, 0,	height);
				Rectangle newRect = new Rectangle(Math.round((translationX-selectionWidth/2)*scaleX),Math.round(translationY*scaleY), Math.round(selectionWidth),height);
				Border updatedBorder = updateBorder(Location.RIGHT, newRect);
				grx.setTransform(oldTx);
				if (updatedBorder.getSelected()){
					Stroke oldStroke = grx.getStroke();
					Color oldColor = grx.getColor();
					grx.setColor(Color.black);
					grx.setStroke(dashedStroke);
					grx.drawRect(newRect.x-3, newRect.y-2, newRect.width+6, newRect.height+4);
					grx.setStroke(oldStroke);
					grx.setColor(oldColor);
				}
			}
		}
	
	/**
	 * Paint the top border
	 */
	protected void drawTopPen(Graphics2D grx, JRPen topPen, JRPen leftPen, JRPen rightPen, JRPrintElement element, int offsetX, int offsetY)
		{
			Stroke topStroke = JRPenUtil.getStroke(topPen, BasicStroke.CAP_BUTT);
			int width = element.getWidth();
			float leftOffset = leftPen.getLineWidth().floatValue() / 2 - BorderOffset.getOffset(leftPen);
			float rightOffset = rightPen.getLineWidth().floatValue() / 2 - BorderOffset.getOffset(rightPen);
			float topPenWidth = 7.0f;
			float selectionWidth = 7.0f;
			LineStyleEnum lineStyle;
			if (topStroke != null && width > 0)
			{
				topPenWidth = topPen.getLineWidth().floatValue();
				if (topPenWidth>7)
					selectionWidth = topPenWidth;
				lineStyle = topPen.getLineStyleValue();
				grx.setStroke(topStroke);
				grx.setColor(topPen.getLineColor());
			} else {
				grx.setStroke(new BasicStroke(7));
				grx.setColor(new Color(255,255,255,0));
				lineStyle = LineStyleEnum.SOLID;
			}
			AffineTransform oldTx = grx.getTransform();
			if (lineStyle == LineStyleEnum.DOUBLE)
			{
				float translationX = element.getX() + offsetX - leftOffset;
				float translationY=  element.getY() + offsetY - topPenWidth / 3;
				grx.translate(translationX, translationY);
				float scaleX = ((width + leftOffset + rightOffset) / width);
				float scaleY = 1; 
				grx.scale(scaleX, scaleY);
				grx.drawLine(0, 0, width, 0);
				grx.setTransform(oldTx);
				Rectangle newRect = new Rectangle(Math.round(translationX*scaleX),Math.round((translationY-selectionWidth/3)*scaleY), width, Math.round(selectionWidth+selectionWidth/3));
				Border updatedBorder = updateBorder(Location.TOP, newRect);
				if (updatedBorder.getSelected()){
					Color oldColor = grx.getColor();
					Stroke oldStroke = grx.getStroke();
					grx.setColor(Color.black);
					grx.setStroke(dashedStroke);
					grx.drawRect(newRect.x-1, newRect.y-1, newRect.width+5, newRect.height+2);
					grx.setStroke(oldStroke);
					grx.setColor(oldColor);
				}
				grx.translate(element.getX() + offsetX + leftOffset / 3, element.getY() + offsetY + topPenWidth / 3);
				if(width > (leftOffset + rightOffset) / 3)
				{
					grx.scale((width - (leftOffset + rightOffset) / 3)/ width,1);
				}
				grx.drawLine(0,	0, width,	0);
				grx.setTransform(oldTx);
			}
			else
			{
				float translationX = element.getX() + offsetX - leftOffset;
				float translationY=  element.getY() + offsetY + BorderOffset.getOffset(topPen);
				grx.translate(translationX,translationY);
				float scaleX = (width + leftOffset + rightOffset)/ width;
				float scaleY = 1; 
				grx.scale(scaleX,scaleY);
				grx.drawLine(0, 0, width, 0);
				Rectangle newRect = new Rectangle(Math.round(translationX*scaleX),Math.round((translationY-selectionWidth/2)*scaleY), width, Math.round(selectionWidth));
				Border updatedBorder = updateBorder(Location.TOP, newRect);
				grx.setTransform(oldTx);
				if (updatedBorder.getSelected()){
					Stroke oldStroke = grx.getStroke();
					Color oldColor = grx.getColor();
					grx.setColor(Color.black);
					grx.setStroke(dashedStroke);
					grx.drawRect(newRect.x-3, newRect.y-2, newRect.width+6, newRect.height+4);
					grx.setStroke(oldStroke);
					grx.setColor(oldColor);
				}
			}
		}

	/**
	 * Paint the bottom border
	 */
	protected void drawBottomPen(Graphics2D grx, JRPen leftPen, JRPen bottomPen, JRPen rightPen, JRPrintElement element, int offsetX, int offsetY)
		{
			Stroke bottomStroke = JRPenUtil.getStroke(bottomPen, BasicStroke.CAP_BUTT);
			int width = element.getWidth();
			int height = element.getHeight();
			float leftOffset = leftPen.getLineWidth().floatValue() / 2 - BorderOffset.getOffset(leftPen);
			float rightOffset = rightPen.getLineWidth().floatValue() / 2 - BorderOffset.getOffset(rightPen);
			float bottomPenWidth = 7.0f;
			float selectionWidth = 7.0f;
			LineStyleEnum lineStyle;
			if (bottomStroke != null && width > 0)
			{
				bottomPenWidth = bottomPen.getLineWidth().floatValue();
				if (bottomPenWidth>7)
					selectionWidth = bottomPenWidth;
				lineStyle = bottomPen.getLineStyleValue();
				grx.setStroke(bottomStroke);
				grx.setColor(bottomPen.getLineColor());
			} else {
				grx.setStroke(new BasicStroke(7));
				grx.setColor(new Color(255,255,255,0));
				lineStyle = LineStyleEnum.SOLID;
			}
			AffineTransform oldTx = grx.getTransform();
			if (lineStyle == LineStyleEnum.DOUBLE)
			{
				float translationX = element.getX() + offsetX - leftOffset;
				float translationY=  element.getY() + offsetY + height + bottomPenWidth / 3;
				grx.translate(translationX, translationY);
				float scaleX = (width + leftOffset + rightOffset)/ width;
				float scaleY = 1; 
				grx.scale(scaleX, scaleY);
				grx.drawLine(0, 0, width, 0);
				Rectangle newRect = new Rectangle(Math.round(translationX*scaleX),Math.round((translationY-selectionWidth)*scaleY), width, Math.round(selectionWidth+selectionWidth/3));
				Border updatedBorder = updateBorder(Location.BOTTOM, newRect);
				grx.setTransform(oldTx);
				if (updatedBorder.getSelected()){
					Color oldColor = grx.getColor();
					Stroke oldStroke = grx.getStroke();
					grx.setColor(Color.black);
					grx.setStroke(dashedStroke);
					grx.drawRect(newRect.x-1, newRect.y-1, newRect.width+5, newRect.height+2);
					grx.setStroke(oldStroke);
					grx.setColor(oldColor);
				}
				grx.translate(element.getX() + offsetX + leftOffset / 3, element.getY() + offsetY + height - bottomPenWidth / 3);
				if(width > (leftOffset + rightOffset) / 3)
				{
					grx.scale((width - (leftOffset + rightOffset) / 3)/ width,1);
				}
				grx.drawLine(0,	0, width, 0);
				grx.setTransform(oldTx);
			}
			else
			{
				float translationX = element.getX() + offsetX - leftOffset;
				float translationY=  element.getY() + offsetY + height - BorderOffset.getOffset(bottomPen);
				grx.translate(translationX, translationY);
				float scaleX = (width + leftOffset + rightOffset)/ width;
				float scaleY = 1; 
				grx.scale(scaleX, scaleY);
				grx.drawLine(0, 0, width, 0);
				Rectangle newRect = new Rectangle(Math.round(translationX*scaleX),Math.round((translationY-selectionWidth/2)*scaleY), width, Math.round(selectionWidth));
				Border updatedBorder = updateBorder(Location.BOTTOM, newRect);
				grx.setTransform(oldTx);
				if (updatedBorder.getSelected()){
					Stroke oldStroke = grx.getStroke();
					Color oldColor = grx.getColor();
					grx.setColor(Color.black);
					grx.setStroke(dashedStroke);
					grx.drawRect(newRect.x-3, newRect.y-2, newRect.width+6, newRect.height+4);
					grx.setStroke(oldStroke);
					grx.setColor(oldColor);
				}
			}
		}
	
}
