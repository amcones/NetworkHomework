using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using System.ComponentModel;
using System.Text.Json;


namespace WebHomework
{
    /// <summary>
    /// MainWindow.xaml 的交互逻辑
    /// </summary>
    public partial class MainWindow : Window
    {
        private Socket socket;
        private EndPoint endPoint;
        private String outBufferStr;
        private Byte[] outBuffer = new Byte[65535];
        private Byte[] inBuffer = new Byte[65535];
        bool connected = false;
        /// <summary>
        /// 消息监听进程
        /// </summary>
        public void listenerhandler()
        {
            while (true)
            {
                byte[] msg = new byte[65535];
                int len = -1;
                try
                {
                    len = socket.Receive(msg);
                }
                catch (SocketException e)
                {
                    if(connected)
                        MessageBox.Show("接受消息时发生异常:" + e.Message);
                    return;
                }
                string msgstr = Encoding.UTF8.GetString(msg, 0, len);
                //Json反序列化
                Console.WriteLine(msgstr);
                ServerInfo serverInfo = new ServerInfo();
                serverInfo = JsonSerializer.Deserialize<ServerInfo>(msgstr);
                this.Dispatcher.Invoke(() =>
                {
                    if(serverInfo.msg!=null)messageFrameTxt.Text += serverInfo.msg+"\n";
                    if (serverInfo.info != null)
                    {
                        usrList.Items.Clear();
                        for (int i = 0; i < serverInfo.info.Length; i++)
                        {
                            usrList.Items.Add(serverInfo.info[i]);
                        }
                    }
                });
            }
        }
        public MainWindow()
        {
            InitializeComponent();
            
            try
            {
                socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);//使用TCP协议的socket
                endPoint = new IPEndPoint(IPAddress.Parse("127.0.0.1"), 6666);
                socket.Connect(endPoint);
                connected = true;
            }
            catch
            {
                MessageBox.Show("未能连接服务器");
            }
            Thread listener = new Thread(listenerhandler);
            listener.Start();
        }
        
        private void DeliverBtn_Click(object sender, RoutedEventArgs e)
        {
            string message = messageTxt.Text;
            try
            {
                outBufferStr = message;
                outBuffer = Encoding.UTF8.GetBytes(outBufferStr);
                socket.Send(outBuffer, outBuffer.Length, SocketFlags.None);

            }
            catch
            {
                MessageBox.Show("未能连接服务器");
            }
            finally
            {
                messageTxt.Text = "";
            }
        }

        private void ClientForm_Closing(object sender, CancelEventArgs e)
        {
            socket.Close();
            connected = false;
        }
    }
}
