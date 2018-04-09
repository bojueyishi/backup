package com.huawei.health.staticscan;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.dom4j.DocumentException;

import com.huawei.health.constants.Constants;
import com.huawei.health.model.BugPatternObject;
import com.huawei.health.model.CheckStyleDataModel;
import com.huawei.health.model.CheckStyleFileItem;
import com.huawei.health.model.CheckStyleItem;
import com.huawei.health.model.CommonConfig;
import com.huawei.health.model.DetailModelObject;
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
import com.huawei.health.utils.CommonUtil;
import com.huawei.health.utils.TableDataModelUtil;
import com.huawei.health.utils.UpdateDialog;

public class StaticScan
{
    private JFrame frame;

    private CheckStyleDataModel checkStyleDataModel;

    private SimianDataModel simianDataModel;

    private PmdDataModel pmdDataModel;

    private SourceMonitorDataModel sourceMonitorDataModel;

    private FindbugsDataModel findbugsDataModel;

    private JPanel jpanelSummary;

    private JTable jtableSummary;

    private JLabel linklabel;

    private JPanel jpanelSelect;

    private JPanel jpanelDetial;

    private JTabbedPane jTabbedpane;

    private JPanel jpanelCheckstyle;

    private JPanel jpanelSimian;

    private JPanel jpanelPMD;

    private JPanel jpanelSourceMonitor;

    private JPanel jpanelFindbugs;

    private boolean isCheckstyleOK = false;

    private boolean isSimianOK = false;

    private boolean isPMDOK = false;

    private boolean isSourceMonitorOK = false;

    private boolean isFindBugsOK = false;

    private String CODE_HOME = null;

    private JCheckBox checkstyle_checkbox;

    private JCheckBox simian_checkbox;

    private JCheckBox pmd_checkbox;

    private JCheckBox sourcemonitor_checkbox;

    private JCheckBox findbugs_checkbox;

    private JButton selectall_btn;

    private JButton execute_btn;

    private JScrollPane simianDetailJsp;

    private JScrollPane checkstyleDetailJsp;

    private JScrollPane pmdDetailJsp;

    private JScrollPane sourcemonitorDetailJsp;

    private JScrollPane findbugsDetailJsp;

    private JProgressBar progressBar;

    private JButton totalCostTime;

    private JLabel project_name;

    private StringBuilder coryright_intfo_message;

    private Font detailOkFont = new Font("宋体", Font.BOLD, 20);

    private Font themeFont = new Font("宋体", Font.BOLD, 15);

    private FileOutputStream out;

    private BufferedReader breader;

    private final String summaryTitle[] =
    {"指标项名称", "数值", "是否达标" }; //表格属性列名

    private final String checkStyleDetailTitle[] =
    {"Severity", "Line", "Column", "Message" }; //表格属性列名

    private final String simianDetailTitle[] =
    {"FileName", "Location" }; //表格属性列名

    private final String pmdDetailTitle[] =
    {"Priority", "Line", "Rule", "Ruleset", "Content" }; //表格属性列名

    private final String sourcemonitorDetailTitle[] =
    {"FileName", "MethodName", "MaxComplexity" }; //表格属性列名

    private final String findBugsDetailTitle[] =
    {"FindBugs详情" }; //表格属性列名

    /**
     * Launch the application.
     */
    public static void main(final String[] args)
    {
        EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                try
                {
                    StaticScan window = new StaticScan(args);
                    window.frame.setVisible(true);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     * @throws IOException
     * @throws DocumentException
     * @throws HeadlessException
     */
    public StaticScan(String[] args)
        throws IOException, HeadlessException, DocumentException
    {
        initialize(args);
    }

    /**
     * Initialize the contents of the frame.
     * @throws IOException
     * @throws DocumentException
     * @throws HeadlessException
     * @throws FileNotFoundException
     */
    private void initialize(String[] args) throws IOException, HeadlessException, DocumentException
    {
        if (args != null && args.length != 0)
        {
            CODE_HOME = args[0].replaceAll("\\\\", "\\\\\\\\");
            if (CommonUtil.checkCodeHome(CODE_HOME) == 3)
            {
                JOptionPane.showMessageDialog(null, "在无效的路径下触发了静态扫描", "错误", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }
        
        //读取指标配置文件
        File config_file = new File(Constants.LOCAL_CONFIG_FILE);
        if (!config_file.exists())
        {
            config_file.createNewFile();
            BufferedWriter bf = new BufferedWriter(new FileWriter(config_file));
            /*bf.write(
                "#注意:每个配置项冒号和值之间必须有一个空格.\n#另外,pmd、findbugs、checkstyle、sourcemonitor必须为整数,simian可以为小数\npmd: 0\nfindbugs: 0\ncheckstyle: 0\nsimian: 4\nsourcemonitor: 15");*/
            bf.write(
                "#Note:pmd findbugs checkstyle sourcemonitor must be int,simian can be double\npmd=0\nfindbugs=0\ncheckstyle=0\nsimian=4\nsourcemonitor=15");
            bf.flush();
            bf.close();
        }
        
        try
        {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(config_file));
            BufferedReader br = new BufferedReader(reader);
            String line = "";
            line = br.readLine();
            
            while (line != null)
            {
                if (line.trim() != "" && line.trim() != null && !line.contains("#"))
                {
                    String[] eachConfig = line.split("=");
                    String key = eachConfig[0];
                    String value = eachConfig[1];
                    
                    if (key.contains("pmd"))
                    {
                        CommonConfig.getInstance().setPmd(Integer.parseInt(value));
                    }
                    if (key.contains("findbugs"))
                    {
                        CommonConfig.getInstance().setFindbugs(Integer.parseInt(value));
                    }
                    if (key.contains("checkstyle"))
                    {
                        CommonConfig.getInstance().setCheckstyle(Integer.parseInt(value));
                    }
                    if (key.contains("sourcemonitor"))
                    {
                        CommonConfig.getInstance().setSourcemonitor(Integer.parseInt(value));
                    }
                    if (key.contains("simian"))
                    {
                        CommonConfig.getInstance().setSimian(Double.parseDouble(value));
                    }
                }
                
                line = br.readLine();
            }
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null, "config.ini解析失败,\n请检查config.ini格式!", "剁手", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(0);
        }
        
        frame = new JFrame();
        frame.setVisible(true);
        frame.setTitle("StaticScan");
        frame.setLayout(new BorderLayout());
        frame.setResizable(false);
        //frame.setIconImage(ImageIO.read(this.getClass().getResource("/img/chinasoft.jpg")));
        
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension scmSize = toolkit.getScreenSize();
        frame.setSize(scmSize.width, scmSize.height - 40);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JMenu jm = new JMenu("配置"); //创建JMenu菜单对象
        
        JMenuItem display_code_home_item = new JMenuItem("显示当前代码主目录"); //菜单项
        display_code_home_item.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JOptionPane.showMessageDialog(null, CODE_HOME, "当前代码主目录路径", JOptionPane.PLAIN_MESSAGE);
            }
        });
        
        jm.add(display_code_home_item); //将菜单项目添加到菜单
        
        JMenuItem set_scan_level_item = new JMenuItem("显示当前各扫描项指标"); //菜单项
        set_scan_level_item.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                String content = "PMD: " + CommonConfig.getInstance().getPmd() + "\n" + "FindBugs: "
                    + CommonConfig.getInstance().getFindbugs() + "\n" + "CheckStyle: "
                    + CommonConfig.getInstance().getCheckstyle() + "\n" + "Simian: "
                    + CommonConfig.getInstance().getSimian() + "\n" + "SourceMonitor: "
                    + CommonConfig.getInstance().getSourcemonitor();
                
                JOptionPane.showMessageDialog(null, content, "当前各扫描项指标值", JOptionPane.PLAIN_MESSAGE);
            }
        });
        
        jm.add(set_scan_level_item); //将菜单项目添加到菜单
        
        JMenu help = new JMenu("帮助"); //创建JMenu菜单对象
        
        JMenuItem update_item = new JMenuItem("更新"); //菜单项
        update_item.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                UpdateDialog update_dialog = new UpdateDialog(frame, true);
                update_dialog.setVisible(true);
            }
        });
        help.add(update_item);
        
        JMenuItem copyright_item = new JMenuItem("CopyRight"); //菜单项
        coryright_intfo_message = new StringBuilder();
        coryright_intfo_message.append("描述: 静态扫描工具" + "\n");
        coryright_intfo_message.append("作者: jWX373864" + "\n");
        coryright_intfo_message.append("发布时间: 2017-6-15" + "\n");
        coryright_intfo_message.append("更新时间: 2017-6-19" + "\n");
        
        copyright_item.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JOptionPane.showMessageDialog(null, coryright_intfo_message, "CopyRight", JOptionPane.PLAIN_MESSAGE);
            }
        });
        help.add(copyright_item);
        
        JMenuBar br = new JMenuBar(); //创建菜单工具栏
        br.add(jm); //将菜单增加到菜单工具栏
        br.add(help);
        frame.setJMenuBar(br); //为 窗体设置  菜单工具栏
        
        /*
         *左侧扫描项选择
        */
        jpanelSelect = new JPanel();
        jpanelSelect.setName("jpanelSelect");
        jpanelSelect.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(null, "操作面板", TitledBorder.CENTER, TitledBorder.TOP, themeFont),
            BorderFactory.createLoweredBevelBorder()));
        jpanelSelect.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(2, 15, 2, 0);
        JLabel project_label = new JLabel("当前工程名:");
        project_label.setFont(themeFont);
        jpanelSelect.add(project_label, c);
        
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(2, 15, 80, 0);
        if (CODE_HOME != null)
        {
            String[] path = CODE_HOME.split("\\\\");
            project_name = new JLabel(path[path.length - 1]);
            project_name.setFont(themeFont);
            jpanelSelect.add(project_name, c);
        }
        
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = 1;
        c.weightx = 2;
        c.gridx = 0;
        c.gridy = 2;
        c.insets = new Insets(2, 15, 2, 0);
        JLabel select_part_title = new JLabel("选择要扫描的指标项:");
        select_part_title.setFont(themeFont);
        jpanelSelect.add(select_part_title, c);
        
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 0;
        c.gridy = 3;
        pmd_checkbox = new JCheckBox("PMD");// 创建复选按钮
        pmd_checkbox.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent e)
            {
                Object obj = e.getItem();
                if (obj.equals(pmd_checkbox))
                {
                    if (!pmd_checkbox.isSelected())
                    {
                        selectall_btn.setText("全选");
                    }
                }
                isChangeAllToCancelStatus();
            }
        });
        
        jpanelSelect.add(pmd_checkbox, c);// 应用复选按钮
        
        c.gridx = 0;
        c.gridy = 4;
        findbugs_checkbox = new JCheckBox("FindBugs");// 创建复选按钮
        findbugs_checkbox.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent e)
            {
                Object obj = e.getItem();
                if (obj.equals(findbugs_checkbox))
                {
                    if (!findbugs_checkbox.isSelected())
                    {
                        selectall_btn.setText("全选");
                    }
                }
                isChangeAllToCancelStatus();
            }
        });
        jpanelSelect.add(findbugs_checkbox, c);// 应用复选按钮
        
        c.gridx = 0;
        c.gridy = 5;
        checkstyle_checkbox = new JCheckBox("CheckStyle");// 创建复选按钮
        checkstyle_checkbox.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent e)
            {
                Object obj = e.getItem();
                if (obj.equals(checkstyle_checkbox))
                {
                    if (!checkstyle_checkbox.isSelected())
                    {
                        selectall_btn.setText("全选");
                    }
                }
                isChangeAllToCancelStatus();
            }
        });
        jpanelSelect.add(checkstyle_checkbox, c);// 应用复选按钮
        
        c.gridx = 0;
        c.gridy = 6;
        simian_checkbox = new JCheckBox("Simian重复率");// 创建复选按钮
        simian_checkbox.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent e)
            {
                Object obj = e.getItem();
                if (obj.equals(simian_checkbox))
                {
                    if (!simian_checkbox.isSelected())
                    {
                        selectall_btn.setText("全选");
                    }
                }
                isChangeAllToCancelStatus();
            }
        });
        jpanelSelect.add(simian_checkbox, c);// 应用复选按钮
        
        c.gridx = 0;
        c.gridy = 7;
        sourcemonitor_checkbox = new JCheckBox("SourceMonitor圈复杂度");// 创建复选按钮
        sourcemonitor_checkbox.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent e)
            {
                Object obj = e.getItem();
                if (obj.equals(sourcemonitor_checkbox))
                {
                    if (!sourcemonitor_checkbox.isSelected())
                    {
                        selectall_btn.setText("全选");
                    }
                }
                isChangeAllToCancelStatus();
            }
        });
        jpanelSelect.add(sourcemonitor_checkbox, c);// 应用复选按钮
        
        c.anchor = GridBagConstraints.EAST;
        c.gridx = 0;
        c.gridy = 8;
        c.gridwidth = 1;
        c.weightx = 1;
        c.insets = new Insets(10, 0, 0, 10);
        selectall_btn = new JButton("全选");
        selectall_btn.setFocusPainted(false);
        selectall_btn.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (selectall_btn.getText().equals("全选"))
                {
                    checkstyle_checkbox.setSelected(true);
                    simian_checkbox.setSelected(true);
                    pmd_checkbox.setSelected(true);
                    sourcemonitor_checkbox.setSelected(true);
                    findbugs_checkbox.setSelected(true);
                    
                    selectall_btn.setText("取消");
                }
                else if (selectall_btn.getText().equals("取消"))
                {
                    checkstyle_checkbox.setSelected(false);
                    simian_checkbox.setSelected(false);
                    pmd_checkbox.setSelected(false);
                    sourcemonitor_checkbox.setSelected(false);
                    findbugs_checkbox.setSelected(false);
                    
                    selectall_btn.setText("全选");
                }
            }
        });
        
        jpanelSelect.add(selectall_btn, c);
        
        c.gridx = 1;
        c.gridy = 8;
        c.gridwidth = 2;
        c.weightx = 2;
        c.insets = new Insets(10, 0, 0, 10);
        execute_btn = new JButton("执行");
        execute_btn.setName("execute_btn");
        execute_btn.setFocusable(false);
        execute_btn.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                
                selectall_btn.setEnabled(false);
                execute_btn.setEnabled(false);
                checkstyle_checkbox.setEnabled(false);
                simian_checkbox.setEnabled(false);
                pmd_checkbox.setEnabled(false);
                sourcemonitor_checkbox.setEnabled(false);
                findbugs_checkbox.setEnabled(false);
                
                totalCostTime.setText("本次扫描花费时间: 00分00秒");
                totalCostTime.setFont(themeFont);
                
                isCheckstyleOK = false;
                isSimianOK = false;
                isPMDOK = false;
                isSourceMonitorOK = false;
                isFindBugsOK = false;
                
                List<Vector<String>> rowList = initSummaryData();
                jtableSummary.setModel(TableDataModelUtil.getTableDateModle(summaryTitle, rowList));
                DefaultTableCellRenderer r = new DefaultTableCellRenderer();
                r.setHorizontalAlignment(JLabel.CENTER);
                jtableSummary.getColumn("数值").setCellRenderer(r);
                jtableSummary.getColumn("是否达标").setCellRenderer(r);
                jtableSummary.repaint();
                
                jTabbedpane.setEnabled(false);
                
                jpanelCheckstyle.removeAll();
                jpanelSimian.removeAll();
                jpanelPMD.removeAll();
                jpanelSourceMonitor.removeAll();
                jpanelFindbugs.removeAll();
                
                JLabel suggestion = new JLabel("正在生成报告，请稍等...");
                suggestion.setFont(new Font("宋体", Font.BOLD, 20));
                
                progressBar = new JProgressBar();
                progressBar.setStringPainted(true); //显示提示信息
                progressBar.setIndeterminate(false); //确定进度的进度条
                progressBar.setMaximum(100);
                
                GridBagConstraints c = new GridBagConstraints();
                c.gridx = 0;
                c.gridy = 0;
                c.anchor = GridBagConstraints.CENTER;
                c.fill = GridBagConstraints.BOTH;
                
                //渲染选项卡当前选择项
                if (jTabbedpane.getSelectedIndex() == 0)
                {
                    jpanelCheckstyle.add(suggestion, c);
                    c.gridy = 1;
                    jpanelCheckstyle.add(progressBar, c);
                    jpanelCheckstyle.repaint();
                }
                else if (jTabbedpane.getSelectedIndex() == 1)
                {
                    jpanelSimian.add(suggestion, c);
                    c.gridy = 1;
                    jpanelSimian.add(progressBar, c);
                    jpanelSimian.repaint();
                }
                else if (jTabbedpane.getSelectedIndex() == 2)
                {
                    jpanelPMD.add(suggestion, c);
                    c.gridy = 1;
                    jpanelPMD.add(progressBar, c);
                    jpanelPMD.repaint();
                }
                else if (jTabbedpane.getSelectedIndex() == 3)
                {
                    jpanelSourceMonitor.add(suggestion, c);
                    c.gridy = 1;
                    jpanelSourceMonitor.add(progressBar, c);
                    jpanelSourceMonitor.repaint();
                }
                else if (jTabbedpane.getSelectedIndex() == 4)
                {
                    jpanelFindbugs.add(suggestion, c);
                    c.gridy = 1;
                    jpanelFindbugs.add(progressBar, c);
                    jpanelFindbugs.repaint();
                }
                
                Thread t = new Thread(new Runnable()
                {
                    public void run()
                    {
                        long start_time = System.currentTimeMillis();
                        progressBar.setValue(5);
                        
                        try
                        {
                            boolean isSuccess = executeGenerateXML();
                            if (!isSuccess)
                            {
                                jpanelCheckstyle.removeAll();
                                jpanelSimian.removeAll();
                                jpanelPMD.removeAll();
                                jpanelSourceMonitor.removeAll();
                                jpanelFindbugs.removeAll();
                                
                                selectall_btn.setEnabled(true);
                                execute_btn.setEnabled(true);
                                checkstyle_checkbox.setEnabled(true);
                                simian_checkbox.setEnabled(true);
                                pmd_checkbox.setEnabled(true);
                                sourcemonitor_checkbox.setEnabled(true);
                                findbugs_checkbox.setEnabled(true);
                                jTabbedpane.setEnabled(true);
                                
                                Thread.currentThread().stop();
                            }
                            progressBar.setValue(97);
                        }
                        catch (InterruptedException e1)
                        {
                            e1.printStackTrace();
                        }
                        
                        try
                        {
                            generateData();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                            //把老数据清掉
                            jpanelCheckstyle.removeAll();
                            jpanelSimian.removeAll();
                            jpanelPMD.removeAll();
                            jpanelSourceMonitor.removeAll();
                            jpanelFindbugs.removeAll();
                            
                            selectall_btn.setEnabled(true);
                            execute_btn.setEnabled(true);
                            checkstyle_checkbox.setEnabled(true);
                            simian_checkbox.setEnabled(true);
                            pmd_checkbox.setEnabled(true);
                            sourcemonitor_checkbox.setEnabled(true);
                            findbugs_checkbox.setEnabled(true);
                            jTabbedpane.setEnabled(true);
                            
                            checkStyleDataModel = null;
                            pmdDataModel = null;
                            simianDataModel = null;
                            sourceMonitorDataModel = null;
                            findbugsDataModel = null;
                            Thread.currentThread().stop();
                        }
                        
                        progressBar.setValue(98);
                        renderSummary();
                        progressBar.setValue(99);
                        
                        //渲染选项卡当前选择项
                        if (jTabbedpane.getSelectedIndex() == 0)
                        {
                            jpanelCheckstyle.removeAll();
                            renderCheckStyleDetial();
                        }
                        else if (jTabbedpane.getSelectedIndex() == 1)
                        {
                            jpanelSimian.removeAll();
                            renderSimianDetial();
                        }
                        else if (jTabbedpane.getSelectedIndex() == 2)
                        {
                            jpanelPMD.removeAll();
                            renderPmdDetial();
                        }
                        else if (jTabbedpane.getSelectedIndex() == 3)
                        {
                            jpanelSourceMonitor.removeAll();
                            renderSourceMonitorDetial();
                        }
                        else if (jTabbedpane.getSelectedIndex() == 4)
                        {
                            jpanelFindbugs.removeAll();
                            renderFindBugsDetial();
                        }
                        
                        selectall_btn.setEnabled(true);
                        execute_btn.setEnabled(true);
                        checkstyle_checkbox.setEnabled(true);
                        simian_checkbox.setEnabled(true);
                        pmd_checkbox.setEnabled(true);
                        sourcemonitor_checkbox.setEnabled(true);
                        findbugs_checkbox.setEnabled(true);
                        jTabbedpane.setEnabled(true);
                        long end_time = System.currentTimeMillis();
                        long total_seconds = (end_time - start_time) / 1000;
                        long minutes = total_seconds / 60;
                        long seconds = total_seconds % 60;
                        String cost_time = minutes + "分" + seconds + "秒";
                        totalCostTime.setText("本次扫描花费时间: " + cost_time);
                        totalCostTime.setFont(themeFont);
                    }
                });
                t.start();
                
            }
        });
        
        jpanelSelect.add(execute_btn, c);
        
        frame.add(jpanelSelect, BorderLayout.WEST);
        
        /*
         *各个指标summary展示
        */
        jpanelSummary = new JPanel();
        jpanelSummary.setLayout(new GridBagLayout());
        jpanelSummary.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(null, "总体结果展示面板", TitledBorder.CENTER, TitledBorder.TOP, themeFont),
            BorderFactory.createLoweredBevelBorder()));
        
        /*
         *各个指标详情展示选项卡
        */
        jpanelDetial = new JPanel();
        jpanelDetial.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(null, "详细结果展示面板", TitledBorder.CENTER, TitledBorder.TOP, themeFont),
            BorderFactory.createLoweredBevelBorder()));
        jpanelDetial.setLayout(new GridBagLayout());
        jTabbedpane = new JTabbedPane();// 存放选项卡的组件
        jTabbedpane.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent e)
            {
                JTabbedPane tabbedPane = (JTabbedPane)e.getSource();
                int selectedIndex = tabbedPane.getSelectedIndex();
                switch (selectedIndex)
                {
                    case 0:
                        renderCheckStyleDetial();
                        break;
                    case 1:
                        renderSimianDetial();
                        break;
                    case 2:
                        renderPmdDetial();
                        break;
                    case 3:
                        renderSourceMonitorDetial();
                        break;
                    case 4:
                        renderFindBugsDetial();
                        break;
                }
            }
        });
        
        String[] tabNames =
        {"CheckStyle", "Simian重复率", "PMD", "SourceMonitor圈复杂度", "FindBugs" };
        
        int i = 0;
        // 第一个标签下的JPanel
        jpanelCheckstyle = new JPanel();
        jpanelCheckstyle.setLayout(new GridBagLayout());
        checkstyleDetailJsp = new JScrollPane(jpanelCheckstyle);
        jTabbedpane.addTab(tabNames[i++], null, checkstyleDetailJsp, "first");// 加入第一个页面
        jTabbedpane.setMnemonicAt(0, KeyEvent.VK_0);// 设置第一个位置的快捷键为0
        
        // 第二个标签下的JPanel
        jpanelSimian = new JPanel();
        jpanelSimian.setLayout(new GridBagLayout());
        simianDetailJsp = new JScrollPane(jpanelSimian);
        jTabbedpane.addTab(tabNames[i++], null, simianDetailJsp, "second");// 加入第二个页面
        jTabbedpane.setMnemonicAt(1, KeyEvent.VK_1);// 设置快捷键为1
        
        // 第三个标签下的JPanel
        jpanelPMD = new JPanel();
        jpanelPMD.setLayout(new GridBagLayout());
        pmdDetailJsp = new JScrollPane(jpanelPMD);
        jTabbedpane.addTab(tabNames[i++], null, pmdDetailJsp, "third");// 加入第三个页面
        jTabbedpane.setMnemonicAt(2, KeyEvent.VK_2);// 设置快捷键为2
        
        // 第四个标签下的JPanel
        jpanelSourceMonitor = new JPanel();
        jpanelSourceMonitor.setLayout(new GridBagLayout());
        sourcemonitorDetailJsp = new JScrollPane(jpanelSourceMonitor);
        jTabbedpane.addTab(tabNames[i++], null, sourcemonitorDetailJsp, "fourth");// 加入第四个页面
        jTabbedpane.setMnemonicAt(3, KeyEvent.VK_3);// 设置快捷键为3
        
        // 第五个标签下的JPanel
        jpanelFindbugs = new JPanel();
        jpanelFindbugs.setLayout(new GridBagLayout());
        findbugsDetailJsp = new JScrollPane(jpanelFindbugs);
        jTabbedpane.addTab(tabNames[i++], null, findbugsDetailJsp, "fifth");// 加入第四个页面
        jTabbedpane.setMnemonicAt(4, KeyEvent.VK_4);// 设置快捷键为3
        
        GridBagConstraints anotherC = new GridBagConstraints();
        //        anotherC.gridx = 0;
        //        anotherC.gridy = 0;
        //        anotherC.anchor = GridBagConstraints.WEST;
        //        JLabel detial_label = new JLabel("详细结果:");
        //        detial_label.setFont(themeFont);
        //        jpanelDetial.add(detial_label, anotherC);
        
        anotherC.gridx = 0;
        anotherC.gridy = 0;
        anotherC.weightx = 1;
        anotherC.weighty = 1;
        anotherC.fill = GridBagConstraints.BOTH;
        jpanelDetial.add(jTabbedpane, anotherC);
        
        //jpanelDetial.setBackground(Color.red);
        
        frame.add(jpanelDetial, BorderLayout.CENTER);
        
        renderSummaryInit();
    }

    private void generateData() throws Exception
    {
        //先把老数据清掉
        this.checkStyleDataModel = null;
        this.pmdDataModel = null;
        this.simianDataModel = null;
        this.sourceMonitorDataModel = null;
        this.findbugsDataModel = null;

        //查看checkbox的选择状态，只生成选中的指标项的数据源
        if (checkstyle_checkbox.isSelected())
        {
            this.checkStyleDataModel = GenerateDataModel.CheckStyleResult();
        }
        if (pmd_checkbox.isSelected())
        {
            this.pmdDataModel = GenerateDataModel.PMDResult();
        }
        if (simian_checkbox.isSelected())
        {
            this.simianDataModel = GenerateDataModel.SimianResult();
        }
        if (sourcemonitor_checkbox.isSelected())
        {
            this.sourceMonitorDataModel = GenerateDataModel.SourceMonitorResult();
        }
        if (findbugs_checkbox.isSelected())
        {
            this.findbugsDataModel = GenerateDataModel.FindbugsResult();
        }
    }

    private void renderSummaryInit()
    {
        List<Vector<String>> rowList = initSummaryData();

        jtableSummary = new JTable(TableDataModelUtil.getTableDateModle(summaryTitle, rowList));// 生成自己的数据模型
        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
        r.setHorizontalAlignment(JLabel.CENTER);
        jtableSummary.getColumn("数值").setCellRenderer(r);
        jtableSummary.getColumn("是否达标").setCellRenderer(r);
        JScrollPane jsp_summary = new JScrollPane(jtableSummary);// 给表格加上滚动条
        jsp_summary.setPreferredSize(new Dimension(1000, 105));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        totalCostTime = new JButton("本次扫描花费时间: 00分00秒");
        totalCostTime.setFocusable(false);
        totalCostTime.setFont(themeFont);
        jpanelSummary.add(totalCostTime, c);

        /* c.gridx = 2;
        c.gridy = 0;
        c.weightx = 0;
        c.anchor = GridBagConstraints.EAST;
        linklabel = new JLabel("<html><h3><u>？有疑问点我</u></h3></html>");
        linklabel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                JOptionPane.showMessageDialog(null, "question", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        jpanelSummary.add(linklabel, c);*/

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 3;
        jpanelSummary.add(jsp_summary, c);

        frame.add(jpanelSummary, BorderLayout.NORTH);

    }

    private void renderSummary()
    {
        List<Vector<String>> rowList = new ArrayList<Vector<String>>();
        //如果pmdDataModel不为空，则表示pmd_checkbox选中了，则需要生成pmd summary数据
        if (pmdDataModel != null)
        {
            int pmd_summary = 0;
            String pmd_summary_comment = null;
            for (PmdFileItem pmdFileItem : pmdDataModel.getPmdFileItem())
            {
                pmd_summary += pmdFileItem.getPmdViolationItem().size();
            }

            if (pmd_summary > CommonConfig.getInstance().getPmd())
            {
                pmd_summary_comment = "不达标:PMD不能超过" + CommonConfig.getInstance().getPmd();
            }
            else
            {
                pmd_summary_comment = "PMD OK";
                isPMDOK = true;
            }

            Vector<String> pmd_result = new Vector<String>();
            pmd_result.add("PMD");
            pmd_result.add(String.valueOf(pmd_summary));
            pmd_result.add(pmd_summary_comment);

            rowList.add(pmd_result);
        }
        else
        {
            //如果pmdDataModel为空,则表示pmd_checkbox没有选中，不需要生成pmd summary数据
            Vector<String> pmd_result = new Vector<String>();
            pmd_result.add("PMD");
            pmd_result.add("-");
            pmd_result.add("-");

            rowList.add(pmd_result);
        }

        //如果findbugsDataModel不为空，则表示fingbugs_checkbox选中了，则需要生成fingbugs summary数据
        if (findbugsDataModel != null)
        {
            String findbugs_summary_comment = null;
            int total_bugs =
                findbugsDataModel.getTotalBugs() == null ? 0 : Integer.parseInt(findbugsDataModel.getTotalBugs());
            if (total_bugs > CommonConfig.getInstance().getFindbugs())
            {
                findbugs_summary_comment = "不达标:findbugs不能超过" + CommonConfig.getInstance().getFindbugs();
            }
            else
            {
                findbugs_summary_comment = "FindBugs OK";
                isFindBugsOK = true;
            }
            Vector<String> findbug_result = new Vector<String>();
            findbug_result.add("FindBugs");
            findbug_result.add(String.valueOf(total_bugs));
            findbug_result.add(findbugs_summary_comment);

            rowList.add(findbug_result);
        }
        else
        {
            //如果findbugsDataModel为空,则表示fingbugs_checkbox没有选中，不需要生成fingbugs summary数据
            Vector<String> findbug_result = new Vector<String>();
            findbug_result.add("FindBugs");
            findbug_result.add("-");
            findbug_result.add("-");

            rowList.add(findbug_result);
        }

        //如果checkStyleDataModel不为空，则表示checkstyle_checkbox选中了，则需要生成checkstyle summary数据
        if (checkStyleDataModel != null)
        {
            int checkstyle_summary = 0;
            String checkstyle_summary_comment = null;
            for (CheckStyleFileItem checkStyleFileItem : checkStyleDataModel.getFiles())
            {
                checkstyle_summary += checkStyleFileItem.getItems().size();
            }

            if (checkstyle_summary > CommonConfig.getInstance().getCheckstyle())
            {
                checkstyle_summary_comment = "不达标:CheckStyle不能超过" + CommonConfig.getInstance().getCheckstyle();
            }
            else
            {
                checkstyle_summary_comment = "CheckStyle OK";
                isCheckstyleOK = true;
            }

            Vector<String> checkstyle_result = new Vector<String>();
            checkstyle_result.add("CheckStyle");
            checkstyle_result.add(String.valueOf(checkstyle_summary));
            checkstyle_result.add(checkstyle_summary_comment);

            rowList.add(checkstyle_result);
        }
        else
        {
            //如果checkStyleDataModel为空,则表示checkstyle_checkbox没有选中，不需要生成checkstyle summary数据
            Vector<String> checkstyle_result = new Vector<String>();
            checkstyle_result.add("CheckStyle");
            checkstyle_result.add("-");
            checkstyle_result.add("-");

            rowList.add(checkstyle_result);
        }

        //如果simianDataModel不为空，则表示simian_checkbox选中了，则需要生成simian summary数据
        if (simianDataModel != null)
        {
            String simian_summary = null;
            String simian_summary_comment = null;

            double dup_line = simianDataModel.getDuplicateLineCount() == null ? 0
                : Double.parseDouble(simianDataModel.getDuplicateLineCount());

            double significant_line = simianDataModel.getTotalSignificantLineCount() == null ? 1
                : Double.parseDouble(simianDataModel.getTotalSignificantLineCount());

            DecimalFormat df = new DecimalFormat("#.##");
            simian_summary = df.format((dup_line / significant_line) * 100);

            if (simian_summary != null && Double.parseDouble(simian_summary) > CommonConfig.getInstance().getSimian())
            {
                simian_summary_comment = "不达标:重复率不能超过" + CommonConfig.getInstance().getSimian() + "%";
            }
            else
            {
                simian_summary_comment = "重复率 OK";
                isSimianOK = true;
            }

            Vector<String> simian_result = new Vector<String>();
            simian_result.add("Simian重复率");
            simian_result.add(simian_summary + "%");
            simian_result.add(simian_summary_comment);

            rowList.add(simian_result);
        }
        else
        {
            //如果simianDataModel为空,则表示simian_checkbox没有选中，不需要生成simian summary数据
            Vector<String> simian_result = new Vector<String>();
            simian_result.add("Simian重复率");
            simian_result.add("-");
            simian_result.add("-");

            rowList.add(simian_result);
        }

        //如果sourceMonitorDataModel不为空，则表示sourcemonitor_checkbox选中了，则需要生成sourcemonitor summary数据
        if (sourceMonitorDataModel != null)
        {
            int source_monitor_summary = sourceMonitorDataModel.getMaxComplexityForAllFiles() == null ? 0
                : Integer.parseInt(sourceMonitorDataModel.getMaxComplexityForAllFiles());
            String source_monitor_summary_comment = null;

            if (source_monitor_summary > CommonConfig.getInstance().getSourcemonitor())
            {
                source_monitor_summary_comment = "不达标:圈复杂度不能超过" + CommonConfig.getInstance().getSourcemonitor();
            }
            else
            {
                source_monitor_summary_comment = "圈复杂度 OK";
                isSourceMonitorOK = true;
            }

            Vector<String> sourcemonitor_result = new Vector<String>();
            sourcemonitor_result.add("SourceMonitor圈复杂度");
            sourcemonitor_result.add(String.valueOf(source_monitor_summary));
            sourcemonitor_result.add(source_monitor_summary_comment);

            rowList.add(sourcemonitor_result);
        }
        else
        {
            //如果sourceMonitorDataModel为空,则表示sourcemonitor_checkbox没有选中，不需要生成sourcemonitor summary数据
            Vector<String> sourcemonitor_result = new Vector<String>();
            sourcemonitor_result.add("SourceMonitor圈复杂度");
            sourcemonitor_result.add("-");
            sourcemonitor_result.add("-");

            rowList.add(sourcemonitor_result);
        }

        AbstractTableModel summary_data = TableDataModelUtil.getTableDateModle(summaryTitle, rowList);
        jtableSummary.setModel(summary_data);
        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
        r.setHorizontalAlignment(JLabel.CENTER);
        jtableSummary.getColumn("数值").setCellRenderer(r);
        jtableSummary.getColumn("是否达标").setCellRenderer(r);
        jtableSummary.repaint();
    }

    private void renderCheckStyleDetial()
    {
        if (isCheckstyleOK)
        {
            jpanelCheckstyle.removeAll();
            JLabel result = new JLabel("CheckStyle is ok");
            result.setFont(detailOkFont);
            jpanelCheckstyle.add(result);
        }
        else
        {
            if (checkStyleDataModel != null)
            {
                int rowIndex = 0;
                for (CheckStyleFileItem checkStyleFileItem : checkStyleDataModel.getFiles())
                {
                    String entry_name = checkStyleFileItem.getFileName();
                    List<Vector<String>> rowList = new ArrayList<Vector<String>>();
                    for (CheckStyleItem checkStyleItem : checkStyleFileItem.getItems())
                    {
                        Vector<String> one_row = new Vector<String>();
                        one_row.add(checkStyleItem.getSeverity());
                        one_row.add(checkStyleItem.getLine());
                        one_row.add(checkStyleItem.getColumn());
                        one_row.add(checkStyleItem.getMessage());
                        rowList.add(one_row);
                    }

                    GridBagConstraints c = new GridBagConstraints();
                    DetailModelObject one_object = new DetailModelObject("File Name:", entry_name,
                        checkStyleDetailTitle, rowList, 200, false, null, null);
                    c.fill = GridBagConstraints.BOTH;
                    c.gridx = 0;
                    c.gridy = rowIndex;
                    c.gridwidth = 1;
                    c.weightx = 1;
                    jpanelCheckstyle.add(one_object, c);
                    rowIndex++;
                }
            }
        }
    }

    private void renderSimianDetial()
    {
        if (isSimianOK)
        {
            jpanelSimian.removeAll();
            JLabel result = new JLabel("Simian重复率 is ok");
            result.setFont(detailOkFont);
            jpanelSimian.add(result);
        }
        else
        {
            if (simianDataModel != null)
            {
                int rowIndex = 0;
                for (SimianDetail eachSimian : simianDataModel.getSimianDetail())
                {
                    String echo_item_line_count = eachSimian.getLineCount();
                    List<Vector<String>> rowList = new ArrayList<Vector<String>>();
                    for (SimianItem eachItem : eachSimian.getSimianItem())
                    {
                        Vector<String> one_row = new Vector<String>();
                        one_row.add(eachItem.getSourceFile());
                        one_row.add(eachItem.getLocation());

                        rowList.add(one_row);
                    }

                    GridBagConstraints c = new GridBagConstraints();
                    DetailModelObject one_object = new DetailModelObject("Duplication:",
                        echo_item_line_count + " lines", simianDetailTitle, rowList, 100, false, null, null);
                    c.fill = GridBagConstraints.BOTH;
                    c.gridx = 0;
                    c.gridy = rowIndex;
                    c.gridwidth = 1;
                    c.weightx = 1;
                    jpanelSimian.add(one_object, c);
                    rowIndex++;
                }
            }
        }
    }

    private void renderPmdDetial()
    {
        if (isPMDOK)
        {
            jpanelPMD.removeAll();
            JLabel result = new JLabel("PMD is ok");
            result.setFont(detailOkFont);
            jpanelPMD.add(result);
        }
        else
        {
            if (pmdDataModel != null)
            {
                int rowIndex = 0;
                for (PmdFileItem echoPmdFile : pmdDataModel.getPmdFileItem())
                {
                    String entry_name = echoPmdFile.getFileName();
                    List<Vector<String>> rowList = new ArrayList<Vector<String>>();
                    for (PmdViolationItem eachItem : echoPmdFile.getPmdViolationItem())
                    {
                        Vector<String> one_row = new Vector<String>();
                        one_row.add(eachItem.getPriority());
                        one_row.add(eachItem.getBeginline().equals(eachItem.getEndline()) ? eachItem.getBeginline()
                            : eachItem.getBeginline() + "~" + eachItem.getEndline());
                        one_row.add(eachItem.getRule());
                        one_row.add(eachItem.getRuleset());
                        one_row.add(eachItem.getContent());

                        rowList.add(one_row);
                    }

                    GridBagConstraints c = new GridBagConstraints();
                    DetailModelObject one_object = new DetailModelObject("File Name:", entry_name, pmdDetailTitle,
                        rowList, 200, false, null, null);
                    c.fill = GridBagConstraints.BOTH;
                    c.gridx = 0;
                    c.gridy = rowIndex;
                    c.gridwidth = 1;
                    c.weightx = 1;
                    jpanelPMD.add(one_object, c);
                    rowIndex++;
                }
            }
        }
    }

    private void renderSourceMonitorDetial()
    {
        if (isSourceMonitorOK)
        {
            jpanelSourceMonitor.removeAll();
            JLabel result = new JLabel("SourceMonitor圈复杂度 is ok");
            result.setFont(detailOkFont);
            jpanelSourceMonitor.add(result);
        }
        else
        {
            if (sourceMonitorDataModel != null && !sourceMonitorDataModel.isNormal())
            {
                int rowIndex = 0;
                String entry_name = "不达标圈复杂度详情:";
                List<Vector<String>> rowList = new ArrayList<Vector<String>>();
                for (SourceMonitorItem eachItem : sourceMonitorDataModel.getAbnormalItems())
                {
                    Vector<String> one_row = new Vector<String>();
                    one_row.add(eachItem.getFileName());
                    one_row.add(eachItem.getMethodName());
                    one_row.add(eachItem.getMaxComplexity());

                    rowList.add(one_row);
                }

                GridBagConstraints c = new GridBagConstraints();
                DetailModelObject one_object =
                    new DetailModelObject(entry_name, null, sourcemonitorDetailTitle, rowList, 200, false, null, null);
                c.fill = GridBagConstraints.BOTH;
                c.gridx = 0;
                c.gridy = rowIndex;
                c.gridwidth = 1;
                c.weightx = 1;
                jpanelSourceMonitor.add(one_object, c);
                rowIndex++;
            }
        }
    }

    private void renderFindBugsDetial()
    {
        if (isFindBugsOK)
        {
            jpanelFindbugs.removeAll();
            JLabel result = new JLabel("FindBugs is ok");
            result.setFont(detailOkFont);
            jpanelFindbugs.add(result);
        }
        else
        {
            if (findbugsDataModel != null && findbugsDataModel.getTotalBugs() != null)
            {
                int rowIndex = 0;
                List<Vector<String>> rowList = null;
                List<Vector<String>> findbugSuggestRowList = null;
                Vector<String> one_row = null;
                for (FindbugsInstance eachBugInstance : findbugsDataModel.getBugsInstances())
                {
                    String entry_name = "FindBug" + (rowIndex + 1);

                    rowList = new ArrayList<Vector<String>>();
                    one_row = new Vector<String>();
                    one_row.add("Type Abbreviation: " + eachBugInstance.getAbbrev());
                    rowList.add(one_row);

                    one_row = new Vector<String>();
                    one_row.add("Bug Type: " + eachBugInstance.getType());
                    rowList.add(one_row);

                    one_row = new Vector<String>();
                    one_row.add("Bug Category: " + eachBugInstance.getCategory());
                    rowList.add(one_row);

                    one_row = new Vector<String>();
                    one_row.add("Message: " + eachBugInstance.getLongMessage());
                    rowList.add(one_row);

                    one_row = new Vector<String>();
                    one_row.add("Class Name: " + eachBugInstance.getClassName());
                    rowList.add(one_row);

                    one_row = new Vector<String>();
                    one_row.add("Method Name: " + eachBugInstance.getMethodName());
                    rowList.add(one_row);

                    one_row = new Vector<String>();
                    one_row.add("Line: " + eachBugInstance.getLine());
                    rowList.add(one_row);

                    findbugSuggestRowList = new ArrayList<Vector<String>>();
                    BugPatternObject bugPattern = eachBugInstance.getBugPattern();
                    one_row = new Vector<String>();
                    one_row.add("Type Abbreviation: " + bugPattern.getAbbrev());
                    findbugSuggestRowList.add(one_row);

                    one_row = new Vector<String>();
                    one_row.add("Bug Type: " + bugPattern.getType() + "--" + bugPattern.getShortDescription());
                    findbugSuggestRowList.add(one_row);

                    one_row = new Vector<String>();
                    one_row.add("Bug Category: " + bugPattern.getCategory());
                    findbugSuggestRowList.add(one_row);

                    one_row = new Vector<String>();
                    one_row.add("Details: " + bugPattern.getDetailsDescription());
                    findbugSuggestRowList.add(one_row);

                    String[] findbugs_suggest_title =
                    {"提示" };

                    GridBagConstraints c = new GridBagConstraints();
                    DetailModelObject one_object = new DetailModelObject(entry_name, null, findBugsDetailTitle, rowList,
                        135, true, findbugs_suggest_title, findbugSuggestRowList);
                    c.fill = GridBagConstraints.BOTH;
                    c.gridx = 0;
                    c.gridy = rowIndex;
                    c.gridwidth = 1;
                    c.weightx = 1;
                    jpanelFindbugs.add(one_object, c);
                    rowIndex++;
                }

            }
        }
    }

    private List<Vector<String>> initSummaryData()
    {
        List<Vector<String>> rowList = new ArrayList<Vector<String>>();

        Vector<String> checkstyle_result = new Vector<String>();
        checkstyle_result.add("CheckStyle");
        checkstyle_result.add("-");
        checkstyle_result.add("-");

        Vector<String> simian_result = new Vector<String>();
        simian_result.add("Simian重复率");
        simian_result.add("-");
        simian_result.add("-");

        Vector<String> pmd_result = new Vector<String>();
        pmd_result.add("PMD");
        pmd_result.add(String.valueOf("-"));
        pmd_result.add("-");

        Vector<String> sourcemonitor_result = new Vector<String>();
        sourcemonitor_result.add("SourceMonitor圈复杂度");
        sourcemonitor_result.add(String.valueOf("-"));
        sourcemonitor_result.add("-");

        Vector<String> findbug_result = new Vector<String>();
        findbug_result.add("FindBugs");
        findbug_result.add("-");
        findbug_result.add("-");

        rowList.add(pmd_result);
        rowList.add(findbug_result);
        rowList.add(checkstyle_result);
        rowList.add(simian_result);
        rowList.add(sourcemonitor_result);

        return rowList;
    }

    private boolean executeGenerateXML() throws InterruptedException
    {
        boolean isSuccess = true;
        progressBar.setValue(6);
        //本次构建之前，先执行mvn clean命令，清楚之前所有的xml报告
        String mvn_clean_result = CommonUtil.executeCMD("cmd /c mvn clean", CODE_HOME);
        progressBar.setValue(10);
        if (mvn_clean_result.indexOf("BUILD SUCCESS") < 0)
        {
            //mvn clean执行失败：jdialog提醒
            JOptionPane.showMessageDialog(null,
                "clean failed! try again. " + "详情:\n" + mvn_clean_result,
                "错误",
                JOptionPane.ERROR_MESSAGE);
            isSuccess = false;
            return isSuccess;
        }

        progressBar.setValue(15);
        if (findbugs_checkbox.isSelected())
        {
            String mvn_findbugs_result = CommonUtil.executeCMD("cmd /c mvn compile findbugs:findbugs", CODE_HOME);
            progressBar.setValue(70);
            if (mvn_findbugs_result.indexOf("BUILD SUCCESS") < 0)
            {
                //mvn compile findbugs:findbugs执行失败：jdialog提醒
                JOptionPane.showMessageDialog(null,
                    "build findbugs failed! try again. " + "详情:\n" + mvn_findbugs_result,
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
                isSuccess = false;
                return isSuccess;
            }
        }

        if (checkstyle_checkbox.isSelected())
        {
            String mvn_checkstyle_result = CommonUtil.executeCMD("cmd /c mvn checkstyle:checkstyle", CODE_HOME);
            progressBar.setValue(80);
            if (mvn_checkstyle_result.indexOf("BUILD SUCCESS") < 0)
            {
                //mvn checkstyle:checkstyle执行失败：jdialog提醒
                JOptionPane.showMessageDialog(null,
                    "build checkstyle failed! try again. " + "详情:\n" + mvn_checkstyle_result,
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
                isSuccess = false;
                return isSuccess;
            }
        }

        if (simian_checkbox.isSelected())
        {
            String mvn_simian_result = CommonUtil.executeCMD("cmd /c mvn simian:simian", CODE_HOME);
            progressBar.setValue(90);
            if (mvn_simian_result.indexOf("BUILD SUCCESS") < 0)
            {
                //mvn simian:simian执行失败：jdialog提醒
                JOptionPane.showMessageDialog(null,
                    "build simian failed! try again. " + "详情:\n" + mvn_simian_result,
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
                isSuccess = false;
                return isSuccess;
            }
        }

        if (pmd_checkbox.isSelected())
        {
            String mvn_pmd_result = CommonUtil.executeCMD("cmd /c mvn pmd:pmd", CODE_HOME);
            progressBar.setValue(90);
            if (mvn_pmd_result.indexOf("BUILD SUCCESS") < 0)
            {
                //mvn pmd:pmd执行失败：jdialog提醒
                JOptionPane.showMessageDialog(null,
                    "build pmd failed! try again. " + "详情:\n" + mvn_pmd_result,
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
                isSuccess = false;
                return isSuccess;
            }
        }

        if (sourcemonitor_checkbox.isSelected())
        {
            String mvn_sourcemonitor_result =
                CommonUtil.executeCMD("cmd /c mvn sourcemonitor:sourcemonitor", CODE_HOME);
            progressBar.setValue(95);
            if (mvn_sourcemonitor_result.indexOf("BUILD SUCCESS") < 0)
            {
                //mvn sourcemonitor:sourcemonitor执行失败：jdialog提醒
                JOptionPane.showMessageDialog(null,
                    "build sourcemonitor failed! try again. " + "详情:\n" + mvn_sourcemonitor_result,
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
                isSuccess = false;
                return isSuccess;
            }
        }

        return isSuccess;

    }

    private void isChangeAllToCancelStatus()
    {
        if (checkstyle_checkbox.isSelected() && pmd_checkbox.isSelected() && simian_checkbox.isSelected()
            && sourcemonitor_checkbox.isSelected() && findbugs_checkbox.isSelected())
        {
            selectall_btn.setText("取消");
        }
    }

}
