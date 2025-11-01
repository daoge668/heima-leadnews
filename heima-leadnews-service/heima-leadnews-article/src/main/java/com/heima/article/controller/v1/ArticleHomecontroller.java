package com.heima.article.controller.v1;

import com.heima.article.service.ApArticleService;
import com.heima.common.constant.ArticleConstants;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.common.dtos.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/article")
public class ArticleHomecontroller {
    @Autowired
    private ApArticleService apArticleService;

    /**
     * load
     * @param articleHomeDto
     * @return
     */
    @PostMapping("/load")
    public ResponseResult load (@RequestBody ArticleHomeDto articleHomeDto){
        log.info("{}",articleHomeDto);
        return apArticleService.load(ArticleConstants.LOADTYPE_LOAD_MORE,articleHomeDto);
    }
    @PostMapping("/loadmore")
    public ResponseResult loadMore (@RequestBody ArticleHomeDto articleHomeDto){
        return apArticleService.load(ArticleConstants.LOADTYPE_LOAD_MORE,articleHomeDto);
    }
    @PostMapping("/loadnew")
    public ResponseResult loadNew (@RequestBody ArticleHomeDto articleHomeDto){
        return apArticleService.load(ArticleConstants.LOADTYPE_LOAD_NEW,articleHomeDto);
    }

}
