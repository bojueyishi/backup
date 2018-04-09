package com.huawei.health.model;

import java.util.ArrayList;
import java.util.List;

public class PmdDataModel
{
    
    private List<PmdFileItem> pmdFileItem = new ArrayList<PmdFileItem>();
    
    public List<PmdFileItem> getPmdFileItem()
    {
        return pmdFileItem;
    }
    
    public void setPmdFileItem(List<PmdFileItem> pmdFileItem)
    {
        this.pmdFileItem = pmdFileItem;
    }
    
}
