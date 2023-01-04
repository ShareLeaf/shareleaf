package co.shareleaf.utils.exception;

import lombok.Getter;

/**
 * @author Biz Melesse
 * created on 01/04/2023
 */
@Getter
public class ShareLeafException extends RuntimeException {

    private ShareLeafExceptionCode shareLeafExceptionCode;
    private String title;
    private String errMessage;

    public ShareLeafException(ShareLeafExceptionCode shareLeafExceptionCode, String errMessage) {
        super(errMessage);
        this.shareLeafExceptionCode = shareLeafExceptionCode;
        this.title = shareLeafExceptionCode.getDescription();
        this.errMessage = errMessage;
    }

    public ShareLeafException(String errMessage) {
        super(errMessage);
        this.errMessage = errMessage;
    }

    public ShareLeafException(String errMessage, Exception ex) {
        super(errMessage, ex);
    }

    public ShareLeafException(Exception ex) {
        super(ex);
    }
}
