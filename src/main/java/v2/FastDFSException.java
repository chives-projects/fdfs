package v2;

/**
 * @Description
 * @Author shichao.chen
 * @Date 2019/9/11 16:23
 * @Version 1.0
 **/
public class FastDFSException extends Exception {
    private String code;
    private String message;

    public FastDFSException() {
    }

    public FastDFSException(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
