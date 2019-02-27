package br.com.crawler.model;

import lombok.*;

import java.io.Serializable;

/**
 * @author Elvis Fernandes on 24/02/19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dados implements Serializable {

    private Long cep;
    private Long numeroImovel;
    private String tecnologia;
    private String linhas;
    private String vinteMb;
    private String cinquentaMb;
    private String trezentosMb;
}
