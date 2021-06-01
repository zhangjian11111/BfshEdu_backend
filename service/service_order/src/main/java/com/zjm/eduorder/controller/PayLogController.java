package com.zjm.eduorder.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjm.commonutils.result.R;
import com.zjm.eduorder.entity.Order;
import com.zjm.eduorder.entity.PayLog;
import com.zjm.eduorder.entity.Vo.ALLOrder;
import com.zjm.eduorder.service.OrderService;
import com.zjm.eduorder.service.PayLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 支付日志表 前端控制器
 * @author ZengJinming
 * @since 2020-04-12
 */
@Api(description="订单服务相关(二维码)")
@RestController
@RequestMapping("/eduorder/pay-log")
//@CrossOrigin
public class PayLogController {
    @Autowired
    private PayLogService payLogService;

    @Autowired
    private OrderService orderService;

    //生成微信支付二维码接口
    //参数是订单号
    @ApiOperation(value = "生成微信支付二维码接口")
    @GetMapping("createQrCode/{orderNo}")
    public R createQrCode(@PathVariable String orderNo) {
        //返回信息，包含二维码地址，还有其他需要的信息
        Map map = payLogService.createQrCode(orderNo);
        System.out.println("****返回二维码map集合:"+map);
        return R.success().data(map);
    }

    //查询订单支付状态
    //参数：订单号，根据订单号查询 支付状态
    @ApiOperation(value = "查询订单支付状态")
    @GetMapping("queryPayStatus/{orderNo}")
    public R queryPayStatus(@PathVariable String orderNo) {
        Map<String,String> map = payLogService.queryPayStatus(orderNo);
        System.out.println("*****查询订单状态map集合:"+map);
        if(map == null) {
            return R.error().message("支付出错了");
        }
        //如果返回map里面不为空，通过map获取订单状态
        if(map.get("trade_state").equals("SUCCESS")) {//支付成功
            //添加记录到支付表，更新订单表订单状态
            payLogService.updateOrdersStatus(map);
            return R.success().message("支付成功");
        }
        return R.success().code(25000).message("支付中");
    }

    //2 查询所有历史订单
    @ApiOperation(value = "查询所有订单信息")
    @PostMapping("getAllLogOrder/{current}/{limit}")
    public R getAllLogOrder(@PathVariable long current, @PathVariable long limit,
                                  @RequestBody(required = false) ALLOrder allOrder){  //@RequestBody(required = false)参数值可以为空
        //创建page对象
        Page<Order> pageOrder = new Page<>(current,limit);

        //构建条件
        QueryWrapper<Order> wrapper = new QueryWrapper<>();

        // 多条件组合查询
        // mybatis学过 动态sql
        String courseId = allOrder.getCourseId();
        String memberId = allOrder.getMemberId();
        String mobile = allOrder.getMobile();
        String orderNo = allOrder.getOrderNo();
        String courseTitle = allOrder.getCourseTitle();
        Date start = allOrder.getGmtCreate();
        Date end = allOrder.getGmtModified();
        //判断条件值是否为空，如果不为空拼接条件
        if(!StringUtils.isEmpty(courseId)) {
            //构建条件
            wrapper.eq("course_id",courseId);
        }
        if(!StringUtils.isEmpty(memberId)) {
            wrapper.eq("member_id",memberId);
        }
        if(!StringUtils.isEmpty(mobile)) {
            wrapper.eq("mobile",mobile);
        }
        if(!StringUtils.isEmpty(orderNo)) {
            wrapper.eq("order_no",orderNo);
        }
        if(!StringUtils.isEmpty(courseTitle)) {
            wrapper.like("course_title",courseTitle);
        }
        if(!StringUtils.isEmpty(start)) {
            wrapper.like("gmt_create",start);
        }
        if(!StringUtils.isEmpty(end)) {
            wrapper.like("gmt_modified",end);
        }
        //排序
        wrapper.orderByDesc("gmt_create");
        //调用方法实现条件查询分页
        orderService.page(pageOrder,wrapper);
        long total = pageOrder.getTotal();//总记录数
        List<Order> records = pageOrder.getRecords(); //数据list集合
        return R.success().data("total",total).data("records",records);
    }

}

