package com.juzipi.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.juzipi.springbootinit.model.entity.Forbiddata;
import com.juzipi.springbootinit.service.ForbiddataService;
import com.juzipi.springbootinit.mapper.ForbiddataMapper;
import org.springframework.stereotype.Service;

/**
* @author 73782
* @description 针对表【forbiddata(违禁词)】的数据库操作Service实现
* @createDate 2025-03-07 17:43:52
*/
@Service
public class ForbiddataServiceImpl extends ServiceImpl<ForbiddataMapper, Forbiddata>
    implements ForbiddataService{

}




