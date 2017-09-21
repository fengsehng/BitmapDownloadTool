/**
 * 图片批量下载
 * Created by fengsehng on 2017/9/16.
 */

public class BitmapDownloadHelper {

  private static final String TAG = "BitmapDownloadHelper";
  private static final String PIC_SUFFIX = ".jpg";

  /**
   * 用于存储返回的结果
   */
  private static ConcurrentHashMap<String, Boolean> sResultMap = new ConcurrentHashMap<>();
  private static String PIC_SIZE = NewHouseConstantUtils.IMAGE_SIZE_POSTFIX.IMG_FRAME_ALBUM;

  private static int sTaskCount;

  /**
   * 批量下载图片
   * @param urlList
   */
  public static void downloadPic(List<String> urlList) {
    ToastUtil.toast(Entry.getContext(), Entry.getResources().getString(R.string.pic_download_begin));
    sTaskCount = urlList.size();
    for (final String url : urlList) {
      downloadSinglePic(url, null);
    }
  }

  /**
   * 下载单张图片
   * @param url
   */
  public static void downloadSinglePic(String url, final OnDownloadResultListener listener) {
    if (listener != null){
      listener.onDownloadBegin();
    }
    final String realUrl = url + PIC_SIZE;
    final String imageName = Md5Util.getMd5(realUrl + System.currentTimeMillis()) + PIC_SUFFIX;
    downloadFromNet(realUrl, imageName, listener);
  }

  private static void downloadFromNet(final String realUrl, final String imageName,
      final OnDownloadResultListener listener){
    ServiceGenerator
        .createService(NetApiService.class)
        .downloadPic(realUrl)
        .subscribeOn(Schedulers.newThread())
        .map(new Func1<ResponseBody, Boolean>() {
          @Override public Boolean call(ResponseBody responseBody) {
            boolean isSuccess = false;
            File dcimFile = PicFileUtil.getDCIMFile(PicFileUtil.PATH_PHOTOGRAPH, imageName);
            InputStream is = responseBody.byteStream();
            try {
              FileOutputStream fos = new FileOutputStream(dcimFile);
              BufferedInputStream bis = new BufferedInputStream(is);
              byte[] buffer = new byte[1024];
              int len;
              while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
              }
              isSuccess = true;
              Uri uri = Uri.fromFile(dcimFile);
              Entry.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
              fos.flush();
              fos.close();
              bis.close();
              is.close();
            } catch (Exception e) {
              isSuccess = false;
            }finally {
              return isSuccess;
            }
          }
        })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<Boolean>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            if (listener != null){
              listener.onDownloadResult(false);
            }else{
              sResultMap.put(realUrl, false);
              toastResult();
            }
          }

          @Override public void onNext(Boolean success) {
            if (listener != null){
              listener.onDownloadResult(success);
            }else{
              sResultMap.put(realUrl, success);
              toastResult();
            }
          }
        });
  }

  private static void toastResult() {
    if (sResultMap.size() == sTaskCount){
      int count = 0;
      Set<Map.Entry<String, Boolean>> entrySet = sResultMap.entrySet();
      for (Map.Entry<String, Boolean> entry : entrySet){
        if (!entry.getValue()){
          count ++;
        }
      }
      if (count > 0){
        ToastUtil.toast(Entry.getContext(), Entry.getResources().getString(R.string.pic_download_failed));
      }else{
        ToastUtil.toast(Entry.getContext(), Entry.getResources().getString(R.string.pic_download_success));
      }
      sResultMap.clear();
      sTaskCount = 0;
    }
  }

  /**
   * 回调下载状态接口
   */
  public interface OnDownloadResultListener{
    void onDownloadBegin();
    void onDownloadResult(boolean success);
  }
}

