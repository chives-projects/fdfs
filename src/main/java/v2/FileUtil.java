package v2;

import java.io.IOException;

/**
 * @Description
 * @Author csc
 * @Date 2019/9/11 17:53
 * @Version 1.0
 **/
public class FileUtil {
    public static void main(String[] args) throws IOException, FastDFSException {
        FastDFSClient fastDFSClient = new FastDFSClient();
        String path = "C:\\file\\csc\\note\\730378_154.jpg";
        String filePath = fastDFSClient.uploadFile(path);
        System.out.println("filePath:   " + filePath);

        System.out.println("del--返回0成功");
        int n = fastDFSClient.deleteFile(filePath);
        System.out.println(n);

        String path1 = "C:\\file\\csc\\note\\test1.jpg";
        fastDFSClient.downloadFile("group1/M00/00/00/Cv11i113No6AWXnRAAAOPvemCYo446.jpg", path1);
    }
}
