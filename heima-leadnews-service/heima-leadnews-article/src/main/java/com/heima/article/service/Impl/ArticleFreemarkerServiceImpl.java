package com.heima.article.service.Impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.article.service.ArticleFreemarkerService;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.pojos.ApArticle;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Service
public class ArticleFreemarkerServiceImpl implements ArticleFreemarkerService {
    /**
     * @param article
     * @param content
     */
    @Autowired
    private Configuration configuration;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ApArticleService apArticleService;
    @Override
    @Async
    public void buildArticleToMinIO(ApArticle article, String content) {
        if(StringUtils.isNotBlank(content)){
            StringWriter out = new StringWriter();
            try {
                Template template = configuration.getTemplate("article.ftl");
                Map<String,Object> contentDataModle = new HashMap<>();
                contentDataModle.put("content", JSONArray.parseArray(content));
                template.process(contentDataModle,out);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(out.toString().getBytes());
            String path = fileStorageService.uploadHtmlFile("", article.getId() + ".html", byteArrayInputStream);
            apArticleService.update(Wrappers.<ApArticle>lambdaUpdate().eq(ApArticle::getId,article.getId()).set(ApArticle::getStaticUrl,path));
        }
    }
}
