package com.recharge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recharge.model.entity.WithdrawRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 提现记录Mapper
 */
@Mapper
public interface WithdrawRecordMapper extends BaseMapper<WithdrawRecord> {

    /**
     * 根据提现单号查询
     */
    default WithdrawRecord selectByWithdrawNo(String withdrawNo) {
        return selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<WithdrawRecord>()
                .eq(WithdrawRecord::getWithdrawNo, withdrawNo));
    }
}
