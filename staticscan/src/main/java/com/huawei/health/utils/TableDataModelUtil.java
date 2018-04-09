package com.huawei.health.utils;

import java.util.List;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class TableDataModelUtil
{
    public static AbstractTableModel getTableDateModle(final String[] title, List<Vector<String>> vectotList)
    {
        final Vector<List<String>> vect = new Vector<List<String>>();// 声明一个向量对象
        AbstractTableModel tm = new AbstractTableModel()
        {
            private static final long serialVersionUID = 1L;
            
            public int getColumnCount()
            {
                return title.length;
            }// 取得表格列数
            
            public int getRowCount()
            {
                return vect.size();
            }// 取得表格行数
            
            public Object getValueAt(int row, int column)
            {
                if (!vect.isEmpty())
                {
                    return ((Vector<?>)vect.elementAt(row)).elementAt(column);
                }
                else
                {
                    return null;
                }
            }// 取得单元格中的属性值
            
            @Override
            public String getColumnName(int column)
            {
                return title[column];
            }// 设置表格列名
            
            @Override
            public void setValueAt(Object value, int row, int column)
            {
            } // 数据模型不可编辑，该方法设置为空
            
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return true;
            }// 设置单元格可编辑，为缺省实现
        };
        
        vect.removeAllElements();// 初始化向量对象
        tm.fireTableStructureChanged();// 更新表格内容
        
        for (Vector<String> echoVector : vectotList)
        {
            vect.add(echoVector);
        }
        
        tm.fireTableStructureChanged();
        
        return tm;
    }
}
