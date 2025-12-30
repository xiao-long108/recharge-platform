package com.recharge.service.share.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.recharge.common.exception.BusinessException;
import com.recharge.mapper.CommissionRecordMapper;
import com.recharge.mapper.SystemConfigMapper;
import com.recharge.mapper.TeamRelationMapper;
import com.recharge.mapper.UserMapper;
import com.recharge.model.dto.response.ShareResponse;
import com.recharge.model.entity.CommissionRecord;
import com.recharge.model.entity.SystemConfig;
import com.recharge.model.entity.TeamRelation;
import com.recharge.model.entity.User;
import com.recharge.service.share.ShareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分享服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShareServiceImpl implements ShareService {

    private final UserMapper userMapper;
    private final TeamRelationMapper teamRelationMapper;
    private final CommissionRecordMapper commissionRecordMapper;
    private final SystemConfigMapper systemConfigMapper;

    @Override
    public ShareResponse getShareInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        ShareResponse response = new ShareResponse();

        // 邀请码
        response.setInviteCode(user.getInviteCode());

        // 邀请链接
        String baseUrl = getConfigValue("app_base_url", "https://app.example.com");
        response.setInviteUrl(baseUrl + "/register?inviteCode=" + user.getInviteCode());

        // 生成二维码
        response.setQrCodeImage(generateQrCode(userId));

        // 分享配置
        response.setShareTitle(getConfigValue("share_title", "话费充值平台"));
        response.setShareDesc(getConfigValue("share_desc", "话费充值低至9.5折，快来一起省钱吧！"));
        response.setShareImage(getConfigValue("share_image", ""));

        // 统计已邀请人数
        LambdaQueryWrapper<TeamRelation> teamWrapper = new LambdaQueryWrapper<>();
        teamWrapper.eq(TeamRelation::getParentId, userId)
                .eq(TeamRelation::getDeleted, 0);
        Long inviteCount = teamRelationMapper.selectCount(teamWrapper);
        response.setInviteCount(inviteCount.intValue());

        // 统计累计佣金
        LambdaQueryWrapper<CommissionRecord> commissionWrapper = new LambdaQueryWrapper<>();
        commissionWrapper.eq(CommissionRecord::getUserId, userId)
                .eq(CommissionRecord::getStatus, 1)
                .eq(CommissionRecord::getDeleted, 0);
        List<CommissionRecord> records = commissionRecordMapper.selectList(commissionWrapper);
        BigDecimal totalCommission = records.stream()
                .map(CommissionRecord::getCommissionAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        response.setTotalCommission(totalCommission.toString());

        return response;
    }

    @Override
    public String generateQrCode(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        String baseUrl = getConfigValue("app_base_url", "https://app.example.com");
        String content = baseUrl + "/register?inviteCode=" + user.getInviteCode();

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);

            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 300, 300, hints);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();

            return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
        } catch (WriterException e) {
            log.error("生成二维码失败", e);
            throw new BusinessException("生成二维码失败");
        } catch (Exception e) {
            log.error("生成二维码图片失败", e);
            throw new BusinessException("生成二维码失败");
        }
    }

    @Override
    public String generatePoster(Long userId) {
        // TODO: 实现海报生成逻辑
        // 这里可以使用 Java 2D 或其他图片处理库来生成海报
        // 包含用户头像、昵称、邀请二维码等元素
        return generateQrCode(userId);
    }

    /**
     * 获取配置值
     */
    private String getConfigValue(String key, String defaultValue) {
        SystemConfig config = systemConfigMapper.selectByKey(key);
        return config != null ? config.getConfigValue() : defaultValue;
    }
}
