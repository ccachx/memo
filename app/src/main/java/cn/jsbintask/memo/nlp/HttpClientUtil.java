//package cn.jsbintask.memo.nlp;
//
//
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.nio.charset.Charset;
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.nio.charset.Charset;
//
//import org.apache.http.HttpResponse;
//import org.apache.http.HttpStatus;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.util.EntityUtils;
//
//
//public class HttpClientUtil {
//
//    private static HttpClient httpClient = null;
//    private static HttpPost method = null;
//    private static HttpResponse response = null;
//    private int status = 0;
//
//    public static String httpPostMethod(String url,String data){
//        String licenseStr="";
//        //XmzhUtil：本人字符串非空判断工具类去掉即可
//        if(!XmzhUtil.isNull(url) && !XmzhUtil.isNull(data)){
//            System.out.println("url==>"+url);
//            System.out.println("data==>"+data);
//            try {
//                //1.建立httpclient
//                httpClient = new DefaultHttpClient();
//                //2.根据url建立请求方式，设置请求方式，添加发送参数
//                method = new HttpPost(url);
//                method.addHeader("Content-type","application/json; charset=utf-8");
//                method.setHeader("Accept", "application/json");
//                //设置参数，官方给出必须是GBK编码格式
//                method.setEntity(new StringEntity(data, Charset.forName("GBK")));
//                //3.发送请求，得到响应
//                response = httpClient.execute(method);
//                //4.获取响应码，判断是否请求成功
//                int statusCode = response.getStatusLine().getStatusCode();
//                if (statusCode == HttpStatus.SC_OK) {
//                    //5.获取响应信息
//                    licenseStr = EntityUtils.toString(response.getEntity());
//                }
//            }  catch (IOException e) {
//                e.printStackTrace();
//                System.out.println("http调用失败");
//            }
//        }
//        return licenseStr;
//    }
//
///*	    public int getStatus() {
//	        return status;
//	    }
//	    public void setStatus(int status) {
//	        this.status = status;
//	    }  */
//
//    public static void main(String[] args) throws UnsupportedEncodingException {
//        //（通用版）请求URL: https://aip.baidubce.com/rpc/2.0/nlp/v1/lexer
//        String url = "https://aip.baidubce.com/rpc/2.0/nlp/v1/lexer?access_token=24.b89362e9c8f8218417975271405b8e00.2592000.1541730494.282335-14384615";
//        //（定制版）请求URL: https://aip.baidubce.com/rpc/2.0/nlp/v1/lexer_custom
//        //	    	String url = "https://aip.baidubce.com/rpc/2.0/nlp/v1/lexer_custom?access_token=24.b89362e9c8f8218417975271405b8e00.2592000.1541730494.282335-14384615";
//        //待解析文本
//        String data ="{\"text\": \"百度是一家高科技公司\"}";
//
//        String httpPostMethod = httpPostMethod(url,data);
//        //本地接受结果乱码，根据自己需求修改编码格式
//        System.out.println(toUTF8(httpPostMethod));
//    }
//
//
//    /**
//     * 将字符串的编码格式转换为utf-8
//     *
//     * @param str
//     * @return Name = new
//     * String(Name.getBytes("ISO-8859-1"), "utf-8");
//     */
//    public static String toUTF8(String str) {
//        if (isEmpty(str)) {
//            return "";
//        }
//        try {
//            if (str.equals(new String(str.getBytes("GB2312"), "GB2312"))) {
//                str = new String(str.getBytes("GB2312"), "utf-8");
//                return str;
//            }
//        } catch (Exception exception) {
//        }
//        try {
//            if (str.equals(new String(str.getBytes("ISO-8859-1"), "ISO-8859-1"))) {
//                str = new String(str.getBytes("ISO-8859-1"), "GBK");
//                return str;
//            }
//        } catch (Exception exception1) {
//        }
//        try {
//            if (str.equals(new String(str.getBytes("GBK"), "GBK"))) {
//                str = new String(str.getBytes("GBK"), "utf-8");
//                return str;
//            }
//        } catch (Exception exception3) {
//        }
//        return str;
//    }
//
//    /**
//     * 判断是否为空
//     *
//     * @param str
//     * @return
//     */
//    public static boolean isEmpty(String str) {
//        // 如果字符串不为null，去除空格后值不与空字符串相等的话，证明字符串有实质性的内容
//        if (str != null && !str.trim().isEmpty()) {
//            return false;// 不为空
//        }
//        return true;// 为空
//    }
//}
//
