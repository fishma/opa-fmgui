/**
 * INTEL CONFIDENTIAL
 * Copyright (c) 2014 Intel Corporation All Rights Reserved.
 * The source code contained or described herein and all documents related to the source code ("Material")
 * are owned by Intel Corporation or its suppliers or licensors. Title to the Material remains with Intel
 * Corporation or its suppliers and licensors. The Material contains trade secrets and proprietary and
 * confidential information of Intel or its suppliers and licensors. The Material is protected by
 * worldwide copyright and trade secret laws and treaty provisions. No part of the Material may be used,
 * copied, reproduced, modified, published, uploaded, posted, transmitted, distributed, or disclosed in
 * any way without Intel's prior express written permission. No license under any patent, copyright,
 * trade secret or other intellectual property right is granted to or conferred upon you by disclosure
 * or delivery of the Materials, either expressly, by implication, inducement, estoppel or otherwise.
 * Any license under such intellectual property rights must be express and approved by Intel in writing.
 */

/*******************************************************************************
 *                       I N T E L   C O R P O R A T I O N
 *	
 *  Functional Group: Fabric Viewer Application
 *
 *  File Name: IntelButtonUI.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.3.2.1  2015/05/06 19:40:33  jijunwan
 *  Archive Log:    L&F improvement on drawing focus border
 *  Archive Log:
 *  Archive Log:    Revision 1.4  2015/04/30 20:56:53  jijunwan
 *  Archive Log:    added drawing focus border when it gets focus
 *  Archive Log:
 *  Archive Log:    Revision 1.3  2015/04/01 21:54:41  jijunwan
 *  Archive Log:    changed default disabledBackgroundColor to INTEL_BORDER_GRAY
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2014/09/05 21:53:48  jijunwan
 *  Archive Log:    improved IntelButtonUI to support L&F for disabled button
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2014/05/21 19:54:52  jijunwan
 *  Archive Log:    Intel Style button
 *  Archive Log:
 *
 *  Overview: 
 *
 *  @author: jijunwan
 *
 ******************************************************************************/

package com.intel.stl.ui.common.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;

import sun.swing.SwingUtilities2;

import com.intel.stl.ui.common.UIConstants;

public class IntelButtonUI extends BasicButtonUI {
    protected int dashedRectGap = 1;

    private Color pressedColor = UIConstants.INTEL_MEDIUM_DARK_BLUE;

    private Color hoverColor = UIConstants.INTEL_MEDIUM_BLUE;

    private Color focusColor = UIConstants.INTEL_WHITE;

    private Color disabledBackgroundColor = UIConstants.INTEL_BORDER_GRAY;

    private Color disabledForegroundColor = UIConstants.INTEL_GRAY;

    /**
     * Description:
     * 
     * @param pressedColor
     */
    public IntelButtonUI(Color hoverColor, Color pressedColor) {
        super();
        this.hoverColor = hoverColor;
        this.pressedColor = pressedColor;
    }

    /**
     * Description:
     * 
     */
    public IntelButtonUI() {
        super();
    }

    /**
     * @return the pressedColor
     */
    public Color getPressedColor() {
        return pressedColor;
    }

    /**
     * @param pressedColor
     *            the pressedColor to set
     */
    public void setPressedColor(Color pressedColor) {
        this.pressedColor = pressedColor;
    }

    /**
     * @return the hoverColor
     */
    public Color getHoverColor() {
        return hoverColor;
    }

    /**
     * @param hoverColor
     *            the hoverColor to set
     */
    public void setHoverColor(Color hoverColor) {
        this.hoverColor = hoverColor;
    }

    /**
     * @return the focusColor
     */
    public Color getFocusColor() {
        return focusColor;
    }

    /**
     * @param focusColor
     *            the focusColor to set
     */
    public void setFocusColor(Color focusColor) {
        this.focusColor = focusColor;
    }

    /**
     * @return the disabledBackgroundColor
     */
    public Color getDisabledBackgroundColor() {
        return disabledBackgroundColor;
    }

    /**
     * @param disabledBackgroundColor
     *            the disabledBackgroundColor to set
     */
    public void setDisabledBackgroundColor(Color disabledBackgroundColor) {
        this.disabledBackgroundColor = disabledBackgroundColor;
    }

    /**
     * @return the disabledForegroundColor
     */
    public Color getDisabledForegroundColor() {
        return disabledForegroundColor;
    }

    /**
     * @param disabledForegroundColor
     *            the disabledForegroundColor to set
     */
    public void setDisabledForegroundColor(Color disabledForegroundColor) {
        this.disabledForegroundColor = disabledForegroundColor;
    }

    /**
     * @return the dashedRectGap
     */
    public int getDashedRectGap() {
        return dashedRectGap;
    }

    /**
     * @param dashedRectGap
     *            the dashedRectGap to set
     */
    public void setDashedRectGap(int dashedRectGap) {
        this.dashedRectGap = dashedRectGap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicButtonUI#paint(java.awt.Graphics,
     * javax.swing.JComponent)
     */
    @Override
    public void paint(Graphics g, JComponent c) {
        AbstractButton b = (AbstractButton) c;
        ButtonModel model = b.getModel();
        if (b.isRolloverEnabled() && model.isRollover()) {
            paintBackground(g, b, getHoverColor());
        } else if (!b.isEnabled()) {
            paintBackground(g, b, getDisabledBackgroundColor());
        }
        super.paint(g, c);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicButtonUI#paintText(java.awt.Graphics,
     * javax.swing.JComponent, java.awt.Rectangle, java.lang.String)
     */
    @Override
    protected void paintText(Graphics g, JComponent c, Rectangle textRect,
            String text) {
        AbstractButton b = (AbstractButton) c;
        ButtonModel model = b.getModel();
        if (model.isEnabled()) {
            super.paintText(g, c, textRect, text);
        } else {
            g.setColor(getDisabledForegroundColor());
            FontMetrics fm = SwingUtilities2.getFontMetrics(c, g);
            int mnemonicIndex = b.getDisplayedMnemonicIndex();
            SwingUtilities2.drawStringUnderlineCharAt(c, g, text,
                    mnemonicIndex, textRect.x, textRect.y + fm.getAscent());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.swing.plaf.basic.BasicButtonUI#paintButtonPressed(java.awt.Graphics
     * , javax.swing.AbstractButton)
     */
    @Override
    protected void paintButtonPressed(Graphics g, AbstractButton b) {
        paintBackground(g, b, getPressedColor());
    }

    protected void paintBackground(Graphics g, AbstractButton b, Color clr) {
        if (b.isContentAreaFilled()) {
            Dimension size = b.getSize();
            g.setColor(clr);
            g.fillRect(0, 0, size.width, size.height);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicButtonUI#paintFocus(java.awt.Graphics,
     * javax.swing.AbstractButton, java.awt.Rectangle, java.awt.Rectangle,
     * java.awt.Rectangle)
     */
    @Override
    protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect,
            Rectangle textRect, Rectangle iconRect) {
        int width = b.getWidth();
        int height = b.getHeight();
        g.setColor(getFocusColor());
        BasicGraphicsUtils.drawDashedRect(g, dashedRectGap, dashedRectGap,
                width - dashedRectGap * 2, height - dashedRectGap * 2);
    }
}