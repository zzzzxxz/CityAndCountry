package com.example.demo.area.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.area.mapper.TChinaMapper;
import com.example.demo.area.entity.TChina;
import com.example.demo.area.service.TChinaService;
@Service
public class TChinaServiceImpl extends ServiceImpl<TChinaMapper, TChina> implements TChinaService{
}
