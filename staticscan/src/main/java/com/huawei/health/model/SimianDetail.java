package com.huawei.health.model;

import java.util.ArrayList;
import java.util.List;

public class SimianDetail
{
    private String lineCount;

    private List<SimianItem> simianItem = new ArrayList<SimianItem>();

    public String getLineCount()
    {
        return lineCount;
    }

    public void setLineCount(String lineCount)
    {
        this.lineCount = lineCount;
    }

    public List<SimianItem> getSimianItem()
    {
        return simianItem;
    }

    public void setSimianItem(List<SimianItem> simianItem)
    {
        this.simianItem = simianItem;
    }
    
}
