package com.ai.paas.fileupload.mvc;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FileUploadServlet extends HttpServlet {
    
    private ServletContext sc;
    private static final long serialVersionUID = 151650843430214502L;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @SuppressWarnings("rawtypes")
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            	
    	String tag = request.getParameter("tag");
		String defid = request.getParameter("defid");
		String image_name = request.getParameter("image_name");
		
		Map<String ,String> map = new HashMap<String,String>();
		map.put("tag", tag);
		map.put("defid", defid);
		map.put("image_name", image_name);
    	
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/x-javascript;charset=UTF-8");
        PrintWriter out = response.getWriter();
    
        String localIp=request.getLocalAddr();//获取本地ip
        int localPort=request.getLocalPort();//获取本地的端口
        String uri = request.getRequestURI();
        uri =  uri.substring(1,uri.indexOf("servlet"));
        String url = "http://"+localIp+":"+localPort+"/"+uri+"";
        
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload servletFileUpload = new ServletFileUpload(factory);//通过工厂生成一个处理文件上传的servlet对象
        
        try {
            List items = servletFileUpload.parseRequest(request);//解析request
            Iterator iterator = items.iterator();
            while (iterator.hasNext()) {
                FileItem item = (FileItem) iterator.next();
                if(!item.isFormField()){               
                    if(item.getName()!=null && !item.getName().equals("")){//一个上传的文件
                        
                        File tempFile = new File(item.getName());//getName得到的文件名称包含了它在客户端的路径
                        System.out.print(sc.getRealPath("/")+tempFile.getName());
                        File file = new File(sc.getRealPath("/"),tempFile.getName());
                        item.write(file);//将上传的文件写入到file中
                        map.put("export_file_url", url+tempFile.getName());
                        map.put("result", "success");
                    }else{
                    	 map.put("result", "error");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            map.put("result", "error");
        }
        
        Gson gson =  new GsonBuilder().create();
        String json = gson.toJson(map);
        out.println(json);
        out.close();        
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        sc = config.getServletContext();        
    }
    
}
