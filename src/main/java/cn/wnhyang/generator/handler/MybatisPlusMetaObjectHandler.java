package cn.wnhyang.generator.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author wnhyang
 * @date 2024/3/14
 **/
@Component
@Slf4j
public class MybatisPlusMetaObjectHandler implements MetaObjectHandler {

    @Value("${mybatis-plus.login:false}")
    private Boolean login;

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());

        if (login) {
            this.strictInsertFill(metaObject, "creator", LocalDateTime.class, LocalDateTime.now());
            this.strictInsertFill(metaObject, "updater", LocalDateTime.class, LocalDateTime.now());

        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());

        if (login) {
            this.strictInsertFill(metaObject, "updater", LocalDateTime.class, LocalDateTime.now());

        }
    }
}
