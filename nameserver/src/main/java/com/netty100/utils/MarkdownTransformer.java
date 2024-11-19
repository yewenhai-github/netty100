package com.netty100.utils;

import cn.hutool.core.io.resource.ClassPathResource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TableBlock;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.AttributeProvider;
import org.commonmark.renderer.html.AttributeProviderContext;
import org.commonmark.renderer.html.AttributeProviderFactory;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author why
 */
@Slf4j
public class MarkdownTransformer {

    public static void toHtml(String mdFileName, String charset) {
        try {
            final File mdFile = new File(new ClassPathResource("static/" + mdFileName).getAbsolutePath());
            final String htmlFileName = FilenameUtils.removeExtension(mdFile.getAbsolutePath()) + ".html";
            final String mdContent = FileUtils.readFileToString(mdFile, charset);
            Parser parser = Parser.builder().build();
            Node document = parser.parse(mdContent);
            IOUtils.write(HtmlRenderer.builder().build().render(document), new FileOutputStream(htmlFileName, false), charset);
        } catch (IOException e) {
            log.error("markdown文件转换html失败,", e);
        }
    }

    public static void extensionToHtml(String mdFileName, String charset) {
        try {
            Set<Extension> headingAnchorExtensions = Collections.singleton(HeadingAnchorExtension.create());
            List<Extension> tableExtension = Collections.singletonList(TablesExtension.create());
            Parser parser = Parser.builder()
                    .extensions(tableExtension)
                    .build();
            final File mdFile = new File(new ClassPathResource("static/" + mdFileName).getAbsolutePath());
            final String htmlFileName = FilenameUtils.removeExtension(mdFile.getAbsolutePath()) + ".html";
            final String mdContent = FileUtils.readFileToString(mdFile, charset);
            Node document = parser.parse(mdContent);
            HtmlRenderer renderer = HtmlRenderer.builder()
                    .extensions(headingAnchorExtensions)
                    .extensions(tableExtension)
                    .attributeProviderFactory(context -> new CustomAttributeProvider())
                    .build();
            IOUtils.write(renderer.render(document), new FileOutputStream(htmlFileName, false), charset);
        } catch (IOException e) {
            log.error("markdown文件转换html失败,", e);
        }

    }

    static class CustomAttributeProvider implements AttributeProvider {
        @Override
        public void setAttributes(Node node, String tagName, Map<String, String> attributes) {
            if (node instanceof Link) {
                attributes.put("target", "_blank");
            }
            if (node instanceof TableBlock) {
                attributes.put("class", "ui celled table");
            }
        }
    }

    public static void main(String[] args) {
        extensionToHtml("游戏端接入.md", StandardCharsets.UTF_8.name());
    }
}
