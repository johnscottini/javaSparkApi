import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.UsuarioDTOInput;
import org.example.service.UsuarioService;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import static junit.framework.TestCase.assertEquals;

public class ServiceTest {

    UsuarioService usuarioService = new UsuarioService();
    @Test
    public void testInsercaoUser() throws JsonProcessingException {
        ObjectMapper modelMapper = new ObjectMapper();
        String jsonRequestBody = "{\"id\": 1, \"nome\": \"joao\", \"senha\": \"123\"}";
        UsuarioDTOInput usuario = modelMapper.readValue(jsonRequestBody, UsuarioDTOInput.class);
        usuarioService.inserirUsuario(usuario);
        assertEquals(1, usuarioService.listarUsuarios().size());
    }

    @Test
    public void testListagemUsers() throws IOException {
        URL url = new URL("http://localhost:4567/usuarios");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        int responseCode = connection.getResponseCode();
        assertEquals(200, responseCode);
    }

    @Test
    public void testInserirUsuarioApi() throws IOException {
        URL url = new URL("https://randomuser.me/api/?inc=name,id,login");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder responseStringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseStringBuilder.append(line);
            }
            JsonNode jsonNode = objectMapper.readTree(responseStringBuilder.toString());
            JsonNode resultsNode = jsonNode.get("results").get(0);
            String firstName = resultsNode.get("name").get("first").asText();
            String lastName = resultsNode.get("name").get("last").asText();
            String fullName = firstName+ " "+lastName;
            int id = new Random().nextInt();
            String password = resultsNode.get("login").get("password").asText();
            UsuarioDTOInput userInput = new UsuarioDTOInput();
            userInput.setId(id);
            userInput.setNome(fullName);
            userInput.setSenha(password);

            URL myUrl = new URL("http://localhost:4567/usuarios");
            HttpURLConnection myConnection = (HttpURLConnection) myUrl.openConnection();
            myConnection.setRequestMethod("POST");
            String json = objectMapper.writeValueAsString(userInput);
            myConnection.setDoOutput(true);
            myConnection.getOutputStream().write(json.getBytes());
            int responseCode = myConnection.getResponseCode();
            assertEquals(201, responseCode);

        }



    }

}
