/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mb;

import entities.Aluno;
import facade.AlunoFacade;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author praveen
 */
@ManagedBean(name = "alunoMBean")
@SessionScoped
public class AlunoMBean implements Serializable {

    /**
     * Creates a new instance of AlunoMBean
     */
    private Part foto;
    private Aluno aluno = new Aluno();
    private List<Aluno> alunos;
    private boolean carregado;
    private boolean uploaded;
    @EJB
    AlunoFacade alunoFacade;

    public AlunoMBean() {
    }

    @PostConstruct
    public void init() {
        alunos = new ArrayList<>();
    }

    public Part getFoto() {
        return foto;
    }

    public void setFoto(Part foto) {
        this.foto = foto;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public void doUpload() {
        try {

            InputStream in = foto.getInputStream();
            
            //File f = new File("D:\\fotos_alunos\\" + foto.getSubmittedFileName());
            
            //para guardar num disco de rede com IP
             File f = new File("\\\\192.168.0.4\\public\\" + foto.getSubmittedFileName());

            /*
            PARA GUARDAR NUMA PASTA DENTRO DO PROJECTO BASTA FAZER
            String path ="/fotos";
            File f = new File(path);
            if(!f.exists()){
              f.mkdir();
            }
             */
            f.createNewFile();
            FileOutputStream out = new FileOutputStream(f);

            byte[] buffer = new byte[1024 * 1024 * 100];

            int length;

            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            out.close();
            in.close();

            //Conteudo para a o campo foto da tabela aluno. Carrega uma string com o nome e extensao do ficheiro
            aluno.setFotoAluno(foto.getSubmittedFileName());

            //Conteudo em byte do ficheiro. Guarda o conteudo em bytes num campo da tabela.            
            byte[] content = IOUtils.toByteArray(foto.getInputStream());
            aluno.setConteudoFoto(content);

            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("path", f.getAbsolutePath());
            uploaded = true;

            FacesMessage msg = new FacesMessage("Ficheito", "\t\t" + f.getName() + "\t" + "\t" + "carregado com sucesso");
            FacesContext.getCurrentInstance().addMessage(null, msg);

        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }

    }

    public void fileUpload(FileUploadEvent event) {
        try {

            //Cria um objeto do tipo UploadedFile, para receber o ficheiro do evento
            UploadedFile arq = event.getFile();

            //transformar a imagem em bytes para guardar na base de dados  
            byte[] foto = IOUtils.toByteArray(arq.getInputstream());

            aluno.setConteudoFoto(foto);
            aluno.setFotoAluno(arq.getFileName());

            //para guardar o ficheiro num pasta local
            InputStream in = new BufferedInputStream(arq.getInputstream());
            File file = new File("D://fotos_alunos//" + arq.getFileName());

          
            FileOutputStream fout = new FileOutputStream(file);
            while (in.available() != 0) {
                fout.write(in.read());
            }
            fout.close();
          
            FacesMessage msg = new FacesMessage("Ficheiro:", arq.getFileName() + "Carregado com sucesso");
            FacesContext.getCurrentInstance().addMessage(null, msg);

        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }

    }

    public void salvar() {
        alunoFacade.create(aluno);
        aluno = new Aluno();
        FacesMessage message = new FacesMessage("Dados guardados com sucesso");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public List<Aluno> getAlunos() {
        System.out.println(">>>>>> Imprimir Lista <<<<<<<<");
        alunos = alunoFacade.findAll();
        return alunos;
    }

    public void setAlunos(List<Aluno> alunos) {
        this.alunos = alunos;
    }
    
    
     public  void ftpUpload() {
        FTPClient client = new FTPClient();

        FileInputStream fis = null;
        try {

            client.connect("192.168.0.13");
            client.login("Admin", "Vpie.2014+");
            client.enterLocalPassiveMode();
            client.changeWorkingDirectory("/home");
            System.out.println("Replay String" + client.getReplyString());
            
            int reply = client.getReplyCode();
            
          /*
            
              String filename = "d:/SQLiteJava.txt";
            fis = new FileInputStream(filename);
            */
            
            File file = new File("/home/"+foto.getSubmittedFileName());
            String filename = file.getName();
            
            fis = new FileInputStream(filename);
            
            if (FTPReply.isPositiveCompletion(reply)) {
                client.storeFile(filename, fis);
            } else {
                System.out.println("Can't upload to th FTP Server");

            }
            client.logout();
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        } finally {

            try {
                if (client.isConnected()) {
                    client.disconnect();
//                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

}
