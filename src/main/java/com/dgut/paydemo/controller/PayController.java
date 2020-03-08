package com.dgut.paydemo.controller;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradePageRefundRequest;
import com.alipay.api.response.AlipayTradePageRefundResponse;

import com.dgut.paydemo.config.ALiPayConfig;
import com.dgut.paydemo.convertor.ConvertALiRequestParamsToMap;
import com.dgut.paydemo.util.AlipayCheckOrderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author YS
 * @data 2019/12/24 15:23
 */
@RestController
@RequestMapping("/pay")
@Slf4j
public class PayController {

    @Autowired
    private ALiPayConfig aLiPayConfig;
//    @Autowired
//    private OrderService orderService;
//    @Autowired
//    private PayService payService;
    private ExecutorService executorService = Executors.newFixedThreadPool(20);
    @GetMapping(value = "/topay")
    public void pay(HttpServletResponse response, HttpServletRequest request, @RequestParam(value = "ordersId") Long ordersId,
                    @RequestParam(value = "redirectUrl") String redirectUrl ){
//        数据库里查找订单
//        Orders orders=orderService.findOne(ordersId);
//        User user = (User) request.getSession().getAttribute("user");
//        if(! user.getUserId().equals(orders.getUserId())){
//            return ResultDTOUtil.returnMsg(ResultEnums.ERROR.getCode(),ResultEnums.ERROR.getMsg(),null);
//        }
        // TODO 验证订单状态及支付状态
        AlipayClient alipayClient = new DefaultAlipayClient(aLiPayConfig.getUrl(),
                aLiPayConfig.getAppId(),
                aLiPayConfig.getPrivate_key(),
                aLiPayConfig.getFormat(),
                aLiPayConfig.getCharset(),
                aLiPayConfig.getAlipay_public_key(),
                aLiPayConfig.getSign_type()); //获得初始化的AlipayClient
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();//创建API对应的request
        alipayRequest.setReturnUrl(redirectUrl);
        alipayRequest.setNotifyUrl("http://ysys.nat300.top/puppy/pay/notify");//在公共参数中设置回跳和通知地址
        alipayRequest.setBizContent("{" +
                "    \"out_trade_no\":\""+ordersId+"\"," +
                "    \"product_code\":\"FAST_INSTANT_TRADE_PAY\"," +

//                订单金额（必须传递的参数，详细看官方文档）
//                "    \"total_amount\": "+orders.getAmount()+"," +
                "    \"total_amount\": "+"6.66"+"," +
                "    \"subject\":\"puppy 支付\"," +
//                "    \"body\":\"Iphone6 16G\"," +
//                "    \"passback_params\":\"merchantBizType%3d3C%26merchantBizNo%3d2016010101111\"," +
                "    \"extend_params\":{" +
                "    \"sys_service_provider_id\":\"2088511833207847\"" +
                "    }"+
                "  }");//填充业务参数
        String form="";
        try {
            form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        response.setContentType("text/html;charset=" + aLiPayConfig.getCharset());
        try {
            response.getWriter().write(form);//直接将完整的表单html输出到页面
            response.getWriter().flush();
            response.getWriter().close();
        }catch (IOException e){
            e.printStackTrace();
        }
        //return ResultDTOUtil.returnMsg(ResultEnums.ERROR.getCode(),ResultEnums.ERROR.getMsg(),null);
    }
//    @PostMapping("/notify")
//    public void notify(HttpServletRequest request){
//        Map<String, String> params = ConvertALiRequestParamsToMap.convertRequestParamsToMap(request); // 将异步通知中收到的待验证所有参数都存放到map中
//        String paramsJson = JSON.toJSONString(params);
//        log.info("支付宝回调，{}", paramsJson);
//        try {
//
//            // 调用SDK验证签名
//            boolean signVerified = AlipaySignature.rsaCheckV1(params, aLiPayConfig.getNotify_public_key(),
//                    aLiPayConfig.getCharset(),aLiPayConfig.getSign_type());
//            if (signVerified) {
//                log.info("支付宝回调签名认证成功");
//                AlipayCheckOrderUtil.check(params);
////                修改订单支付状态
//                String outTradeNo = params.get("out_trade_no");
//                log.info(" out_trade_no ={}", outTradeNo);
//                Orders orders=orderService.findOne(Long.parseLong(outTradeNo));
//                if(orders == null){
//                    throw new RuntimeException("订单不存在 [outTradeNo="+outTradeNo+"]");
//                }
//                orderService.pay(orders.getOrdersId());
////                return ResultDTOUtil.returnMsg(ResultEnums.PAY_ORDER_SUCCESS.getCode(),ResultEnums.PAY_ORDER_SUCCESS.getMsg(),null);
//            } else {
//                log.info("支付宝回调签名认证失败，signVerified=false, paramsJson:{}", paramsJson);
////                return ResultDTOUtil.returnMsg(ResultEnums.PAY_ORDER_FAIL.getCode(),ResultEnums.PAY_ORDER_FAIL.getMsg(),null);
//            }
//        } catch (AlipayApiException e) {
//            log.error("支付宝回调签名认证失败,paramsJson:{},errorMsg:{}", paramsJson, e.getMessage());
////            return ResultDTOUtil.returnMsg(ResultEnums.PAY_ORDER_FAIL.getCode(),ResultEnums.PAY_ORDER_FAIL.getMsg(),null);
//        }
//
//    }
//    @PostMapping("/AppToPay")
//    public String AppToPay(@RequestParam(value = "ordersId") Long ordersId){
//        Orders orders = orderService.findOne(ordersId);
//        return  payService.getAliPayOrderStr(orders);
//    }
//
//    @PostMapping("/refund/{ordersId}")
//    public Object refund(@PathVariable(value = "ordersId") Long ordersId){
//        Orders orders =orderService.findOne(ordersId);
//        AlipayClient alipayClient = new DefaultAlipayClient(aLiPayConfig.getUrl(),
//                aLiPayConfig.getAppId(),
//                aLiPayConfig.getPrivate_key(),
//                aLiPayConfig.getFormat(),
//                aLiPayConfig.getCharset(),
//                aLiPayConfig.getAlipay_public_key(),
//                aLiPayConfig.getSign_type());
//        AlipayTradePageRefundRequest request = new AlipayTradePageRefundRequest();
//        request.setBizContent("{" +
////                "\"trade_no\":\"2014112611001004680073956707\"," +
//                "\"out_trade_no\":\" "+orders.getOrdersId()+"\"," +
//                "\"out_request_no\":\"HZ01RF001\"," +
//                "\"refund_amount\":"+orders.getAmount()+"," +
//                "\"biz_type\":\"CREDIT_REFUND\"," +
//                "\"refund_reason\":\"正常退款\"," +
////                "\"operator_id\":\"OP001\"," +
////                "\"store_id\":\"NJ_S_001\"," +
////                "\"terminal_id\":\"NJ_T_001\"," +
////                "\"extend_params\":{" +
////                "\"credit_service_id\":\"2019031400000000000000369900\"," +
////                "\"credit_category_id\":\"REFUND\"" +
////                "    }" +
//                "  }");
//        log.info("request = {}",request);
//        try {
//            AlipayTradePageRefundResponse response = alipayClient.pageExecute(request);
//            if(response.isSuccess()){
//                log.info("reponse= {} ",response);
//                System.out.println("调用成功");
//                return orderService.refund(orders.getOrdersId());
//            } else {
//                System.out.println("调用失败");
//            }
//        }catch (AlipayApiException e){
//            e.printStackTrace();
//        }
//
//        return  ResultDTOUtil.returnMsg(ResultEnums.REFUND_ORDER_FAIL.getCode(),ResultEnums.REFUND_ORDER_FAIL.getMsg(),null);
//
//    }



}
