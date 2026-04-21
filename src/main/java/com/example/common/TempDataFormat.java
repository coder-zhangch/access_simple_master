package com.example.common;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TempDataFormat {

    private static final Pattern HEX_PATTERN = Pattern.compile("^[0-9A-Fa-f]+$");
    private static final double PRECISION = 0.0625;

    /**
     * 解析16进制温度数据
     * @param tempData 16进制温度数据字符串
     * @return 解析后的温度值列表
     * @throws IllegalArgumentException 当数据格式不正确时抛出异常
     */
    public static List<Double> parseTemperatureData(String tempData) {
        // 验证输入数据
        if (tempData == null || tempData.isEmpty()) {
            throw new IllegalArgumentException("输入数据不能为空");
        }

        // 检查是否只包含16进制字符
        if (!HEX_PATTERN.matcher(tempData).matches()) {
            throw new IllegalArgumentException("输入数据包含非法字符，只能包含0-9、A-F和a-f");
        }

        // 检查数据长度是否为4的倍数（每4个字符表示一个温度值）
        if (tempData.length() % 4 != 0) {
            throw new IllegalArgumentException("输入数据长度不正确，必须是4的倍数");
        }

        List<Double> temperatures = new ArrayList<>();

        // 每4个字符一组进行解析
        for (int i = 0; i < tempData.length(); i += 4) {
            String hexValue = tempData.substring(i, i + 4);
            double temperature = parseSingleTemperature(hexValue);
            temperatures.add(temperature);
        }

        return temperatures;
    }

    /**
     * 解析单个温度值
     * @param hexValue 4字符的16进制字符串
     * @return 解析后的温度值
     */
    private static double parseSingleTemperature(String hexValue) {
        // 将16进制字符串转换为二进制字符串（16位）
        String binaryString = hexToBinary(hexValue);

        // 检查最高位（符号位）
        boolean isNegative = binaryString.charAt(0) == '1';

        // 处理负数（补码转原码）
        if (isNegative) {
            binaryString = complementToOriginal(binaryString);
        }

        // 取低11位
        String valueBits = binaryString.substring(5); // 前5位是符号位和保留位，取后11位

        // 将11位二进制转换为十进制
        int decimalValue = Integer.parseInt(valueBits, 2);

        // 计算温度值
        double temperature = decimalValue * PRECISION;

        // 如果是负数，添加负号
        if (isNegative) {
            temperature = -temperature;
        }

        return temperature;
    }

    /**
     * 将16进制字符串转换为16位二进制字符串
     * @param hexString 4字符的16进制字符串
     * @return 16位二进制字符串
     */
    private static String hexToBinary(String hexString) {
        // 将16进制字符串转换为整数
        int value = Integer.parseInt(hexString, 16);

        // 转换为16位二进制字符串，前面补0
        String binary = Integer.toBinaryString(value);

        // 确保是16位
        while (binary.length() < 16) {
            binary = "0" + binary;
        }

        return binary;
    }

    /**
     * 将补码转换为原码
     * @param complement 补码二进制字符串
     * @return 原码二进制字符串
     */
    private static String complementToOriginal(String complement) {
        // 各位取反
        StringBuilder inverted = new StringBuilder();
        for (char c : complement.toCharArray()) {
            inverted.append(c == '0' ? '1' : '0');
        }

        // 加1
        StringBuilder original = new StringBuilder(inverted.toString());
        boolean carry = true;
        for (int i = original.length() - 1; i >= 0 && carry; i--) {
            if (original.charAt(i) == '0') {
                original.setCharAt(i, '1');
                carry = false;
            } else {
                original.setCharAt(i, '0');
            }
        }

        return original.toString();
    }

    // 测试函数

    //获取17根
    public static List<Integer> getCircleConfig17() {
        List<Integer> circleConfig = new ArrayList<>();
        circleConfig.add(20);
        circleConfig.add(20);
        circleConfig.add(20);
        circleConfig.add(20);
        circleConfig.add(20);
        circleConfig.add(20);
        circleConfig.add(20);
        circleConfig.add(20);
        circleConfig.add(20);
        circleConfig.add(18);
        circleConfig.add(18);
        circleConfig.add(18);
        circleConfig.add(18);
        circleConfig.add(18);
        circleConfig.add(18);
        circleConfig.add(18);
        circleConfig.add(18);
        return circleConfig;
    }
    //获取11根
    public static List<Integer> getCircleConfig11() {
        List<Integer> circleConfig = new ArrayList<>();
        circleConfig.add(19);
        circleConfig.add(19);
        circleConfig.add(19);
        circleConfig.add(19);
        circleConfig.add(19);
        circleConfig.add(19);
        circleConfig.add(19);
        circleConfig.add(19);
        circleConfig.add(19);
        circleConfig.add(19);
        circleConfig.add(19);
        return circleConfig;
    }
    //获取5根
    public static List<Integer> getCircleConfig5() {
        List<Integer> circleConfig = new ArrayList<>();
        circleConfig.add(15);
        circleConfig.add(13);
        circleConfig.add(13);
        circleConfig.add(13);
        circleConfig.add(13);
        return circleConfig;
    }
//    public static void main(String[] args) {
//// 圈配置: 从内圈到外圈，每圈的点数
//
//
//        String src1="0236023802560235024D025A0252024E0256021B020E020D0210020D020D02140215020C0218020D02340247024E023E024C0249024C024A02560214021002100213020D020D0212021005500212020D02360235023C0251024B0240025D02510257020C0214020B020C020B021502110209020E0214020602350234022E022F0240023F0247022E023D0204020902080209020B02120211020A0210021701FF02370234022A0235023F023B0243022D02350209020A020A020D020E021002150216020C0212020202360236023D022D0243023C02410237022C02170206020A0209020C020C02120213021302120209023302420237023802400236023B02330233020E0207020C020B021102100210020F0211020D01F8FFFF02350234022A0237024A0247022A023302070209020A020C020C0210021502100207020F02100237023402310238023B023802430239022F0206020B0209020F021102140210020E020D021502010232022D02270228022A0228022C0219020902060205020A020E021102120210020C02100231023C02240228021F022502280224020B020302060209020C020C02110212020E02100233022D02330228021D022B0236021F02240208020D02080210020F021202150212020D0232022C022A022702280226022702180219020402080207020B020C0211020F0210020C022F022F0232022502210218022A0220022102000203020302060209020E02110213020C0232022D022702260226021F022C022802140207020B0209020D020D021502160213021802300229022A0227022502210222021B0211020402060206020A020D02130214020D0211022F022C02260224022C022C02260222020802080204020A020E02120213021202100212";
//
//        List<Double> temperatures = parseTemperatureData(src1);
//
//
//        int q=17;//多少根
//        int maxTheta=20;//最大的一根数量
//
//
//        try {
//            // 生成检测点描述
//            List<String> pointDescriptions = generatePointDescriptions(q,maxTheta);
//            System.out.println("检测点描述:");
//            for (int i = 0; i < pointDescriptions.size(); i++) {
//                System.out.println(pointDescriptions.get(i));
//                if ((i + 1) % maxTheta == 0) {
//                    System.out.println("------ 第 " + ((i / maxTheta) + 1) + " 圈结束 ------");
//                }
//            }
//
//            // 生成筒仓格式
//            String siloFormat = formatTemperatureData(getCircleConfig17(), temperatures,q,maxTheta,3);
//            System.out.println("筒仓格式:");
//            System.out.println(siloFormat);
//
//            // 统计占位符数量
//            int placeholderCount = 0;
//            for (Double temp : temperatures) {
//                if (temp == -200.0) {
//                    placeholderCount++;
//                }
//            }
//            System.out.println("\n使用了 " + placeholderCount + " 个占位符(-200)");
//
//        } catch (IllegalArgumentException e) {
//            System.out.println("错误: " + e.getMessage());
//        }
//    }

    public static String getSiloFormat(List<Integer> circleConfig, String tempData, Integer type, Integer augTemp){
        int q = circleConfig.size();//多少根
        int maxTheta = circleConfig.stream().max(Integer::compareTo).get();//最大的一根数量

        List<Double> temperatures = parseTemperatureData(tempData);
        try {
            // 生成检测点描述
//            List<String> pointDescriptions = generatePointDescriptions();
//            System.out.println("检测点描述:");
//            for (int i = 0; i < pointDescriptions.size(); i++) {
//                System.out.println(pointDescriptions.get(i));
//                if ((i + 1) % maxTheta == 0) {
//                    System.out.println("------ 第 " + ((i / maxTheta) + 1) + " 圈结束 ------");
//                }
//            }

            // 生成筒仓格式
            String siloFormat = formatTemperatureData(circleConfig, temperatures, q, maxTheta, type, augTemp);
//            System.out.println("筒仓格式:");
//            System.out.println(siloFormat);

            // 统计占位符数量
//            int placeholderCount = 0;
//            for (Double temp : temperatures) {
//                if (temp == -200.0) {
//                    placeholderCount++;
//                }
//            }
//            System.out.println("\n使用了 " + placeholderCount + " 个占位符(-200)");

            return siloFormat;
        } catch (IllegalArgumentException e) {
            System.out.println("错误: " + e.getMessage());
            return null;
        }
    }

    /**
     * 格式化温度数据为筒仓点位字符串（带占位符）
     * @param circleConfig 每圈电缆上的点数列表（从内圈到外圈）
     * @param temperatures 温度值列表
     * @param augTemp 平均温度
     * @return 格式化后的点位字符串
     */
    //1-1、1-5、2-1、2-3、2-4、3-1、3-3 一共两圈， 第一圈1；第二圈2-5   第一根15个点，后面13个点
    //Q31、Q32  2圈， 第一圈1-2；第二圈3-11  =19
    //5-3、5-4、6-1  3圈，第一圈1-3，第二圈4-9，第三圈10-17   =20，18
    public static String formatTemperatureData(List<Integer> circleConfig, List<Double> temperatures,int q,int maxTheta,int type, Integer augTemp) {
        // 验证输入数据
        if (circleConfig == null || temperatures == null) {
            throw new IllegalArgumentException("圈配置和温度列表不能为空");
        }

        List<String> pointStrings = new ArrayList<>();
        int temperatureIndex = 0;

        // 按照规则排序: 先高度方向(z轴), 再圆弧方向(θ角度), 最后是半径方向(r轴)
        // 由于我们只有单层高度数据，z轴固定为0
        int z = 0; // 圈
        int k = 0; // 块
        boolean bz=false;

        // 遍历半径方向(r轴) - 5圈
        for (int r = 0; r < q; r++) {
            // 获取当前圈应有的点数
            int expectedPoints = circleConfig.get(r);

            // 检查当前圈实际可用的温度数据点数
            int availablePoints = Math.min(expectedPoints, temperatures.size() - temperatureIndex);

            // 添加占位符直到达到15个点
            for (int theta = 0; theta < maxTheta; theta++) {
                double temperature;

                if (theta < (maxTheta - availablePoints)) {
                    // 添加前置占位符
                    temperature = augTemp.doubleValue();
                } else if (theta >= (maxTheta - availablePoints) && temperatureIndex < temperatures.size()) {
                    // 使用实际温度数据
                    temperature = temperatures.get(temperatureIndex++);
                } else {
                    // 添加后置占位符（理论上不会执行到这里）
                    temperature = -200.0;
                }

                // 格式化温度值，占位符直接使用整数值
                String tempStr;
                if (temperature == 85) {
                    tempStr = "-";
                } else if (temperature == -200.0 || temperature == -100.0) {
                    tempStr = String.format("%.0f", temperature);
                } else {
                    tempStr = String.format("%.1f", temperature);
                }

                pointStrings.add(String.format("%s,%d,%d,%d", tempStr,z, k, theta));

            }
            if(type==1){
                if(r==0){
                    z=1;
                    bz=true;
                }
                k++;
                if(bz){
                    k=0;
                    bz=false;
                }
            }

            if(type==2){
                if(r==1){
                    z=1;
                    bz=true;
                }
                k++;
                if(bz){
                    k=0;
                    bz=false;
                }
            }
            if(type==3){
                if(r==2){
                    z=1;
                    bz=true;
                }
                if(r==8){
                    z=2;
                    bz=true;
                }
                if(r==17){
                    z=3;
                    bz=true;
                }
                k++;
                if(bz){
                    k=0;
                    bz=false;
                }
            }

        }
        return String.join("|", pointStrings);
    }

    /**
     * 生成筒仓检测点描述（用于验证和调试）
     * @return 检测点描述列表
     */
    public static List<String> generatePointDescriptions(int q, int maxTheta) {
        List<String> descriptions = new ArrayList<>();
        int pointNumber = 1;

        // 遍历半径方向(r轴) - 5圈
        for (int r = 0; r < q; r++) {
            // 每圈固定15个点
            for (int theta = 0; theta < maxTheta; theta++) {
                descriptions.add(String.format("Q[%d,%d,0] - 第%d个检测点", r, theta, pointNumber++));
            }
        }

        return descriptions;
    }
}
