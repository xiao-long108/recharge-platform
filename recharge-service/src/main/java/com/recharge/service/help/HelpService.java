package com.recharge.service.help;

import com.recharge.model.dto.response.HelpResponse;
import com.recharge.model.entity.HelpArticle;

import java.util.List;

/**
 * 帮助中心服务接口
 */
public interface HelpService {

    /**
     * 获取帮助中心数据（分类+文章）
     *
     * @return 帮助中心数据
     */
    HelpResponse getHelpData();

    /**
     * 获取文章详情
     *
     * @param articleId 文章ID
     * @return 文章详情
     */
    HelpArticle getArticleDetail(Long articleId);

    /**
     * 搜索文章
     *
     * @param keyword 关键词
     * @return 文章列表
     */
    List<HelpArticle> searchArticles(String keyword);
}
