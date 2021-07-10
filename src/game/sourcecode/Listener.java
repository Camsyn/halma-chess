package game.sourcecode;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class Listener implements AutoCloseable {
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    private Object buffer;

    public Listener(String ip) throws Exception {

        this.socket = new Socket(ip, 20000);
        oos = new ObjectOutputStream(socket.getOutputStream());
        ois = new ObjectInputStream(socket.getInputStream());

    }

    public Listener(Socket socket) {
        this.socket = socket;
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void update(Object data) {
        try {
            if (data==null){
                this.close();
                return;
            }
            buffer=data;

          //  oos.writeObject(data); 考虑到了地址，倘若之前写过相同地址的对象，则再次写入先前的对象的序列化流（哪怕对象内容改变了，只为提高效率）
            //重点
            oos.writeUnshared(data);
            //一定记住flush！！！！！！！！
            oos.flush();
        } catch (Exception e) {

        }
    }

    void updateForSubject(Object data) throws Exception {

            if (data==null){
                this.close();
                return;
            }
            buffer=data;

            //  oos.writeObject(data); 考虑到了地址，倘若之前写过相同地址的对象，则再次写入先前的对象的序列化流（哪怕对象内容改变了，只为提高效率）
            //重点
            oos.writeUnshared(data);
            //一定记住flush！！！！！！！！
            oos.flush();

    }

    public Object read() {
        try {
            buffer=ois.readUnshared();
            return buffer;
        } catch (Exception e) {
        }
        return buffer;
    }

    public Object readForSubject() throws IOException, ClassNotFoundException {
        buffer=ois.readUnshared();
        return buffer;
    }

    public Object getBuffer() {
        return buffer;
    }

    @Override
    public void close() throws Exception {
        socket.close();
    }
}
