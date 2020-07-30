package v2;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.TrackerServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

/**
 * @Description
 * @Author csc
 * @Date 2019/9/11 16:54
 * @Version 1.0
 **/
public class TrackerServerPool {
    private static Logger logger = LoggerFactory.getLogger(TrackerServerPool.class);
    private static final String FASTDFS_CONFIG_PATH = "config.properties";
    @Value("${max_storage_connection}")
    private static int maxStorageConnection;

    private static GenericObjectPool<TrackerServer> trackerServerPool;

    private TrackerServerPool() {
    }

    private static synchronized GenericObjectPool<TrackerServer> getObjectPool() {
        if (trackerServerPool == null) {
            try {
                ClientGlobal.initByProperties(FASTDFS_CONFIG_PATH);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (MyException e) {
                e.printStackTrace();
            }
            if (logger.isDebugEnabled())
                logger.debug("ClientGlobal configInfo", ClientGlobal.configInfo());

            GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
            poolConfig.setMinIdle(2);
            if (maxStorageConnection > 0)
                poolConfig.setMaxTotal(maxStorageConnection);
            trackerServerPool = new GenericObjectPool<>(new TrackerServerFactory(), poolConfig);
        }
        return trackerServerPool;
    }

    public static TrackerServer borrowObject() throws FastDFSException {
        TrackerServer trackerServer = null;
        try {
            trackerServer = getObjectPool().borrowObject();
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof FastDFSException)
                throw (FastDFSException) e;
        }
        return trackerServer;
    }

    public static void returnObject(TrackerServer trackerServer) {
        getObjectPool().returnObject(trackerServer);
    }

}
