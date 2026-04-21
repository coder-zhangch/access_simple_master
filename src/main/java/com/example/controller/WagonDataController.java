package com.example.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.example.entity.*;
import com.example.request.BaseResult;
import com.example.service.ListVideoDataService;
import com.example.service.WagonDataService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/WagonData")
public class WagonDataController {

    private final static String PNG = ".png";
    private final static String MP4 = ".mp4";

    @Autowired
    private WagonDataService wagonDataService;
    @Autowired
    private ListVideoDataService listVideoDataService;

    @PostMapping("/getAndPush")
    public BaseResult<WagonDataVO> getAndPush(@RequestBody WagonDataDTO dto){
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            String json = mapper.writeValueAsString(dto);
//            log.info("=====接收地磅数据==={}", json);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
        log.info("=====接收地磅数据==id={}", dto.getId());

        try {
            //查询数据库，判断该id是否已经存在
            WagonDataVO vo = wagonDataService.getById(dto.getId());
            if(vo != null){
                return BaseResult.fail("地磅照片数据对应id已存在，保存失败：" + dto.getId());
            }
            //转成 WeighingData 对象
            WagonDataVO data = new WagonDataVO();
            data.setId(dto.getId());
            data.setMatchid(dto.getMatchid());
            data.setCarno(dto.getCarno());
            data.setMaterialname(dto.getMaterialname());
            data.setMaterialspec(dto.getMaterialspec());
            data.setSourcename(dto.getSourcename());
            data.setTargetname(dto.getTargetname());
            data.setGross(dto.getGross());
            data.setGrosstime(dto.getGrosstime());
            data.setTare(dto.getTare());
            data.setTaretime(dto.getTaretime());
            data.setSuttle(dto.getSuttle());
            data.setShip(dto.getShip());
            data.setCreatedate(dto.getCreatedate());
            data.setGrossoperator(dto.getGrossoperator());
            data.setTareoperator(dto.getTareoperator());
            data.setGrossweigh(dto.getGrossweigh());
            data.setTareweigh(dto.getTareweigh());
            data.setClientId(dto.getClientId());
            data.setIsSend(0);

            //listPic 和 listVideo 转 json String
            ObjectMapper mapper = new ObjectMapper();
            String listPicJson = mapper.writeValueAsString(dto.getListPic());
            data.setListPic(listPicJson);
//            String listVideoJson = mapper.writeValueAsString(dto.getListVideo());
//            data.setListVideo(listVideoJson);

            int inserted = wagonDataService.insert(data);
            log.info("=====接收地磅数据保存结果==={}", inserted);

            return BaseResult.ok(data);
        } catch (Exception e) {
            log.info("=====接收地磅数据保存失败===");
            return BaseResult.fail("接收地磅数据保存失败！");
        }
    }


    @PostMapping("/listVideo")
    public BaseResult<ListVideoDataVO> listVideo(@RequestBody ListVideoDataDTO dto){
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            String json = mapper.writeValueAsString(dto);
//            log.info("=====接收地磅视频数据==={}", json);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
        log.info("=====接收地磅视频数据==id={}", dto.getId());

        try {
            //查询数据库，判断该id是否已经存在
            ListVideoDataVO vo = listVideoDataService.getById(dto.getId());
            if(vo != null){
                return BaseResult.fail("地磅视频数据对应id已存在，保存失败：" + dto.getId());
            }
            //转成 ListVideoDataVO 对象
            ListVideoDataVO data = new ListVideoDataVO();
            data.setId(dto.getId());
            data.setMatchid(dto.getMatchid());
            data.setCarno(dto.getCarno());

            //listVideo 转 json String
            ObjectMapper mapper = new ObjectMapper();
            String listVideoJson = mapper.writeValueAsString(dto.getListVideo());
            data.setListVideo(listVideoJson);

            int inserted = listVideoDataService.insert(data);
            log.info("=====接收地磅视频数据保存结果==={}", inserted);

            return BaseResult.ok(data);
        } catch (Exception e) {
            log.info("=====接收地磅视频数据保存失败===");
            return BaseResult.fail("接收地磅视频数据保存失败！");
        }
    }

    @GetMapping("/save/{id}")
    public BaseResult<String> saveVideoAndPic(@PathVariable("id") Long id){
        log.info("=====保存地磅图片和视频==id={}", id);
        //是否有文件生成
        boolean created = false;
        String dateStr = DateUtil.format(new Date(), "yyyyMMdd-HHmmss");
        //C:\scalesFile\15743-20240809-115233\
        String urlPrefix = "C:/scalesFile/"+ id + "-" + dateStr + "/";
        try {
            //创建目录
            File dir = new File(urlPrefix);
            if(dir.exists()){
                return BaseResult.fail("目录已存在，保存图片失败！==path==" + urlPrefix);
            }else {
                dir.mkdirs();
            }

            ObjectMapper mapper = new ObjectMapper();
            //保存图片信息
            WagonDataVO vo = wagonDataService.getById(id);
            if(vo != null && StrUtil.isNotBlank(vo.getListPic())){
                try {
                    List list = mapper.readValue(vo.getListPic(), List.class);
                    for (int i = 0; i < list.size(); i++) {
                        PicData picData = BeanUtil.copyProperties(list.get(i), PicData.class);
                        if(picData == null){
                            continue;
                        }
                        if(picData.getFirPic1() != null){
                            toFile(picData.getFirPic1(), urlPrefix, "firPic1-", i, PNG);
                            created = true;
                        }
                        if(picData.getFirPic2() != null){
                            toFile(picData.getFirPic2(), urlPrefix, "firPic2-", i, PNG);
                            created = true;
                        }
                        if(picData.getFirPic3() != null){
                            toFile(picData.getFirPic3(), urlPrefix, "firPic3-", i, PNG);
                            created = true;
                        }
                        if(picData.getFirPic4() != null){
                            toFile(picData.getFirPic4(), urlPrefix, "firPic4-", i, PNG);
                            created = true;
                        }
                        if(picData.getSecPic1() != null){
                            toFile(picData.getSecPic1(), urlPrefix, "secPic1-", i, PNG);
                            created = true;
                        }
                        if(picData.getSecPic2() != null){
                            toFile(picData.getSecPic2(), urlPrefix, "secPic2-", i, PNG);
                            created = true;
                        }
                        if(picData.getSecPic3() != null){
                            toFile(picData.getSecPic3(), urlPrefix, "secPic3-", i, PNG);
                            created = true;
                        }
                        if(picData.getSecPic4() != null){
                            toFile(picData.getSecPic4(), urlPrefix, "secPic4-", i, PNG);
                            created = true;
                        }
                    }
                } catch (IOException e) {
                    log.info("=====获取图片信息失败==id={}", id);
                }
            }

            //保存视频文件
            ListVideoDataVO videoVO = listVideoDataService.getById(id);
            if(videoVO != null && StrUtil.isNotBlank(videoVO.getListVideo())){
                try {
                    List list = mapper.readValue(videoVO.getListVideo(), List.class);
                    for (int i = 0; i < list.size(); i++) {
                        VideoData videoData = BeanUtil.copyProperties(list.get(i), VideoData.class);
                        if(videoData == null){
                            continue;
                        }
                        if(videoData.getFirVideo() != null){
                            toFile(videoData.getFirVideo(), urlPrefix, "firVideo-", i, MP4);
                            created = true;
                        }
                        if(videoData.getSecVideo() != null){
                            toFile(videoData.getSecVideo(), urlPrefix, "secVideo-", i, MP4);
                            created = true;
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if(created){
                return BaseResult.ok("保存成功 == " + urlPrefix);
            }
            return BaseResult.fail("保存失败！");
        } catch (RuntimeException e) {
            e.printStackTrace();
            if(created){
                return BaseResult.ok("保存成功 == " + urlPrefix);
            }
            return BaseResult.fail("保存失败！");
        }
    }

    private static void toFile(String base64, String urlPrefix, String fileName, int i, String fileSuffix){
        byte[] bytes = Base64.getDecoder().decode(base64);
        //C:\scalesFile\15743-20240809-115233\firPic1-0.png
        //C:\scalesFile\15743-20240809-115233\firVideo-0.mp4
        String path = urlPrefix + fileName + i + fileSuffix;
        saveByteToFile(bytes, path);
    }

    private static void saveByteToFile(byte[] bytes, String outputPath) {
        log.info("=====保存文件==path={}", outputPath);
        try {
            Files.write(Paths.get(outputPath), bytes);
        } catch (Exception e) {
            log.info("=====保存文件失败！==path={}", outputPath);
            e.printStackTrace();
        }
    }
}
