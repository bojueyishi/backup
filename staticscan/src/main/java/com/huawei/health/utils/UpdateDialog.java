package com.huawei.health.utils;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import com.huawei.health.constants.Constants;

public class UpdateDialog extends JDialog
{
    /**
     * 注释内容
     */
    private static final long serialVersionUID = 1L;

    private JButton cancelBtn;

    private JProgressBar updateProgressBar;
    
    private Thread updateThread;
    
    private JLabel title;

    public UpdateDialog(final JFrame parent, boolean modal)
    {
        super(parent, modal);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); //得到屏幕的尺寸
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        setTitle("更新静态扫描工具");
        setSize(360, 130);
        setLayout(new GridBagLayout());
        setResizable(false);
        setLocation((screenWidth - this.getWidth()) / 2, (screenHeight - this.getHeight()) / 2);
        this.addWindowListener(new WindowListener()
        {
            
            public void windowClosed(WindowEvent e)
            {
            }

            public void windowActivated(WindowEvent e)
            {
            }

            public void windowOpened(WindowEvent e)
            {
            }

            public void windowClosing(WindowEvent e)
            {
            }

            public void windowIconified(WindowEvent e)
            {
            }

            public void windowDeiconified(WindowEvent e)
            {
            }

            public void windowDeactivated(WindowEvent e)
            {
                dispose();
                updateThread.stop();
            }
        });

        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(10, 0, 5, 0);
        title = new JLabel("更新进度 ");
        this.add(title, c);

        updateProgressBar = new JProgressBar();
        updateProgressBar.setStringPainted(true); //显示提示信息
        updateProgressBar.setIndeterminate(true); //确定进度的进度条
        updateProgressBar.setPreferredSize(new Dimension(400, 50));
        updateProgressBar.setMaximum(100);
        updateProgressBar.setValue(0);

        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        c.insets = new Insets(0, 0, 0, 0);
        ;
        c.fill = GridBagConstraints.HORIZONTAL;
        this.add(updateProgressBar, c);

        cancelBtn = new JButton("取消");
        cancelBtn.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                dispose();
                updateThread.stop();
            }
        });

        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0;
        c.insets = new Insets(10, 0, 0, 10);
        c.anchor = GridBagConstraints.EAST;
        this.add(cancelBtn, c);

        updateThread = new Thread(new Runnable()
        {
            public void run()
            {
                updateProgressBar.setValue(1);
                //检查更新包所处廊坊机器是否可达
                try
                {
                    if (CommonUtil.executeCMD("cmd /c ping -n 3 10.21.0.188", "C:\\Windows").indexOf("TTL") < 0)
                    {
                        JOptionPane.showMessageDialog(null,
                            "下载url连接失败，请确保廊坊vpn处于连接状态",
                            "连接错误",
                            JOptionPane.ERROR_MESSAGE);
                        Thread.currentThread().stop();
                    }
                    updateProgressBar.setValue(50);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                    Thread.currentThread().stop();
                }

                //可达则检查是否要版本信息，是否要更新
                if (DownloadFileModel.downloadFile(Constants.VERSION_URL, Constants.DOWNLOAD_TARGET_DIR))
                {
                    //下载version成功比对是否一致
                    File local_version_file = new File(Constants.LOCAL_VERSION_FILE);

                    if (local_version_file.exists())
                    {
                        try
                        {
                            BufferedReader br = new BufferedReader(new FileReader(local_version_file));
                            String local_version = br.readLine();
                            br.close();
                            local_version = local_version == null ? "0" : local_version.split("=")[1];
                            BufferedReader bread = new BufferedReader(new FileReader(Constants.REMOTE_VERSION_FILE));
                            String remote_version = bread.readLine();
                            bread.close();
                            remote_version = remote_version == null ? "0" : remote_version.split("=")[1];

                            if (Double.parseDouble(local_version) >= Double.parseDouble(remote_version))
                            {
                                CommonUtil.executeCMD("cmd /c del /f /s /q version-latest.txt",
                                    Constants.DOWNLOAD_TARGET_DIR);
                                JOptionPane.showMessageDialog(null, "已经是最新版本", "提示", JOptionPane.PLAIN_MESSAGE);
                                Thread.currentThread().stop();
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            Thread.currentThread().stop();
                        }
                    }
                    else
                    {
                        try
                        {
                            CommonUtil.executeCMD("cmd /c del /f /s /q version-latest.txt",
                                Constants.DOWNLOAD_TARGET_DIR);
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                            Thread.currentThread().stop();
                        }
                        JOptionPane.showMessageDialog(null, "本地版本文件不存在", "错误", JOptionPane.ERROR_MESSAGE);
                        Thread.currentThread().stop();
                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "下载版本信息失败", "下载错误", JOptionPane.ERROR_MESSAGE);
                    Thread.currentThread().stop();
                }

                //可达则下载jar
                //先备份原来的jar
                try
                {
                    CommonUtil.executeCMD("cmd /c copy staticscan.jar staticscan-backup.jar",
                        "C:\\Program Files\\StaticScan");
                    updateProgressBar.setValue(70);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                    Thread.currentThread().stop();
                }

                if (DownloadFileModel.downloadFile(Constants.DOWNLOAD_MAIN_JAR_URL, Constants.DOWNLOAD_TARGET_DIR))
                {
                    try
                    {
                        CommonUtil.executeCMD("cmd /c del /f /s /q staticscan-backup.jar",
                            "C:\\Program Files\\StaticScan");
                        updateProgressBar.setValue(90);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                        Thread.currentThread().stop();
                    }
                    
                    try
                    {
                        CommonUtil.executeCMD("cmd /c copy version-latest.txt version.txt",
                            Constants.DOWNLOAD_TARGET_DIR);
                        CommonUtil.executeCMD("cmd /c del /f /s /q version-latest.txt", Constants.DOWNLOAD_TARGET_DIR);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                        Thread.currentThread().stop();
                    }
                    
                    updateProgressBar.setValue(100);
                    title.setText("更新成功，请重新触发静态扫描，等待3秒关闭...");
                    try
                    {
                        Thread.sleep(3000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    System.exit(0);
                }
                else
                {
                    try
                    {
                        CommonUtil.executeCMD("cmd /c move /Y staticscan-backup.jar staticscan.jar",
                            "C:\\Program Files\\StaticScan");
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                        Thread.currentThread().stop();
                    }
                    JOptionPane.showMessageDialog(null, "更新失败", "错误", JOptionPane.ERROR_MESSAGE);
                    Thread.currentThread().stop();
                }

                //下载依赖的jar包，放到本地lib下

            }
        });
        updateThread.start();

    }

}
