import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Server {
    Socket socket;
    BufferedReader br;
    PrintWriter pw;
	Thread readThread, writeThread;
	private volatile boolean runningFlag = true;
    JFrame frame = new JFrame("Server Messagener");
    JLabel heading;
    JTextArea messageArea;
    JTextField messageField;
    Font font;

    public Server(){
        try {
            ServerSocket serverSocket = new ServerSocket(6666);
            System.out.println("Server is ready to accept connection. Waiting...");
            socket = serverSocket.accept();
            createGUI();
         
            System.out.println("Connection granted...");

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(socket.getOutputStream());
            readingData();
            eventHandler();

            //writingData();

        } catch (Exception e){
            JOptionPane.showMessageDialog(frame, e.getMessage(),"Connection Not Established",JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(0);
        }
    }

    void createGUI(){
        frame.setSize(600, 600);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        font = new Font("Roboto",Font.PLAIN,15);

        heading = new JLabel();
        messageArea = new JTextArea();
        messageField = new JTextField();

        //Heading
        heading.setFont(font);
        heading.setText("Server Messagener");
        heading.setIcon(new ImageIcon(new ImageIcon("msgLogo.png").getImage().getScaledInstance(30,30,Image.SCALE_DEFAULT)));
        heading.setHorizontalAlignment(SwingUtilities.CENTER);
        heading.setHorizontalTextPosition(SwingUtilities.CENTER);
        heading.setVerticalTextPosition(SwingUtilities.BOTTOM);
        heading.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //Message Textarea
        messageArea.setFont(font);
        messageArea.setLineWrap(true);
        messageArea.setEditable(false);
        JScrollPane msgScrollPane = new JScrollPane(messageArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        //Message Field
        messageField.setFont(font);
        messageField.setHorizontalAlignment(SwingConstants.LEFT);
        messageField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        messageField.setAutoscrolls(true);

        frame.add(heading, BorderLayout.NORTH);
        frame.add(msgScrollPane, BorderLayout.CENTER);
        frame.add(messageField, BorderLayout.SOUTH);
        frame.setVisible(true);
    }
    
    void eventHandler(){
        messageField.addKeyListener(new KeyListener(){

            @Override
            public void keyTyped(KeyEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void keyPressed(KeyEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == 10){
                    String data = messageField.getText().toString();
                    if(data != null && !data.trim().equals("")){
                        pw.println(data);
                        pw.flush();
                        messageArea.append("Me : "+data+"\n");
                    }
                    messageField.setText("");
                }
            }

        });
    }

    void readingData() {
        Runnable runnable=()->{
            try {
                while (runningFlag) {
                    String data = br.readLine();
					if(!runningFlag){
						break;
					}
                    /*if (data != null && data.equals("exit")) {
                        System.out.println("Client closed the connection...");
						terminateChat();
                        break;
                    }*/
                    //System.out.println("Client : " + data);
                    messageArea.append("Client : "+data+"\n");
                    
                }
            } catch (Exception e) {
                System.out.println("Connection Closed");
                try {
                    terminateChat();
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
				//e.printStackTrace();
            }
        };

        readThread = new Thread(runnable);
		readThread.start();
    }

    void writingData(){
        Runnable runnable=()->{
            try {
                while (runningFlag) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                    String data = bufferedReader.readLine();
					pw.println(data);
					pw.flush();
					if (data != null && data.equals("exit")) {
						System.out.println("Server closed the connection...");
						terminateChat();
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Connection Closed");
				//e.printStackTrace();
            }
        };

        writeThread = new Thread(runnable);
		writeThread.start();
    }

    public static void main(String[] args) {
        System.out.println("This is the server going to start");
		new Server();
    }
	
	void terminateChat() throws Exception{
		runningFlag = false;
		socket.close();
		br.close();
		pw.close();
		//System.out.println("Connection Closed");
        JOptionPane.showMessageDialog(frame, "Chat Terminated", "Connection Closed", JOptionPane.ERROR_MESSAGE);
        messageField.setEnabled(false);
	}
	
}
