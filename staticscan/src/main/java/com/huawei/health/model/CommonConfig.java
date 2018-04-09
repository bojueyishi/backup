package com.huawei.health.model;

import java.util.ArrayList;
import java.util.List;

public class CommonConfig
{
    private static CommonConfig instance;

    private int pmd = 0;

    private int findbugs = 0;
    
    private int checkstyle = 0;

    private double simian = 4;

    private int sourcemonitor = 15;
    
    private List<String> subModuleList = new ArrayList<String>();

    private CommonConfig()
    {
    }
    
    public static CommonConfig getInstance()
    {
        if (instance == null)
        {
            instance = new CommonConfig();
        }
        return instance;
    }
    
    public void setInstance(CommonConfig instanceInfo)
    {
        instance = instanceInfo;
    }
    
    public int getPmd()
    {
        return pmd;
    }
    
    public void setPmd(int pmd)
    {
        this.pmd = pmd;
    }
    
    public int getFindbugs()
    {
        return findbugs;
    }
    
    public void setFindbugs(int findbugs)
    {
        this.findbugs = findbugs;
    }
    
    public int getCheckstyle()
    {
        return checkstyle;
    }
    
    public void setCheckstyle(int checkstyle)
    {
        this.checkstyle = checkstyle;
    }
    
    public double getSimian()
    {
        return simian;
    }
    
    public void setSimian(double simian)
    {
        this.simian = simian;
    }
    
    public int getSourcemonitor()
    {
        return sourcemonitor;
    }
    
    public void setSourcemonitor(int sourcemonitor)
    {
        this.sourcemonitor = sourcemonitor;
    }
    
    public List<String> getSubModuleList()
    {
        return subModuleList;
    }
    
    public void setSubModuleList(List<String> subModuleList)
    {
        this.subModuleList = subModuleList;
    }
    
}
