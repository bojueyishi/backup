package com.huawei.health.model;

import java.util.ArrayList;
import java.util.List;

public class SourceMonitorDataModel
{
    private String file_count;

    private boolean isNormal = true;
    
    private String maxComplexityForAllFiles;

    private List<SourceMonitorItem> abnormalItems = new ArrayList<SourceMonitorItem>();

    public String getFile_count()
    {
        return file_count;
    }

    public void setFile_count(String file_count)
    {
        this.file_count = file_count;
    }

    public boolean isNormal()
    {
        return isNormal;
    }

    public void setNormal(boolean isNormal)
    {
        this.isNormal = isNormal;
    }
    
    public List<SourceMonitorItem> getAbnormalItems()
    {
        return abnormalItems;
    }
    
    public void setAbnormalItems(List<SourceMonitorItem> abnormalItems)
    {
        this.abnormalItems = abnormalItems;
    }
    
    public String getMaxComplexityForAllFiles()
    {
        return maxComplexityForAllFiles;
    }
    
    public void setMaxComplexityForAllFiles(String maxComplexityForAllFiles)
    {
        this.maxComplexityForAllFiles = maxComplexityForAllFiles;
    }
}
