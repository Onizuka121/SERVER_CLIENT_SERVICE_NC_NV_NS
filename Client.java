
//import delle classi / interfacce che servono per il programma
import java.io.*;
import static java.lang.System.*;
import java.net.*;
import java.util.Scanner;

public class Client {
	/** DICHIARAZIONE DELLE REFERENCE E VARIABILI **/
	Socket client;//socket client
	InputStream input;//inputstream per la lettura
	OutputStream output;//outputstream per la scrittura 
	BufferedReader buffer;//buffer che serve per bufferizzare i dati in input ( dal server )
	PrintStream print_stream;//printStream per la scrittura dei dati (output to server)
	Scanner scanner = new Scanner(System.in);//scanner per la lettura da tastiera
	
	//variabile booleana per gestire un ciclo (nel metodo main) per far continuare l'esecuzione del programma del client
	boolean end;
	
	public Client(String address,int port)throws Exception { //constructor della classe Client
		/**INIZIALIZZAZIONE DELLE REFERENCE**/
		client = new Socket(address,port);//passo nel construtor della classe Socket( ip del server , porta ) 
		input = client.getInputStream();
		buffer = new BufferedReader(new InputStreamReader(input));
		output = client.getOutputStream();
		print_stream = new PrintStream(output,true);
		out.println("-------------- CLIENT ---------------");
		end = false;
	}
	
	public void ReadData() throws Exception { // metodo che gestisce la lettura dei dati
		String message = buffer.readLine(); // con il buffer (BufferedReader) leggo la linea che arriva e la assegno ad una varibile String
		
		/*
		 *  CON IL SEGUENTE STACK DI CODICE GESTISCO LA STRINGA ARRIVATA (message)
		 *   il formato della stringa è " n_consonanti&n_vocali&n_spazi " 
		 *   quindi vado a dividere questa stringa ( secondo questo regex "&" ) -> ritorna un array e ne prendo i valori 
		 *   [0] -> numero di consonanti
		 *   [1] -> numero di vocali
		 *   [2] -> nuemero di spazio
		 */
		if(message.contains("&")) {
			String[] message_split = message.split("&");
			if(message_split[0].equals(message_split[1])) {
				shutdown(); // SE IL NUMERO DI CONS E' UGUALE AL NUMERO DI VOCALI FINISCE L'ESECUZIONE DEL CLIENT
				out.println("--numero di consonanti e vocali uguale--");
			}
			
			message =  "CONSONANTI -> "+message_split[0]+" VOCALI -> " + message_split[1] + " SPAZI -> " + message_split[2];
		}
		out.println("---> SERVER :"+message);
	}
	
	public void WriteData() {//metodo che gestisce la SCRITTURA dei dati
		err.print("client@#: ");
		String message = scanner.nextLine(); //lettura da tastiera della striniga da inviare
		if(message.equalsIgnoreCase("/quit")) { //controlla se l'utente decide di uscire con comando "/quit"
			shutdown();
			return;
		}
		print_stream.println(message);//scrittura della stringa
	}
	
	public void shutdown() {
		this.end = true;
		print_stream.println("/quit");//invio al server che il client è uscito
	}
	
	
	
    public static void main(String[] args){
    	String host = null;
    	do {
	    	out.print("client@#: IP ADDRESS SERVER :");
			host = new Scanner(System.in).next();
			if(!CheckIp(host)) {
				out.println("ip non valido riprova");
			}
    	}while(!CheckIp(host));
		
    	
		try {
			Client client = new Client(host,9090);
			client.ReadData();
	    	while(!client.end) {
	    		client.WriteData();
	    		if(!client.end)
	        	    client.ReadData();
	    	}
		} catch (Exception e) {
			out.println("SERVER CHIUSO O NON ESISTENTE CON IP : "+host);
		}finally {
			err.println("ARRIVEDERCI E GRAZIE");
		}
	
    }
    
  //metodo statico per il controllo che il formato stringa in input sia di tipo ip 
    public static boolean CheckIp(String ip) {
    	 return ip.split("\\.").length == 4;
    }
    
    
}
