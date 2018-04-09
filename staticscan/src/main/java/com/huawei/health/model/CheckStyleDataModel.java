package com.huawei.health.model;

import java.util.ArrayList;
import java.util.List;

public class CheckStyleDataModel
{
    List<CheckStyleFileItem> files = new ArrayList<CheckStyleFileItem>();

    public List<CheckStyleFileItem> getFiles()
    {
        return files;
    }

    public void setFiles(List<CheckStyleFileItem> files)
    {
        this.files = files;
    }
}
