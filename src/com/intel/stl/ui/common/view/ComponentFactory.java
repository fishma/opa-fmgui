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
package com.intel.stl.ui.common.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import org.jdesktop.swingx.JXLabel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.CategoryTextAnnotation;
import org.jfree.chart.annotations.XYTitleAnnotation;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAnchor;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.StackedXYBarRenderer;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYStepAreaRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.Range;
import org.jfree.data.RangeType;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;

import com.intel.stl.api.Utils;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UIImages;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.model.NodeTypeViz;

/**
 * @author jijunwan
 * 
 */
public class ComponentFactory {
    /**
     * 
     * <i>Description:</i> simple method that creates single/multi line label
     * with specified label width based on a source label. For more advanced
     * label attributes, it's the developer's responsibility to set them back.
     * 
     * @param source
     * @param wrap
     * @param maxWidth
     * @return
     */
    public static JLabel deriveLabel(JLabel source, final boolean wrap,
            final int maxWidth) {
        JXLabel label =
                new JXLabel(source.getText(), source.getIcon(),
                        source.getHorizontalAlignment()) {
                    private static final long serialVersionUID =
                            -4816144910055350011L;

                    private Font cachedFont;

                    private String chahedRawText, chahedText;

                    /*
                     * (non-Javadoc)
                     * 
                     * @see javax.swing.JLabel#getText()
                     */
                    @Override
                    public String getText() {
                        String text = super.getText();
                        if (wrap || maxWidth <= 0 || text == null
                                || text.isEmpty()) {
                            return text;
                        }

                        if (getFont().equals(cachedFont)
                                && text.equals(chahedRawText)) {
                            return chahedText;
                        }

                        chahedRawText = text;
                        cachedFont = getFont();
                        FontMetrics fm = getFontMetrics(cachedFont);
                        char[] chars = text.toCharArray();
                        int width = fm.charsWidth(chars, 0, chars.length);
                        if (width < maxWidth) {
                            chahedText = text;
                        } else {
                            width += fm.charWidth('.') * 3;
                            int pos = chars.length - 1;
                            for (; pos >= 0 && width > maxWidth; pos--) {
                                width -= fm.charWidth(chars[pos]);
                            }
                            chahedText = new String(chars, 0, pos) + "...";
                            if (getToolTipText() == null) {
                                setToolTipText(text);
                            }
                        }

                        return chahedText;
                    }

                };
        if (wrap) {
            label.setLineWrap(true);
        }
        if (maxWidth > 0) {
            label.setMaxLineSpan(maxWidth);
        }
        label.setEnabled(source.isEnabled());
        label.setForeground(source.getForeground());
        label.setOpaque(source.isOpaque());
        label.setBackground(source.getBackground());
        label.setFont(source.getFont());
        label.setBorder(source.getBorder());
        label.setToolTipText(source.getToolTipText());
        return label;
    }

    public static JLabel getIntelH1Label(String text, int style) {
        JLabel label = new JLabel(text);
        label.setForeground(UIConstants.INTEL_BLUE);
        label.setFont(UIConstants.H1_FONT.deriveFont(style));
        return label;
    }

    public static JLabel getIntelH2Label(String text, int style) {
        JLabel label = new JLabel(text);
        label.setForeground(UIConstants.INTEL_BLUE);
        label.setFont(UIConstants.H2_FONT.deriveFont(style));
        return label;
    }

    public static JLabel getIntelH3Label(String text, int style) {
        JLabel label = new JLabel(text);
        label.setForeground(UIConstants.INTEL_BLUE);
        label.setFont(UIConstants.H3_FONT.deriveFont(style));
        return label;
    }

    public static JLabel getH1Label(String text, int style) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.H1_FONT.deriveFont(style));
        label.setForeground(UIConstants.INTEL_DARK_GRAY);
        return label;
    }

    public static JLabel getH2Label(String text, int style) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.H2_FONT.deriveFont(style));
        label.setForeground(UIConstants.INTEL_DARK_GRAY);
        return label;
    }

    public static JLabel getH3Label(String text, int style) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.H3_FONT.deriveFont(style));
        label.setForeground(UIConstants.INTEL_DARK_GRAY);
        return label;
    }

    public static JLabel getH4Label(String text, int style) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.H4_FONT.deriveFont(style));
        label.setForeground(UIConstants.INTEL_DARK_GRAY);
        return label;
    }

    public static JLabel getH5Label(String text, int style) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.H5_FONT.deriveFont(style));
        label.setForeground(UIConstants.INTEL_DARK_GRAY);
        return label;
    }

    public static JLabel getH6Label(String text, int style) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.H6_FONT.deriveFont(style));
        label.setForeground(UIConstants.INTEL_DARK_GRAY);
        return label;
    }

    public static JLabel getFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(UIConstants.INTEL_DARK_GRAY);
        label.setFont(UIConstants.H5_FONT.deriveFont(Font.BOLD));
        return label;
    }

    public static JLabel getFieldLabel(String text, Color clr) {
        JLabel label = new JLabel(text);
        label.setForeground(clr);
        label.setFont(UIConstants.H5_FONT.deriveFont(Font.BOLD));
        return label;
    }

    public static JLabel getFieldContent(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(UIConstants.INTEL_DARK_GRAY);
        label.setFont(UIConstants.H3_FONT);
        return label;
    }

    public static JLabel getFieldContent(String text, Color clr) {
        JLabel label = new JLabel(text);
        label.setForeground(clr);
        label.setFont(UIConstants.H3_FONT);
        return label;
    }

    public static JButton getImageButton(ImageIcon icon) {
        JButton res = new JButton(icon);
        res.setMargin(new Insets(0, 0, 0, 0));
        res.setBorderPainted(false);
        res.setContentAreaFilled(false);
        return res;
    }

    public static JButton getIntelActionButton(String text) {
        JButton btn = new JButton(text);
        btn.setUI(new IntelButtonUI(UIConstants.INTEL_MEDIUM_BLUE,
                UIConstants.INTEL_MEDIUM_DARK_BLUE));
        btn.setBackground(UIConstants.INTEL_BLUE);
        btn.setForeground(UIConstants.INTEL_WHITE);
        // btn.setFont(UIConstants.H5_FONT);
        return btn;
    }

    public static JButton getDebugActionButton(String text) {
        JButton btn = new JButton(text) {
            private static final long serialVersionUID = 9164124909538571267L;

            @Override
            public String toString() {

                return new String(getClass().getName() + "@" + hashCode());
            }
        };
        btn.setUI(new IntelButtonUI(UIConstants.INTEL_MEDIUM_BLUE,
                UIConstants.INTEL_MEDIUM_DARK_BLUE));
        btn.setBackground(UIConstants.INTEL_BLUE);
        btn.setForeground(UIConstants.INTEL_WHITE);
        return btn;
    }

    // This function is used to make buttons equal width based on the
    // width of the widest button
    public static void makeSameWidthButtons(JButton[] btnGroup) {
        int maxWidth = 0;

        // Find widest button
        for (int i = 0; i < btnGroup.length; i++) {
            int width = btnGroup[i].getPreferredSize().width;
            maxWidth = (maxWidth > width) ? maxWidth : width;
        }

        // set all buttons to the widest width
        for (int i = 0; i < btnGroup.length; i++) {
            btnGroup[i].setPreferredSize(new java.awt.Dimension(maxWidth,
                    btnGroup[i].getPreferredSize().height));
        }
    }

    public static JButton getIntelActionButton(String text, Icon icon) {
        JButton btn = new JButton(text);
        btn.setUI(new IntelButtonUI(UIConstants.INTEL_MEDIUM_BLUE,
                UIConstants.INTEL_MEDIUM_DARK_BLUE));
        btn.setBackground(UIConstants.INTEL_BLUE);
        btn.setForeground(UIConstants.INTEL_WHITE);
        btn.setIcon(icon);
        return btn;
    }

    public static JButton getIntelActionButton(AbstractAction action) {
        JButton btn = new JButton(action);
        btn.setUI(new IntelButtonUI(UIConstants.INTEL_MEDIUM_BLUE,
                UIConstants.INTEL_MEDIUM_DARK_BLUE));
        btn.setBackground(UIConstants.INTEL_BLUE);
        btn.setForeground(UIConstants.INTEL_WHITE);
        // btn.setFont(UIConstants.H5_FONT);
        return btn;
    }

    public static JButton getIntelCancelButton(String text) {
        JButton btn = new JButton(text);
        btn.setUI(new IntelButtonUI(UIConstants.INTEL_BACKGROUND_GRAY,
                UIConstants.INTEL_LIGHT_GRAY));
        btn.setBackground(UIConstants.INTEL_TABLE_BORDER_GRAY);
        btn.setForeground(UIConstants.INTEL_DARK_GRAY);
        // btn.setFont(UIConstants.H5_FONT);
        return btn;
    }

    public static JButton getIntelCancelButton(AbstractAction action) {
        JButton btn = new JButton(action);
        btn.setUI(new IntelButtonUI(UIConstants.INTEL_BACKGROUND_GRAY,
                UIConstants.INTEL_LIGHT_GRAY));
        btn.setBackground(UIConstants.INTEL_TABLE_BORDER_GRAY);
        btn.setForeground(UIConstants.INTEL_DARK_GRAY);
        // btn.setFont(UIConstants.H5_FONT);
        return btn;
    }

    public static JButton getIntelDeleteButton(String text) {
        JButton btn = new JButton(text);
        btn.setUI(new IntelButtonUI(UIConstants.INTEL_MEDIUM_RED,
                UIConstants.INTEL_MEDIUM_DARK_RED));
        btn.setBackground(UIConstants.INTEL_RED);
        btn.setForeground(UIConstants.INTEL_WHITE);
        // btn.setFont(UIConstants.H5_FONT);
        return btn;
    }

    public static JButton getIntelDeleteButton(AbstractAction action) {
        JButton btn = new JButton(action);
        btn.setUI(new IntelButtonUI(UIConstants.INTEL_MEDIUM_RED,
                UIConstants.INTEL_MEDIUM_DARK_RED));
        btn.setBackground(UIConstants.INTEL_RED);
        btn.setForeground(UIConstants.INTEL_WHITE);
        // btn.setFont(UIConstants.H5_FONT);
        return btn;
    }

    public static JButton getIntelWizardStepButton(String text,
            final ActionListener listener) {
        JButton btn = new JButton(text) {
            /**
             *
             */
            private static final long serialVersionUID = 7408863234629205179L;

            @Override
            public void setEnabled(boolean b) {
                if (b) {
                    addActionListener(listener);
                } else {
                    removeActionListener(listener);
                }
            }
        };
        btn.setUI(new IntelButtonUI(UIConstants.INTEL_MEDIUM_BLUE,
                UIConstants.INTEL_MEDIUM_DARK_BLUE));
        btn.setBackground(UIConstants.INTEL_BLUE);
        btn.setForeground(UIConstants.INTEL_WHITE);
        return btn;
    }

    public static JCheckBox getIntelCheckBox(String text) {
        JCheckBox box = new JCheckBox(text);
        box.setIcon(UIImages.EMPTY_BOX_ICON.getImageIcon());
        box.setSelectedIcon(UIImages.CHECK_BOX_ICON.getImageIcon());
        box.setBackground(Color.WHITE);
        return box;
    }

    public static JCheckBoxMenuItem getIntelCheckBoxMenuItem(String text) {
        final JCheckBoxMenuItem box =
                new JCheckBoxMenuItem(text,
                        UIImages.EMPTY_BOX_ICON.getImageIcon());
        // should do it through L&F, but couldn't find the way to do it
        // so we are using action listener to dynamically change icon
        box.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                box.setIcon(box.isSelected() ? UIImages.CHECK_BOX_ICON
                        .getImageIcon() : UIImages.EMPTY_BOX_ICON
                        .getImageIcon());
            }
        });
        return box;
    }

    public static JTextField createNumericTextField(
            DocumentListener... docListeners) {
        final JTextField field = new JTextField();
        if (docListeners != null) {
            for (DocumentListener docListener : docListeners) {
                field.getDocument().addDocumentListener(docListener);
            }
        }

        // Add a mouse listener to clear out the field before the user types
        field.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                try {
                    JTextComponent tc = (JTextComponent) e.getSource();
                    String txt = tc.getText();
                    if (!txt.isEmpty()) {
                        Utils.toLong(txt);
                    }
                } catch (Exception ex) {
                    field.setText("");
                }
            }
        });

        // Add the input verifier
        field.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                JTextField field = (JTextField) input;
                String txt = field.getText();
                if (txt.isEmpty()) {
                    field.setToolTipText(UILabels.STL50084_CANT_BE_BLANK
                            .getDescription());
                    input.setBackground(UIConstants.INTEL_LIGHT_RED);
                    return false;
                }
                try {
                    Utils.toLong(txt);
                    input.setBackground(UIConstants.INTEL_WHITE);
                    return true;
                } catch (Exception e) {
                    field.setToolTipText(UILabels.STL50085_MUST_BE_NUMERIC
                            .getDescription());
                    input.setBackground(UIConstants.INTEL_LIGHT_RED);
                }
                return false;
            }

        });
        return field;
    }

    public static JTextField createTextField(DocumentListener... docListeners) {
        final JTextField field = new JTextField();
        if (docListeners != null) {
            for (DocumentListener docListener : docListeners) {
                field.getDocument().addDocumentListener(docListener);
                field.getDocument().putProperty("owner", field);
            }
        }

        // Add the input verifier
        field.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                JTextField field = (JTextField) input;
                String txt = field.getText();
                if (txt.isEmpty()) {
                    field.setToolTipText(UILabels.STL50084_CANT_BE_BLANK
                            .getDescription());
                    input.setBackground(UIConstants.INTEL_LIGHT_RED);
                    return false;
                } else {
                    input.setBackground(UIConstants.INTEL_WHITE);
                    return true;
                }
            }

        });

        return field;
    }

    public static JTextField createRestrictedTextField(
            final int maxFieldLength, DocumentListener... docListeners) {
        final JTextField field = new JTextField();
        if (docListeners != null) {
            for (DocumentListener docListener : docListeners) {
                field.getDocument().addDocumentListener(docListener);
                field.getDocument().putProperty("owner", field);
            }
        }

        // Add the input verifier
        field.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                JTextField field = (JTextField) input;
                String txt = field.getText();
                if (txt.isEmpty()) {
                    field.setToolTipText(UILabels.STL50084_CANT_BE_BLANK
                            .getDescription());
                    input.setBackground(UIConstants.INTEL_LIGHT_RED);
                    return false;
                } else if (txt.length() > maxFieldLength) {
                    field.setToolTipText(UILabels.STL50095_TEXT_FIELD_LIMIT
                            .getDescription(maxFieldLength));
                    input.setBackground(UIConstants.INTEL_LIGHT_RED);
                    return false;
                } else {
                    input.setBackground(UIConstants.INTEL_WHITE);
                    field.setToolTipText("");
                    return true;
                }
            }

        });

        return field;
    }

    public static JComboBox<String> createComboBox(String[] strArray,
            DocumentListener... docListeners) {

        JComboBox<String> cbox = new JComboBox<String>(strArray);
        cbox.setUI(new IntelComboBoxUI());

        // Get the text editor for this combo box
        final JTextComponent tc =
                (JTextComponent) cbox.getEditor().getEditorComponent();

        // Initialize the document listeners
        for (DocumentListener docListener : docListeners) {
            tc.getDocument().addDocumentListener(docListener);
        }

        // Add a mouse listener to clear out the field before the user types
        tc.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                try {
                    JTextComponent tc = (JTextComponent) e.getSource();
                    String txt = tc.getText();
                    if (!txt.isEmpty()) {
                        Utils.toLong(txt);
                    }
                } catch (Exception ex) {
                    tc.setText("");
                }
            }
        });

        // Add the input verifier
        tc.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {

                JTextComponent txtComp = (JTextComponent) input;
                String txt = txtComp.getText();

                if (txt.isEmpty()) {
                    txtComp.setToolTipText(UILabels.STL50084_CANT_BE_BLANK
                            .getDescription());
                    input.setBackground(UIConstants.INTEL_LIGHT_RED);
                    return false;
                } else {
                    input.setBackground(UIConstants.INTEL_WHITE);
                    return true;
                }
            }
        });

        return cbox;
    }

    public static JComboBox<String> createNumericComboBox(String[] strArray,
            DocumentListener... docListeners) {

        JComboBox<String> cbox = new JComboBox<String>(strArray);
        cbox.setUI(new IntelComboBoxUI());

        // Get the text editor for this combo box
        final JTextComponent tc =
                (JTextComponent) cbox.getEditor().getEditorComponent();

        // Initialize the document listeners
        for (DocumentListener docListener : docListeners) {
            tc.getDocument().addDocumentListener(docListener);
        }

        // Add a mouse listener to clear out the field before the user types
        tc.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                try {
                    JTextComponent tc = (JTextComponent) e.getSource();
                    String txt = tc.getText();
                    if (!txt.isEmpty()) {
                        Utils.toLong(txt);
                    }
                } catch (Exception ex) {
                    tc.setText("");
                }
            }
        });

        // Add the input verifier
        tc.setInputVerifier(new InputVerifier() {

            @Override
            public boolean verify(JComponent input) {

                JTextComponent txtComp = (JTextComponent) input;
                String txt = txtComp.getText();

                if (txt.isEmpty()) {
                    input.setBackground(UIConstants.INTEL_LIGHT_RED);
                    txtComp.setToolTipText(UILabels.STL50084_CANT_BE_BLANK
                            .getDescription());
                    return false;
                }
                try {
                    Utils.toLong(txt);
                    input.setBackground(UIConstants.INTEL_WHITE);
                    return true;
                } catch (Exception e) {
                    txtComp.setToolTipText(UILabels.STL50085_MUST_BE_NUMERIC
                            .getDescription());
                    input.setBackground(UIConstants.INTEL_LIGHT_RED);
                }
                return false;
            }
        });

        return cbox;
    }

    public static JTable createIntelNonSortableSimpleTable(Object[][] rowData,
            Object[] columnNames) {

        JTable table = new JTable(rowData, columnNames);
        table.setBackground(UIConstants.INTEL_WHITE);
        table.getTableHeader().setBackground(UIConstants.INTEL_BORDER_GRAY);
        table.getTableHeader().setForeground(UIConstants.INTEL_DARK_GRAY);
        table.getTableHeader().setFont(
                UIConstants.H5_FONT.deriveFont(Font.BOLD));
        return table;
    }

    public static JTable createIntelNonSortableSimpleTable() {

        JTable table = new JTable();
        table.setBackground(UIConstants.INTEL_WHITE);
        table.getTableHeader().setBackground(UIConstants.INTEL_BORDER_GRAY);
        table.getTableHeader().setForeground(UIConstants.INTEL_DARK_GRAY);
        table.getTableHeader().setFont(
                UIConstants.H5_FONT.deriveFont(Font.BOLD));
        return table;
    }

    public static TitledBorder createTitledBorder(String title) {
        TitledBorder res = BorderFactory.createTitledBorder(title);
        res.setTitleColor(UIConstants.INTEL_DARK_GRAY);
        // res.setTitleFont(UIConstants.H4_FONT);
        return res;
    }

    /**
     * Create a simple pie chart without title, legend, label etc.
     * 
     * @param dataset
     *            the dataset to be renderer
     * @param colors
     *            an color array specify each item's color. The order of the
     *            array correspond to the item order in dataset
     * @return a pie chart
     */
    public static JFreeChart createPlainPieChart(PieDataset dataset,
            Color[] colors) {
        if (dataset == null) {
            throw new IllegalArgumentException("No dataset.");
        }
        if (colors != null && colors.length != dataset.getItemCount()) {
            throw new IllegalArgumentException("Data have "
                    + dataset.getItemCount() + " values, while we have "
                    + colors.length + " colors for them.");
        }

        JFreeChart jfreechart =
                ChartFactory.createPieChart(null, dataset, false, true, false);
        PiePlot pieplot = (PiePlot) jfreechart.getPlot();
        if (colors != null) {
            for (int i = 0; i < colors.length; i++) {
                pieplot.setSectionPaint(dataset.getKey(i), colors[i]);
            }
        }
        pieplot.setBackgroundPaint(null);
        pieplot.setOutlineStroke(null);
        pieplot.setLabelGenerator(null);
        pieplot.setNoDataMessage(UILabels.STL40001_ERROR_No_DATA
                .getDescription());
        pieplot.setCircular(true);
        pieplot.setInteriorGap(0.000001);
        return jfreechart;
    }

    public static JFreeChart createPlainHistoryChart(IntervalXYDataset dataset,
            XYItemLabelGenerator labelGenerator) {
        if (dataset == null) {
            throw new IllegalArgumentException("No dataset.");
        }

        JFreeChart jfreechart =
                ChartFactory.createXYBarChart(null, null, true, null, dataset,
                        PlotOrientation.VERTICAL, false, true, false);
        XYPlot xyplot = (XYPlot) jfreechart.getPlot();
        xyplot.setBackgroundPaint(null);
        xyplot.setOutlinePaint(null);
        XYBarRenderer renderer = (XYBarRenderer) xyplot.getRenderer();
        renderer.setShadowVisible(false);
        renderer.setBaseItemLabelsVisible(true);
        if (labelGenerator != null) {
            renderer.setBaseItemLabelGenerator(labelGenerator);
        }
        renderer.setBaseItemLabelFont(UIConstants.H4_FONT);
        renderer.setBarPainter(new StandardXYBarPainter());
        renderer.setSeriesPaint(0, UIConstants.INTEL_BLUE);
        // xyplot.getDomainAxis().setVisible(false);
        xyplot.getDomainAxis().setAxisLineVisible(true);
        xyplot.getDomainAxis().setTickLabelsVisible(false);
        NumberAxis axis = (NumberAxis) xyplot.getRangeAxis();
        axis.setRangeType(RangeType.POSITIVE);
        axis.setVisible(false);
        return jfreechart;
    }

    public static JFreeChart createStepAreaChart(XYDataset dataset,
            XYItemLabelGenerator labelGenerator) {
        if (dataset == null) {
            throw new IllegalArgumentException("No dataset.");
        }

        JFreeChart jfreechart =
                ChartFactory.createXYLineChart(null, null, null, dataset,
                        PlotOrientation.VERTICAL, false, true, false);
        XYPlot xyplot = (XYPlot) jfreechart.getPlot();
        xyplot.setBackgroundPaint(UIConstants.INTEL_BACKGROUND_GRAY);
        // xyplot.setOutlinePaint(null);
        XYStepAreaRenderer xysteparearenderer =
                new XYStepAreaRenderer(XYStepAreaRenderer.AREA);
        xysteparearenderer.setDataBoundsIncludesVisibleSeriesOnly(false);
        xysteparearenderer
                .setBaseToolTipGenerator(new StandardXYToolTipGenerator());
        xysteparearenderer.setDefaultEntityRadius(6);
        xyplot.setRenderer(xysteparearenderer);

        if (labelGenerator != null) {
            xysteparearenderer.setBaseItemLabelGenerator(labelGenerator);
        }
        xysteparearenderer.setSeriesPaint(0, UIConstants.INTEL_GREEN);
        xyplot.setOutlinePaint(UIConstants.INTEL_DARK_GRAY);
        xyplot.setDomainGridlinePaint(UIConstants.INTEL_DARK_GRAY);
        xyplot.setRangeGridlinePaint(UIConstants.INTEL_DARK_GRAY);

        xyplot.getDomainAxis().setVisible(false);

        NumberAxis axis = (NumberAxis) xyplot.getRangeAxis();
        axis.setRangeType(RangeType.POSITIVE);
        axis.setAxisLineVisible(false);

        return jfreechart;
    }

    public static JFreeChart createXYBarChart(String xAxisLabel,
            String yAxisLabel, IntervalXYDataset dataset,
            XYItemLabelGenerator labelGenerator) {
        if (dataset == null) {
            throw new IllegalArgumentException("No dataset.");
        }

        JFreeChart jfreechart =
                ChartFactory.createXYBarChart(null, xAxisLabel, false,
                        yAxisLabel, dataset, PlotOrientation.VERTICAL, false,
                        true, false);
        XYPlot xyplot = (XYPlot) jfreechart.getPlot();
        xyplot.setBackgroundPaint(null);
        xyplot.setOutlinePaint(null);
        xyplot.setRangeGridlinePaint(UIConstants.INTEL_DARK_GRAY);

        NumberAxis axis = (NumberAxis) xyplot.getRangeAxis();
        axis.setRangeType(RangeType.POSITIVE);
        axis.setLabelFont(UIConstants.H5_FONT);
        xyplot.getDomainAxis().setLabelFont(UIConstants.H5_FONT);
        XYBarRenderer renderer = (XYBarRenderer) xyplot.getRenderer();
        renderer.setShadowVisible(false);
        renderer.setBaseItemLabelsVisible(true);
        if (labelGenerator != null) {
            renderer.setBaseItemLabelGenerator(labelGenerator);
        }
        renderer.setBaseItemLabelFont(UIConstants.H5_FONT);
        renderer.setBarPainter(new StandardXYBarPainter());
        renderer.setSeriesPaint(0, UIConstants.INTEL_BLUE);
        return jfreechart;
    }

    public static JFreeChart createBarChart(String xAxisLabel,
            String yAxisLabel, CategoryDataset dataset) {
        if (dataset == null) {
            throw new IllegalArgumentException("No dataset.");
        }

        JFreeChart jfreechart =
                ChartFactory.createBarChart(null, xAxisLabel, yAxisLabel,
                        dataset, PlotOrientation.VERTICAL, false, true, false);
        CategoryPlot categoryplot = jfreechart.getCategoryPlot();
        categoryplot.setBackgroundPaint(null);
        categoryplot.setOutlinePaint(null);
        categoryplot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
        categoryplot.setRangePannable(true);
        categoryplot.setRangeGridlinePaint(UIConstants.INTEL_DARK_GRAY);
        categoryplot.getRangeAxis().setLabelFont(UIConstants.H5_FONT);
        categoryplot.getDomainAxis().setLabelFont(UIConstants.H5_FONT);

        BarRenderer barrenderer = (BarRenderer) categoryplot.getRenderer();
        barrenderer.setBarPainter(new StandardBarPainter());
        barrenderer.setBaseItemLabelsVisible(true);
        barrenderer.setShadowVisible(false);
        barrenderer.setSeriesPaint(0, UIConstants.INTEL_BLUE);

        CategoryAxis categoryaxis = categoryplot.getDomainAxis();
        categoryaxis.setCategoryMargin(0.02D);
        categoryaxis.setUpperMargin(0.01D);
        categoryaxis.setLowerMargin(0.01D);
        // categoryaxis.setAxisLineVisible(false);
        categoryaxis.setMaximumCategoryLabelWidthRatio(0.95F);

        NumberAxis axis = (NumberAxis) categoryplot.getRangeAxis();
        axis.setRangeType(RangeType.POSITIVE);

        return jfreechart;
    }

    public static JFreeChart createHistogramChart(String xAxisLabel,
            String yAxisLabel, IntervalXYDataset dataset) {
        JFreeChart jfreechart =
                ChartFactory.createHistogram(null, xAxisLabel, yAxisLabel,
                        dataset, PlotOrientation.VERTICAL, false, true, false);
        XYPlot xyplot = (XYPlot) jfreechart.getPlot();
        xyplot.setBackgroundPaint(null);
        xyplot.setOutlinePaint(null);
        xyplot.setRangeGridlinePaint(UIConstants.INTEL_DARK_GRAY);
        NumberAxis yAxis = (NumberAxis) xyplot.getRangeAxis();
        yAxis.setRangeType(RangeType.POSITIVE);
        yAxis.setLabelFont(UIConstants.H5_FONT);
        yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        xyplot.getDomainAxis().setLabelFont(UIConstants.H5_FONT);
        xyplot.getRangeAxis().setStandardTickUnits(
                NumberAxis.createIntegerTickUnits());
        XYBarRenderer renderer = (XYBarRenderer) xyplot.getRenderer();
        renderer.setShadowVisible(false);
        renderer.setBaseItemLabelsVisible(true);
        renderer.setBaseItemLabelFont(UIConstants.H5_FONT);
        renderer.setBarPainter(new StandardXYBarPainter());
        renderer.setSeriesPaint(0, UIConstants.INTEL_BLUE);
        return jfreechart;
    }

    public static JFreeChart createXYAreaChart(String xAxisLabel,
            String yAxisLabel, XYDataset dataset, boolean includeLegend) {
        JFreeChart jfreechart =
                ChartFactory.createXYAreaChart(null, xAxisLabel, yAxisLabel,
                        dataset, PlotOrientation.VERTICAL, false, true, false);
        XYPlot xyplot = (XYPlot) jfreechart.getPlot();
        xyplot.setDomainPannable(true);
        xyplot.setBackgroundPaint(null);
        xyplot.setOutlinePaint(null);
        xyplot.setForegroundAlpha(0.8F);
        xyplot.setRangeGridlinePaint(UIConstants.INTEL_DARK_GRAY);
        DateAxis dateaxis = new DateAxis(xAxisLabel);
        dateaxis.setLowerMargin(0.0D);
        dateaxis.setUpperMargin(0.0D);
        xyplot.setDomainAxis(dateaxis);
        NumberAxis rangeAxis = (NumberAxis) xyplot.getRangeAxis();
        rangeAxis.setRangeType(RangeType.POSITIVE);
        rangeAxis.setLabelFont(UIConstants.H5_FONT);
        rangeAxis.setLabelInsets(new RectangleInsets(0, 0, 0, 0));

        if (includeLegend) {
            LegendTitle legendtitle = new LegendTitle(xyplot);
            legendtitle.setItemFont(UIConstants.H5_FONT);
            legendtitle.setBackgroundPaint(UIConstants.INTEL_WHITE);
            legendtitle.setFrame(new BlockBorder(UIConstants.INTEL_BLUE));
            legendtitle.setPosition(RectangleEdge.BOTTOM);
            XYTitleAnnotation xytitleannotation =
                    new XYTitleAnnotation(0.97999999999999998D,
                            0.79999999999999998D, legendtitle,
                            RectangleAnchor.BOTTOM_RIGHT);
            xytitleannotation.setMaxWidth(0.47999999999999998D);
            xyplot.addAnnotation(xytitleannotation);
        }

        XYItemRenderer xyitemrenderer = xyplot.getRenderer();
        xyitemrenderer.setSeriesPaint(1, UIConstants.INTEL_DARK_GRAY);
        xyitemrenderer.setSeriesPaint(0, NodeTypeViz.SWITCH.getColor());
        xyitemrenderer
                .setBaseToolTipGenerator(new StandardXYToolTipGenerator(
                        "<html><b>{0}</b><br> Time: {1}<br> Data: {2}</html>",
                        new SimpleDateFormat("HH:mm:ss"), new DecimalFormat(
                                "#,##0.00")));
        return jfreechart;
    }

    public static JFreeChart createXYAreaSparkline(XYDataset dataset) {
        JFreeChart jfreechart =
                ChartFactory.createXYAreaChart(null, null, null, dataset,
                        PlotOrientation.VERTICAL, false, false, false);
        XYPlot xyplot = (XYPlot) jfreechart.getPlot();
        xyplot.setDomainPannable(true);
        xyplot.setBackgroundPaint(null);
        xyplot.setOutlinePaint(null);
        xyplot.setForegroundAlpha(0.8F);
        xyplot.setDomainGridlinesVisible(false);
        xyplot.setDomainCrosshairVisible(false);
        xyplot.setRangeGridlinesVisible(false);
        xyplot.setRangeCrosshairVisible(false);

        DateAxis dateaxis = new DateAxis("");
        dateaxis.setTickLabelsVisible(false);
        dateaxis.setTickMarksVisible(false);
        dateaxis.setAxisLineVisible(false);
        dateaxis.setNegativeArrowVisible(false);
        dateaxis.setPositiveArrowVisible(false);
        dateaxis.setVisible(false);
        xyplot.setDomainAxis(dateaxis);

        ValueAxis rangeAxis = xyplot.getRangeAxis();
        rangeAxis.setTickLabelsVisible(false);
        rangeAxis.setTickMarksVisible(false);
        rangeAxis.setAxisLineVisible(false);
        rangeAxis.setNegativeArrowVisible(false);
        rangeAxis.setPositiveArrowVisible(false);
        rangeAxis.setVisible(false);

        XYItemRenderer xyitemrenderer = xyplot.getRenderer();
        xyitemrenderer.setSeriesPaint(1, UIConstants.INTEL_DARK_GRAY);
        xyitemrenderer.setSeriesPaint(0, NodeTypeViz.SWITCH.getColor());
        return jfreechart;
    }

    @SuppressWarnings("unchecked")
    public static JFreeChart createTopNBarChart2(String yAxisLabel,
            CategoryDataset dataset) {
        JFreeChart jfreechart =
                ChartFactory.createBarChart(null, null, yAxisLabel, dataset,
                        PlotOrientation.HORIZONTAL, false, true, false);
        CategoryPlot categoryplot = jfreechart.getCategoryPlot();
        categoryplot.setBackgroundPaint(null);
        categoryplot.setOutlinePaint(null);
        categoryplot.setDomainGridlinesVisible(true);
        categoryplot.setDomainGridlinePosition(CategoryAnchor.END);
        categoryplot.setDomainGridlineStroke(new BasicStroke(0.5F));
        categoryplot.setDomainGridlinePaint(UIConstants.INTEL_BORDER_GRAY);
        categoryplot.setRangeGridlinesVisible(false);
        categoryplot.clearRangeMarkers();
        CategoryAxis categoryaxis = categoryplot.getDomainAxis();
        categoryaxis.setVisible(false);
        categoryaxis.setCategoryMargin(0.75D);

        NumberAxis axis = (NumberAxis) categoryplot.getRangeAxis();
        axis.setRangeType(RangeType.POSITIVE);
        axis.setVisible(false);

        BarRenderer barrenderer = (BarRenderer) categoryplot.getRenderer();
        barrenderer.setShadowVisible(false);
        barrenderer.setSeriesPaint(0, UIConstants.INTEL_BLUE);
        barrenderer.setDrawBarOutline(false);
        barrenderer.setBaseItemLabelsVisible(true);
        barrenderer.setBaseItemLabelFont(UIConstants.H5_FONT);
        barrenderer.setBarPainter(new StandardBarPainter());

        List<String> names = dataset.getColumnKeys();
        for (String name : names) {
            CategoryTextAnnotation categorytextannotation =
                    new CategoryTextAnnotation(name, name, 0.0D);
            categorytextannotation.setFont(UIConstants.H6_FONT);
            categorytextannotation.setTextAnchor(TextAnchor.BOTTOM_LEFT);
            categorytextannotation.setCategoryAnchor(CategoryAnchor.MIDDLE);
            categoryplot.addAnnotation(categorytextannotation);
        }
        return jfreechart;
    }

    public static JFreeChart createTopNBarChart(String yAxisLabel,
            CategoryDataset dataset) {
        JFreeChart jfreechart =
                ChartFactory.createBarChart(null, null, yAxisLabel, dataset,
                        PlotOrientation.HORIZONTAL, true, true, false);
        CategoryPlot categoryplot = jfreechart.getCategoryPlot();
        categoryplot.setBackgroundPaint(null);
        categoryplot.setOutlinePaint(null);
        categoryplot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
        categoryplot.setRangePannable(true);
        categoryplot.setRangeGridlinesVisible(true);
        categoryplot.setRangeGridlinePaint(UIConstants.INTEL_DARK_GRAY);

        BarRenderer barrenderer = (BarRenderer) categoryplot.getRenderer();
        barrenderer.setBarPainter(new StandardBarPainter());
        barrenderer.setShadowVisible(false);
        barrenderer.setItemMargin(0.015);
        barrenderer.setSeriesPaint(0, UIConstants.INTEL_BLUE);
        barrenderer.setSeriesPaint(1, UIConstants.INTEL_LIGHT_BLUE);

        CategoryAxis categoryaxis = categoryplot.getDomainAxis();
        categoryaxis.setCategoryMargin(0.15D);
        categoryaxis.setUpperMargin(0.02D);
        categoryaxis.setLowerMargin(0.02D);
        categoryaxis.setMaximumCategoryLabelWidthRatio(0.8F);

        NumberAxis numberaxis = (NumberAxis) categoryplot.getRangeAxis();
        numberaxis.setRangeType(RangeType.POSITIVE);
        numberaxis.setStandardTickUnits(createLargeNumberTickUnits());
        numberaxis.setUpperMargin(0.20000000000000001D);
        numberaxis.setLabelFont(UIConstants.H5_FONT);
        numberaxis.setLabelInsets(new RectangleInsets(0, 0, 0, 0));
        numberaxis.setTickMarksVisible(true);
        numberaxis.setTickLabelsVisible(true);

        LegendTitle legend = jfreechart.getLegend();
        legend.setFrame(BlockBorder.NONE);
        legend.setItemFont(barrenderer.getBaseItemLabelFont().deriveFont(10.0f));

        return jfreechart;
    }

    public static JFreeChart createBulletChart(CategoryDataset dataset,
            double[] thresholds, Color[] colors) {
        if (thresholds.length != colors.length) {
            throw new IllegalArgumentException(
                    "Inconsistant array sizes: thresholds=" + thresholds.length
                            + " colors=" + colors.length);
        }

        JFreeChart jfreechart =
                ChartFactory.createBarChart(null, null, null, dataset,
                        PlotOrientation.HORIZONTAL, false, true, false);
        CategoryPlot categoryplot = jfreechart.getCategoryPlot();
        categoryplot.setBackgroundPaint(null);
        categoryplot.setOutlinePaint(null);

        categoryplot.getDomainAxis().setVisible(false);

        NumberAxis rangeAxis = (NumberAxis) categoryplot.getRangeAxis();
        rangeAxis.setVisible(false);
        rangeAxis.setRange(new Range(0, 1.0));

        double last = 0.0;
        for (int i = 0; i < thresholds.length; i++) {
            IntervalMarker marker =
                    new IntervalMarker(last, thresholds[i], colors[i]);
            categoryplot.addRangeMarker(marker, Layer.BACKGROUND);
            last = thresholds[i];
        }

        BarRenderer renderer = (BarRenderer) categoryplot.getRenderer();
        renderer.setShadowVisible(false);
        renderer.setMaximumBarWidth(0.33);
        renderer.setSeriesPaint(0, UIConstants.INTEL_DARK_GRAY);

        return jfreechart;
    }

    public static JFreeChart createStackedXYBarChart(XYDataset dataset,
            String title, String domainAxisLabel, String rangeAxisLabel,
            boolean legend) {
        DateAxis dateaxis = new DateAxis(domainAxisLabel);
        NumberAxis numberaxis = new NumberAxis(rangeAxisLabel);
        StackedXYBarRenderer stackedxybarrenderer =
                new StackedXYBarRenderer(0.10000000000000001D);
        XYPlot xyplot =
                new XYPlot(dataset, dateaxis, numberaxis, stackedxybarrenderer);
        JFreeChart jfreechart =
                new JFreeChart(title, UIConstants.H5_FONT, xyplot, legend);
        ChartUtilities.applyCurrentTheme(jfreechart);

        stackedxybarrenderer.setShadowVisible(false);
        stackedxybarrenderer.setDrawBarOutline(false);

        xyplot.setBackgroundPaint(null);
        xyplot.setOutlinePaint(null);
        xyplot.setRangeGridlinePaint(UIConstants.INTEL_BORDER_GRAY);

        dateaxis.setLabelFont(UIConstants.H5_FONT);
        dateaxis.setLowerMargin(0.0D);
        dateaxis.setUpperMargin(0.0D);

        numberaxis.setRangeType(RangeType.POSITIVE);
        numberaxis.setLabelFont(UIConstants.H5_FONT);
        numberaxis.setLabelInsets(new RectangleInsets(0, 0, 0, 0));
        numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        return jfreechart;
    }

    public static TickUnitSource createLargeNumberTickUnits() {
        TickUnits units = new TickUnits();
        DecimalFormat df0 = new DecimalFormat("0");
        DecimalFormat df1 = new DecimalFormat("#,##0");
        DecimalFormat df2 = new DecimalFormat("0.##E0");
        units.add(new NumberTickUnit(1, df0, 2));
        units.add(new NumberTickUnit(2, df0, 2));
        units.add(new NumberTickUnit(5, df0, 5));
        units.add(new NumberTickUnit(10, df0, 2));
        units.add(new NumberTickUnit(20, df0, 2));
        units.add(new NumberTickUnit(50, df0, 5));
        units.add(new NumberTickUnit(100, df0, 2));
        units.add(new NumberTickUnit(200, df0, 2));
        units.add(new NumberTickUnit(500, df0, 5));
        units.add(new NumberTickUnit(1000, df1, 2));
        units.add(new NumberTickUnit(2000, df1, 2));
        units.add(new NumberTickUnit(5000, df1, 5));
        units.add(new NumberTickUnit(10000, df1, 2));
        units.add(new NumberTickUnit(20000, df1, 2));
        units.add(new NumberTickUnit(50000, df1, 5));
        units.add(new NumberTickUnit(100000, df1, 2));
        units.add(new NumberTickUnit(200000, df1, 2));
        units.add(new NumberTickUnit(500000, df1, 5));
        units.add(new NumberTickUnit(1000000, df2, 2));
        units.add(new NumberTickUnit(2000000, df2, 2));
        units.add(new NumberTickUnit(5000000, df2, 5));
        units.add(new NumberTickUnit(10000000, df2, 2));
        units.add(new NumberTickUnit(20000000, df2, 2));
        units.add(new NumberTickUnit(50000000, df2, 5));
        units.add(new NumberTickUnit(100000000, df2, 2));
        units.add(new NumberTickUnit(200000000, df2, 2));
        units.add(new NumberTickUnit(500000000, df2, 5));
        units.add(new NumberTickUnit(1000000000, df2, 2));
        units.add(new NumberTickUnit(2000000000, df2, 2));
        units.add(new NumberTickUnit(5000000000.0, df2, 5));
        units.add(new NumberTickUnit(10000000000.0, df2, 2));
        return units;
    }
}