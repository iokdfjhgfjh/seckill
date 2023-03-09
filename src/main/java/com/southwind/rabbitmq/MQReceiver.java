package com.southwind.rabbitmq;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.southwind.entity.Cart;
import com.southwind.entity.Product;
import com.southwind.entity.SeckillMessage;
import com.southwind.entity.User;
import com.southwind.exception.MMallException;
import com.southwind.mapper.CartMapper;
import com.southwind.mapper.ProductMapper;
import com.southwind.result.ResponseEnum;
import com.southwind.service.CartService;
import com.southwind.service.ProductService;
import com.southwind.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQReceiver {

    @Autowired
    private ProductService productService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private CartService cartService;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;


    @RabbitListener(queues = "seckillQueue")
    public void receive(String message){
        log.info("接收消息："+message);
        ValueOperations valueOperations = redisTemplate.opsForValue();
        SeckillMessage seckillMessage = JsonUtil.jsonStr2Object(message,SeckillMessage.class);
        int productId = seckillMessage.getProductId();
        User user = seckillMessage.getUser();
        Product product = productService.findByGoodsId(productId);
        if(product.getStock()<1){
            return;
        }
        Cart seckillCart = (Cart)redisTemplate.opsForValue().get("order:"+user.getId()+":"+productId);
        if(seckillCart!=null){
            return;
        }

        //商品减库存
        Integer stock = this.productMapper.getStockById(productId);
        if(stock == null){
            log.info("【添加购物车】商品不存在");
            return;
        }
        if(stock == 0){
            log.info("【添加购物车】库存不足");
            throw new MMallException(ResponseEnum.PRODUCT_STOCK_ERROR);
        }
//        Integer newStock = stock - cart.getQuantity();
//        if(newStock < 0){
//            log.info("【添加购物车】库存不足");
//            throw new MMallException(ResponseEnum.PRODUCT_STOCK_ERROR);
//        }
        boolean upresult = productService.update(new UpdateWrapper<Product>().setSql("stock="+"stock-1").eq(
                "id",productId).gt("stock",0));
//        this.productMapper.updateStockById(cart.getProductId(), newStock);
        if (stock-1<1){
            valueOperations.set("isStockEmpty:"+productId,"0");
        }
        Cart cart = new Cart();
        cart.setUserId(user.getId());
        cart.setProductId(productId);
        cart.setQuantity(1);
        cart.setCost(product.getPrice());
        this.cartMapper.insert(cart);

        // 这个地方的问题，rabbit 需要配置异常处理策略，是继续消费还是怎么样
        // 这个地方应该是你后加的，向redis中写入，这个是写字符串，cart是对象
            redisTemplate.opsForValue().set("order:"+user.getId()+":"+productId,cart.toString());

    }
}
