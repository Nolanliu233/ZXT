package cn.tongyuankeji.common.web;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

  
public class HttpClient {
	/** 
     * 发送GET请求 
     *  
     * @param url 
     *            目的地址 
     * @param parameters 
     *            请求参数，Map类型。 
     * @return 远程响应结果 
     */  
    public static String sendGet(String url, Map<String, Object> parameters) { 
        String result="";
        BufferedReader in = null;// 读取响应输入流  
        StringBuffer sb = new StringBuffer();// 存储参数  
        String params = "";// 编码之后的参数
        try {
            // 编码请求参数  
            if(parameters.size()==1){
                for(String name:parameters.keySet()){
            		if(parameters.get(name) != null)
            			sb.append(name).append("=").append(
                            java.net.URLEncoder.encode(parameters.get(name).toString(),  
                            "UTF-8"));
                }
                params=sb.toString();
            }else{
                for (String name : parameters.keySet()) {  
            		if(parameters.get(name) != null)
            			sb.append(name).append("=").append(  
                            java.net.URLEncoder.encode(parameters.get(name).toString(),  
                                    "UTF-8")).append("&");  
                }  
                String temp_params = sb.toString();  
                params = temp_params.substring(0, temp_params.length() - 1);  
            }
            String full_url = url + "?" + params; 
            // 创建URL对象  
            java.net.URL connURL = new java.net.URL(full_url);  
            // 打开URL连接  
            java.net.HttpURLConnection httpConn = (java.net.HttpURLConnection) connURL  
                    .openConnection();  
            // 设置通用属性  
            httpConn.setRequestProperty("Accept", "*/*");  
            httpConn.setRequestProperty("Connection", "Keep-Alive");  
            httpConn.setRequestProperty("User-Agent",  
                    "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)");  
            // 建立实际的连接  
            httpConn.connect();  
            // 定义BufferedReader输入流来读取URL的响应,并设置编码方式  
            in = new BufferedReader(new InputStreamReader(httpConn  
                    .getInputStream(), "UTF-8"));  
            String line;  
            // 读取返回的内容  
            while ((line = in.readLine()) != null) {  
                result += line;  
            }  
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {  
                if (in != null) {  
                    in.close();  
                }  
            } catch (IOException ex) {  
                ex.printStackTrace();  
            }  
        }
        return result ;
    }  
  
    /** 
     * 发送POST请求 
     *  
     * @param url 
     *            目的地址 
     * @param parameters 
     *            请求参数，Map类型。 
     * @return 远程响应结果 
     */  
    public static String sendPost(String url, Map<String, Object> parameters) {  
        String result = "";// 返回的结果  
        BufferedReader in = null;// 读取响应输入流  
        PrintWriter out = null;  
        StringBuffer sb = new StringBuffer();// 处理请求参数  
        String params = "";// 编码之后的参数  
        try {  
            // 编码请求参数  
            if (parameters.size() == 1) {  
                for (String name : parameters.keySet()) {  
                	if(parameters.get(name) != null){
                		sb.append(name).append("=").append(  
                            java.net.URLEncoder.encode(parameters.get(name).toString(),  
                                    "UTF-8"));  
                	}
                }  
                params = sb.toString();  
            } else {  
                for (String name : parameters.keySet()) {  
                	if(parameters.get(name) != null || (
                			(parameters.get(name)  instanceof   Object[]) && ((Object[])parameters.get(name)).length > 0)){
                		sb.append(name).append("=").append(  
                            java.net.URLEncoder.encode(parameters.get(name).toString(),  
                                    "UTF-8")).append("&"); 
                	}
                }  
                String temp_params = sb.toString();  
                params = temp_params.substring(0, temp_params.length() - 1);  
            }  
            // 创建URL对象  
            java.net.URL connURL = new java.net.URL(url);  
            // 打开URL连接  
            java.net.HttpURLConnection httpConn = (java.net.HttpURLConnection) connURL  
                    .openConnection();  
            // 设置通用属性  
            httpConn.setRequestProperty("Accept", "*/*");   
            httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");  
            httpConn.setRequestProperty("Connection", "Keep-Alive");  
            httpConn.setRequestProperty("User-Agent",  
                    "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)");  
            // 设置POST方式  
            httpConn.setDoInput(true);  
            httpConn.setDoOutput(true);  
            // 获取HttpURLConnection对象对应的输出流  
            out = new PrintWriter(httpConn.getOutputStream());  
            // 发送请求参数  
            out.write(params);  
            // flush输出流的缓冲  
            out.flush();  
            // 定义BufferedReader输入流来读取URL的响应，设置编码方式  
            in = new BufferedReader(new InputStreamReader(httpConn  
                    .getInputStream(), "UTF-8"));  
            String line;  
            // 读取返回的内容  
            while ((line = in.readLine()) != null) {  
                result += line;  
            }  
        } catch (Exception e) {  
            throw new RuntimeException("调用分词索引接口失败:" + e.getLocalizedMessage() + "("+url+")");
        } finally {  
            try {  
                if (out != null) {  
                    out.close();  
                }  
                if (in != null) {  
                    in.close();  
                }  
            } catch (IOException ex) {  
                ex.printStackTrace();  
            }  
        }  
        return result;  
    }  
  
    /** 
     * 主函数，测试请求 
     *  
     * @param args 
     */  
//    public static void main(String[] args) {  
//        Map<String, Integer> parameters = new HashMap<String, Integer>();  
//        parameters.put("infoId", 534);  
//        String result =sendGet("http://localhost:8080/segment/Segment.html", parameters);
//        System.out.println(result); 
//    }  
    
    /** 
     * 发送Http协议 传参数到接口并返回数据 
     *  
     */  
    public static String httpPost(String urlStr,Map<String,String> params,String type){  
         URL connect;  
         StringBuffer data = new StringBuffer();    
        try {    
            connect = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection)connect.openConnection();    
            connection.setRequestMethod(type == null ? "POST" : type);    
            connection.setDoOutput(true);   
            connection.setDoInput(true);  
            connection.setUseCaches(false);//post不能使用缓存  
            connection.setInstanceFollowRedirects(true);  
            connection.setRequestProperty("accept", "*/*");  
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");  
            connection.setRequestProperty("connection", "Keep-Alive");  
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");  
            OutputStreamWriter paramout = new OutputStreamWriter(    
                    connection.getOutputStream(),"UTF-8");   
            String paramsStr = "";   //拼接Post 请求的参数  
           for(String param : params.keySet()){  
               paramsStr += "&" + param + "=" + params.get(param);  
           }    
           if(!paramsStr.isEmpty()){  
               paramsStr = paramsStr.substring(1);  
           }  
            paramout.write(paramsStr);    
            paramout.flush();    
            BufferedReader reader = new BufferedReader(new InputStreamReader(    
                    connection.getInputStream(), "UTF-8"));    
            String line;                
            while ((line = reader.readLine()) != null) {            
                data.append(line);              
            }    
            paramout.close();    
            reader.close();    
        } catch (Exception e) {    
            // TODO Auto-generated catch block    
            e.printStackTrace();    
        }
        return data.toString();  
    }  
}
