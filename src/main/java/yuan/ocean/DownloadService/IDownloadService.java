package yuan.ocean.DownloadService;

/**
 * Created by Yuan on 2017/6/3.
 */
 public interface IDownloadService {
    /**
     * download file
     * @param url the host of ftp
     * @param userName the username /also can be used as property when in http api
     * @param passWord the password
     * @param urlParentPath the fileParentPath of ftp
     * @param filterPattern the filterPattern for download file
     * @param fileName the saved filename in the system
     * @param saveFilePath the saved filepath in the system
     */
    void download(String url, String userName, String passWord, String urlParentPath,String filterPattern, String fileName, String saveFilePath);
}
