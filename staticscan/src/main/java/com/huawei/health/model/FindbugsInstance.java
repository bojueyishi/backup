package com.huawei.health.model;

public class FindbugsInstance
{
    private String abbrev;

    private String category;
    
    private String type;
    
    private String longMessage;

    private String className;
    
    private String methodName;

    private String line;
    
    private BugPatternObject bugPattern;

    public String getAbbrev()
    {
        return abbrev;
    }

    public void setAbbrev(String abbrev)
    {
        this.abbrev = abbrev;
    }

    public String getLongMessage()
    {
        return longMessage;
    }

    public void setLongMessage(String longMessage)
    {
        this.longMessage = longMessage;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public String getMethodName()
    {
        return methodName;
    }

    public void setMethodName(String methodName)
    {
        this.methodName = methodName;
    }

    public String getLine()
    {
        return line;
    }

    public void setLine(String line)
    {
        this.line = line;
    }
    
    public BugPatternObject getBugPattern()
    {
        return bugPattern;
    }
    
    public void setBugPattern(BugPatternObject bugPattern)
    {
        this.bugPattern = bugPattern;
    }
    
}
