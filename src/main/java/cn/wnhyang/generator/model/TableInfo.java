package cn.wnhyang.generator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wnhyang
 * @date 2024/5/21
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableInfo {

    private String name;

    private String comment;
}
