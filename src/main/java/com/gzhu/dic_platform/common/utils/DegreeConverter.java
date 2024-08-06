package com.gzhu.dic_platform.common.utils;

/**
 * 经纬度转换工具类
 */
public class DegreeConverter {

    /**
     * 将带有方向的度、分、秒表示转换为小数度数，并以字符串形式返回。 "116°39′02″E" -> "116.650556"
     *
     * @param direction 方向字符（E - 东经, W - 西经, N - 北纬, S - 南纬）
     * @param degrees 度的整数部分
     * @param minutes 分的整数部分
     * @param seconds 秒的整数部分
     * @return 小数表示的度数，以字符串形式返回。东经和北纬为正，西经和南纬为负。
     */
    public static String toDecimalDegrees(char direction, int degrees, int minutes, int seconds) {
        // 将分转换为小数度数
        double decimalMinutes = minutes / 60.0;
        // 将秒转换为小数度数
        double decimalSeconds = seconds / 3600.0;
        // 计算总的十进制度数
        double decimalDegrees = degrees + decimalMinutes + decimalSeconds;

        // 根据方向字符确定正负
        if (direction == 'W' || direction == 'S') {
            decimalDegrees = -decimalDegrees;
        }

        // 格式化为字符串并返回
        return String.valueOf(decimalDegrees);
    }

    /**
     * 从字符串格式的度、分、秒表示中提取方向、度、分、秒，并转换为小数度数字符串。
     *
     * @param dms 表示度、分、秒和方向的字符串，例如 "116°39′02″E"
     * @return 小数表示的度数字符串
     * @throws IllegalArgumentException 如果输入格式不正确
     */
    public static String parseAndConvert(String dms) throws IllegalArgumentException {
        try {
            // 提取方向（最后一个字符）
            char direction = dms.charAt(dms.length() - 1);
            // 提取度
            int degrees = Integer.parseInt(dms.substring(0, dms.indexOf('°')));
            // 提取分
            int minutes = Integer.parseInt(dms.substring(dms.indexOf('°') + 1, dms.indexOf('′')));
            // 提取秒
            int seconds = Integer.parseInt(dms.substring(dms.indexOf('′') + 1, dms.indexOf('″')));

            // 调用转换方法并返回结果
            return toDecimalDegrees(direction, degrees, minutes, seconds);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid format for DMS string: " + dms);
        }
    }
}
