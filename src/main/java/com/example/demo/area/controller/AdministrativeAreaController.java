package com.example.demo.area.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.demo.area.ChinaTree;
import com.example.demo.area.Result;
import com.example.demo.area.entity.TChina;
import com.example.demo.area.rateLimit.RateLimit;
import com.example.demo.area.service.TChinaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zx
 */
@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class AdministrativeAreaController {

    private final TChinaService service;
    private static final String PROVINCE = "province";
    private static final String CITY = "city";
    private static final String DISTRICT = "district";


    @GetMapping("/api/address/list")
    public Result getAddress(String condition) {
        log.info("入参: {}", condition);
        List<TChina> list = new ArrayList<>(20);
        if (StringUtils.isEmpty(condition)) {
            List<TChina> chinaTree = new ChinaTree(service.list()).buildTree();
            return Result.success(chinaTree);
        } else {
            //查询出符合条件的数据
            Wrapper<TChina> queryWrapper = Wrappers.<TChina>lambdaQuery()
                    .like(TChina::getName, condition).or().eq(TChina::getId, condition);
            List<TChina> resultList = service.list(queryWrapper);
            //保存省或直辖市的Id
            List<Integer> provinces = new ArrayList<>(5);
            resultList.forEach(c -> provinces.add(c.getProvince()));

            if (!CollectionUtils.isEmpty(provinces)) {
                //通过省或直辖市的id查询出该id下的所有市和区
                Wrapper<TChina> provincesWrapper = Wrappers.<TChina>lambdaQuery()
                        .in(TChina::getProvince, provinces);
                List<TChina> chinaList = service.list(provincesWrapper);
                log.info("条件搜索返回: {}", JSON.toJSONString(resultList));
                //过滤数据，保留符合条件的数据
                resultList.forEach(result -> chinaList.forEach(c -> {
                    switch (result.getType()) {
                        case PROVINCE:
                            //如果查询出来的结果是省或直辖市,直接向下找出所有市和区
                            if (result.getId().equals(c.getProvince())) {
                                list.add(c);
                            }
                            break;
                        case CITY:
                            //如果查询出来的是市，则向上找出省级,向下找出区或县
                            if (result.getParent().equals(c.getId())) {
                                list.add(c);
                                list.add(result);
                            }
                            if (DISTRICT.equals(c.getType()) && c.getParent().equals(result.getId())) {
                                list.add(c);
                            }
                            break;
                        case DISTRICT:
                            //如果查询出来的是区或县，则向上找出省和市
                            if (PROVINCE.equals(c.getType()) && result.getProvince().equals(c.getId())) {
                                list.add(c);
                            } else if (CITY.equals(c.getType()) && result.getParent().equals(c.getId())) {
                                list.add(c);
                                list.add(result);
                            }
                            break;
                        default:
                    }
                }));

                log.info("最终结果: {}", JSON.toJSONString(list));
                List<TChina> chinaTree = new ChinaTree(list).buildTree();
                return Result.success(chinaTree);
            }
        }
        return Result.success(null);
    }

    /**
     *此处是做的限流处理，也可以用redis
     * @param name
     * @param ss
     * @return
     */
    @RateLimit(limitNum = 1)
    @GetMapping("/api/address/test")
    public Object test(@RequestParam(value = "name", required = false) String name,
                       @RequestParam(value = "ss", required = false) String ss) {
        return "name:"+name+",ss:"+ss;
    }


}
