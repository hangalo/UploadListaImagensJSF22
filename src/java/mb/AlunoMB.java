/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mb;

import entities.Aluno;
import facade.AlunoFacade;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.Part;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author informatica
 */
@Named(value = "alunoMB")
@SessionScoped
public class AlunoMB implements Serializable {

    /**
     * Creates a new instance of AlunoMB
     */
    // private Part foto;
    private Part foto;
    private Aluno aluno = new Aluno();
    private List<Aluno> alunos = new ArrayList<>();

    @Inject
    AlunoFacade alunoFacade;

    public AlunoMB() {
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
            Scanner s = new Scanner(foto.getInputStream());
            String fileContent = s.useDelimiter("\\A").next();
            s.close();
            //   System.out.println(fileContent);
            aluno.setFotoAluno(foto.getSubmittedFileName());

            byte[] content = IOUtils.toByteArray(foto.getInputStream());
            aluno.setConteudoFoto(content);
            Files.write(Paths.get("c:\\fotos_alunos\\" + foto.getSubmittedFileName()), fileContent.getBytes(), StandardOpenOption.CREATE);

        } catch (IOException ioe) {
            ioe.printStackTrace(System.out);
        }
    }

    public void salvar(ActionEvent event) {
        alunoFacade.create(aluno);
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
