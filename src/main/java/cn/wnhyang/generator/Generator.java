package cn.wnhyang.generator;

import cn.wnhyang.generator.model.MbpGeneratorConfig;
import cn.wnhyang.generator.util.MbpGeneratorUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author wnhyang
 * @date 2024/3/15
 **/
public class Generator {

    /**
     * 数据源
     */
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306";

    /**
     * 数据库名
     */
    private static final String DATABASE = "generatorDb";

    /**
     * 驱动类名
     */
    private static final String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";

    /**
     * 用户名
     */
    private static final String USERNAME = "wnhyang";

    /**
     * 密码
     */
    private static final String PASSWORD = "123456";

    /**
     * 父包名
     */
    private static final String PARENT = "cn.wnhyang.generator";

    /**
     * 模块名
     */
    private static final String MODULE_NAME = "";

    /**
     * 表名
     */
    private static final List<String> TABLES = new ArrayList<>(
            Arrays.asList("de_condition"));

    /**
     * 表前缀
     */
    private static final String TABLE_PREFIX = "sys_,t_,c_,de_";

    /**
     * 作者
     */
    private static final String AUTHOR = "wnhyang";


    public static void main(String[] args) {
        MbpGeneratorConfig config = new MbpGeneratorConfig()
                .setJdbcUrl(JDBC_URL).setDriverClassName(DRIVER_CLASS_NAME)
                .setUsername(USERNAME).setPassword(PASSWORD)
                .setDatabase(DATABASE).setTableNames(TABLES)
                .setTablePrefix(TABLE_PREFIX).setParent(PARENT)
                .setModuleName(MODULE_NAME).setAuthor(AUTHOR);
        MbpGeneratorUtil.generate(config);
    }
}
