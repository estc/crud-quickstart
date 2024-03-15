package cn.wnhyang.generator.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author wnhyang
 * @date 2024/3/13
 **/
@Data
@AllArgsConstructor
public class ErrorCode {

    private Integer code;

    private String msg;
}
