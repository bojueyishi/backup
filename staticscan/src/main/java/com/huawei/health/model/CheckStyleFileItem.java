package com.huawei.health.model;

import java.util.ArrayList;
import java.util.List;

public class CheckStyleFileItem
{
    private String fileName;
    
    private List<CheckStyleItem> items = new ArrayList<CheckStyleItem>();
    
    public String getFileName()
    {
        return fileName;
    }
    
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }
    
    public List<CheckStyleItem> getItems()
    {
        return items;
    }
    
    public void setItems(List<CheckStyleItem> items)
    {
        this.items = items;
    }
    
}
