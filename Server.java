package com.project.service;
//import delle classi / interfacce che servono per il programma
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static java.lang.System.*;
import java.io.*;

public class Server {
        ServerSocket server;
        Socket client;
        PrintStream stream;
        BufferedReader bufferedReader;
        InputStream input;
        OutputStream output;
        LocalTime open_time;//ora di apertura del server
        boolean listen;
        boolean work_data;
        int port;
        private final String PASSWORD = "server";

        public Server(int port){//costruttore della classe server (porta)
            try {
            	
                this.server = new ServerSocket(port);
            } catch (IOException e) {
                out.println("ERRORE NELLA CREAZIONE DEL SERVER");
                return;
            }
            this.port = port;
            listen = true; 
            open_time = LocalTime.now();
            out.println("----------------SERVER IN ASCOLTO NELLA PORTA "+this.port+" "+ server.getLocalSocketAddress()+"----------------");
        }

        public void connect()throws Exception{
        	
        	while(listen) {//inizio di ciclo di ascolto nella porta 
	        	out.println("SERVER IN ATTESA DI UNA CONNESSIONE NELLA PORTA "+this.port);
	            client = server.accept();
	            out.println("ip client : "+client.getInetAddress());
	            input = client.getInputStream();
	       	 	bufferedReader = new BufferedReader(new InputStreamReader(input));
		       	output = client.getOutputStream();
		        stream = new PrintStream(output);
		        this.work_data = true;
	            out.println("-CLIENT COLLEGATO!");
	            stream.println("BENVENUTO IN CONNESIONE per aiuto comandi ADMIN digitare /admin -help ");
           
	            while(work_data){
	            	
	                //LETTURA DATI
	            	String sen = this.ReadData();
	            	//LAVORO E SCRITTURA DATI E CONTROLLO DEI COMANDI DATI DAL CLIENT
	            	if(sen.equalsIgnoreCase("/admin shutdown")) {
	            		if(isAdmin()) {
	            			disconnect_client();
	            			shutdown();
	            		}else
	            			stream.println("no admin access!");
	            	}else if(sen.equalsIgnoreCase("/quit")) {
	            		disconnect_client();
	            	}else if(sen.equalsIgnoreCase("/admin info")) {
	            		if(isAdmin()) {
	            			SendInfo();
	            		}else
	            			stream.println("no admin access!");
	            	}else if(sen.equalsIgnoreCase("/admin -help")) {
            			ShowCommand();
            		}else
	            		WriteAndWorkData(sen);
	            	
	            }
        	}
        }
        private void ShowCommand() {
			
        	stream.println("/admin COMMAND -> { /ADMIN INFO -> [ info server ] || /ADMIN SHUTDOWN -> [ shutdown server ] }");
        	
		}

		public String ReadData()throws Exception {
        	 return bufferedReader.readLine();
        }
        public void WriteAndWorkData(String sentence) throws Exception {
            stream.println(getNumberoDiConsonanti(sentence));
        }
      //metodo per controllo dell'admin access
        public boolean isAdmin() {
        	
        	boolean is_admin = false;
        	
        	stream.println(" - inserisci password -");
        	try {
				String p = ReadData();
				if(p.equals(PASSWORD)) {
					is_admin = true;
				    out.println("-CLIENT HA FATTO ACCESSO COME ADMIN");
				}
			} catch (Exception e) {
				out.println("ERRORE LETTURA");
			}
        	
        	return is_admin;
        }
        // metodo per l'algoritmo per il calcolo delle CONSONANTI - VOCALI - SPAZI
        public static String getNumberoDiConsonanti(String sentence){
        	//dichiarazione delle varibile neccessarie
            int contatore_vocali = 0;
            int contatore_cons = 0;
            int contatore_spazio = 0;
            char[] vocali = {'a','e','i','o','u'};//dominio delle vocali
       
            sentence = sentence.toLowerCase();//imposto la stringa in Lower Case ( minuscolo )
            for(int i=0;i < sentence.length();i++){//ciclo per ogni carattere contenuto nella stringa
                if(sentence.charAt(i) == ' ') contatore_spazio++;
                int c = contatore_vocali;
                for(int j=0; j < vocali.length;j++){
                    if(sentence.charAt(i) == vocali[j]){
                        contatore_vocali++;
                    }
                }
                if(contatore_vocali == c) contatore_cons++;
            }
            return (contatore_cons-contatore_spazio)+"&" + contatore_vocali + "&" + contatore_spazio;
        }

        public void disconnect_client(){
            this.work_data = false;
            out.println("-CLIENT USCITO  !");
        }
        public void shutdown() {
        	this.listen = false;
        	out.println("SERVER CHIUSO DA ADMIN  !");
        }
        //invio al client delle info del server
        public void SendInfo() {
        	//scrittura delle info del server
        	stream.println("{ ORA -> "+open_time.format(DateTimeFormatter.ofPattern("hh:mm:ss")) +" }"+"{ PORTA -> "+this.port +" }"+"{ IP -> "+this.server.getInetAddress() +" }");
        }
      
    public static void main(String[] args) {
          Server server = new Server(9090);//inizializzo server nella porta 9090
          try {
            server.connect();
        } catch (Exception e) {
           out.println(e.getMessage());
        }
        }

}