package learn.webserver;

import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

public class HttpRequestHandler implements Runnable {

    final static String CRLF = "\r\n";     //定义常量：HTTP message 的结束符：回车符/换行符-\r\n
    Socket socket;     //套接字获得
    InputStream input;     //socket输入流
    OutputStream output;     //socket输出流
    BufferedReader bufR;     //BufferedReader类从字符输入流中读取文本并缓冲字符，以便有效地读取字符，数组和行

    //construct method
    public HttpRequestHandler(Socket socket)
            throws Exception {
        this.socket = socket;
        this.input = socket.getInputStream();     //获得socket输入流
        this.output = socket.getOutputStream();     //获得socket输出流
        this.bufR = new BufferedReader(new
                InputStreamReader(socket.getInputStream()));       //将从输入流获取的信息存入buffer中，进行处理
    }

    //implement the run() method of Runnable interface
    public void run(){
        try {
            processRequest();
        }
        catch(Exception e) {
            System.out.println(e);     //Print error Massage
        }
    }
    private void processRequest() throws Exception {
        while(true) {

            //读取并显示Web browser提交的请求信息
            String singleLine = bufR.readLine();
//            System.out.println("The information from the client is : \" " + singleLine + "\"");
            if(singleLine.equals(CRLF) || singleLine.equals("")) {
                break;
            }

            //process message
            StringTokenizer sL = new StringTokenizer(singleLine);
            String temp = sL.nextToken();

//            System.out.println("The first token is : " + "\"" + temp + "\"");
            if(temp.equals("GET")) {

                //对路径进行处理
//                String basePath = System.getProperty("java.class.path");
                String basePath = "";
                try{
//                    basePath = HttpRequestHandler.class.getClassLoader().getResource("").toURI().getPath();
//                    basePath = HttpRequestHandler.class.getResource("/").getPath();

                    File test = new File("");
                    basePath = test.getAbsolutePath();

//                    System.out.println("Original basePath　：" + basePath);
//                    StringTokenizer basePathProcess = new StringTokenizer(basePath,"/");
//                    String basePathTemp = "";
//                    String tempBase = basePathProcess.nextToken();
//                    while (!tempBase.equals("out")){
//                        tempBase =tempBase + "/";
//                        basePathTemp = basePathTemp + tempBase;
//                        System.out.println(basePathTemp);
//                        tempBase = basePathProcess.nextToken();
//                    }
//                    basePath = basePathTemp;
//                    System.out.println("basePath = " + basePath);
                }catch (Exception e){
                    System.out.println(e);
                }

                Boolean isDefault = false;
                String fileName = sL.nextToken();
//                System.out.println("The request token is : " + "\"" + fileName + "\"");
                if(fileName.equals("/")){
                    isDefault = true;     //判断是否返回默认网页
//                    System.out.println("panduan if is default");
                }

                fileName = basePath + fileName;
                System.out.println("The requested file is located in : " + fileName);

                //打开所请求的文件
                FileInputStream fileIS = null;
                boolean fileExists = true;
                try    {
                    fileIS = new FileInputStream(fileName);      //文件输出流
                }
                catch(FileNotFoundException e) {
                    fileExists = false;
                    System.err.println("file not found :"+fileName);
                }

                //完成回应消息
                String serverLine = "Web Server";
                String statusLine = null;     //状态行信息
                String contentTypeLine = null;     //内容类型行
//                String entityBody = null;
                String contentLengthLine = "error";
                if(fileExists) {
                    statusLine = "HTTP/1.0 200 OK" + CRLF ;
                    contentTypeLine = "Content-type: " +
                            contentType( fileName ) + CRLF ;
                    contentLengthLine = "Content-Length: "
                            + (Integer.valueOf(fileIS.available())).toString()
                            + CRLF;

                    System.out.println("Return the file : " + fileName);
                }
                else {
                    String defaultFile = "";
                    String errorFile = "";
                    if(isDefault){
                        defaultFile = basePath + "/default.html";

                        fileIS = new FileInputStream(defaultFile);
                        statusLine = "HTTP/1.0 200 OK" + CRLF ;
                        contentTypeLine = "text/html" ;

                        contentTypeLine = "Content-type: " +
                                contentType( defaultFile ) + CRLF ;
                        contentLengthLine = "Content-Length: "
                                + (Integer.valueOf(fileIS.available())).toString()
                                + CRLF;

                        System.out.println("Return the defaultFile : " + defaultFile);
                    }else{
                        errorFile = new String(fileName = basePath +"/error404.html" );
                        fileIS = new FileInputStream(errorFile);
                        statusLine = "HTTP/1.0 404 Not Found" + CRLF ;
                        contentTypeLine = "text/html" ;

                        contentTypeLine = "Content-type: " +
                                contentType( errorFile ) + CRLF ;
                        contentLengthLine = "Content-Length: "
                                + (Integer.valueOf(fileIS.available())).toString()
                                + CRLF;
                        System.out.println("Return the errorFile : " + errorFile);
                    }

                }

                // 发送到服务器信息
                output.write(statusLine.getBytes());
                output.write(serverLine.getBytes());
                output.write(contentTypeLine.getBytes());
                output.write(contentLengthLine.getBytes());
                output.write(CRLF.getBytes());
                // 发送信息内容

                sendBytes(fileIS, output) ;      //按字节发送信息
                fileIS.close();//关闭文件
            }
        }

        //关闭套接字和流
        try {
            output.close();      //关闭输出流
            bufR.close();
            socket.close();
        }
        catch(Exception e) {}
    }

    private static void sendBytes(FileInputStream fis,
                                  OutputStream os) throws Exception {
        byte[] buffer = new byte[1024] ;     // 创建一个 1K buffer
        int bytes = 0 ;

        // 将文件输出到套接字输出流中
        while ((bytes = fis.read(buffer)) != -1 ) {
            os.write(buffer, 0, bytes);      //通过写缓冲的方式
        }
    }
    private static String contentType(String fileName) {
        if (fileName.endsWith(".htm") ||
                fileName.endsWith(".html"))    {
            return "text/html";      //如果文件为HTML文件，返回"text/html"
        }
        return "fileName";      //否则直接返回文件名
    }
}
