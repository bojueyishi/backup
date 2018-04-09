package com.huawei.health.model;

public class CheckStyleItem
{
    private String Severity;
    
    private String Line;
    
    private String Column;
    
    private String Message;

    public String getSeverity()
    {
        return Severity;
    }

    public void setSeverity(String severity)
    {
        Severity = severity;
    }

    public String getLine()
    {
        return Line;
    }

    public void setLine(String line)
    {
        Line = line;
    }

    public String getColumn()
    {
        return Column;
    }

    public void setColumn(String column)
    {
        Column = column;
    }

    public String getMessage()
    {
        return Message;
    }

    public void setMessage(String message)
    {
        Message = message;
    }
}
