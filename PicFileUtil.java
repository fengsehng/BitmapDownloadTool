
import android.graphics.Bitmap;
import android.os.Environment;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by fengsehng  on 2017/9/19.
 * @author fengsehng
 *
 */

public class PicFileUtil {
  /**
   * 相册文件夹
   */

  public static String PATH_PHOTOGRAPH = "/fengsehng/";


  /**
   * 保存图片
   *
   * @param bitmap
   * @param filePath
   */
  public static void saveBitmap(Bitmap bitmap, String filePath) {
    FileOutputStream bos = null;
    File file = new File(filePath);
    if (!file.getParentFile().exists()) {
      file.getParentFile().mkdirs();
    }
    try {
      bos = new FileOutputStream(file);
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } finally {
      try {
        bos.flush();
        bos.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }


  public static void deleteDir(File directory) {
    if (directory != null){
      if (directory.isFile()) {
        directory.delete();
        return;
      }

      if (directory.isDirectory()) {
        File[] childFiles = directory.listFiles();
        if (childFiles == null || childFiles.length == 0) {
          directory.delete();
          return;
        }

        for (int i = 0; i < childFiles.length; i++) {
          deleteDir(childFiles[i]);
        }
        directory.delete();
      }
    }
  }

  /**
   * 获取保存到相册的最终文件
   * @param filePath
   * @param imageName
   * @return
   */
  public static File getDCIMFile(String filePath, String imageName) {
    if (Environment.getExternalStorageState().equals(
        Environment.MEDIA_MOUNTED)) { // 文件可用
      // 首先保存图片
      String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + filePath;
      File appDir = new File(storePath);
      if (!appDir.exists()) {
        appDir.mkdir();
      }
      File file = new File(appDir, imageName);
      if (!file.exists()) {
        try {
          //在指定的文件夹中创建文件
          file.createNewFile();
        } catch (Exception e) {
        }
      }
      return file;
    } else {
      return null;
    }

  }



  public static File saveBitmap2(Bitmap bitmap, String fileName, File baseFile) {
    FileOutputStream bos = null;
    File imgFile = new File(baseFile, "/" + fileName);
    try {
      bos = new FileOutputStream(imgFile);
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } finally {
      try {
        bos.flush();
        bos.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return imgFile;
  }

  public static File getBaseFile(String filePath) {
    if (Environment.getExternalStorageState().equals(
        Environment.MEDIA_MOUNTED)) { // 文件可用
      File f = new File(Environment.getExternalStorageDirectory(),
          filePath);
      if (!f.exists())
        f.mkdirs();
      return f;
    } else {
      return null;
    }
  }


  /**
   * 由指定的路径和文件名创建文件
   */
  public static File createFile(String path, String name) throws IOException {
    File folder = new File(path);
    if (!folder.exists()) {
      folder.mkdirs();
    }
    File file = new File(path + name);
    if (!file.exists()) {
      file.createNewFile();
    }
    return file;
  }

  /**
   * 把url字符串转化为对应的文件
   * @param urlList
   * @return
   */
  public static HashMap<String,String> getMd5List(List<String> urlList){
    HashMap<String,String> map = new HashMap<>();
    for(int i= 0; i < urlList.size();i++){
      map.put(Md5Util.getMd5(urlList.get(i)) + ".jpg",urlList.get(i));
    }
    return map;
  }
  /**
   * 过滤已经存在的md5文件
   * @param urlList
   * @return
   */
  public static List<String> getResultList(List<String> urlList){
    HashMap<String,String> map = new HashMap<>();
    map = getMd5List(urlList);
    List<String> fileNameList = new ArrayList<>();
    for(Map.Entry<String,String> entry:map.entrySet()){
      fileNameList.add(entry.getKey());
    }
    String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + PicFileUtil.PATH_PHOTOGRAPH;
    File appDir = new File(storePath);
    if (!appDir.exists()) {
      appDir.mkdir();
    }
    File[] fileList = appDir.listFiles();
    Iterator<String> it = fileNameList.iterator();
    while(it.hasNext()){
      String x = it.next();
      for(File f:fileList){
        if(x.equals(f.getName())){
          map.remove(x);
        }
      }
    }
    List<String> resultUrlList = new ArrayList<>();
    for(Map.Entry<String,String> entry:map.entrySet()){
      resultUrlList.add(entry.getValue());
    }
    return resultUrlList;
  }

  /**
   * 图片是否存在
   * @param fileName
   * @return
   */
  public static boolean isFileExit(String fileName){
    String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + PicFileUtil.PATH_PHOTOGRAPH;
    File appDir = new File(storePath);
    if (!appDir.exists()) {
      appDir.mkdir();
    }
    File[] files = appDir.listFiles();
    for (File file: files) {
      if (fileName.equals(file.getName())){
        return true;
      }
    }
    return false;
  }
}
