package cn.locusc.spring.security.lagos.service;

import cn.locusc.spring.security.lagos.domain.Product;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ProductService extends IService<Product> {

    List<Product> findAll();

}
