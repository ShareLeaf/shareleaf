package co.shareleaf.utils.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Biz Melesse
 * created on 01/04/2023
 */
@Getter
@AllArgsConstructor
public enum ShareLeafExceptionCode {

    INTERNAL_SERVER_ERROR("Internal Server Error");

    private final String description;
}

