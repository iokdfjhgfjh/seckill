package com.southwind;

import com.southwind.entity.Product;
import com.southwind.service.ProductCategoryService;
import com.southwind.vo.ProductCategoryVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.yaml.snakeyaml.events.Event;

import java.util.List;

@SpringBootTest
class MmallApplicationTests {

    @Autowired
    private ProductCategoryService service;

    @Test
    void contextLoads() {
        List<ProductCategoryVO> li = service.findAllProductByCategoryLevelOne();
        System.out.println(li.get(0).getId());


    }

}
