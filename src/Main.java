import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.io.InputStream;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws IOException {
        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
	    ServerSocket serverSocket=new ServerSocket(6666);
        System.out.println("服务器启动");
        while(true)
        {
            System.out.println("线程id="+Thread.currentThread().getId()+",名字="+Thread.currentThread().getName());
            System.out.println("等待连接");
            final Socket socket= serverSocket.accept();
            System.out.println("连接到一个客户端");
            //新建一个线程
            newCachedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    handler(socket);
                }
            });
        }
    }
    public static void handler(Socket socket)
    {
        try{
            System.out.println("线程id="+Thread.currentThread().getId()+",名字="+Thread.currentThread().getName());
            byte[] inputBuffer=new byte[65535];
            InputStream inputStream=socket.getInputStream();
            while(true)
            {
                System.out.println("线程id="+Thread.currentThread().getId()+",名字="+Thread.currentThread().getName());
                System.out.println("读取客户端输入");
                int len=inputStream.read(inputBuffer);
                if(len!=-1){
                    System.out.println(new String(inputBuffer,0,len));
                }
                else{
                    break;
                }
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }finally {
            System.out.println("关闭和客户端连接");
            try{
                socket.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
