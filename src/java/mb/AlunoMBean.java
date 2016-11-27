/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mb;

import entities.Aluno;
import facade.AlunoFacade;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;
import org.apache.commons.io.IOUtils;

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
    private List<Aluno> alunos = new ArrayList<>();
    private boolean carregado;
    @EJB
    AlunoFacade alunoFacade;

    public AlunoMBean() {
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

    public void upload() {
        try {

            /*Definicao do path externo equivalente a 
             1 - "C:\\fotos\\" -> no Windows;
             2 - /fotos 
             ou ainda /Users/fotos -> No Mac OS
             3 - /usr/fotos - > no Linux           
            
             Pode-se também buscar a basta do utilizador mendiante
             capturando o valor user.dir da propriedade do sistema fazendo:
            
             String pastaUtilizador = System.getProperty("user.dir");
            
             */
            //Este definicao torna o sistema independente da pataforma 
            //em termos de nomeclatura par ao path
            //       String separador = System.getProperty("file.separator");
            //    String caminhoAbsoluto = "C:" + separador + "fotos_alunos" + separador + foto.getSubmittedFileName();
            //   System.out.println(">>>>>>>>>>>>>>>>>>>>>>>" + caminhoAbsoluto);

            /*Crea um objecto do tipo InputStream chamado entrada.
             O metodo getInputStream da Interface Part retorna o conteudo 
             de um ojecto do tipo Part
             */
            InputStream entrada = foto.getInputStream();

            //    File ficheiro = new File(caminhoAbsoluto + foto.getSubmittedFileName());
            File ficheiro = new File("D:\\fotos_alunos\\" + foto.getSubmittedFileName());
            /*
             Equivante a escrever
                  
             //  File ficheiro = new File("C:\\fotos\\"+foto.getSubmittedFileName());
             */
            //Se a pasta nao existe a cria
            if (!ficheiro.exists()) {

                ficheiro.mkdir();
            }
            /* o metodo createNewFile, cria um novo arquivo vazio nomeado por este 
             caminho abstrato se e somente se um arquivo com esse nome ainda não existe.*/
            ficheiro.createNewFile();

            FileOutputStream saida = new FileOutputStream(ficheiro);

            byte[] buffer = new byte[1024 * 1024 * 100];
            int length;

            while ((length = entrada.read(buffer)) > 0) {
                saida.write(buffer, 0, length);

            }
            saida.close();
            entrada.close();
            // nome do ficheiro a guardar na tabela da base de dados
            aluno.setFotoAluno(foto.getSubmittedFileName());

            // conteudo o ficheiro a ser guardado na tabela da base de dados -> nao e visto como boa pratica
            byte[] conteudo = IOUtils.toByteArray(foto.getInputStream());
            aluno.setConteudoFoto(conteudo);

            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("path", ficheiro.getAbsolutePath());

            FacesMessage msg = new FacesMessage("Ficheito", "" + ficheiro.getName() + "" + "" + "Carregado com sucesso");
            FacesContext.getCurrentInstance().addMessage(null, msg);

        } catch (IOException ioex) {
            ioex.printStackTrace(System.out);
        }

    }

    public void salvar() {
        alunoFacade.create(aluno);
        aluno = new Aluno();
        FacesMessage message = new FacesMessage("Dados guardados com sucesso");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public List<Aluno> getAlunos() {
        if (alunos == null) {
            alunos = alunoFacade.findAll();
        }
        return alunos;
    }

    public void setAlunos(List<Aluno> alunos) {
        this.alunos = alunos;
    }

}
