package com.ystech.core.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

/** 
 * @author 作者  舒三战
 * @version 创建时间：2012-11-20 上午9:28:10 
 * 类说明 
 **/
public class FileNameUtil {
	/**
	 * 未上传文件命名
	 * @param fileName
	 * @return
	 */
	public static File getResourceFile(String fileName){
			File dataFile = null;
			try {
				if (null != fileName && !fileName.trim().equals("")) {// getName()返回文件名称，如果是空字符串，说明没有选择文件。
					//保存文件路径规则：WebRoot根目录/archives/username/2012-11-20
					//如：WebRoot/archives/admin/2012-11-20
					String path = PathUtil.getWebRootPath()	+ System.getProperty("file.separator") + 
							"archives"+ System.getProperty("file.separator")+
							"image"+System.getProperty("file.separator");
					//判断路径是否存在，如果不存在，创建路径
					File file = new File(path);
					boolean exists = file.exists();
					if (!file.exists()) {
						FileUtils.forceMkdir(file);
					}
					//文件命名规则为：20121120805022+加文件名称
					//如：20121120093622psb.jpg
					dataFile = new File(PathUtil.getWebRootPath()
							+ System.getProperty("file.separator") + 
							"archives" + System.getProperty("file.separator")+
							"image"+System.getProperty("file.separator")
							+DateUtil.formatFile(new Date())+fileName);
					return dataFile;
				} else {

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	/**
	 * 未上传文件命名
	 * @param fileName
	 * @return
	 */
	public static File getResourceFileCoupon(String fileName){
		File dataFile = null;
		try {
			if (null != fileName && !fileName.trim().equals("")) {// getName()返回文件名称，如果是空字符串，说明没有选择文件。
				//保存文件路径规则：WebRoot根目录/archives/username/2012-11-20
				//如：WebRoot/archives/admin/2012-11-20
				String path = PathUtil.getWebRootPath()	+ System.getProperty("file.separator") + 
						"archives"+ System.getProperty("file.separator")+
						"image"+System.getProperty("file.separator");
				//判断路径是否存在，如果不存在，创建路径
				File file = new File(path);
				boolean exists = file.exists();
				if (!file.exists()) {
					FileUtils.forceMkdir(file);
				}
				//文件命名规则为：20121120805022+加文件名称
				//如：20121120093622psb.jpg
				dataFile = new File(PathUtil.getWebRootPath()
						+ System.getProperty("file.separator") + 
						"archives" + System.getProperty("file.separator")+
						"coupon"+System.getProperty("file.separator")
						+DateUtil.formatFile(new Date())+fileName);
				return dataFile;
			} else {
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/** 
     * 将存放在sourceFilePath目录下的源文件，打包成fileName名称的zip文件，并存放到zipFilePath路径下 
     * @param sourceFilePath :待压缩的文件路径 
     * @param zipFilePath :压缩后存放路径 
     * @param fileName :压缩后文件的名称 
     * @return 
     */  
    public static boolean fileToZip(String sourceFilePath,String zipFilePath,String fileName){  
        boolean flag = false;  
        File sourceFile = new File(sourceFilePath);  
        FileInputStream fis = null;  
        BufferedInputStream bis = null;  
        FileOutputStream fos = null;  
        ZipOutputStream zos = null;  
        if(sourceFile.exists() == false){  
            System.out.println("待压缩的文件目录："+sourceFilePath+"不存在.");  
        }else{  
            try {  
                File zipFile = new File(zipFilePath + "/" + fileName +".zip");  
                if(zipFile.exists()){  
                    System.out.println(zipFilePath + "目录下存在名字为:" + fileName +".zip" +"打包文件.");  
                    return true;
                }else{  
                    File[] sourceFiles = sourceFile.listFiles();  
                    if(null == sourceFiles || sourceFiles.length<1){  
                        System.out.println("待压缩的文件目录：" + sourceFilePath + "里面不存在文件，无需压缩.");  
                    }else{  
                        fos = new FileOutputStream(zipFile);  
                        zos = new ZipOutputStream(new BufferedOutputStream(fos));  
                        byte[] bufs = new byte[1024*10];  
                        for(int i=0;i<sourceFiles.length;i++){  
                            //创建ZIP实体，并添加进压缩包  
                        	String fileNameUTF=new String(sourceFiles[i].getName());
                            ZipEntry zipEntry = new ZipEntry(fileNameUTF);  
                            zos.putNextEntry(zipEntry);  
                            //读取待压缩的文件并写进压缩包里  
                            fis = new FileInputStream(sourceFiles[i]);  
                            bis = new BufferedInputStream(fis, 1024*10);  
                            int read = 0;  
                            while((read=bis.read(bufs, 0, 1024*10)) != -1){  
                                zos.write(bufs,0,read);  
                            }  
                        }  
                        zos.setEncoding("gbk");
                        zos.closeEntry();
                        zos.close();
                        flag = true;  
                    }  
                }  
            } catch (FileNotFoundException e) {  
                e.printStackTrace();  
                throw new RuntimeException(e);  
            } catch (IOException e) {  
                e.printStackTrace();  
                throw new RuntimeException(e);  
            } finally{  
                //关闭流  
                try {  
                    if(null != bis) bis.close();  
                    if(null != zos) zos.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                    throw new RuntimeException(e);  
                }  
            }  
        }  
        return flag;  
    }  
    
}
