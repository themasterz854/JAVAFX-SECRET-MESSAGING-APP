package sample;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.net.Socket;

public class workerclass extends Task{

        Socket s;

        workerclass(Socket s){
            this.s = s;

        }
        @Override
        protected Object call() throws Exception {

            return null;
        }
    }

