# crud-quickstart

个人博客：[无奈何杨（wnhyang）](https://wnhyang.github.io/)

个人语雀：[wnhyang](https://www.yuque.com/wnhyang "wnhyang")

共享语雀：[在线知识共享](https://www.yuque.com/wnh "在线知识共享")

Github：[wnhyang - Overview](https://github.com/wnhyang)

* * *

# 简介

如标题所言，本篇文章介绍如何使用`MybatisPlus-Generator`自定义模版生成`CRUD`、`DTO`、`VO`、`Convert`等。

项目已在开源，可以通过以下`Github`/`Gitee`链接下载源码使用，目前生成器还不是很灵活，你可以下载源码自定义修改，或者真的有需要可以提`Issues`，我们一起来完善。

[GitHub - wnhyang/crud-quickstart](https://github.com/wnhyang/crud-quickstart)

[Gitee - wnhyang/crud-quickstart](https://gitee.com/wnhyang/crud-quickstart)

Mybatis官网：[代码生成器配置新 | MyBatis-Plus](https://baomidou.com/pages/981406/)

![](https://jsd.cdn.zzko.cn/gh/wnhyang/picx-images-hosting@master/20240316/.7ljqxmoerv.webp)

# 项目说明

看图就行了

![image](https://jsd.cdn.zzko.cn/gh/wnhyang/picx-images-hosting@master/20240316/image.86tejyp5qr.webp)

从下面截图和这里的生成器可以看到除了`MybatisPlus`自带的`entity.java`、`mapper.java`、`mapper.xml`、`service.java`、`serviceImpl.java`、`controller.java`外，自定了`CreateVO.java`、`UpdateVO.java`、`VO.java`、`Page.java`、`DTO.java`、`Convert.java`模版。

![image](https://jsd.cdn.zzko.cn/gh/wnhyang/picx-images-hosting@master/20240316/image.231mhi1f0a.webp)

# 使用范围

数据源：`MySQL`，因为我主要使用的是`MySQL`，其他数据源没测试过，不敢保证。

工具：`lombok`、`mapstruct`，因为项目使用`mapstruct`做`DTO`、`VO`的转换工具，所以这些是必要的。

基础但非必要：`JDK17` + `Spring Boot3` + `MybatisPlus`最新版，非必要指的是版本。

# 使用方法

如上截图，因为是我日常使用，添加了一些我需要的依赖、配置和自定义类。`pom`依赖就多了些，你可以根据自需要取舍。这里就不贴了，有点多。

## 必要依赖

因为自定义的不止这些，所以单单使用这些是不够的。

```xml
<dependencies>
    <dependency>
      <groupId>com.baomidou</groupId>
      <artifactId>mybatis-plus-generator</artifactId>
    </dependency>
    
    <dependency>
      <groupId>org.apache.velocity</groupId>
      <artifactId>velocity-engine-core</artifactId>
    </dependency>
</dependencies>
```

## 示例sql

项目中自带有示例`sql`，如下，有两张表，他们通用的特点是共有5个字段（`creator`、`create_time`、`updater`、`update_time`、`deleted`），`deleted`是逻辑删除字段。

也是因此，生成的`entity`共同继承了一个基础类`BasePO.java`。

```sql
-- auto-generated definition
create table sys_dict_data
(
  id          bigint auto_increment comment '字典数据主键' primary key,
  sort        int          default 0                 not null comment '字典排序',
  label       varchar(100) default ''                not null comment '字典标签',
  value       varchar(100) default ''                not null comment '字典键值',
  dict_type   varchar(100) default ''                not null comment '字典类型',
  status      tinyint      default 0                 not null comment '状态（0正常 1停用）',
  remark      varchar(500) null comment '备注',
  creator     varchar(64)  default '' null comment '创建者',
  create_time datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
  updater     varchar(64)  default '' null comment '更新者',
  update_time datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
  deleted     bit          default b'0'              not null comment '是否删除'
) comment '字典数据表' charset = utf8mb4;

-- auto-generated definition
create table sys_operate_log
(
  id               bigint auto_increment comment '日志主键' primary key,
  user_id          bigint                                  not null comment '用户编号',
  module           varchar(50)                             not null comment '模块标题',
  name             varchar(50)                             not null comment '操作名',
  type             int           default 0                 not null comment '操作分类',
  content          varchar(2000) default ''                not null comment '操作内容',
  exts             varchar(512)  default ''                not null comment '拓展字段',
  request_method   varchar(16)   default '' null comment '请求方法名',
  request_url      varchar(255)  default '' null comment '请求地址',
  user_ip          varchar(50) null comment '用户 IP',
  user_agent       varchar(200) null comment '浏览器 UA',
  java_method      varchar(512)  default ''                not null comment 'Java 方法名',
  java_method_args varchar(8000) default '' null comment 'Java 方法的参数',
  start_time       datetime                                not null comment '操作时间',
  duration         int                                     not null comment '执行时长',
  result_code      int           default 0                 not null comment '结果码',
  result_msg       varchar(512)  default '' null comment '结果提示',
  result_data      varchar(4000) default '' null comment '结果数据',
  creator          varchar(64)   default '' null comment '创建者',
  create_time      datetime      default CURRENT_TIMESTAMP not null comment '创建时间',
  updater          varchar(64)   default '' null comment '更新者',
  update_time      datetime      default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
  deleted          bit           default b'0'              not null comment '是否删除'
) comment '操作日志记录' charset = utf8mb4;
```

## 生成器

使用时修改静态常量就可以，如：数据源、用户名密码、模块名、包名、作者、表名等。

```java
/**
 * @author wnhyang
 * @date 2024/3/15
 **/
public class Generator {
    /**
     * 数据源
     */
    private static final String DATASOURCE_URL = "jdbc:mysql://mysql:3306/dbName?allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowPublicKeyRetrieval=true";

    /**
     * 用户名
     */
    private static final String USERNAME = "wnhyang";

    /**
     * 密码
     */
    private static final String PASSWORD = "wnhyang";

    private static final String MODULE_NAME = "";

    private static final String OUTPUT_PATH = System.getProperty("user.dir");

    /**
     * 父包名
     */
    private static final String PARENT_PATH = "cn.wnhyang.generator";

    /**
     * 作者
     */
    private static final String AUTHOR = "wnhyang";

    /**
     * 表名
     */
    private static final List<String> TABLES = new ArrayList<>(
        Arrays.asList("sys_dict_data", "sys_operate_log"));


    public static void main(String[] args) {
        //1、配置数据源
        FastAutoGenerator.create(DATASOURCE_URL, USERNAME, PASSWORD)
        //2、全局配置
        .globalConfig(builder -> {
            builder.disableOpenDir() // 禁止打开输出目录 默认 true
            .outputDir(OUTPUT_PATH + "/src/main/java")   // 设置输出路径：项目的 java 目录下
            .author(AUTHOR) // 设置作者名p
            // .enableKotlin() //开启 kotlin 模式 默认false
            // .enableSwagger()   // 开启 swagger 模式 默认false
            .dateType(DateType.TIME_PACK)   // 定义生成的实体类中日期的类型 TIME_PACK=LocalDateTime;ONLY_DATE=Date;
            .commentDate("yyyy/MM/dd"); // 注释日期 默认值 yyyy-MM-dd
        })
        //3、包配置
        .packageConfig(builder -> {
            builder.parent(PARENT_PATH) // 父包名 默认值 com.baomidou
            .moduleName(MODULE_NAME)   // 父包模块名 默认值 无
            .entity("entity")   // Entity 包名 默认值 entity
            .service("service") //Service 包名 默认值 service
            .serviceImpl("service.impl") // Service Impl 包名 默认值:service.impl
            .mapper("mapper")   // Mapper 包名 默认值 mapper
            .xml("mapper")  // Mapper XML 包名 默认值 mapper.xml
            .controller("controller") // Controller 包名 默认值 controller
            .pathInfo(Collections.singletonMap(OutputFile.xml, OUTPUT_PATH + "/src/main/resources/mapper"));    //配置 mapper.xml 路径信息：项目的 resources 目录下
        })
        //4、模版配置
        .templateConfig(builder -> {
            builder.entity("/templates/entity.java")
            .service("/templates/service.java")
            .serviceImpl("/templates/serviceImpl.java")
            .mapper("/templates/mapper.java")
            .xml("/templates/mapper.xml")
                            .controller("/templates/controller.java");

                })
                //5、策略配置
                .strategyConfig(builder -> {
                    builder.addInclude(TABLES) // 设置需要生成的数据表名
                            .addTablePrefix("t_", "c_", "sys_", "de_") // 设置过滤表前缀

                            //5.1、实体类策略配置
                            .entityBuilder()
                            .enableFileOverride() // 覆盖entity
                            .superClass(BasePO.class)
                            //.disableSerialVersionUID()  // 禁用生成 serialVersionUID 默认值 true
                            .enableLombok() // 开启 Lombok 默认值:false
                            .enableTableFieldAnnotation()       // 开启生成实体时生成字段注解 默认值 false
                            .logicDeleteColumnName("deleted")   // 逻辑删除字段名
                            .naming(NamingStrategy.underline_to_camel)  //数据库表映射到实体的命名策略：下划线转驼峰命
                            .columnNaming(NamingStrategy.underline_to_camel)    // 数据库表字段映射到实体的命名策略：下划线转驼峰命
                            // .addSuperEntityColumns("creator", "create_time", "updater", "update_time")
                            // .addTableFills(
                            //  new Column("creator", FieldFill.INSERT),
                            //  new Column("updater", FieldFill.INSERT_UPDATE)
                            // )   // 添加表字段填充，"create_time"字段自动填充为插入时间，"modify_time"字段自动填充为插入修改时间
                            .formatFileName("%s")

                            //5.2、Mapper策略配置
                            .mapperBuilder()
                            .enableFileOverride() // 覆盖mapper
                            .superClass(BaseMapperX.class)   // 设置父类
                            .mapperAnnotation(org.apache.ibatis.annotations.Mapper.class)      // 开启 @Mapper 注解
                            // .enableBaseResultMap() //启用 BaseResultMap 生成
                            .formatMapperFileName("%sMapper")   // 格式化 mapper 文件名称
                            .formatXmlFileName("%sMapper") // 格式化 Xml 文件名称

                            //5.3、service 策略配置
                            .serviceBuilder()
                            .enableFileOverride() // 覆盖service
                            .formatServiceFileName("%sService") // 格式化 service 接口文件名称，%s进行匹配表名，如 UserService
                            .formatServiceImplFileName("%sServiceImpl") // 格式化 service 实现类文件名称，%s进行匹配表名，如 UserServiceImpl

                            //5.4、Controller策略配置
                            .controllerBuilder()
                            .enableFileOverride() // 覆盖controller
                            .enableRestStyle()  // 开启生成 @RestController 控制器
                            //.enableHyphenStyle() //开启驼峰转连字符 默认false
                            .formatFileName("%sController"); // 格式化 Controller 类文件名称，%s进行匹配表名，如 UserController

                })
                //6、自定义配置
                .injectionConfig(consumer -> {
                    Map<String, Object> customMap = new HashMap<>();
                    customMap.put("dto", PARENT_PATH + ".dto");
                    customMap.put("create", PARENT_PATH + ".vo.create");
                    customMap.put("update", PARENT_PATH + ".vo.update");
                    customMap.put("vo", PARENT_PATH + ".vo");
                    customMap.put("page", PARENT_PATH + ".vo.page");
                    customMap.put("convert", PARENT_PATH + ".convert");

                    consumer.customMap(customMap);
                    // DTO
                    List<CustomFile> customFiles = new ArrayList<>();
                    customFiles.add(new CustomFile.Builder().packageName("dto").fileName("DTO.java")
                            .templatePath("/templates/dto/DTO.java.vm").enableFileOverride().build());
                    customFiles.add(new CustomFile.Builder().packageName("vo/create").fileName("CreateVO.java")
                            .templatePath("/templates/vo/CreateVO.java.vm").enableFileOverride().build());
                    customFiles.add(new CustomFile.Builder().packageName("vo/update").fileName("UpdateVO.java")
                            .templatePath("/templates/vo/UpdateVO.java.vm").enableFileOverride().build());
                    customFiles.add(new CustomFile.Builder().packageName("vo").fileName("VO.java")
                            .templatePath("/templates/vo/VO.java.vm").enableFileOverride().build());
                    customFiles.add(new CustomFile.Builder().packageName("vo/page").fileName("PageVO.java")
                            .templatePath("/templates/vo/PageVO.java.vm").enableFileOverride().build());
                    customFiles.add(new CustomFile.Builder().packageName("convert").fileName("Convert.java")
                            .templatePath("/templates/convert/Convert.java.vm").enableFileOverride().build());
                    consumer.customFile(customFiles);
                })
                //7、模板
                .templateEngine(new VelocityTemplateEngine())

                /*
                    .templateEngine(new VelocityTemplateEngine())
                    .templateEngine(new FreemarkerTemplateEngine())
                    .templateEngine(new BeetlTemplateEngine())
                */

                //8、执行
                .execute();
    }
}
```

## 运行生成器

在运行成功后，生成如下类

![image](https://jsd.cdn.zzko.cn/gh/wnhyang/picx-images-hosting@master/20240316/image.64dlvxtdbu.webp)



![image](https://jsd.cdn.zzko.cn/gh/wnhyang/picx-images-hosting@master/20240316/image.sypb87xi1.webp)



![image](https://jsd.cdn.zzko.cn/gh/wnhyang/picx-images-hosting@master/20240316/image.39kxq5fma9.webp)

## 运行web项目

1、在`IDEA`中右键项目，选择`Reformat Code`

![image](https://jsd.cdn.zzko.cn/gh/wnhyang/picx-images-hosting@master/20240316/image.3uulcgctxu.webp)

2、然后优化导包

![image](https://jsd.cdn.zzko.cn/gh/wnhyang/picx-images-hosting@master/20240316/image.45hf5lsuqw.webp)

3、修改`application.yml`

`dbname`、`dbname`、`dbpassword`、`redispassword`等

```yml
spring:
  datasource:
    dynamic:
      primary: master #设置默认的数据源或者数据源组,默认值即为master
      strict: false #严格匹配数据源,默认false. true未匹配到指定数据源时抛异常,false使用默认数据源
      datasource:
        master:
          url: jdbc:mysql://mysql:3306/dbName?allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowPublicKeyRetrieval=true
          username: dbname
          password: dbpassword
          driver-class-name: com.mysql.cj.jdbc.Driver # 3.2.0开始支持SPI可省略此配置
        slave:
          url: jdbc:mysql://mysql:3306/dbName?allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowPublicKeyRetrieval=true
          username: dbname
          password: dbpassword
          driver-class-name: com.mysql.cj.jdbc.Driver # 3.2.0开始支持SPI可省略此配置
  data:
    redis:
      host: redis
      port: 6379
      # cluster:
      #   nodes: 127.0.0.1:6379
      database: 0
      password: redispassword
      lettuce:
        pool:
          max-active: 64 #最大连接数，0表示无限制
          max-idle: 32 #最大等待连接数，0表示无限制
          min-idle: 0 #最小等待连接数，0表示无限制
          max-wait: 20ms #最大建立连接等待时间，-1表示无限制
        shutdown-timeout: 100ms
```

4、运行`GeneratorApplication`

![image](https://jsd.cdn.zzko.cn/gh/wnhyang/picx-images-hosting@master/20240316/image.67x7tnw2wi.webp)

## 测试

使用`Postman`之类的工具，或者使用`IDEA`的`RestfulTool`插件测试

![image](https://jsd.cdn.zzko.cn/gh/wnhyang/picx-images-hosting@master/20240316/image.60tzy8d9et.webp)

我使用的是`Apifox`插件配合客户端，所以在配置了`Apifox`插件项目之后，右键项目的`controller`，选择`Upload to Apifox`同步接口。

![image](https://jsd.cdn.zzko.cn/gh/wnhyang/picx-images-hosting@master/20240316/image.60tzy8fwvj.webp)

然后在`Apifox`客户端就可以看到所有接口了。

![image](https://jsd.cdn.zzko.cn/gh/wnhyang/picx-images-hosting@master/20240316/image.6bgtrdzm4t.webp)

1、设置环境；2、选择接口；3、自动生成；4、发送

完美！！！

![image](https://jsd.cdn.zzko.cn/gh/wnhyang/picx-images-hosting@master/20240316/image.1755244cfn.webp)

# 欢迎使用

[GitHub - wnhyang/crud-quickstart](https://github.com/wnhyang/crud-quickstart)

[Gitee - wnhyang/crud-quickstart](https://gitee.com/wnhyang/crud-quickstart)

# 写在最后

拙作艰辛，字句心血，望诸君垂青，多予支持，不胜感激。

* * *

个人博客：[无奈何杨（wnhyang）](https://wnhyang.github.io/)

个人语雀：[wnhyang](https://www.yuque.com/wnhyang "wnhyang")

共享语雀：[在线知识共享](https://www.yuque.com/wnh "在线知识共享")

Github：[wnhyang - Overview](https://github.com/wnhyang)

![](https://wnhyang.github.io/images/wechat_channel.webp)