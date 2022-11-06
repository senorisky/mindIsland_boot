package com.lifemind.bluer;


import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.junit.jupiter.api.Test;

public class GeneratorTest {
    @Test
    public void generate() {
        AutoGenerator generator = new AutoGenerator();
        // 全局配置
        GlobalConfig config = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        // 设置输出到的目录
        config.setOutputDir(projectPath + "/src/test/java");
        config.setAuthor("ckz");
        // 生成结束后是否打开文件夹
        config.setOpen(false);

        // 全局配置添加到 generator 上
        generator.setGlobalConfig(config);

        // 数据源配置
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setUrl("jdbc:mysql://localhost:3306/lifemind?characterEncoding=utf-8&serverTimezone=UTC");
        dataSourceConfig.setDriverName("com.mysql.cj.jdbc.Driver");
        dataSourceConfig.setUsername("root");
        dataSourceConfig.setPassword("Zsql2312728");

        // 数据源配置添加到 generator
        generator.setDataSource(dataSourceConfig);

        // 包配置, 生成的代码放在哪个包下
        PackageConfig packageConfig = new PackageConfig();
        packageConfig.setParent("com.lifemind.bluer");

        // 包配置添加到 generator
        generator.setPackageInfo(packageConfig);

        // 策略配置
        StrategyConfig strategyConfig = new StrategyConfig();
        // 下划线驼峰命名转换

        strategyConfig.setNaming(NamingStrategy.underline_to_camel);
        strategyConfig.setColumnNaming(NamingStrategy.underline_to_camel);
        // 开启lombok
        strategyConfig.setEntityLombokModel(true);
        // 开启RestController
        strategyConfig.setRestControllerStyle(true);
        generator.setStrategy(strategyConfig);
        generator.setTemplateEngine(new FreemarkerTemplateEngine());

        // 开始生成
        generator.execute();
    }
}