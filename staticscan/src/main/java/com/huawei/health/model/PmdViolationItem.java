package com.huawei.health.model;

public class PmdViolationItem
{
    private String priority;

    private String beginline;

    private String endline;

    private String rule;

    private String ruleset;

    private String content;

    public String getPriority()
    {
        return priority;
    }

    public void setPriority(String priority)
    {
        this.priority = priority;
    }

    public String getBeginline()
    {
        return beginline;
    }

    public void setBeginline(String beginline)
    {
        this.beginline = beginline;
    }

    public String getEndline()
    {
        return endline;
    }

    public void setEndline(String endline)
    {
        this.endline = endline;
    }

    public String getRule()
    {
        return rule;
    }

    public void setRule(String rule)
    {
        this.rule = rule;
    }

    public String getRuleset()
    {
        return ruleset;
    }

    public void setRuleset(String ruleset)
    {
        this.ruleset = ruleset;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }
}
