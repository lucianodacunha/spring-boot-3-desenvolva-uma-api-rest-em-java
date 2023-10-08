package io.github.lucianodacunha.api.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Para que o Spring reconheça a classe como um Controller,
 * a classe deve ser anotada como um RestController.
 */
@RestController
/**
 * Mapeamento da url.
 */
@RequestMapping("/hello")
public class HelloController {

    /**
     * Mapeamento do método que será executado.
     * @return
     */
    @GetMapping
    public String helloWorld(){
        System.out.println("get recebido...");
        return "E aí mundão, firmeza?!";
    }
}
