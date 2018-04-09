package com.huawei.health.model;

public class BugPatternObject
{
    private String abbrev;
    
    private String category;

    private String type;

    private String shortDescription;
    
    private String detailsDescription;

    public String getAbbrev()
    {
        return abbrev;
    }

    public void setAbbrev(String abbrev)
    {
        this.abbrev = abbrev;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }
    
    public String getShortDescription()
    {
        return shortDescription;
    }
    
    public void setShortDescription(String shortDescription)
    {
        this.shortDescription = shortDescription;
    }
    
    public String getDetailsDescription()
    {
        return detailsDescription;
    }
    
    public void setDetailsDescription(String detailsDescription)
    {
        this.detailsDescription = detailsDescription;
    }
    
}
