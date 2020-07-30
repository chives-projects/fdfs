import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import v2.FastDFSClient;
import v2.FastDFSException;

import java.io.FileNotFoundException;

/**
 * @Description
 * @Author csc
 * @Date 2019/9/12 15:01
 * @Version 1.0
 **/
public class FileDFSClientTest {
    private static Logger logger = LoggerFactory.getLogger(FileDFSClientTest.class);

    private FastDFSClient fastDFSClient = new FastDFSClient();
    private String path = "C:\\file\\csc\\note\\test.jpg";
    private String path1 = "C:\\file\\csc\\note\\test1.jpg";
    @Test
    public void upload() throws FastDFSException {
        String filePath = fastDFSClient.uploadFile(path);
        logger.info(filePath);
    }
    @Test
    public void del() throws FastDFSException {
        int res = fastDFSClient.deleteFile(path);
        logger.info(String.valueOf(res));
    }
    @Test
    public void download() throws FastDFSException, FileNotFoundException {
        fastDFSClient.downloadFile("group1/M00/00/00/Cv11i113No6AWXnRAAAOPvemCYo446.jpg", path1);
    }

}
