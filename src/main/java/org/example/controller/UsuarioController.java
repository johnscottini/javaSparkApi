package org.example.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.UsuarioDTOInput;
import org.example.dto.UsuarioDTOOutput;
import org.example.model.Usuario;
import org.example.service.UsuarioService;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.NoSuchElementException;

import static spark.Spark.*;

public class UsuarioController {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UsuarioService userService = new UsuarioService();
    public UsuarioController() {
        respostasRequisicoes();
    }
    public void respostasRequisicoes() {
        get("/usuarios", ((request, response) -> {
            response.type("application/json");
            response.status(200);
           try {

               String json = objectMapper.writeValueAsString(userService.listarUsuarios());
               return json;
           } catch (ArrayIndexOutOfBoundsException e) {
               response.status(HttpURLConnection.HTTP_BAD_REQUEST);
               return getErrorMessageAsJson(e.toString());
           }
        }));

        get("/usuarios/:id", ((request, response) -> {
            response.type("application/json");
            response.status(200);
            try{
                int userId = Integer.parseInt(request.params("id"));
                UsuarioDTOOutput userOutput = userService.buscarUsuario(userId);
                String json = objectMapper.writeValueAsString(userOutput);
                return json;
            } catch (NumberFormatException e) {
                response.status(HttpURLConnection.HTTP_BAD_REQUEST);
                return getErrorMessageAsJson("ID invalido, o valor deve ser um número inteiro.");
            } catch (NoSuchElementException e) {
                response.status(HttpURLConnection.HTTP_NOT_FOUND);
                return getErrorMessageAsJson(e.toString());
            }

        }));

        delete("/usuarios/:id", ((request, response) -> {
            response.type("application/json");
            response.status(200);
            try {
                int userId = Integer.parseInt(request.params("id"));
                UsuarioDTOOutput userOutput = userService.buscarUsuario(userId);
                userService.removerUsuario(userId);
                String json = objectMapper.writeValueAsString(userOutput);
                return "Usuário removido com sucesso";
            } catch (NoSuchElementException e) {
                    response.status(HttpURLConnection.HTTP_NOT_FOUND);
                    return getErrorMessageAsJson(e.toString());
            } catch (NumberFormatException e) {
                response.status(HttpURLConnection.HTTP_BAD_REQUEST);
                return getErrorMessageAsJson("ID invalido, o valor deve ser um número inteiro.");
            }
        }));

        post("/usuarios", (request, response) -> {
            String json = request.body();
            try {
                UsuarioDTOInput userInput = objectMapper.readValue(request.body(), UsuarioDTOInput.class);
                userService.inserirUsuario(userInput);
                response.status(201);
                response.type("application/json");
                return "Usuário incluído com sucesso";
            } catch (Exception e) {
                response.status(HttpURLConnection.HTTP_BAD_REQUEST);
                return getErrorMessageAsJson("Usuário inserido inválido.");
            }
        });

        put("/usuarios", ((request, response) -> {
            String json = request.body();
            try {
                response.status(200);
                response.type("application/json");
                UsuarioDTOInput userInput = objectMapper.readValue(json, UsuarioDTOInput.class);
                userService.alterarUsuario(userInput);
                return "Usuário alterado com sucesso.";
            }
            catch (Exception e) {
                response.status(HttpURLConnection.HTTP_BAD_REQUEST);
                return getErrorMessageAsJson("Alteração inválida.");
            }

        }));
    }

    private String getErrorMessageAsJson(String errorMessage) {
        return "{\"error\":\"" + errorMessage + "\"}";
    }
}
