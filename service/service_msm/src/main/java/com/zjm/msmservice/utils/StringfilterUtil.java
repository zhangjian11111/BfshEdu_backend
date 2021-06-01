package com.zjm.msmservice.utils;

/**
 * @author 张建
 * @version 1.0
 * @date 2021/3/10 13:26
 */
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class StringfilterUtil {

    public static void main(String[] args) {
        String str = "[9, 5, 7, 6, 2, 4]";
        System.out.println(str);
        System.out.println(StringFilter(str));
    }// 过滤特殊字符

    public static String StringFilter(String str) throws PatternSyntaxException {
        // 只允许字母和数字、中文
        // String regEx="[^a-zA-Z0-9]";
        // 清除掉[]中所有特殊字符
        String regEx = "[`~☆★!@#$%^&*()+=|{}':;,\\[\\]》·.<>/?~！@#￥%……（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim().replace(" ", "").replace("\\", "");
    }
}
