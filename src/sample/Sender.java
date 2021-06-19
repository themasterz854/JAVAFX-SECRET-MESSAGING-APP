package sample;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Sender extends Thread{
    Socket s1;
    public Sender(Socket s1)
    {
        this.s1 = s1;
    }
    public void run()
    {       try {
        DataOutputStream dout = new DataOutputStream(s1.getOutputStream());
        BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
        String str ;
        while(true)
        {
            str=br.readLine();
            dout.writeUTF(str);
            dout.flush();
            if(str.equals("exit"))
            {   dout = new DataOutputStream(s1.getOutputStream());
                dout.writeUTF("exit");
                sleep(3000);
                dout.close();
                break;
            }
        }

    } catch (IOException | InterruptedException e) {
        e.printStackTrace();
    }
    }
}