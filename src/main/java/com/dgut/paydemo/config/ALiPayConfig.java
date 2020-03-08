package com.dgut.paydemo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Ys
 * @data 2019/12/24 15:48
 */
@Data
@Component
@ConfigurationProperties(prefix = "ali")
public class ALiPayConfig {
    private  String url; // 支付宝网关，一般固定
    private String appId;
    private String private_key;
    private String format;
    private String charset;
    private String alipay_public_key;
    private String sign_type;
    private String notify_public_key;
    private String notify_url;
}
