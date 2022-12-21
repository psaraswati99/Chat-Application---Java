import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class Client{
    BufferedReader br;
    PrintWriter pw;
    Socket socket;
	private volatile boolean runningFlag = true;
    JFrame frame = new JFrame("Client Messagener");
    JLabel heading;
    JTextArea messageArea;
    JTextField messageField;
    Font font;
    public Client(){
        try{
            socket = new Socket("127.0.0.1",6666);
            if(socket.isConnected()){
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                pw = new PrintWriter(socket.getOutputStream());

                createGUI();
                readingData();
                eventHandler();
             
             
                //writingData();
            }else{
                JOptionPane.showMessageDialog(frame, "Server is not ready to connect! Please try later...","Connection Not Established",JScrollPane.ERROR);
                System.exit(0);
            }
        }catch (Exception e){
            JOptionPane.showMessageDialog(frame, e.getMessage(),"Connection Not Established",JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(0);
        }
    }

    void createGUI(){
        font = new Font("Roboto",Font.PLAIN,15);
        frame.setSize(600, 600);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Heading
        heading = new JLabel("Client Messagener");
        heading.setIcon(new ImageIcon(new ImageIcon("msgLogo.png").getImage().getScaledInstance(30, 30, Image.SCALE_DEFAULT)));
        heading.setFont(font);
        heading.setHorizontalAlignment(SwingUtilities.CENTER);
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        //TextArea
        messageArea = new JTextArea();
        messageArea.setFont(font);
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        JScrollPane msgScrollPane = new JScrollPane(messageArea,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        //TextField
        messageField = new JTextField();
        messageField.setFont(font);
        messageField.setHorizontalAlignment(SwingConstants.LEFT);
        messageField.setAutoscrolls(true);
        messageField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
     

        frame.add(heading,BorderLayout.NORTH);
        frame.add(msgScrollPane, BorderLayout.CENTER);
        frame.add(messageField,BorderLayout.SOUTH);
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
    void readingData(){
        Runnable runnable=()->{
            try {
                while (runningFlag) {
                    System.out.print("Start Reading..");
                    String data = br.readLine();
                    System.out.println(data);
					if(!runningFlag){
						break;
					}
                    /*if (data != null && data.equals("exit")) {
                        System.out.println("Server closed the connection...");
						terminateChat();
                        break;
                    }*/
                    //System.out.println("Server : " + data);
                    messageArea.append("Server : "+data+"\n");
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
        new Thread(runnable).start();
    }

    void writingData() {
        Runnable runnable=()->{
            try {
                while (runningFlag) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                    String data = bufferedReader.readLine();
					pw.println(data);
					pw.flush();
                    if (data != null && data.equals("exit")) {
                        System.out.println("Client closed the connection...");
						terminateChat();
                        break;
                    }

                }
            } catch (Exception e) {
                System.out.println("Connection Closed");
				//e.printStackTrace();
            }
        };
        new Thread(runnable).start();
    }

    public static void main(String[] args) {
        System.out.println("This is the client...");
		new Client();
    }
	
	void terminateChat() throws Exception{
		runningFlag = false;
		socket.close();
		br.close();
		pw.close();
		//System.out.println("Connection Closed");
        JOptionPane.showMessageDialog(frame, "Chat Terminated", "Connection Closed", JOptionPane.ERROR_MESSAGE);
        messageField.setEnabled(false);
		//System.exit(0);
	} 
}
