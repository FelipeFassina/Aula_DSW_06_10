package br.senac.sp.tads.dsw.exemplo5.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import br.senac.sp.tads.dsw.exemplo5.model.Departamento;
import br.senac.sp.tads.dsw.exemplo5.repository.DepartamentoRepository;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest 
@AutoConfigureMockMvc 
@Transactional 

public class DepartamentoControllerTest {

    @Autowired 
    private MockMvc mockMvc;

    @Autowired
    private DepartamentoRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test 
    void deveCriarDepartamentoComSucesso()throws Exception {
        //1 Criar um objeto que queremos enviar
        Departamento departamento = new Departamento();
        departamento.setNome("Turma B - TADS");
        departamento.setOrcamento(3600.00);

        //2 Converter objeto JAVA para JSON
        String json = objectMapper.writeValueAsString(departamento); 

        //3 Enviar POST para API
        mockMvc.perform(post("/api/departamentos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))

        //4 Checar se a resposta é oque foi enviado
            .andExpect(status().isCreated()) //201
            .andExpect(jsonPath("$.id").exists()) //verifica se o ID existe
            .andExpect(jsonPath("$.nome").value("Turma B - TADS")) //Verifica o nome
            .andExpect(jsonPath("$.orcamento").value(3600.00));  //Verifica o valor
    }

    @Test 
    void deveListarTodosOsDepartamentos() throws Exception {
        Departamento departamento = new Departamento();
        departamento.setNome("Turma B - TADS");
        departamento.setOrcamento(3600.00);
        repository.save(departamento);

        mockMvc.perform(get("/api/departamentos"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void deveBuscarDepartamentoPorId() throws Exception {
        Departamento departamento = new Departamento();
        departamento.setNome("Turma B - TADS");
        departamento.setOrcamento(3600.00);
        repository.save(departamento);

        mockMvc.perform(get("/api/departamentos/" + departamento.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(departamento.getId()))
        .andExpect(jsonPath("$.nome").value("Turma B - TADS")) //Verifica o nome
        .andExpect(jsonPath("$.orcamento").value(3600.00));  //Verifica o valor

    }

    @Test
    void deveAtualizarDepartaamento() throws Exception{
        Departamento departamento = new Departamento();
        departamento.setNome("Turma B - TADS");
        departamento.setOrcamento(3600.00);
        Departamento departamentoSalvo = repository.save(departamento);

        departamentoSalvo.setNome("Turma do Luiz");

        String json = objectMapper.writeValueAsString(departamentoSalvo); 

        mockMvc.perform(put("/api/departamentos/"+ departamentoSalvo.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))

            .andExpect(status().isOk()) //200
            .andExpect(jsonPath("$.nome").value("Turma do Luiz"))
            .andExpect(jsonPath("$.orcamento").value(3600.00));


    }

    @Test 
    void deveApagarDepartamento() throws Exception {
        Departamento departamento = new Departamento();
        departamento.setNome("Turma B - TADS");
        departamento.setOrcamento(3600.00);
        Departamento departamentoSalvo = repository.save(departamento);

        mockMvc.perform(delete("/api/departamentos/"+departamentoSalvo.getId()))
                                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/departamentos/"+departamentoSalvo.getId()))
                                .andExpect(status().isNotFound());

    }

}
