package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.example.dto.UsuarioDTOInput;
import org.example.dto.UsuarioDTOOutput;
import org.example.model.Usuario;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Getter
public class UsuarioService {
    private final ObjectMapper objectMapper = new ObjectMapper();
  private final ModelMapper modelMapper = new ModelMapper();
  private final List<Usuario> listaUsuarios= new ArrayList<>();

   public List<UsuarioDTOOutput> listarUsuarios() {
       List<UsuarioDTOOutput> outputList = new ArrayList<>();
       for (Usuario user: listaUsuarios
            ) {
           UsuarioDTOOutput outputObj = modelMapper.map(user, UsuarioDTOOutput.class);
           outputList.add(outputObj);
       }
       if(outputList.size() != 0) {
           return outputList;
       } else {
           throw new ArrayIndexOutOfBoundsException("A lista de usuários está vazia.");
       }
   }

   public void inserirUsuario(UsuarioDTOInput usuarioDTOInput) {
       Usuario user = modelMapper.map(usuarioDTOInput, Usuario.class);
       if(user.getNome() == null || user.getSenha() == null) {
           throw new IllegalArgumentException("Os valores do usuário não podem ser nulos.");
       } else {
       listaUsuarios.add(user);
       }
   }

    public void alterarUsuario(UsuarioDTOInput usuarioDTOInput) {
        Usuario user = modelMapper.map(usuarioDTOInput, Usuario.class);
        int id = user.getId();
        for (Usuario editUser : listaUsuarios
             ) {
            if (editUser.getId() == id) {
                int index = listaUsuarios.indexOf(editUser);
                listaUsuarios.set(index, user);
            }
        }
    }

    public UsuarioDTOOutput buscarUsuario(int id) {
       Usuario foundUser = null;
        for (Usuario user: listaUsuarios
        ) {
            if(user.getId() == id) {
                foundUser = user;
            }
        }
        if(foundUser != null) {
            return modelMapper.map(foundUser, UsuarioDTOOutput.class);
        } else {
            throw new NoSuchElementException("Usuário não existe.");
        }

   }

    public void removerUsuario(int id) {
        Usuario foundUser = null;
        for (Usuario user: listaUsuarios
        ) {
            if(user.getId() == id) {
                foundUser = user;
            }
        }
        listaUsuarios.remove(foundUser);
    }
}
