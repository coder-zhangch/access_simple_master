package com.example.controller;

import cn.hutool.core.bean.BeanUtil;
import com.example.entity.ScaleData;
import com.example.entity.WagonDataVO;
import com.example.entity.WeighingData;
import com.example.request.BaseResult;
import com.example.request.HttpsRequest;
import com.example.service.WeighingDataService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/ScaleData")
public class ScaleDataController {

    private final static String EMPTY = "";
    private final static BigDecimal ZERO = BigDecimal.ZERO;

    @Autowired
    private WeighingDataService weighingDataService;

    @PostMapping("/push")
    public BaseResult<ScaleData> getMessage(@RequestBody ScaleData scaleData){
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(scaleData);
            log.info("===============接收散料数据==={}", json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return BaseResult.ok(scaleData);
    }

    @PostMapping("/getAndPush")
    public BaseResult<WeighingData> getAndPush(@RequestBody ScaleData scaleData){
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(scaleData);
            log.info("=====接收散料数据==={}", json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        try {
            //查询数据库，判断该id是否已经存在
            WeighingData vo = weighingDataService.getById(scaleData.getId());
            if(vo != null){
                return BaseResult.fail("散料数据对应id已存在，保存失败：" + scaleData.getId());
            }
            //转成 WeighingData 对象
            WeighingData data = new WeighingData();
            data.setClientId(scaleData.getClientId());
            data.setCcNo(scaleData.getCcNo());
            data.setCydw(scaleData.getCydw());
            data.setId(scaleData.getId());
            data.setLoader(scaleData.getLoader());
            data.setMaterialName(scaleData.getMaterialName());
            data.setOperator(scaleData.getOperator());
            data.setOrderNo(scaleData.getOrderNo());
            data.setScaleNo(scaleData.getScaleNo());
            data.setGross(scaleData.getGross());
            data.setSuttle(scaleData.getSuttle());
            data.setTare(scaleData.getTare());
            data.setTotalWeight(scaleData.getTotalWeight());
            data.setWeightDate(scaleData.getWeightDate());
            data.setWeightTime(scaleData.getWeightTime());
            data.setYsgj(scaleData.getYsgj());
            data.setZiDong(scaleData.getZiDong());
            data.setDwmc(scaleData.getDwmc());
            data.setLastWeigh(scaleData.getLastWeigh());
            data.setDataFrom(scaleData.getDataFrom() == null ? EMPTY : scaleData.getDataFrom());
            data.setBanCi(scaleData.getBanCi() == null ? EMPTY : scaleData.getBanCi());
            data.setDanPrice(scaleData.getDanPrice() == null ? ZERO : scaleData.getDanPrice());
            data.setAmount(scaleData.getAmount() == null ? ZERO : scaleData.getAmount());

            int inserted = weighingDataService.insert(data);
            log.info("=====接收散料数据保存结果==={}", inserted);

            return BaseResult.ok(data);
        } catch (Exception e) {
            log.info("=====接收散料数据保存失败===");
            return BaseResult.fail("接收散料数据保存失败！");
        }
    }
}
