package v2;

public enum ErrorCode {
    FILE_PATH_ISNULL("error.fastdfs.file_path_isnull", "文件路径为空"),

    FILE_ISNULL("error.fastdfs.file_isnull", "文件为空"),

    FILE_UPLOAD_FAILED("error.fastdfs.file_upload_failed", "文件上传失败"),

    FILE_NOT_EXIST("error.fastdfs.file_not_exist", "文件不存在"),

    FILE_DOWNLOAD_FAILED("error.fastdfs.file_download_failed", "文件下载失败"),

    FILE_DELETE_FAILED("error.fastdfs.file_delete_failed", "删除文件失败"),

    FILE_SERVER_CONNECTION_FAILED("error.fastdfs.file_server_connection_failed", "文件服务器连接失败"),

    FILE_OUT_SIZE("error.fastdfs.file_server_connection_failed", "文件超过大小");


    public String CODE;
    public String MESSAGE;

    ErrorCode(String CODE, String MESSAGE) {
        this.CODE = CODE;
        this.MESSAGE = MESSAGE;
    }


}
