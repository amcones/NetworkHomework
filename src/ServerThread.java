import java.io.*;
import java.net.Socket;
import java.util.Arrays;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 服务器线程，主要来处理多个客户端的请求
 */
public class ServerThread extends Servers implements Runnable{

    Socket socket;
    String socketName;
    int cnt=0;

    public ServerThread(Socket socket){
        this.socket = socket;
    }
    @Override
    public void run() {
        try {
            //BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //设置该客户端的端点地址
            socketName = socket.getRemoteSocketAddress().toString();
            //有用户变化时发送用户列表
            System.out.println("Client@"+socketName+"已加入聊天");
            //序列化为Json
            ObjectMapper objectMapper=new ObjectMapper();
            info.add("Client@"+socketName);
            cnt++;
            msg="Client@"+socketName+"已加入聊天";
            String json= objectMapper.writeValueAsString(new ServerInfo(info,msg));
            System.out.println(json);
            print(json);
            byte[] inputBuffer=new byte[65535];
            InputStream inputStream=socket.getInputStream();
            boolean flag = true;
            while (flag)
            {
                //阻塞，等待该客户端的输出流
                int len=inputStream.read(inputBuffer);
                //若客户端退出，则退出连接。
                if (len==-1){
                    flag = false;
                }
                else {
                    msg = "Client@" + socketName + ":" + new String(inputBuffer,0,len);
                    System.out.println(msg);
                    //向在线客户端输出信息
                    json= objectMapper.writeValueAsString(new ServerInfo(info,msg));
                    print(json);
                }
            }

            closeConnect();
        } catch (IOException e) {
            try {
                closeConnect();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
    /**
     * 向所有在线客户端socket转发消息
     * @param msg
     * @throws IOException
     */
    private void print(String msg) throws IOException {
        PrintWriter out = null;
        synchronized (sockets){
            for (Socket sc : sockets){
                out = new PrintWriter(sc.getOutputStream());
                out.println(msg);
                out.flush();
            }
        }
    }
    /**
     * 关闭该socket的连接
     * @throws IOException
     */
    public void closeConnect() throws IOException {
        ObjectMapper objectMapper=new ObjectMapper();
        cnt--;
        info.remove("Client@"+socketName);
        msg="Client@"+socketName+"已退出聊天";
        System.out.println("Client@"+socketName+"已退出聊天");
        String json=objectMapper.writeValueAsString(new ServerInfo(super.info,msg));
        print(json);
        //移除没连接上的客户端
        synchronized (sockets){
            sockets.remove(socket);
        }
        socket.close();
    }
}