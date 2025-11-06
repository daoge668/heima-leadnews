package com.heima.article.service.Impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.article.service.ArticleFreemarkerService;
import com.heima.common.constant.ArticleConstants;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.apache.commons.lang.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.jsoup.helper.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper,ApArticle> implements ApArticleService {
    @Autowired
    private ApArticleMapper apArticleMapper;
    @Autowired
    private ApArticleConfigMapper apArticleConfigMapper;
    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

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

    /**
     * @param dto
     * @return
     */
    @Autowired
    private ArticleFreemarkerService articleFreemarkerService;
    @Override
    public ResponseResult saveArticle(ArticleDto dto) {
        //检查参数
        if (dto == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //提取dto的内容
        ApArticle article = new ApArticle();
        BeanUtils.copyProperties(dto,article);

        //通过是否存在id判断是增加还是更改操作
        if(dto.getId() == null){
            //新增操作
            //保存文章
            save(article);
            //保存文章设定
            ApArticleConfig apArticleConfig = new ApArticleConfig(article.getId());
            apArticleConfigMapper.insert(apArticleConfig);
            //保存文章内容
            ApArticleContent apArticleContent = new ApArticleContent();
            apArticleContent.setContent(dto.getContent());
            apArticleContent.setArticleId(article.getId());
            apArticleContentMapper.insert(apArticleContent);
        }else {
            //有id,修改操作
            //修改文章
            updateById(article);
            //修改文章内容
            ApArticleContent apArticleContent = apArticleContentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, article.getId()));
            apArticleContent.setContent(dto.getContent());
            apArticleContentMapper.updateById(apArticleContent);
        }
        //异步调用,在minio中生成静态页面
        articleFreemarkerService.buildArticleToMinIO(article,dto.getContent());
        //返回结果
        return ResponseResult.okResult(article.getId());

    }
}
