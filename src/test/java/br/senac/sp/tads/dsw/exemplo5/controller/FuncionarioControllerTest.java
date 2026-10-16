package br.senac.sp.tads.dsw.exemplo5.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import br.senac.sp.tads.dsw.exemplo5.model.Departamento;
import br.senac.sp.tads.dsw.exemplo5.model.Funcionario;
import br.senac.sp.tads.dsw.exemplo5.repository.DepartamentoRepository;
import br.senac.sp.tads.dsw.exemplo5.repository.FuncionarioRepository;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional 
public class FuncionarioControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Departamento departamentoPadrao;

    @BeforeEach
    void setUp() {
        Departamento departamento = new Departamento();
        departamento.setNome("Desenvolvimento");
        departamento.setOrcamento(200000.00);
        departamentoPadrao = departamentoRepository.save(departamento);
    }

    @Test
    void deveCriarFuncionarioComSucesso() throws Exception {

                //1 Criar um objeto que queremos enviar
        Funcionario funcionario = new Funcionario();
        funcionario.setNome("Ednaldo");
        funcionario.setDataContratacao(LocalDate.of(2026, 3, 10));
        funcionario.setDepartamento(departamentoPadrao);
        funcionario.setTrabalhoRemoto(true);

        //2 Converter objeto JAVA para JSON
        String json = objectMapper.writeValueAsString(funcionario); 

        //3 Enviar POST para API
        mockMvc.perform(post("/api/funcionarios")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))

        //4 Checar se a resposta é oque foi enviado
            .andExpect(status().isCreated()) //201
            .andExpect(jsonPath("$.id").exists()) //verifica se o ID existe
            .andExpect(jsonPath("$.nome").value("Ednaldo")) //Verifica o nome
            .andExpect(jsonPath("$.dataContratacao").value("2026-03-10"))
            .andExpect(jsonPath("$.trabalhoRemoto").value(true));  //Verifica o valor


    }

    @Test
    void deveRetornarErro400AoCriarFuncionarioComDataFutura() throws Exception {

                        //1 Criar um objeto que queremos enviar
        Funcionario funcionario = new Funcionario();
        funcionario.setNome("Ednaldo");
        funcionario.setDataContratacao(LocalDate.of(2027, 3, 10));
        funcionario.setDepartamento(departamentoPadrao);
        funcionario.setTrabalhoRemoto(true);

        //2 Converter objeto JAVA para JSON
        String json = objectMapper.writeValueAsString(funcionario); 

        //3 Enviar POST para API
        mockMvc.perform(post("/api/funcionarios")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))

            .andExpect(status().isBadRequest());

    }

    @Test
    void deveListarTodosOsFuncionarios() throws Exception {

        Funcionario funcionario = new Funcionario();
        funcionario.setNome("Ednaldo");
        funcionario.setDataContratacao(LocalDate.of(2026, 3, 10));
        funcionario.setDepartamento(departamentoPadrao);
        funcionario.setTrabalhoRemoto(true);
        funcionarioRepository.save(funcionario);

        mockMvc.perform(get("/api/funcionarios"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0]").exists());

    }

    @Test
    void deveBuscarFuncionarioPorId() throws Exception {

        Funcionario funcionario = new Funcionario();
        funcionario.setNome("Deiverson");
        funcionario.setDataContratacao(LocalDate.of(2026, 3, 10));
        funcionario.setDepartamento(departamentoPadrao);
        funcionario.setTrabalhoRemoto(true);
        funcionarioRepository.save(funcionario);

         mockMvc.perform(get("/api/funcionarios/" + funcionario.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(funcionario.getId()))
        .andExpect(jsonPath("$.nome").value("Deiverson")) //Verifica o nome
        .andExpect(jsonPath("$.dataContratacao").value("2026-03-10"))
        .andExpect(jsonPath("$.trabalhoRemoto").value(true));  


    }

    @Test
    void deveAtualizarFuncionario() throws Exception {

        Funcionario funcionario = new Funcionario();
        funcionario.setNome("Deiverson");
        funcionario.setDataContratacao(LocalDate.of(2026, 3, 10));
        funcionario.setDepartamento(departamentoPadrao);
        funcionario.setTrabalhoRemoto(true);
        Funcionario funcionarioSalvo = funcionarioRepository.save(funcionario);

        funcionarioSalvo.setNome("Alisson");
        funcionarioSalvo.setDataContratacao(LocalDate.of(2025, 11, 22));
        funcionarioSalvo.setTrabalhoRemoto(false);

        mockMvc.perform(get("/api/funcionarios/" + funcionarioSalvo.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nome").value("Alisson")) //Verifica o nome
        .andExpect(jsonPath("$.dataContratacao").value("2025-11-22"))
        .andExpect(jsonPath("$.trabalhoRemoto").value(false)); 

    }

    @Test 
    void deveApagarFuncionario() throws Exception {

        Funcionario funcionario = new Funcionario();
        funcionario.setNome("Deiverson");
        funcionario.setDataContratacao(LocalDate.of(2026, 3, 10));
        funcionario.setDepartamento(departamentoPadrao);
        funcionario.setTrabalhoRemoto(true);
        Funcionario funcionarioSalvo = funcionarioRepository.save(funcionario);

        mockMvc.perform(delete("/api/funcionarios/" + funcionarioSalvo.getId()))
        .andExpect(status().isNoContent());

         mockMvc.perform(delete("/api/funcionarios/" + funcionarioSalvo.getId()))
        .andExpect(status().isNotFound());

    }
    
}
