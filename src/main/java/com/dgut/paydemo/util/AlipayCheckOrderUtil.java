package com.dgut.paydemo.util;

import com.alipay.api.AlipayApiException;

import com.dgut.paydemo.config.ALiPayConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Map;

/**
 * @author YS
 * @data 2019/12/25 11:17
 */
@Component
public class AlipayCheckOrderUtil {


//    @Autowired
//    private OrderService orderService;

    @Autowired
    private ALiPayConfig aLiPayConfig;

    private static AlipayCheckOrderUtil alipayCheckOrderUtil;
    public static void check (Map<String, String> params) throws AlipayApiException {
        String outTradeNo = params.get("out_trade_no");
//        OrderService orderService=new OrderServiceImpl();
//        ALiPayConfig aLiPayConfig=new ALiPayConfig();
        // 1、商户需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，

//        Orders orders = alipayCheckOrderUtil.orderService.findOne(Long.parseLong(outTradeNo)); // 这个方法自己实现
//        if (orders == null) {
//            throw new AlipayApiException("out_trade_no错误");
//        }

        // 2、判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额），
        long total_amount = new BigDecimal(params.get("total_amount")).multiply(new BigDecimal(100)).longValue();
//        if (total_amount != orders.getAmount().multiply(new BigDecimal(100)).longValue()) {
//            throw new AlipayApiException("error total_amount");
//        }

        // 3、校验通知中的seller_id（或者seller_email)是否为out_trade_no这笔单据的对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email），
        // 第三步可根据实际情况省略

        // 4、验证app_id是否为该商户本身。
        if (!params.get("app_id").equals(alipayCheckOrderUtil.aLiPayConfig.getAppId())){
            throw new AlipayApiException("app_id不一致");
        }
    }

    @PostConstruct
    public void init() {
        alipayCheckOrderUtil=this;
//        alipayCheckOrderUtil.orderService=this.orderService;
        alipayCheckOrderUtil.aLiPayConfig=this.aLiPayConfig;
    }

}
