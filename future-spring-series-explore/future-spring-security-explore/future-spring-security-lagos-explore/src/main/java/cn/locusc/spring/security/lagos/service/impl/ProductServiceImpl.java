package cn.locusc.spring.security.lagos.service.impl;

import cn.locusc.spring.security.lagos.domain.Product;
import cn.locusc.spring.security.lagos.mapper.ProductMapper;
import cn.locusc.spring.security.lagos.service.ProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Override
    public List<Product> findAll() {
        return null;
    }
}
