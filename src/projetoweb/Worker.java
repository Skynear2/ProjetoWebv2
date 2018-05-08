/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetoweb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author Wesley
 */
class Worker extends Thread {

    Socket socket;
    BufferedWriter out;
    BufferedReader in;
    String HTTPmethod;
    String ResourcePATH;
    String HTTPProtocol;
    String QParam;
    String cookie;
    Integer count = 0;
    String Resposta; 
    private HashMap headerMap;
    String dynParam;
    String dynfName;
    String dynResponse;
    String dynfile;
    String dynfPath;
    long dynfTam;
//    String exePathFile;
//    String exeResposta;
//    String exeHtml;

    

    public BufferedReader getIn() {
        return in;
    }

    public void setIn(BufferedReader in) {
        this.in = in;
    }

    public String getHTTPmethod() {
        return HTTPmethod;
    }

    public void setHTTPmethod(String HTTPmethod) {
        this.HTTPmethod = HTTPmethod;
    }

    public String getResourcePATH() {
        return ResourcePATH;
    }

    public void setResourcePATH(String ResourcePATH) {
        this.ResourcePATH = ResourcePATH;
    }

    public String getHTTPProtocol() {
        return HTTPProtocol;
    }

    public void setHTTPProtocol(String HTTPProtocol) {
        this.HTTPProtocol = HTTPProtocol;
    }

    public String getQParam() {
        return QParam;
    }

    public void setQParam(String QParam) {
        this.QParam = QParam;
    }
    

    public Worker(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        //this.myMap = new HashMap<String,String>();
         this.headerMap = new HashMap();
        this.start();
    }

    public void processarCabecalho() throws IOException {
        adicionaData();
        int flag = 0;
        String msg;
        String[] first;
        
        //List<String> listasplit = new ArrayList();
        
        
 
        /*this.setHTTPmethod(first[0]);
        if(first[1].contains("?")) {
            String[] aux = first[1].split("?");
            this.setResourcePATH(aux[0]);
            this.setQParam(aux[1]);
        }
        else{
            this.setResourcePATH(first[1]);}
        this.setHTTPProtocol(first[2]);*/
        while (!(msg = in.readLine()).equals("")) {
            if(flag == 0){
               first = msg.split(" ");
               this.setHTTPmethod(first[0]);
               if(first[1].contains("?")) {
                     String[] aux = first[1].split("?");
                     this.setResourcePATH(aux[0]);
                     this.setQParam(aux[1]);
               }
               else{
                   if(first[1].equals("\favicon.ico")){
                       char c;
                       c = first[1].charAt(1);
                       first[1] = Character.toString(c); 
                       
                   }
                   this.setResourcePATH(first[1]);}
            
                System.out.println("first 2"+ first[2]);
            this.HTTPProtocol = first[2];
            flag = 1;
            }
            else {
                first = msg.split(": ");
                this.headerMap.put(first[0], first[1]);
            }
        }
            /*    String[] spli = msg.split(": ");
            //System.out.println("split1: "+spli[0]+" split 2: "+spli[1]);
            this.myMap.put(spli[0], spli[1]);
            if (msg.equals("")) {
                break;
            }
        }
        
        */
        System.out.println("-------------------------------------");
}
        

public void setCookie(){
        this.cookie = ("HTTP 200 TUDO OK  Set-Cookie: Count=0\r\n\r\n");
    
}    

public void Diretorios() throws IOException{
        
    String dirName = this.ResourcePATH;

        Files.list(new File(dirName).toPath())
                .limit(10)
                .forEach(path -> {
                    System.out.println(path);
                });
} 

public void adicionaData(){
    Date data = new Date();
    this.Resposta = ("Date: " + data.toString() + "\r\n");
}

public void concatenaResposta (String str){
    
    if(this.Resposta.isEmpty()){
        this.Resposta = str;
    }
    else {
        this.Resposta = this.Resposta.concat(str);
    }
    
    
}

public void escreveNoArquivo(File file) throws IOException {
        byte[] buffer = new byte[1024];
        int bytes;
        FileInputStream In = new FileInputStream(file);
        OutputStream Out = new DataOutputStream(this.socket.getOutputStream());

        while ((bytes = In.read(buffer)) != -1) {
            Out.write(buffer, 0, bytes);
            Out.flush();
        }
        In.close();
    }

public boolean autorizado() throws UnsupportedEncodingException{
        boolean flag;
    if (this.headerMap.containsKey("Authorization")) {
            String aux = (String) this.headerMap.get("Authorization");
            String[] aux2 = aux.split("Basic ");
            byte[] decodificado = Base64.getDecoder().decode(aux2[1]);
            String login = new String(decodificado, "UTF-8");
            String[] split = login.split(":");
            if (split[0].equals("teste") && split[1].equals("teste")) {
                flag = true;}
            else{
                flag = false;}
    }
   else {
         flag = false;}

    return flag;
         
}

/*public void executaexe() throws IOException {
        Process processo = Runtime.getRuntime().exec(this.ResourcePATH);
        BufferedReader in = new BufferedReader(new InputStreamReader(processo.getInputStream()));
        String linha;
        while ((linha = in.readLine()) != null) {
            if (this.exeResposta.isEmpty()){
            this.exeResposta = linha;
        }
        this.exeResposta =this.exeResposta.concat(linha);
        }
   }
*/
public void subResposta(){
        String regex = Pattern.quote("<%")+"(.*?)"+Pattern.quote("%>");
        this.dynfile = this.dynfile.replaceAll(regex, this.dynResponse);
    }

public void executarFuncao() {
    Date data = new Date();
    SimpleDateFormat date;
    try{
        date = new SimpleDateFormat(this.dynParam);
        this.dynResponse = date.format(data).toString();
    }catch (Exception e){
        System.out.println(e.getMessage());
        this.dynResponse = "Erro";
    }
}

public void dynNomeParam() throws FileNotFoundException{
    String file = new String();
    File arquivo = new File(this.ResourcePATH);
    this.dynfTam = arquivo.length();
    Scanner scanner = new Scanner(arquivo);
    while(scanner.hasNextLine()){
        file = file.concat(scanner.nextLine());
    }
    this.dynfile = file;
    String[] split = file.split("<%");
    split = split[1].split("%>");
    String funcao = split[0];
    funcao = funcao.replace("\"", "");
    funcao = funcao.trim();
    this.dynParam =funcao.substring(funcao.indexOf("(") + 1, funcao.indexOf(")"));
    this.dynfName = funcao.substring(0, funcao.indexOf("("));
}

public void requisiçao401() throws IOException {
        this.out.write("HTTP/1.1 401 Authorization Required\r\n");
        this.out.write("WWW-Authenticate: Basic realm=\"User Visible Realm\"");
        this.out.write("\r\n");
        this.out.flush();
}
public String tudoOk(){
    return("HTTP/1.1 200 OK\r\n");
    }

public void GET() throws IOException{
    Path arquivos = Paths.get(this.getResourcePATH());
    if(Files.isDirectory(arquivos)){
        if(autorizado() == true){ //autorizado() ==
            if ((this.ResourcePATH.charAt(this.ResourcePATH.length() - 1)) != '/') {
                  System.out.println("REPOSTA 301");
            }        
        
        else {
                    File diretorio = new File(this.ResourcePATH);
                    File[] arquivosAux = diretorio.listFiles();
                    this.out.write(tudoOk());
                    this.out.write(this.Resposta);
                    this.out.write("\r\n");
                    this.out.flush();
                    String html = new String();
                    for (File f : arquivosAux){
                       
                       // html = "<tr><td valign=\"top\">"+"<img src=\"/src/unknown.gif\" alt=\"[   ]\"></td>"+"<td><a href=\">kurumin-7.0.iso</a>"
                        html = html.concat("<tr><td valign=\"top\"></td><td><a href=\"" + f.getName() + "\">" + f.getName()
                    + "</a></td><td></td><td>" + f.length() + "</td></tr>\n");
                    }
                    String file = "<!DOCTYPE html>"+
                                  "<html>"+
                                        "<head>"+
                                                "<link rel=\"shortcut icon\" type=\"image/x-icon\" href=\"./favicon.ico\">"+
                                                "<title>localhost</title>"+
                                                "<meta charset=\"UTF-8\">"+
                                                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"+
                                        "</head>"+
                                        "<body>"+
                                             "<div style=\"width: 80%\">"+
                                                    "<h1>Conteúdo</h1>"+
                                                    "<table style=\"width: 90%\">"+
                                                        "<tr>"+
                                                           "<th>Nome do Arquivo</th>"+
                                                           "<th>Tamanho</th>"+
                                                        "</tr>"+
                                                        html+
                                                    "</table>"+
                                                "</div>"+
                                        "</body>"+
                                  "</html>";
                   file = file.trim();
                   String regex = Pattern.quote("<%") + "(.*?)" + Pattern.quote("%>");
                   file = file.replaceAll(regex, html);
                   this.out.write(file);
                   
        }
    
    }
        else {
            this.requisiçao401();
        }
    }
    else if (Files.exists(arquivos)) {
        if (this.ResourcePATH.endsWith(".dyn")) {
            dynNomeParam();
            executarFuncao();
            subResposta();
        
        this.out.write(this.tudoOk());
        this.concatenaResposta("content-type: " + Files.probeContentType(arquivos) + "\r\n");
        this.concatenaResposta("content-lenght: " + this.dynfTam + "\r\n");
        this.out.write(this.Resposta);
        this.out.write("\r\n");
        this.out.flush();
        this.out.write(this.dynfile);
       }
    
        
    }
    
    else{
        File pathArq = new File(this.ResourcePATH);
        this.out.write(tudoOk());
        this.concatenaResposta("content-type: " + Files.probeContentType(arquivos) + "\r\n");
        this.concatenaResposta("content-lenght: " + pathArq.length() + "\r\n");
        this.out.write(this.Resposta);
        this.out.write("\r\n");
        this.out.flush();
        this.escreveNoArquivo(pathArq);
    }
}



public void chamaMetodo() throws IOException{
    if(this.HTTPmethod.equals("GET")){
        GET();
    }
    else {
        System.out.println("nao foi metodo get.");
            }
    
}

public void cookie(){
        boolean flag;
        if (this.headerMap.containsKey("Cookie")) {
            flag =  true;
        } else {
            flag = false;
        }
    
}

@Override
        public void run() {
        String str = "HTTP 200 OK \r\n\r\n\r\n mensagem deu certo";
        try {
            
          //  this.out.writeBytes(str);
            
            processarCabecalho();
            cookie();
            chamaMetodo();
            //Diretorios();
            out.close();
            socket.close();
        

} catch (IOException ex) {
            Logger.getLogger(Worker.class
.getName()).log(Level.SEVERE, null, ex);
        }
        
}
}



/*index of mostra primeira vez q ocorreu : 
if key == cookie 
processa cookie
else
this.header.put(key,value)

*/


/*
    this.requestCookie(key, value)

switch (metodo)
case get: process get  //verifica o path, ve se o resource existe   se for um DIR (diretorio)
case post:
case xxx:

*/

/* 
    count = 1 tem q vira 2
    this.response cookie  == variavel global da classe

linha / header / conteudo 

this.responseHeader 
*/