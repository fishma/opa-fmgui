/**
 * Copyright (c) 2015, Intel Corporation
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Intel Corporation nor the names of its contributors
 *       may be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*******************************************************************************
 *                       I N T E L   C O R P O R A T I O N
 *	
 *  Functional Group: Fabric Viewer Application
 *
 *  File Name: IntelComboBoxUI.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.13  2015/09/14 16:07:35  jijunwan
 *  Archive Log:    PR 130229 - The text component of all editable combo boxes should provide validation of the input
 *  Archive Log:    - fixed IntelComboBox border issues
 *  Archive Log:
 *  Archive Log:    Revision 1.12  2015/08/19 15:48:10  jijunwan
 *  Archive Log:    PR 130058 - inconsistent look and feel on ComboBox
 *  Archive Log:    - added code to support set editor border
 *  Archive Log:
 *  Archive Log:    Revision 1.11  2015/08/17 18:53:36  jijunwan
 *  Archive Log:    PR 129983 - Need to change file header's copyright text to BSD license txt
 *  Archive Log:    - changed frontend files' headers
 *  Archive Log:
 *  Archive Log:    Revision 1.10  2015/08/10 20:56:36  fisherma
 *  Archive Log:    Fixing border for Intel L&F ComboBox on Linux platforms.
 *  Archive Log:
 *  Archive Log:    Revision 1.9  2015/06/12 20:51:32  fisherma
 *  Archive Log:    PR 129207 - removed references to SwingSet2 and DefaultLookup as they are internal proprietary API's and may be removed in a future release.
 *  Archive Log:
 *  Archive Log:    Revision 1.8  2015/03/27 15:49:55  jijunwan
 *  Archive Log:    added border support
 *  Archive Log:
 *  Archive Log:    Revision 1.7  2015/03/10 18:12:54  fisherma
 *  Archive Log:    Changed the background of the combo boxes to INTEL_WHITE.
 *  Archive Log:
 *  Archive Log:    Revision 1.6  2015/03/05 17:37:28  jijunwan
 *  Archive Log:    new common widgets
 *  Archive Log:
 *  Archive Log:    Revision 1.5  2014/09/05 21:52:31  jijunwan
 *  Archive Log:    improved IntelComboBoxUI to support editable Combo Box
 *  Archive Log:
 *  Archive Log:    Revision 1.4  2014/07/22 18:29:45  jijunwan
 *  Archive Log:    added tooltip support
 *  Archive Log:
 *  Archive Log:    Revision 1.3  2014/05/19 22:12:32  jijunwan
 *  Archive Log:    look and feel adjustment on performance page
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2014/04/17 16:59:50  jijunwan
 *  Archive Log:    integrate SetupWizard into main frame
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2014/04/16 22:36:09  jijunwan
 *  Archive Log:    added Intel style combobox
 *  Archive Log:
 *
 *  Overview: 
 *
 *  @author: jijunwan
 *
 ******************************************************************************/

package com.intel.stl.ui.common.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxEditor;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.ComboPopup;

import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UIImages;

public class IntelComboBoxUI extends BasicComboBoxUI {
    private String arrowButtonTooltip;

    private Border arrowButtonBorder = BorderFactory
            .createLineBorder(UIConstants.INTEL_GRAY);

    private Border editorBorder = BorderFactory.createMatteBorder(1, 1, 1, 0,
            UIConstants.INTEL_GRAY);

    private Border rendererBorder = BorderFactory.createCompoundBorder(
            editorBorder, BorderFactory.createEmptyBorder(0, 2, 0, 2));

    private final Border emptyBorder = new EmptyBorder(1, 3, 1, 3);

    protected int horizontalAlignment = JLabel.LEFT;

    private Object oldValue;

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicComboBoxUI#installDefaults()
     */
    @Override
    protected void installDefaults() {
        super.installDefaults();
        comboBox.setForeground(UIConstants.INTEL_DARK_GRAY);
        comboBox.setBackground(UIConstants.INTEL_WHITE);
        comboBox.setBorder(BorderFactory.createEmptyBorder());
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicComboBoxUI#createArrowButton()
     */
    @Override
    protected JButton createArrowButton() {
        JButton button =
                ComponentFactory.getImageButton(UIImages.DOWN_ICON
                        .getImageIcon());
        button.setToolTipText(arrowButtonTooltip);
        if (arrowButtonBorder == null) {
            button.setBorderPainted(false);
        } else {
            button.setBorderPainted(true);
            button.setBorder(arrowButtonBorder);
        }
        return button;
    }

    public void setArrowButtonTooltip(String tooltip) {
        arrowButtonTooltip = tooltip;
        if (arrowButton != null) {
            arrowButton.setToolTipText(tooltip);
        }
    }

    public void setArrowButtonBorder(Border border) {
        arrowButtonBorder = border;
        if (arrowButton != null) {
            if (border == null) {
                arrowButton.setBorderPainted(false);
            } else {
                arrowButton.setBorderPainted(true);
                arrowButton.setBorder(border);
            }
        }
    }

    public void setEditorBorder(Border border) {
        editorBorder = border;
        rendererBorder =
                BorderFactory.createCompoundBorder(editorBorder,
                        BorderFactory.createEmptyBorder(0, 2, 0, 2));
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicComboBoxUI#createRenderer()
     */
    @Override
    protected ListCellRenderer createRenderer() {
        return new BasicComboBoxRenderer.UIResource() {
            private static final long serialVersionUID = 4146544528251981068L;

            /*
             * (non-Javadoc)
             * 
             * @see
             * javax.swing.DefaultListCellRenderer#getListCellRendererComponent
             * (javax.swing.JList, java.lang.Object, int, boolean, boolean)
             */
            @Override
            public Component getListCellRendererComponent(JList list,
                    Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                JLabel label =
                        (JLabel) super.getListCellRendererComponent(list,
                                value, index, isSelected, cellHasFocus);
                label.setText(getValueString(value));
                label.setToolTipText(getValueTooltip(value));
                label.setHorizontalAlignment(horizontalAlignment);
                if (index == -1) {
                    label.setBorder(rendererBorder);
                } else {
                    label.setBorder(emptyBorder);
                }

                /*
                 * This code is not being used now:
                 */
                /*
                 * Border border = null; if (cellHasFocus) { if (isSelected) {
                 * // This is not a valid UIManager property, so this //
                 * statement gets ignored border = UIManager.getBorder(
                 * "List.focusSelectedCellHighlightBorder"); } // This due to
                 * above statement not setting the b if (border == null) {
                 * border = UIManager.getBorder(
                 * "List.focusCellHighlightBorder"); } } else { border = new
                 * EmptyBorder(1, 2, 1, 2); }
                 * 
                 * label.setBorder(border);
                 */

                return label;
            }

        };
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicComboBoxUI#createEditor()
     */
    // @Override
    protected ComboBoxEditor createEditor2() {
        ComboBoxEditor editor = new ComboBoxEditor() {

            private final JLabel label = ComponentFactory.getH5Label("",
                    Font.PLAIN);
            {
                label.setForeground(UIConstants.INTEL_DARK_GRAY);
                label.setToolTipText(comboBox.getToolTipText());
                label.setHorizontalAlignment(horizontalAlignment);
            }

            @Override
            public Component getEditorComponent() {
                return label;
            }

            @Override
            public void setItem(Object anObject) {
                label.setText(getValueString(anObject));
                label.setToolTipText(getValueTooltip(anObject));
            }

            @Override
            public Object getItem() {
                return null;
            }

            @Override
            public void selectAll() {
            }

            @Override
            public void addActionListener(ActionListener l) {
            }

            @Override
            public void removeActionListener(ActionListener l) {
            }

        };
        return editor;
    }

    @Override
    protected ComboBoxEditor createEditor() {
        ComboBoxEditor editor = new BasicComboBoxEditor.UIResource() {

            @Override
            public Component getEditorComponent() {
                JTextField editor = (JTextField) super.getEditorComponent();
                editor.setForeground(UIConstants.INTEL_DARK_GRAY);
                editor.setBorder(editorBorder);
                editor.setToolTipText(comboBox.getToolTipText());
                return editor;
            }

            /*
             * (non-Javadoc)
             * 
             * @see
             * javax.swing.plaf.basic.BasicComboBoxEditor#setItem(java.lang.
             * Object)
             */
            @Override
            public void setItem(Object anObject) {
                if (anObject != null) {
                    editor.setText(getValueString(anObject));
                    editor.setToolTipText(getValueTooltip(anObject));
                    oldValue = anObject;
                } else {
                    editor.setText("");
                    editor.setToolTipText(null);
                }
            }

            /*
             * (non-Javadoc)
             * 
             * @see javax.swing.plaf.basic.BasicComboBoxEditor#getItem()
             */
            @Override
            public Object getItem() {
                Object newValue = editor.getText();

                if (oldValue != null && !(oldValue instanceof String)) {
                    if (newValue.equals(getValueString(oldValue))) {
                        return oldValue;
                    } else {
                        return getValueByString(editor.getText());
                    }
                }
                return newValue;
            }

        };
        return editor;
    }

    protected String getValueString(Object value) {
        if (value == null) {
            return "";
        } else {
            return value.toString();
        }
    }

    protected String getValueTooltip(Object value) {
        if (value == null) {
            return null;
        } else {
            return value.toString();
        }
    }

    /**
     * 
     * <i>Description:</i> try to get value based on a string by assuming it has
     * #valueOf method. We should override this method when necessary
     * 
     * @param text
     * @return
     */
    protected Object getValueByString(String text) {
        Class<?> cls = oldValue.getClass();
        try {
            Method method =
                    cls.getMethod("valueOf", new Class[] { String.class });
            return method.invoke(oldValue, new Object[] { text });
        } catch (Exception ex) {
        }
        return text;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicComboBoxUI#createPopup()
     */
    @Override
    protected ComboPopup createPopup() {
        ComboPopup res = super.createPopup();
        ((JComponent) res).setBorder(BorderFactory
                .createLineBorder(UIConstants.INTEL_BORDER_GRAY));
        return res;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.swing.plaf.basic.BasicComboBoxUI#paintCurrentValue(java.awt.Graphics
     * , java.awt.Rectangle, boolean)
     */
    @Override
    public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
        Color oldListBkg = listBox.getSelectionBackground();
        Color oldListFgd = listBox.getSelectionForeground();
        listBox.setSelectionBackground(comboBox.getBackground());
        listBox.setSelectionForeground(comboBox.getForeground());
        super.paintCurrentValue(g, bounds, hasFocus);
        listBox.setSelectionBackground(oldListBkg);
        listBox.setSelectionForeground(oldListFgd);
    }

}
