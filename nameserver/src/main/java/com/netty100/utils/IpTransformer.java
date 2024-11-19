package com.netty100.utils;

import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.ArrayUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbMakerConfigException;
import org.lionsoul.ip2region.DbSearcher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author why
 */
@Slf4j
@Component
public class IpTransformer {

    private File dbFile;

    @PostConstruct
    public void init() {
        try {
            String path = "ip2region/ip2region.db";
            String name = "ip2region.db";
            dbFile = new File(System.getProperty("java.io.tmpdir") + File.separator + name);
            FileUtils.copyInputStreamToFile(new ClassPathResource(path).getStream(), dbFile);
        } catch (IOException e) {
            log.error("加载IP转换数据库失败", e);
            System.exit(-1);
        }
    }

    public DbSearcher getDbSearcher() {
        try {
            return new DbSearcher(new DbConfig(), dbFile.getPath());
        } catch (FileNotFoundException | DbMakerConfigException e) {
            log.error("创建dbSearch失败", e);
            return null;
        }
    }

    public void close(DbSearcher dbSearcher) {
        try {
            if (Objects.nonNull(dbSearcher)) {
                dbSearcher.close();
            }
            log.info("dbSearch成功关闭");
        } catch (IOException e) {
            log.error("dbSearch关闭失败", e);
        }
    }

    public String[] transform(DbSearcher dbSearcher, String ip) {
        String var0 = "内网";
        try {
            if (!StringUtils.hasText(ip) || Objects.isNull(dbSearcher)) {
                return null;
            }
            final DataBlock dataBlock = dbSearcher.memorySearch(ip);
            String address = dataBlock.getRegion().replace("0|", "");
            char symbol = '|';
            if (address.charAt(address.length() - 1) == symbol) {
                address = address.substring(0, address.length() - 1);
            }
            if (address.contains(var0)) {
                return null;
            }
            String[] split = address.split("\\|");
            return ArrayUtil.sub(split, 1, 3);
        } catch (IOException e) {
            log.error("IP转换地区失败", e);
            return null;
        }
    }
}
