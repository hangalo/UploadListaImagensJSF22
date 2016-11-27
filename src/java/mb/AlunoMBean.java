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
    private boolean uploaded;
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

    public void doUpload() {
        try {

            InputStream in = foto.getInputStream();
            File f = new File("D:\\fotos_alunos\\" + foto.getSubmittedFileName());
             
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

    public void salvar() {
        alunoFacade.create(aluno);
        aluno = new Aluno();
        FacesMessage message = new FacesMessage("Dados guardados com sucesso");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public List<Aluno> getAlunos() {
        if(alunos== null){
        alunos = alunoFacade.findAll();
        }
        return alunos;
    }

    public void setAlunos(List<Aluno> alunos) {
        this.alunos = alunos;
    }

}
