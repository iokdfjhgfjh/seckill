package com.southwind.controller;

import com.southwind.entity.Cart;
import com.southwind.entity.Product;
import com.southwind.entity.SeckillMessage;
import com.southwind.entity.User;
import com.southwind.exception.MMallException;
import com.southwind.rabbitmq.MQSender;
import com.southwind.result.RespBean;
import com.southwind.result.RespBeanEnum;
import com.southwind.result.ResponseEnum;
import com.southwind.service.CartService;
import com.southwind.service.ProductService;
import com.southwind.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
@Slf4j
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MQSender mqSender;

    private Map<Integer,Boolean> EmptyStockMap = new HashMap<>();

    @Autowired
    private RedisScript<Long> script;

    @Autowired
    private ProductService productService;
    @Autowired
    private CartService cartService;

    @RequestMapping("/doSeckill")
    @ResponseBody
    public RespBean doSeckill(HttpSession session, Integer productId) {
//        RespBean respBean = new RespBean();
//        respBean.setCode();
//        respBean.setMessage();
//        respBean.setObj();
//        return respBean;
        User user = (User) session.getAttribute("user");
        if (user == null) {
            //未登录
            log.info("【秒杀商品】当前为未登录状态");
            throw new MMallException(ResponseEnum.NOT_LOGIN);
        }

//        boolean flag = true;
//        if (flag) return RespBean.success(0);

            ValueOperations valueOperations = redisTemplate.opsForValue();
//判断是否重复抢购
            Cart seckillCart = (Cart) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + productId);
            if (seckillCart != null) {
                return RespBean.error(RespBeanEnum.REPEATE_ERROR);
            }
            if (EmptyStockMap.get(productId)) {
                return RespBean.error(RespBeanEnum.EMPTY_STOCK);
            }
//        long stock = valueOperations.decrement("seckillGoods:"+goodsId);
            // redis 库存减一
            Long stock = (long) redisTemplate.execute(script, Collections.singletonList("seckillProduct:" + productId),
                    Collections.EMPTY_LIST);

            // 缓冲中没有了？
            if (stock < 0) {
                EmptyStockMap.put(productId, true);
                valueOperations.increment("seckillGoods:" + productId);
                return RespBean.error(RespBeanEnum.EMPTY_STOCK);
            }
            SeckillMessage seckillMessage = new SeckillMessage(user, productId);
            mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(seckillMessage));
            return RespBean.success(0);
        }

        //    OrderId:成功， -1：秒杀失败 0：排队中
        @RequestMapping(value="/result", method = RequestMethod.GET)
        @ResponseBody
        public RespBean getResult(HttpSession session, Integer productId){
            User user = (User) session.getAttribute("user");
            if (user == null) {
                log.info("【秒杀商品】当前为未登录状态");
                throw new MMallException(ResponseEnum.NOT_LOGIN);
            }
            Integer orderId = cartService.getResult(user, productId);
            return RespBean.success(orderId);
        }

        @Override
        public void afterPropertiesSet() throws Exception{
            List<Product> list = productService.findAllByTypeAndProductCategoryId(1, 438);
            if (CollectionUtils.isEmpty(list)) {
                return;
            }
            list.forEach(product -> {
                redisTemplate.opsForValue().set("seckillProduct:" + product.getId(), product.getStock());
                EmptyStockMap.put(product.getId(), false);
            });
        }
    }
