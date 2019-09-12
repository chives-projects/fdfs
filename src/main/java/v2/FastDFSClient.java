package v2;

import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.TrackerServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author shichao.chen
 * @Date 2019/9/11 16:40
 * @Version 1.0
 **/
public class FastDFSClient {
    private static Logger logger = LoggerFactory.getLogger(FastDFSClient.class);
    public static final String SEPARATOR = "/";
    public static final String POINT = ".";
    public static final String FILENAME = "filename";

    public FastDFSClient() {
    }

    public String uploadFile(String filePath) throws FastDFSException {
        return upload(filePath, null);
    }

    public String upload(String filePath, Map<String, String> descriptions) throws FastDFSException {
        if (StringUtils.isBlank(filePath))
            throw new FastDFSException(ErrorCode.FILE_PATH_ISNULL.CODE, ErrorCode.FILE_PATH_ISNULL.MESSAGE);
        File file = new File(filePath);
        String path = null;
        try {
            InputStream inputStream = new FileInputStream(file);
            filePath = toLocal(filePath);
            String fileName = filePath.substring(filePath.lastIndexOf(SEPARATOR) + 1);
            path = upload(inputStream, fileName, descriptions);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return path;
    }

    public String upload(InputStream is, String filename, Map<String, String> descriptions) throws FastDFSException {
        if (is == null) throw new FastDFSException(ErrorCode.FILE_ISNULL.CODE, ErrorCode.FILE_ISNULL.MESSAGE);
        String path = null;
        NameValuePair[] nvps = null;
        List<NameValuePair> nvpsList = new ArrayList<>();
        String suffix = getFilenameSuffix(filename);
        if (StringUtils.isNotBlank(filename)) nvpsList.add(new NameValuePair(FILENAME, filename));
        if (descriptions != null && descriptions.size() > 0) {
            descriptions.forEach((key, value) -> {
                nvpsList.add(new NameValuePair(key, value));
            });
        }
        if (nvpsList.size() > 0) {
            nvps = new NameValuePair[nvpsList.size()];
            nvpsList.toArray(nvps);
        }
        TrackerServer trackerServer = TrackerServerPool.borrowObject();
        StorageClient1 storageClient = new StorageClient1(trackerServer, null);
        try {
            byte[] fileBuff = new byte[is.available()];
            is.read(fileBuff, 0, fileBuff.length);
            path = storageClient.upload_file1(fileBuff, suffix, nvps);

            if (StringUtils.isBlank(path))
                throw new FastDFSException(ErrorCode.FILE_UPLOAD_FAILED.CODE, ErrorCode.FILE_UPLOAD_FAILED.MESSAGE);
            if (logger.isDebugEnabled()) logger.debug("upload file success, return path is {}", path);
        } catch (IOException e) {
            e.printStackTrace();
            throw new FastDFSException(ErrorCode.FILE_UPLOAD_FAILED.CODE, ErrorCode.FILE_UPLOAD_FAILED.MESSAGE);
        } catch (MyException e) {
            e.printStackTrace();
            throw new FastDFSException(ErrorCode.FILE_UPLOAD_FAILED.CODE, ErrorCode.FILE_UPLOAD_FAILED.MESSAGE);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        TrackerServerPool.returnObject(trackerServer);
        return path;
    }

    public void downloadFile(String filepath, String filename) throws FastDFSException, FileNotFoundException {
        if (StringUtils.isBlank(filepath))
            throw new FastDFSException(ErrorCode.FILE_PATH_ISNULL.CODE, ErrorCode.FILE_PATH_ISNULL.MESSAGE);
        filepath = toLocal(filepath);
        if (StringUtils.isBlank(filename)) filename = getOriginalFilename(filepath);
        if (logger.isDebugEnabled())
            logger.debug("download file, filepath = {}, filename = {}", filepath, filename);
        TrackerServer trackerServer = TrackerServerPool.borrowObject();
        StorageClient1 storageClient = new StorageClient1(trackerServer, null);
        OutputStream os = new FileOutputStream(filename);
        InputStream is = null;
        try {
            byte[] fileByte = storageClient.download_file1(filepath);
            if (fileByte == null)
                throw new FastDFSException(ErrorCode.FILE_NOT_EXIST.CODE, ErrorCode.FILE_NOT_EXIST.MESSAGE);
            is = new ByteArrayInputStream(fileByte);
            byte[] buffer = new byte[1024 * 5];
            int len = 0;
            while ((len = is.read(buffer)) > 0) {
                os.write(buffer, 0, len);
            }
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
            throw new FastDFSException(ErrorCode.FILE_DOWNLOAD_FAILED.CODE, ErrorCode.FILE_DOWNLOAD_FAILED.MESSAGE);
        } finally {
            try {
                if (is != null) is.close();
                if (os != null) os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        TrackerServerPool.returnObject(trackerServer);
    }

    public int deleteFile(String filepath) throws FastDFSException {
        if (StringUtils.isBlank(filepath))
            throw new FastDFSException(ErrorCode.FILE_PATH_ISNULL.CODE, ErrorCode.FILE_PATH_ISNULL.MESSAGE);
        TrackerServer trackerServer = TrackerServerPool.borrowObject();
        StorageClient1 storageClient = new StorageClient1(trackerServer, null);
        int success = 0;
        try {
            success = storageClient.delete_file1(filepath);
            if (success != 0)
                throw new FastDFSException(ErrorCode.FILE_DELETE_FAILED.CODE, ErrorCode.FILE_DELETE_FAILED.MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
            throw new FastDFSException(ErrorCode.FILE_DELETE_FAILED.CODE, ErrorCode.FILE_DELETE_FAILED.MESSAGE);
        }
        TrackerServerPool.returnObject(trackerServer);
        return success;
    }

    public static String toLocal(String path) {
        if (StringUtils.isNotBlank(path)) {
            path = path.replaceAll("\\\\", SEPARATOR);
            if (path.contains(POINT)) {
                String pre = path.substring(0, path.lastIndexOf(POINT) + 1);
                String suffix = path.substring(path.lastIndexOf(POINT) + 1).toLowerCase();
                path = pre + suffix;
            }
        }
        return path;
    }

    public static String getFilenameSuffix(String filename) {
        String suffix = null;
        String originalFilename = filename;
        if (StringUtils.isNotBlank(filename)) {
            if (filename.contains(SEPARATOR)) {
                filename = filename.substring(filename.lastIndexOf(SEPARATOR) + 1);
            }
            if (filename.contains(POINT)) {
                suffix = filename.substring(filename.lastIndexOf(POINT) + 1);
            } else {
                if (logger.isErrorEnabled()) {
                    logger.error("filename error without suffix : {}", originalFilename);
                }
            }
        }
        return suffix;
    }

    public String getOriginalFilename(String filepath) throws FastDFSException {
        Map<String, Object> descriptions = getFileDescriptions(filepath);
        if (descriptions != null && descriptions.get(FILENAME) != null) {
            return (String) descriptions.get(FILENAME);
        }
        return null;
    }

    public Map<String, Object> getFileDescriptions(String filepath) throws FastDFSException {
        TrackerServer trackerServer = TrackerServerPool.borrowObject();
        StorageClient1 storageClient = new StorageClient1(trackerServer, null);
        NameValuePair[] nvps = null;
        try {
            nvps = storageClient.get_metadata1(filepath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        TrackerServerPool.returnObject(trackerServer);

        Map<String, Object> infoMap = null;

        if (nvps != null && nvps.length > 0) {
            infoMap = new HashMap<>(nvps.length);
            for (NameValuePair nvp : nvps) {
                infoMap.put(nvp.getName(), nvp.getValue());
            }
        }
        return infoMap;
    }
}