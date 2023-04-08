import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Main {
    public static <Int> void main(String[] args) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(RequestConfig.custom().setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                .build()).build();
        HttpGet request = new HttpGet("https://api.nasa.gov/planetary/apod?api_key=ezB1gEBoRINhxjVMohnXeYegpxYJeOkqBJg8dvNX");
        try {
            CloseableHttpResponse response = httpClient.execute(request);
            String string = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
            ObjectMapper objectMapper = new ObjectMapper();
            Nasa nasa = objectMapper.readValue(string, new TypeReference<>() {
            });
            System.out.println(nasa);
            response = httpClient.execute(new HttpGet(nasa.url));
            byte[] fig = response.getEntity().getContent().readAllBytes();
            String[] urlParts = nasa.url.split("/");
            File file = new File(urlParts[urlParts.length - 1]);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(fig);
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
