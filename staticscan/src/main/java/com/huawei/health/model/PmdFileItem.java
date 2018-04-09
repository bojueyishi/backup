package com.huawei.health.model;

import java.util.ArrayList;
import java.util.List;

public class PmdFileItem
{
    private String fileName;
    
    private List<PmdViolationItem> pmdViolationItem = new ArrayList<PmdViolationItem>();
    
    public String getFileName()
    {
        return fileName;
    }
    
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }
    
    public List<PmdViolationItem> getPmdViolationItem()
    {
        return pmdViolationItem;
    }
    
    public void setPmdViolationItem(List<PmdViolationItem> pmdViolationItem)
    {
        this.pmdViolationItem = pmdViolationItem;
    }
    
}
