package com.huawei.health.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.huawei.health.model.CommonConfig;

public class CommonUtil
{
    private final static SAXReader reader = new SAXReader();
    
    public static int checkCodeHome(String codeHome) throws DocumentException
    {
        if (codeHome == null || codeHome.trim().equals(""))
        {
            return 1;
        }
        else
        {
            File codeDir = new File(codeHome);
            if (!codeDir.exists())
            {
                return 2;
            }
            else
            {
                //之前的版本针对src+pom.xml的情况，现在要兼容pom.xml中有modules标签的情况
                boolean srcExist = checkFileExist(codeDir, "src");
                boolean pomExist = checkFileExist(codeDir, "pom.xml");
                //1、第一种情况是src+pom.xml的情况，即是一级工程
                if (srcExist && pomExist)
                {
                    CommonConfig.getInstance().getSubModuleList().add(codeHome);
                }
                else
                {
                    if (pomExist)
                    {
                        //如果只有pom.xml，没有src的时候，要去判断pom.xml中有没有modules标签
                        if (!checkModules(codeHome))
                        {
                            return 3;
                        }
                    }
                    else
                    {
                        return 3;
                    }
                }
            }
        }
        return 0;
    }

    public static boolean checkFileExist(File codeDir, String targetFileName)
    {
        if (codeDir != null)
        {
            boolean isValid = false;
            for (File file : codeDir.listFiles())
            {
                if (file.getName().equals(targetFileName))
                {
                    isValid = true;
                    return isValid;
                }
            }
        }
        
        return false;
    }
    
    public static boolean checkModules(String codeHome) throws DocumentException
    {
        Document document = reader.read(new File(codeHome + "\\pom.xml"));
        Element project_element = document.getRootElement();
        Element modules_element = project_element.element("modules");
        if (modules_element != null)
        {
            boolean moduleValid = true;
            if (modules_element.elementIterator().hasNext())
            {
                //如果pom.xml有modules标签，则解析modules，获取子module内容
                for (Iterator<?> it = modules_element.elementIterator(); it.hasNext();)
                {
                    Element module_element = (Element)it.next();
                    if (module_element != null)
                    {
                        if (module_element.getText() != null && module_element.getText().trim().length() != 0)
                        {
                            moduleValid = true;
                            CommonConfig.getInstance()
                                .getSubModuleList()
                                .add(codeHome + "\\" + module_element.getText().trim());
                        }
                        else
                        {
                            moduleValid = false;
                        }
                    }
                    
                }
            }
            else
            {
                return false;
            }
            return moduleValid;
        }
        else
        {
            return false;
        }
    }

    public static String executeCMD(String mvn_cmd, String cmd_home) throws InterruptedException
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            Process p = Runtime.getRuntime().exec(mvn_cmd, null, new File(cmd_home));
            
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            
            while ((line = br.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            p.waitFor();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
        return sb.toString();
    }
    
}
