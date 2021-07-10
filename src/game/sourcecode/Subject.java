package game.sourcecode;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.LockSupport;

@SuppressWarnings("all")
public class Subject implements AutoCloseable {
    private ServerSocket server;
    private List<Listener> listeners = new ArrayList<>();
    private List<Thread> threads = new ArrayList<>();
    private Thread observe = new Thread();
    private final List<Object> data = new ArrayList<>();


    private int invoker;
    private boolean flag = true;

    public Subject() {
        try {
            server = new ServerSocket(20000);
        } catch (IOException e) {
            server = null;
            return;
        }
        Thread attach=new Thread(() -> {

            while (flag) {
                accept();
            }
        });
        attach.setDaemon(true);
        attach.start();
        observe = new Thread(() -> {
            while (flag) {
                LockSupport.park();
                synchronized (data) {
                    data.notifyAll();
                }
                broadcast();
            }
        });
        observe.setDaemon(true);
        observe.start();
    }

    public void accept() {
        Socket cli = null;
        try {
            if (server != null) {
                cli = server.accept();
            }
        } catch (IOException e) {

        }
        Listener listener = new Listener(cli);
        attach(listener);

        Thread thread = new Thread(() -> {
            while (listeners.contains(listener)) {
                try {
                    data.add(0, listener.readForSubject());
                } catch (Exception e) {
                    listeners.remove(listener);
                }
                if (this.data.get(0)==null){
                    Object temp=data.stream().filter(t->t!=null).toArray()[0];
                    data.add(0,temp);
                }
                if (this.data.size() > 1) {
                    this.data.remove(1);
                }
                LockSupport.unpark(observe);
            }
        },
                ""+(listeners.size()-1));
        threads.add(thread);
        thread.start();
    }

    public void attach(Listener listener) {
        listeners.add(listener);
    }

    public void setData(Object data) {
        this.data.add(0, data);
        if (this.data.size() > 1) {
            this.data.remove(1);
        }
        broadcast();
    }

    public Object read() {
        if (listeners.size() == 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return data.get(0);
        }

        synchronized (data) {
            try {
                data.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return data.get(0);
    }

    public void broadcast() {
        for(int a=0;a!=listeners.size();a++){
            try {
                listeners.get(a).updateForSubject(data.get(0));
            } catch (Exception e) {
                a++;
            }
        }
    }

    private void broadcast(int exceptor) {
        for(int a=0;a!=listeners.size();a++){
            if(a!=exceptor){
                listeners.get(a).update(data);
            }
        }
    }


    public Object getData() {
        return data.get(0);
    }

    public int size() {
        return listeners.size();
    }

    @Override
    public void close() throws Exception {
        flag = false;
        if (null != server) {
            server.close();
        }
        listeners.forEach(e -> {
            try {
                e.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }
}
