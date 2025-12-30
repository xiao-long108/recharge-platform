package com.recharge.service.help.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.recharge.common.exception.BusinessException;
import com.recharge.mapper.HelpArticleMapper;
import com.recharge.mapper.HelpCategoryMapper;
import com.recharge.model.dto.response.HelpResponse;
import com.recharge.model.entity.HelpArticle;
import com.recharge.model.entity.HelpCategory;
import com.recharge.service.help.HelpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 帮助中心服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HelpServiceImpl implements HelpService {

    private final HelpCategoryMapper helpCategoryMapper;
    private final HelpArticleMapper helpArticleMapper;

    @Override
    public HelpResponse getHelpData() {
        HelpResponse response = new HelpResponse();

        // 获取所有分类
        LambdaQueryWrapper<HelpCategory> categoryWrapper = new LambdaQueryWrapper<>();
        categoryWrapper.eq(HelpCategory::getStatus, 1)
                .eq(HelpCategory::getDeleted, 0)
                .orderByAsc(HelpCategory::getSortOrder);
        List<HelpCategory> categories = helpCategoryMapper.selectList(categoryWrapper);

        // 获取所有文章
        LambdaQueryWrapper<HelpArticle> articleWrapper = new LambdaQueryWrapper<>();
        articleWrapper.eq(HelpArticle::getStatus, 1)
                .eq(HelpArticle::getDeleted, 0)
                .orderByAsc(HelpArticle::getSortOrder);
        List<HelpArticle> articles = helpArticleMapper.selectList(articleWrapper);

        // 按分类分组
        Map<Long, List<HelpArticle>> articleMap = articles.stream()
                .collect(Collectors.groupingBy(HelpArticle::getCategoryId));

        // 构建响应
        List<HelpResponse.CategoryItem> categoryItems = new ArrayList<>();
        for (HelpCategory category : categories) {
            HelpResponse.CategoryItem categoryItem = new HelpResponse.CategoryItem();
            categoryItem.setId(category.getId());
            categoryItem.setName(category.getName());
            categoryItem.setIcon(category.getIcon());

            List<HelpArticle> categoryArticles = articleMap.getOrDefault(category.getId(), new ArrayList<>());
            List<HelpResponse.ArticleItem> articleItems = categoryArticles.stream().map(article -> {
                HelpResponse.ArticleItem articleItem = new HelpResponse.ArticleItem();
                articleItem.setId(article.getId());
                articleItem.setTitle(article.getTitle());
                articleItem.setContent(article.getContent());
                articleItem.setViewCount(article.getViewCount());
                return articleItem;
            }).collect(Collectors.toList());

            categoryItem.setArticles(articleItems);
            categoryItems.add(categoryItem);
        }

        response.setCategories(categoryItems);
        return response;
    }

    @Override
    public HelpArticle getArticleDetail(Long articleId) {
        HelpArticle article = helpArticleMapper.selectById(articleId);
        if (article == null || article.getDeleted() == 1 || article.getStatus() != 1) {
            throw new BusinessException("文章不存在");
        }

        // 增加浏览次数
        helpArticleMapper.incrementViewCount(articleId);

        return article;
    }

    @Override
    public List<HelpArticle> searchArticles(String keyword) {
        LambdaQueryWrapper<HelpArticle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HelpArticle::getStatus, 1)
                .eq(HelpArticle::getDeleted, 0)
                .and(w -> w.like(HelpArticle::getTitle, keyword)
                        .or()
                        .like(HelpArticle::getContent, keyword))
                .orderByAsc(HelpArticle::getSortOrder);

        return helpArticleMapper.selectList(wrapper);
    }
}
