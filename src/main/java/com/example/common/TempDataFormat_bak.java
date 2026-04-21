package com.example.common;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TempDataFormat_bak {

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
    /*public static void main(String[] args) {
// 圈配置: 从内圈到外圈，每圈的点数
        List<Integer> circleConfig = new ArrayList<>();
        circleConfig.add(15); // 最内圈有15个点
        circleConfig.add(13); // 第二圈有13个点
        circleConfig.add(13); // 第三圈有13个点
        circleConfig.add(13); // 第四圈有13个点
        circleConfig.add(13); // 最外圈有13个点

        String src1="020A020E026F02B002710239023B02340235022D02170211021C02290247020F0210025F02510227022B02310233022B021802170221022B020E020902620254023702260229022B02220219020F02180226020D02060256024802230223022002270229021B021302150227FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";
        List<Double> temperatures = parseTemperatureData(src1);


        try {
            // 生成检测点描述
            List<String> pointDescriptions = generatePointDescriptions();
            System.out.println("检测点描述:");
            for (int i = 0; i < pointDescriptions.size(); i++) {
                System.out.println(pointDescriptions.get(i));
                if ((i + 1) % maxTheta == 0) {
                    System.out.println("------ 第 " + ((i / maxTheta) + 1) + " 圈结束 ------");
                }
            }


            // 生成筒仓格式
            String siloFormat = formatTemperatureData(circleConfig, temperatures);
            System.out.println("筒仓格式:");
            System.out.println(siloFormat);

            // 统计占位符数量
            int placeholderCount = 0;
            for (Double temp : temperatures) {
                if (temp == -200.0) {
                    placeholderCount++;
                }
            }
            System.out.println("\n使用了 " + placeholderCount + " 个占位符(-200)");

        } catch (IllegalArgumentException e) {
            System.out.println("错误: " + e.getMessage());
        }
    }*/

    public static String getSiloFormat(List<Integer> circleConfig, String tempData){
        List<Double> temperatures = parseTemperatureData(tempData);
        try {
            // 生成检测点描述
            List<String> pointDescriptions = generatePointDescriptions();
//            System.out.println("检测点描述:");
//            for (int i = 0; i < pointDescriptions.size(); i++) {
//                System.out.println(pointDescriptions.get(i));
//                if ((i + 1) % maxTheta == 0) {
//                    System.out.println("------ 第 " + ((i / maxTheta) + 1) + " 圈结束 ------");
//                }
//            }

            // 生成筒仓格式
            String siloFormat = formatTemperatureData(circleConfig, temperatures);
//            System.out.println("筒仓格式:");
//            System.out.println(siloFormat);

            // 统计占位符数量
            int placeholderCount = 0;
            for (Double temp : temperatures) {
                if (temp == -200.0) {
                    placeholderCount++;
                }
            }
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
     * @return 格式化后的点位字符串
     */
    public static String formatTemperatureData(List<Integer> circleConfig, List<Double> temperatures) {
        // 验证输入数据
        if (circleConfig == null || temperatures == null) {
            throw new IllegalArgumentException("圈配置和温度列表不能为空");
        }

        // 计算实际需要的点数（每圈15个点，共5圈）
        int totalPoints = maxTheta * q; // 总共需要75个点

        // 如果温度值数量不足75个，用-200占位符补充
        if (temperatures.size() < totalPoints) {
            int missingPoints = totalPoints - temperatures.size();
            for (int i = 0; i < missingPoints; i++) {
                temperatures.add(-200.0); // 使用-200作为无点位的占位符
            }
        }

        List<String> pointStrings = new ArrayList<>();
        int temperatureIndex = 0;

        // 按照规则排序: 先高度方向(z轴), 再圆弧方向(θ角度), 最后是半径方向(r轴)
        // 由于我们只有单层高度数据，z轴固定为0
        int z = 0; // 高度方向固定为0（只有一层）

        // 遍历半径方向(r轴) - 5圈
        for (int r = 0; r < q; r++) {
            // 每圈固定15个点
            for (int theta = 0; theta < maxTheta; theta++) {
                double temperature = temperatures.get(temperatureIndex++);

                // 格式化温度值，占位符直接使用整数值
                String tempStr;
                if (temperature == -200.0 || temperature == -100.0) {
                    tempStr = String.format("%.0f", temperature);
                } else {
                    tempStr = String.format("%.1f", temperature);
                }

                pointStrings.add(String.format("%s,%d,%d,%d", tempStr, r, theta, z));
            }
        }

        return String.join("|", pointStrings);
    }
    static int q=5;
    static int maxTheta=15;
    /**
     * 生成筒仓检测点描述（用于验证和调试）
     * @return 检测点描述列表
     */
    public static List<String> generatePointDescriptions() {
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
