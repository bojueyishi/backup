package com.huawei.health.model;

import java.util.ArrayList;
import java.util.List;

public class SimianDataModel
{
    private String duplicateFileCount;
    
    private String duplicateLineCount;
    
    private String duplicateBlockCount;
    
    private String totalFileCount;
    
    private String totalRawLineCount;
    
    private String totalSignificantLineCount;
    
    private List<SimianDetail> simianDetail = new ArrayList<SimianDetail>();

    public String getDuplicateFileCount()
    {
        return duplicateFileCount;
    }

    public void setDuplicateFileCount(String duplicateFileCount)
    {
        this.duplicateFileCount = duplicateFileCount;
    }

    public String getDuplicateLineCount()
    {
        return duplicateLineCount;
    }

    public void setDuplicateLineCount(String duplicateLineCount)
    {
        this.duplicateLineCount = duplicateLineCount;
    }

    public String getDuplicateBlockCount()
    {
        return duplicateBlockCount;
    }

    public void setDuplicateBlockCount(String duplicateBlockCount)
    {
        this.duplicateBlockCount = duplicateBlockCount;
    }

    public String getTotalFileCount()
    {
        return totalFileCount;
    }

    public void setTotalFileCount(String totalFileCount)
    {
        this.totalFileCount = totalFileCount;
    }

    public String getTotalRawLineCount()
    {
        return totalRawLineCount;
    }

    public void setTotalRawLineCount(String totalRawLineCount)
    {
        this.totalRawLineCount = totalRawLineCount;
    }

    public String getTotalSignificantLineCount()
    {
        return totalSignificantLineCount;
    }

    public void setTotalSignificantLineCount(String totalSignificantLineCount)
    {
        this.totalSignificantLineCount = totalSignificantLineCount;
    }

    public List<SimianDetail> getSimianDetail()
    {
        return simianDetail;
    }

    public void setSimianDetail(List<SimianDetail> simianDetail)
    {
        this.simianDetail = simianDetail;
    }

}
