package cn.codingcrea.nccommunity.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class NcCommunityUtil {

    //生成不含“-”的随机字符串
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    //MD5加密
    public static String md5(String s) {
        if(StringUtils.isBlank(s)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(s.getBytes());
    }


}
