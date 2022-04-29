import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Vector;

public class Main {
    public static void main(String[] args){
        new ServerListener().start();
    }
}
class ServerListener extends Thread{
    public void run(){
        try{
            ServerSocket serverSocket= new ServerSocket(6666);
            while(true){
                Socket socket=serverSocket.accept();
                System.out.println("有客户端连接");
                ChatSocket cs=new ChatSocket(socket);
                cs.start();
                ChatManager.getChatManager().add(cs);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
class ChatSocket extends Thread
{
    Socket socket;
    public ChatSocket(Socket socket){
        this.socket=socket;
    }
    public void out(String msg)
    {
        try{
            socket.getOutputStream().write(msg.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void run(){
        try{
            BufferedReader br=new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream(),"UTF-8"
                    )
            );
            String line=null;
            while((line=br.readLine())!=null){
                System.out.println(line);
                ChatManager.getChatManager().publish(this,line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
class ChatManager
{
    private ChatManager(){}
    private static final ChatManager chatManager=new ChatManager();
    public static ChatManager getChatManager(){
        return chatManager;
    }
    Vector<ChatSocket> vector = new Vector<ChatSocket>();
    public void add(ChatSocket chatSocket)
    {
        vector.add(chatSocket);
    }

    public void publish(ChatSocket socket,String msg){
        for(int i=0;i<vector.size();i++){
            ChatSocket chatSocket=vector.get(i);
            chatSocket.out(msg);
        }
    }
}
