package com.recharge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recharge.model.entity.HelpArticle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 帮助文章Mapper
 */
@Mapper
public interface HelpArticleMapper extends BaseMapper<HelpArticle> {

    /**
     * 增加浏览次数
     */
    @Update("UPDATE t_help_article SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(@Param("id") Long id);
}
