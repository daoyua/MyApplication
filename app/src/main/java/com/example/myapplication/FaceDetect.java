package com.example.myapplication;

import com.example.myapplication.utils.GsonUtils;
import com.example.myapplication.utils.HttpUtil;

import java.util.HashMap;
import java.util.Map;

public class FaceDetect {
    /**
     * 重要提示代码中所需工具类
     * FileUtil,Base64Util,HttpUtil,GsonUtils请从
     * https://ai.baidu.com/file/658A35ABAB2D404FBF903F64D47C1F72
     * https://ai.baidu.com/file/C8D81F3301E24D2892968F09AE1AD6E2
     * https://ai.baidu.com/file/544D677F5D4E4F17B4122FBD60DB82B3
     * https://ai.baidu.com/file/470B3ACCA3FE43788B5A963BF0B625F3
     * 下载
     */
    public static String detect(String token, String code) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/detect";
        try {
            //https://image.baidu.com/search/detail?ct=503316480&z=0&ipn=d&word=%E4%BA%BA%E8%84%B8&step_word=&hs=0&pn=0&spn=0&di=14042782160&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&istype=0&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=undefined&cs=3351762121%2C3856605346&os=1765595036%2C4219843444&simid=0%2C0&adpicid=0&lpn=0&ln=1654&fr=&fmq=1550414774519_R&fm=&ic=undefined&s=undefined&hd=undefined&latest=undefined&copyright=undefined&se=&sme=&tab=0&width=undefined&height=undefined&face=undefined&ist=&jit=&cg=&bdtype=0&oriquery=&objurl=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F0153ec589a710da801219c773bb4f6.jpg%401280w_1l_2o_100sh.png&fromurl=ippr_z2C%24qAzdH3FAzdH3Fooo_z%26e3Bu6jjr_z%26e3BvgAzdH3Fp57ptw5AzdH3Fnal9amc_z%26e3Bip4&gsm=0&rpstart=0&rpnum=0&islist=&querylist=&force=undefined
            Map<String, Object> map = new HashMap<>();
//            map.put("image", "027d8308a2ec665acb1bdf63e513bcb9");
//            map.put("face_field", "faceshape,facetype");
//            map.put("image_type", "FACE_TOKEN");

            map.put("image", code);
            map.put("face_field", "faceshape,facetype");
            map.put("image_type", "BASE64");
            String param = GsonUtils.toJson(map);

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = token;

            String result = HttpUtil.post(url, accessToken, "application/json", param);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}

