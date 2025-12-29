package com.recharge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recharge.model.entity.TeamRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 团队关系Mapper
 */
@Mapper
public interface TeamRelationMapper extends BaseMapper<TeamRelation> {

    /**
     * 根据用户ID查询团队关系
     */
    default TeamRelation selectByUserId(Long userId) {
        return selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<TeamRelation>()
                .eq(TeamRelation::getUserId, userId));
    }

    /**
     * 查询直属下级数量
     */
    @Select("SELECT COUNT(*) FROM t_team_relation WHERE parent_id = #{userId}")
    int countDirectChildren(@Param("userId") Long userId);

    /**
     * 查询所有下级数量
     */
    @Select("SELECT COUNT(*) FROM t_team_relation WHERE ancestor_path LIKE CONCAT('%,', #{userId}, ',%') OR parent_id = #{userId}")
    int countAllChildren(@Param("userId") Long userId);

    /**
     * 查询上级链（用于佣金计算）
     */
    @Select("SELECT * FROM t_team_relation WHERE user_id IN (SELECT parent_id FROM t_team_relation WHERE user_id = #{userId} UNION SELECT SUBSTRING_INDEX(SUBSTRING_INDEX(ancestor_path, ',', n.n), ',', -1) AS ancestor FROM t_team_relation, (SELECT 1 n UNION SELECT 2 UNION SELECT 3) n WHERE user_id = #{userId} AND LENGTH(ancestor_path) - LENGTH(REPLACE(ancestor_path, ',', '')) >= n.n) ORDER BY level LIMIT #{maxLevel}")
    List<TeamRelation> selectAncestors(@Param("userId") Long userId, @Param("maxLevel") int maxLevel);
}
