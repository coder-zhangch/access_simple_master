package com.example.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class WareHouseInfo implements Serializable {

    private static final long serialVersionUId = 1L;

    //仓库名
    private String WareHouseName;
    private String AddressInfo;
    private String ActualRoute;
    //表示改仓库的每根电缆点数（每3个数字为一根电缆的点数，10进制；例如015010010010010，表示15点、10点、10点、10点、10点）按照电缆根号顺序排布
    private String LogicRoute;
    private Integer ActualRouteNum;
    //表示该仓库有多少根电缆（10进制，例如20，表示有20根电缆，点数按照顺序从LogicRoute中读取）
    private Integer LogicRouteNum;
    private Integer VentiNum;
    private Integer VentiFenji;
    private Integer HouseType;
    private Integer Length;
    private Integer Width;
    private Integer height;
    private String Parameter;
    private Integer in_Humidity_fenji;
    private Integer in_Humidity_lu;
    private Integer out_Humidity_fenji;
    private Integer out_Humidity_lu;
    private String TelePhone;
    private Integer Point;
}
