package com.heima.article.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.common.constant.ArticleConstants;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;
import org.apache.commons.lang.StringUtils;
import org.jsoup.helper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper,ApArticle> implements ApArticleService {
    @Autowired
    private ApArticleMapper apArticleMapper;
    private static final short MAX_PAGE_SIZE = 50;

    /**
     * @param loadType
     * @param dto
     * @return
     */
    @Override
    public ResponseResult load(Short loadType, ArticleHomeDto dto) {
        //校验数据
        Integer size = dto.getSize();
        if (size == null || size == 0){
            size =10;
        }
        size = Math.min(MAX_PAGE_SIZE,size);


        if(!loadType.equals(ArticleConstants.LOADTYPE_LOAD_MORE) && !loadType.equals(ArticleConstants.LOADTYPE_LOAD_NEW)){
            loadType = ArticleConstants.LOADTYPE_LOAD_MORE;
        }

        if(StringUtils.isEmpty(dto.getTag())){
            dto.setTag(ArticleConstants.DEFAULT_TAG);
        }

        if (dto.getMinBehotTime() == null) dto.setMinBehotTime(new Date());
        if (dto.getMaxBehotTime() == null) dto.setMaxBehotTime(new Date());
        //查询数据
        List<ApArticle> apArticles = apArticleMapper.loadArticleList(dto, loadType);
        //结果封装和返回
        return ResponseResult.okResult(apArticles);
    }
}
