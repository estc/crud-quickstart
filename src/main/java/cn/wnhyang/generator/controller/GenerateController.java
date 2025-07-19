package cn.wnhyang.generator.controller;

import cn.wnhyang.generator.model.MbpGeneratorConfig;
import cn.wnhyang.generator.model.TableInfo;
import cn.wnhyang.generator.pojo.CommonResult;
import cn.wnhyang.generator.util.MbpGeneratorUtil;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.IDbQuery;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Map;

/**
 * @author wnhyang
 * @date 2024/5/21
 **/
@Slf4j
@Controller
@RequiredArgsConstructor
public class GenerateController {

    private final DataSourceProperties dataSourceProperties;

    @GetMapping("/")
    public String index(Model model) {
        MbpGeneratorConfig dbConfig = new MbpGeneratorConfig();
        if (dataSourceProperties != null) {
            dbConfig.setJdbcUrl(dataSourceProperties.getUrl());
            dbConfig.setDriverClassName(dataSourceProperties.getDriverClassName());
            dbConfig.setUsername(dataSourceProperties.getUsername());
            dbConfig.setPassword(dataSourceProperties.getPassword());
        }
        model.addAttribute("data", "生成器");
        model.addAttribute("dbConfig", dbConfig);
        return "index";
    }

    @PostMapping("/api/databases")
    @ResponseBody
    public CommonResult<MbpGeneratorConfig> databases(@RequestBody MbpGeneratorConfig dbConfig) {
        log.info("dbConfig: {}", dbConfig);
        // 测试连接
        boolean connectionOK = isConnectionOK(dbConfig.getJdbcUrl(), dbConfig.getUsername(), dbConfig.getPassword());
        if (!connectionOK) {
            return CommonResult.error(400, "连接失败");
        }
        // 获取数据库列表
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(dbConfig.getJdbcUrl());
        ds.setUsername(dbConfig.getUsername());
        ds.setPassword(dbConfig.getPassword());
        ds.setDriverClassName(dbConfig.getDriverClassName());
        JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
        List<Map<String, Object>> list = jdbcTemplate.queryForList("SHOW DATABASES");
        List<String> databaseInfos = Lists.newArrayList();
        databaseInfos.add("-- 请选择数据库 --");
        list.forEach(map -> {
            String database = (String) map.get("Database");
            databaseInfos.add(database);
        });
        dbConfig.setDatabases(databaseInfos);
        dbConfig.setStep(1);
        return CommonResult.success(dbConfig);
    }

    @PostMapping("/api/tables")
    @ResponseBody
    public CommonResult<MbpGeneratorConfig> tables(@RequestBody MbpGeneratorConfig dbConfig) {
        log.info("dbConfig: {}", dbConfig);
        // 测试连接
        boolean connectionOK = isConnectionOK(dbConfig.getJdbcUrl(), dbConfig.getUsername(), dbConfig.getPassword());
        if (!connectionOK) {
            return CommonResult.error(400, "连接失败");
        }

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(dbConfig.getJdbcUrl());  //getFullJdbcUrl
        ds.setUsername(dbConfig.getUsername());
        ds.setPassword(dbConfig.getPassword());
        ds.setDriverClassName(dbConfig.getDriverClassName());
        JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);

        DataSourceConfig dataSourceConfig = new DataSourceConfig.Builder(dbConfig.getJdbcUrl(), dbConfig.getUsername(), dbConfig.getPassword()).build();
        IDbQuery dbQuery = dataSourceConfig.getDbQuery();
        List<Map<String, Object>> results = jdbcTemplate.queryForList(dbQuery.tablesSql());
        List<TableInfo> tableInfos = Lists.newArrayList();
        for (Map<String, Object> table : results) {
            TableInfo tableInfo = new TableInfo();
            tableInfo.setName((String) table.get(dbQuery.tableName()));
            tableInfo.setComment((String) table.get(dbQuery.tableComment()));
            tableInfos.add(tableInfo);
        }
        dbConfig.setTableInfos(tableInfos);
        dbConfig.setStep(2);
        return CommonResult.success(dbConfig);

    }

    @PostMapping("/api/generate")
    @ResponseBody
    public CommonResult<MbpGeneratorConfig> generate(@RequestBody MbpGeneratorConfig dbConfig) {

        log.info("dbConfig: {}", dbConfig);

        MbpGeneratorUtil.generate(dbConfig);
        dbConfig.setStep(3);
        return CommonResult.success(dbConfig);
    }

    private boolean isConnectionOK(String url, String username, String password) {
        try (Connection ignored = DriverManager.getConnection(url, username, password)) {
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
