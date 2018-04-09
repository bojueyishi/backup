package com.huawei.health.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DownloadFileModel
{
    
    public static boolean downloadFile(String urlPath, String downloadDir)
    {
        File file = null;
        try
        {
            URL url = new URL(urlPath);
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection)urlConnection;
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.connect();
            
            //int fileLength = httpURLConnection.getContentLength();
            
            String filePathUrl = httpURLConnection.getURL().getFile();
            String fileFullName = filePathUrl.substring(filePathUrl.lastIndexOf(File.separatorChar) + 1);

            //URLConnection con = url.openConnection();
            
            BufferedInputStream bin = new BufferedInputStream(httpURLConnection.getInputStream());
            
            String path = downloadDir + File.separatorChar + fileFullName;
            file = new File(path);
            if (!file.getParentFile().exists())
            {
                file.getParentFile().mkdirs();
            }
            OutputStream out = new FileOutputStream(file);
            int size = 0;
            int len = 0;
            byte[] buf = new byte[1024];
            while ((size = bin.read(buf)) != -1)
            {
                len += size;
                out.write(buf, 0, size);
            }
            bin.close();
            out.close();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
            return false;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        finally
        {
            return true;
        }
    }
}
