package com.huawei.health.model;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;

import com.huawei.health.utils.TableDataModelUtil;

public class DetailModelObject extends JPanel
{
    /**
     * 注释内容
     */
    private static final long serialVersionUID = 1L;

    public DetailModelObject(String entryNamePrefix, String entryName, String[] title, List<Vector<String>> vectotList,
        int jspHeight, boolean isFindBugDetail, String[] findbugSuggestTitle,
        List<Vector<String>> findbugSuggestRowList)
    {
        this.setLayout(new GridBagLayout());
        
        GridBagConstraints c = new GridBagConstraints();
        JLabel entry_prefix = new JLabel(entryNamePrefix);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        this.add(entry_prefix, c);
        
        if (entryName != null)
        {
            JTextField entry = new JTextField(entryName);
            c.fill = GridBagConstraints.BOTH;
            c.gridx = 1;
            c.gridy = 0;
            c.gridwidth = 1;
            c.weightx = 1;
            this.add(entry, c);
        }
        
        JTable jtable = new JTable(TableDataModelUtil.getTableDateModle(title, vectotList));
        JScrollPane jsp = new JScrollPane(jtable);
        jsp.setPreferredSize(new Dimension(1200, jspHeight));
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        c.weightx = 1;
        this.add(jsp, c);
        
        if (isFindBugDetail)
        {
            final AbstractTableModel tableDataModel =
                TableDataModelUtil.getTableDateModle(findbugSuggestTitle, findbugSuggestRowList);
            JTable jtable_findbug_suggest = new JTable(tableDataModel);
            
            jtable_findbug_suggest.addMouseListener(new MouseAdapter()
            {
                @Override
                public void mouseClicked(MouseEvent e)
                {
                    if (e.getClickCount() == 1)
                    {
                        int row = ((JTable)e.getSource()).rowAtPoint(e.getPoint()); //获得行位置
                        int col = ((JTable)e.getSource()).columnAtPoint(e.getPoint()); //获得列位置
                        if (row == 3)
                        {
                            String cellVal = (String)(tableDataModel.getValueAt(row, col));
                            JOptionPane.showMessageDialog(null, cellVal, "详细提示", JOptionPane.WARNING_MESSAGE);
                            //                            JOptionPane.showMessageDialog(null,
                            //                                new JLabel("<html><h2><p><font color='red'>" + cellVal + "</font></p></h2></html>"));
                        }
                    }
                }
            });
            
            JScrollPane jsp_findbug_suggest = new JScrollPane(jtable_findbug_suggest);
            jsp_findbug_suggest.setPreferredSize(new Dimension(1200, jspHeight));
            c.fill = GridBagConstraints.BOTH;
            c.gridx = 0;
            c.gridy = 2;
            c.gridwidth = 2;
            c.weightx = 1;
            this.add(jsp_findbug_suggest, c);
        }
    }
}
