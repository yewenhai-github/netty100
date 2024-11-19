package com.netty100;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import lombok.val;
import org.apache.commons.lang.time.DateUtils;

import java.util.*;

/**
 * <p>
 * 代码生成器
 * </p>
 *
 * @author why
 */
public class AppMpGenerator {

    public static void main(String[] args) {
//        String name = "user_cluster,warn_config,warn_info,warn_times";
//        String[] names = name.split(",");
//
//        for (String tableName : names) {
//            genTables(tableName);
//        }
        val date = "2022-04-24 01:01:01";
        val date1 = DateUtil.parse(date, "yyyy-MM-dd HH:mm:ss");
        int hour = DateUtil.hour(date1, true);
        int min = DateUtil.minute(date1);
        int seconds = DateUtil.second(date1);
        System.out.println(hour * 3600 + min * 60 + seconds);
    }

    @SuppressWarnings("DuplicatedCode")
    static void genTables(String tableName) {
        AutoGenerator mpg = new AutoGenerator();
        GlobalConfig gc = new GlobalConfig();
        gc.setOutputDir("C:\\codes\\netty100-nameserver\\nameserver\\src\\main\\java");
        gc.setAuthor("why");
        gc.setOpen(true);
        gc.setSwagger2(true);
        gc.setDateType(DateType.ONLY_DATE);
        gc.setBaseResultMap(true);
        gc.setActiveRecord(true);
        mpg.setGlobalConfig(gc);
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://192.168.70.101/netty100?useUnicode=true&amp&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true&allowMutiQueries=true&serverTimezone=GMT%2B8&useOldAliasMetadataBehavior=true");
        dsc.setDriverName("com.mysql.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("u6tO0fB20sTZlZev");
        mpg.setDataSource(dsc);
        PackageConfig pc = new PackageConfig();
        pc.setParent("com.netty100.app");
        mpg.setPackageInfo(pc);
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };
        String templatePath = "/templates/mapper.xml";
        List<FileOutConfig> focList = new ArrayList<>();
        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);
        TemplateConfig templateConfig = new TemplateConfig();
        templateConfig.setXml(templatePath);
        mpg.setTemplate(templateConfig);
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setRestControllerStyle(true);
        strategy.setInclude(tableName);
        strategy.setControllerMappingHyphenStyle(false);
        strategy.setTablePrefix(pc.getModuleName() + "_");
        strategy.setEntityTableFieldAnnotationEnable(true);
        strategy.setEntityBooleanColumnRemoveIsPrefix(false);
        mpg.setStrategy(strategy);
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();
    }
}