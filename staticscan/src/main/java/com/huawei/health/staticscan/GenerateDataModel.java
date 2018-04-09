package com.huawei.health.staticscan;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.huawei.health.constants.Constants;
import com.huawei.health.model.BugPatternObject;
import com.huawei.health.model.CheckStyleDataModel;
import com.huawei.health.model.CheckStyleFileItem;
import com.huawei.health.model.CheckStyleItem;
import com.huawei.health.model.CommonConfig;
import com.huawei.health.model.FindbugsDataModel;
import com.huawei.health.model.FindbugsInstance;
import com.huawei.health.model.PmdDataModel;
import com.huawei.health.model.PmdFileItem;
import com.huawei.health.model.PmdViolationItem;
import com.huawei.health.model.SimianDataModel;
import com.huawei.health.model.SimianDetail;
import com.huawei.health.model.SimianItem;
import com.huawei.health.model.SourceMonitorDataModel;
import com.huawei.health.model.SourceMonitorItem;

public class GenerateDataModel
{
    private final static SAXReader reader = new SAXReader();

    public static CheckStyleDataModel CheckStyleResult() throws DocumentException
    {
        CheckStyleDataModel checkStyleDataModel = new CheckStyleDataModel();
        
        for (String eachCodeHome : CommonConfig.getInstance().getSubModuleList())
        {
            Document document = reader.read(new File(eachCodeHome + Constants.CHECKSTYLE_FILE));
            Element checkstyle_element = document.getRootElement();
            for (Iterator<?> it = checkstyle_element.elementIterator(); it.hasNext();)
            {
                Element file_element = (Element)it.next();
                if (file_element != null)
                {
                    CheckStyleFileItem checkStyleFileItem = new CheckStyleFileItem();
                    checkStyleFileItem.setFileName(file_element.attributeValue("name"));

                    Iterator<?> item = file_element.elementIterator();
                    while (item.hasNext())
                    {
                        
                        Element itemEachFile = (Element)item.next();
                        CheckStyleItem oneCheckStyleItem = new CheckStyleItem();
                        oneCheckStyleItem.setSeverity(itemEachFile.attributeValue("severity"));
                        oneCheckStyleItem.setLine(itemEachFile.attributeValue("line"));
                        oneCheckStyleItem.setColumn(itemEachFile.attributeValue("column"));
                        oneCheckStyleItem.setMessage(itemEachFile.attributeValue("message"));

                        checkStyleFileItem.getItems().add(oneCheckStyleItem);
                    }
                    
                    if (!checkStyleFileItem.getItems().isEmpty())
                    {
                        checkStyleDataModel.getFiles().add(checkStyleFileItem);
                    }
                }
            }
        }

        return checkStyleDataModel;
    }

    public static SimianDataModel SimianResult() throws DocumentException
    {
        SimianDataModel simianDataModel = new SimianDataModel();
        
        int duplicateFileCount = 0;
        int duplicateLineCount = 0;
        int duplicateBlockCount = 0;
        int totalFileCount = 0;
        int totalRawLineCount = 0;
        int totalSignificantLineCount = 0;
        
        for (String eachCodeHome : CommonConfig.getInstance().getSubModuleList())
        {
            Document document = reader.read(new File(eachCodeHome + Constants.SIMIAN_FILE));
            Element root_simian = document.getRootElement();
            Element check_element = root_simian.element("check");
            
            for (Iterator<?> it = check_element.elementIterator(); it.hasNext();)
            {
                Element set_element = (Element)it.next();
                if ("set".equals(set_element.getName()))
                {
                    SimianDetail simianDetail = new SimianDetail();
                    simianDetail.setLineCount(set_element.attributeValue("lineCount"));
                    
                    Iterator<?> setIt = set_element.elementIterator();
                    while (setIt.hasNext())
                    {
                        Element block_element = (Element)setIt.next();
                        
                        SimianItem simianItem = new SimianItem();
                        simianItem.setSourceFile(block_element.attributeValue("sourceFile"));
                        simianItem.setLocation("Line" + block_element.attributeValue("startLineNumber") + " - " + "Line"
                            + block_element.attributeValue("endLineNumber"));
                        
                        simianDetail.getSimianItem().add(simianItem);
                    }
                    
                    if (!simianDetail.getSimianItem().isEmpty())
                    {
                        simianDataModel.getSimianDetail().add(simianDetail);
                    }
                }
                else if ("summary".equals(set_element.getName()))
                {
                    duplicateFileCount += set_element.attributeValue("duplicateFileCount") == null ? 0
                        : Integer.parseInt(set_element.attributeValue("duplicateFileCount"));
                    duplicateLineCount += set_element.attributeValue("duplicateLineCount") == null ? 0
                        : Integer.parseInt(set_element.attributeValue("duplicateLineCount"));
                    duplicateBlockCount += set_element.attributeValue("duplicateBlockCount") == null ? 0
                        : Integer.parseInt(set_element.attributeValue("duplicateBlockCount"));
                    totalFileCount += set_element.attributeValue("totalFileCount") == null ? 0
                        : Integer.parseInt(set_element.attributeValue("totalFileCount"));
                    totalRawLineCount += set_element.attributeValue("totalRawLineCount") == null ? 0
                        : Integer.parseInt(set_element.attributeValue("totalRawLineCount"));
                    totalSignificantLineCount += set_element.attributeValue("totalSignificantLineCount") == null ? 0
                        : Integer.parseInt(set_element.attributeValue("totalSignificantLineCount"));
                }
            }
        }
        
        simianDataModel.setDuplicateFileCount(String.valueOf(duplicateFileCount));
        simianDataModel.setDuplicateLineCount(String.valueOf(duplicateLineCount));
        simianDataModel.setDuplicateBlockCount(String.valueOf(duplicateBlockCount));
        simianDataModel.setTotalFileCount(String.valueOf(totalFileCount));
        simianDataModel.setTotalRawLineCount(String.valueOf(totalRawLineCount));
        simianDataModel.setTotalSignificantLineCount(String.valueOf(totalSignificantLineCount));
        
        return simianDataModel;
    }

    public static PmdDataModel PMDResult() throws DocumentException
    {
        PmdDataModel pmdDataModel = new PmdDataModel();
        
        for (String eachCodeHome : CommonConfig.getInstance().getSubModuleList())
        {
            Document document = reader.read(new File(eachCodeHome + Constants.PMD_FILE));
            Element root_pmd = document.getRootElement();
            for (Iterator<?> it = root_pmd.elementIterator(); it.hasNext();)
            {
                Element file_element = (Element)it.next();
                if (file_element != null)
                {
                    PmdFileItem pmdFileItem = new PmdFileItem();
                    pmdFileItem.setFileName(file_element.attributeValue("name"));
                    
                    Iterator<?> fileIt = file_element.elementIterator();
                    
                    while (fileIt.hasNext())
                    {
                        Element violation_element = (Element)fileIt.next();

                        PmdViolationItem pmdViolationItem = new PmdViolationItem();
                        pmdViolationItem.setPriority(violation_element.attributeValue("priority"));
                        pmdViolationItem.setBeginline(violation_element.attributeValue("beginline"));
                        pmdViolationItem.setEndline(violation_element.attributeValue("endline"));
                        pmdViolationItem.setRule(violation_element.attributeValue("rule"));
                        pmdViolationItem.setRuleset(violation_element.attributeValue("ruleset"));
                        pmdViolationItem.setContent(violation_element.getText());
                        
                        pmdFileItem.getPmdViolationItem().add(pmdViolationItem);
                    }

                    if (!pmdFileItem.getPmdViolationItem().isEmpty())
                    {
                        pmdDataModel.getPmdFileItem().add(pmdFileItem);
                    }
                }
            }
            
        }

        return pmdDataModel;
    }
    
    public static SourceMonitorDataModel SourceMonitorResult() throws DocumentException
    {
        SourceMonitorDataModel sourceMonitorDataModel = new SourceMonitorDataModel();

        int file_counts = 0;
        int maxComplexityForAllFiles = 0;
        for (String eachCodeHome : CommonConfig.getInstance().getSubModuleList())
        {
            Document document = reader.read(new File(eachCodeHome + Constants.SOURCEMONITOR_FILE));
            Element root_sourcemonitor_metrics = document.getRootElement();
            Element project_element = root_sourcemonitor_metrics.element("project");
            Element checkpoints_element = project_element.element("checkpoints");
            Element checkpoint_element = checkpoints_element.element("checkpoint");
            Element files_element = checkpoint_element.element("files");

            if (files_element != null)
            {
                int file_count_temp = files_element.attributeValue("file_count") == null ? 0
                    : Integer.parseInt(files_element.attributeValue("file_count"));
                file_counts += file_count_temp;

                for (Iterator<?> it = files_element.elementIterator(); it.hasNext();)
                {
                    Element file_element = (Element)it.next();

                    if (file_element != null)
                    {
                        Element metrics_element = file_element.element("method_metrics");
                        if (metrics_element != null)
                        {
                            List<?> methods = metrics_element.elements("method");
                            for (Iterator<?> methodsIt = methods.iterator(); methodsIt.hasNext();)
                            {
                                Element one_method = (Element)methodsIt.next();

                                if (one_method != null)
                                {
                                    String one_method_complexity = one_method.element("complexity").getText();
                                    int one_method_complexity_temp =
                                        one_method_complexity == null ? 0 : Integer.parseInt(one_method_complexity);
                                    if (one_method_complexity_temp > maxComplexityForAllFiles)
                                    {
                                        maxComplexityForAllFiles = one_method_complexity_temp;
                                    }

                                    if (one_method_complexity_temp > CommonConfig.getInstance().getSourcemonitor())
                                    {
                                        SourceMonitorItem sourceMonitorItem = new SourceMonitorItem();
                                        sourceMonitorItem.setFileName(file_element.attributeValue("file_name"));
                                        sourceMonitorItem.setMaxComplexity(one_method_complexity);
                                        sourceMonitorItem.setMethodName(one_method.attributeValue("name"));

                                        sourceMonitorDataModel.getAbnormalItems().add(sourceMonitorItem);
                                        sourceMonitorDataModel.setNormal(false);
                                    }
                                }
                            }
                        }
                    }
                }
                
            }
        }
        
        sourceMonitorDataModel.setMaxComplexityForAllFiles(String.valueOf(maxComplexityForAllFiles));
        sourceMonitorDataModel.setFile_count(String.valueOf(file_counts));
        return sourceMonitorDataModel;
    }

    public static FindbugsDataModel FindbugsResult() throws DocumentException
    {
        FindbugsDataModel findbugsDataModel = new FindbugsDataModel();
        
        int total_find_bugs = 0;

        for (String eachCodeHome : CommonConfig.getInstance().getSubModuleList())
        {
            Document document = reader.read(new File(eachCodeHome + Constants.FINDBUGS_FILE));
            Element root_bug_collection = document.getRootElement();
            Element findbugs_summary_element = root_bug_collection.element("FindBugsSummary");
            String total_bugs = findbugs_summary_element.attributeValue("total_bugs");
            //            findbugsDataModel.setTotalBugs(total_bugs);
            int total_bugs_temp = total_bugs == null ? 0 : Integer.parseInt(total_bugs);
            total_find_bugs += total_bugs_temp;
            
            if (total_bugs_temp > 0)
            {
                List<BugPatternObject> allBugPatternObjects = new ArrayList<BugPatternObject>();
                
                List<?> bugPattern_elements = root_bug_collection.elements("BugPattern");
                for (Iterator<?> it = bugPattern_elements.iterator(); it.hasNext();)
                {
                    Element one_bug_pattern_element = (Element)it.next();
                    if (one_bug_pattern_element != null)
                    {
                        BugPatternObject one_bug_pattern_object = new BugPatternObject();
                        one_bug_pattern_object.setAbbrev(one_bug_pattern_element.attributeValue("abbrev"));
                        one_bug_pattern_object.setCategory(one_bug_pattern_element.attributeValue("category"));
                        one_bug_pattern_object.setType(one_bug_pattern_element.attributeValue("type"));
                        one_bug_pattern_object
                            .setShortDescription(one_bug_pattern_element.element("ShortDescription").getText());
                        
                        String details = one_bug_pattern_element.element("Details").getText();
                        String regEx_html = "<[^>]+>"; //定义HTML标签的正则表达式
                        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
                        Matcher m_html = p_html.matcher(details);
                        details = m_html.replaceAll(""); //过滤html标签
                        one_bug_pattern_object.setDetailsDescription(details);
                        
                        allBugPatternObjects.add(one_bug_pattern_object);
                    }
                }
                
                List<?> bugInstance_elements = root_bug_collection.elements("BugInstance");
                for (Iterator<?> it = bugInstance_elements.iterator(); it.hasNext();)
                {
                    Element one_bug_instance_element = (Element)it.next();
                    
                    if (one_bug_instance_element != null)
                    {
                        FindbugsInstance one_bug_instance = new FindbugsInstance();
                        
                        String abbrev = one_bug_instance_element.attributeValue("abbrev");
                        one_bug_instance.setAbbrev(abbrev);
                        
                        String category = one_bug_instance_element.attributeValue("category");
                        one_bug_instance.setCategory(category);
                        
                        String type = one_bug_instance_element.attributeValue("type");
                        one_bug_instance.setType(type);
                        
                        Element long_message = one_bug_instance_element.element("LongMessage");
                        
                        if (long_message != null)
                        {
                            one_bug_instance.setLongMessage(long_message.getText());
                        }
                        
                        if (one_bug_instance_element.element("Class") != null)
                        {
                            if (one_bug_instance_element.element("Class").element("Message") != null)
                            {
                                one_bug_instance.setClassName(
                                    one_bug_instance_element.element("Class").element("Message").getText());
                            }
                        }
                        
                        String method_name = null;
                        if (one_bug_instance_element.element("Method") != null)
                        {
                            if (one_bug_instance_element.element("Method").element("Message") != null)
                            {
                                method_name = one_bug_instance_element.element("Method").element("Message").getText();
                            }
                        }
                        else if (one_bug_instance_element.element("Field") != null)
                        {
                            if (one_bug_instance_element.element("Field").element("Message") != null)
                            {
                                method_name = one_bug_instance_element.element("Field").element("Message").getText();
                            }
                            
                        }
                        one_bug_instance.setMethodName(method_name);
                        
                        if (one_bug_instance_element.element("SourceLine") != null)
                        {
                            if (one_bug_instance_element.element("SourceLine").element("Message") != null)
                            {
                                one_bug_instance.setLine(
                                    one_bug_instance_element.element("SourceLine").element("Message").getText());
                            }
                        }
                        
                        for (BugPatternObject onePatternObject : allBugPatternObjects)
                        {
                            if (abbrev.equals(onePatternObject.getAbbrev())
                                && category.equals(onePatternObject.getCategory())
                                && type.equals(onePatternObject.getType()))
                            {
                                one_bug_instance.setBugPattern(onePatternObject);
                            }
                        }
                        
                        findbugsDataModel.getBugsInstances().add(one_bug_instance);
                    }
                }
            }
        }
        
        findbugsDataModel.setTotalBugs(String.valueOf(total_find_bugs));
        
        return findbugsDataModel;
    }
}