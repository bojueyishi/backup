package com.huawei.health.model;

import java.util.ArrayList;
import java.util.List;

public class FindbugsDataModel
{
    private String totalBugs;

    private List<FindbugsInstance> bugsInstances = new ArrayList<FindbugsInstance>();
    
    public String getTotalBugs()
    {
        return totalBugs;
    }

    public void setTotalBugs(String totalBugs)
    {
        this.totalBugs = totalBugs;
    }

    public List<FindbugsInstance> getBugsInstances()
    {
        return bugsInstances;
    }

    public void setBugsInstances(List<FindbugsInstance> bugsInstances)
    {
        this.bugsInstances = bugsInstances;
    }
    
}
