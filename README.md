# Spring Boot 3: desenvolva uma API Rest em Java


## Aula 1: Criação do projeto

- Criar um projeto Spring Boot utilizando o site do Spring Initializr;
- Importar o projeto no IntelliJ e executar uma aplicação Spring Boot pela classe contendo o método main;
- Criar uma classe Controller e mapear uma URL nela utilizando as anotações @RestController e @RequestMapping;
- Realizar uma requisição de teste no browser acessando a URL mapeada no Controller.

## Aula 2: Requisições POST

- Mapear requisições POST em uma classe Controller;
- Enviar requisições POST para a API utilizando o Insomnia;
- Enviar dados para API no formato JSON;
- Utilizar a anotação @RequestBody para receber os dados do corpo da requisição em um parâmetro no Controller;
- Utilizar o padrão DTO (Data Transfer Object), via Java Records, para representar os dados recebidos em uma requisição POST.

### Habilitando diferentes origens no Spring Boot

Para configurar o CORS e habilitar uma origem específica para consumir a API, basta criar uma classe de configuração como a seguinte:

```
@Configuration
public class CorsConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:3000")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "TRACE", "CONNECT");
    }
}

```

`allowedOrigins` seria o endereço da aplicação Front-end e `.allowedMethods` os métodos que serão permitidos para serem executados. Com isso, você poderá consumir a sua API sem problemas a partir de uma aplicação Front-end.

## Aula 3: Spring Data JPA

- Adicionar novas dependências no projeto;
- Mapear uma entidade JPA e criar uma interface Repository para ela;
- Utilizar o Flyway como ferramenta de Migrations do projeto;
- Realizar validações com Bean Validation utilizando algumas de suas anotações, como a @NotBlank.

Em alguns projetos em Java, dependendo da tecnologia escolhida, é comum encontrarmos classes que seguem o padrão DAO, utilizado para isolar o acesso aos dados. Entretanto, neste curso utilizaremos um outro padrão, conhecido como Repository.

Mas aí podem surgir algumas dúvidas: qual a diferença entre as duas abordagens e o porquê dessa escolha?

### Padrão DAO

O padrão de projeto DAO, conhecido também por Data Access Object, é utilizado para persistência de dados, onde seu principal objetivo é separar regras de negócio de regras de acesso a banco de dados. Nas classes que seguem esse padrão, isolamos todos os códigos que lidam com conexões, comandos SQLs e funções diretas ao banco de dados, para que assim tais códigos não se espalhem por outros pontos da aplicação, algo que dificultaria a manutenção do código e também a troca das tecnologias e do mecanismo de persistência.

#### Implementação

Vamos supor que temos uma tabela de produtos em nosso banco de dados. A implementação do padrão DAO seria o seguinte:

Primeiro, seria necessário criar uma classe básica de domínio Produto:

```
public class Produto {
    private Long id;
    private String nome;
    private BigDecimal preco;
    private String descricao;

    // construtores, getters e setters
}
```

Em seguida, precisaríamos criar a classe ProdutoDao, que fornece operações de persistência para a classe de domínio Produto:

```
public class ProdutoDao {

    private final EntityManager entityManager;

    public ProdutoDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void create(Produto produto) {
        entityManager.persist(produto);
    }

    public Produto read(Long id) {
        return entityManager.find(Produto.class, id);
    }

    public void update(Produto produto) {
        entityManger.merge(produto);
    }

    public void remove(Produto produto) {
        entityManger.remove(produto);
   }

}
```

No exemplo anterior foi utilizado a JPA como tecnologia de persistência dos dados da aplicação.

#### Padrão Repository

De acordo com o famoso livro Domain-Driven Design, de Eric Evans:

>> O repositório é um mecanismo para encapsular armazenamento, recuperação e comportamento de pesquisa, que emula uma coleção de objetos.

Simplificando, um repositório também lida com dados e oculta consultas semelhantes ao DAO. No entanto, ele fica em um nível mais alto, mais próximo da lógica de negócios de uma aplicação. Um repositório está vinculado à regra de negócio da aplicação e está associado ao agregado dos seus objetos de negócio, retornando-os quando preciso.

Só que devemos ficar atentos, pois assim como no padrão DAO, regras de negócio que estão envolvidas com processamento de informações não devem estar presentes nos repositórios. Os repositórios não devem ter a responsabilidade de tomar decisões, aplicar algoritmos de transformação de dados ou prover serviços diretamente a outras camadas ou módulos da aplicação. Mapear entidades de domínio e prover as funcionalidades da aplicação são responsabilidades muito distintas.

Um repositório fica entre as regras de negócio e a camada de persistência:

1. Ele provê uma interface para as regras de negócio onde os objetos são acessados como em uma coleção;
2. Ele usa a camada de persistência para gravar e recuperar os dados necessários para persistir e recuperar os objetos de negócio.

Portanto, é possível até utilizar um ou mais DAOs em um repositório.

#### Por que o padrão repository ao invés do DAO utilizando Spring?

O padrão de repositório incentiva um design orientado a domínio, fornecendo uma compreensão mais fácil do domínio e da estrutura de dados. Além disso, utilizando o repository do Spring não temos que nos preocupar em utilizar diretamente a API da JPA, bastando apenas criar os métodos que o Spring cria a implementação em tempo de execução, deixando o código muito mais simples, menor e legível.

#### Bean Validation

o Bean Validation é composto por diversas anotações que devem ser adicionadas nos atributos em que desejamos realizar as validações. Vimos algumas dessas anotações, como a @NotBlank, que indica que um atributo do tipo String não pode ser nulo e nem vazio.

- [Docs](https://jakarta.ee/specifications/bean-validation/3.0/jakarta-bean-validation-spec-3.0.html#builtinconstraints)

## Aula 4: Requisições GET

- Utilizar a anotação @GetMapping para mapear métodos em Controllers que produzem dados;
- Utilizar a interface Pageable do Spring para realizar consultas com paginação;
- Controlar a paginação e a ordenação dos dados devolvidos pela API com os parâmetros page, size e sort;
- Configurar o projeto para que os comandos SQL sejam exibidos no console.

### DTO's x Entidades

Estamos utilizando DTOs para representar os dados que recebemos e devolvemos pela API, mas você provavelmente deve estar se perguntando “Por que ao invés de criar um DTO não devolvemos diretamente a entidade JPA no Controller?”. Para fazer isso, bastaria alterar o método listar no Controller para:


```
@GetMapping
public List<Medico> listar() {
    return repository.findAll();
}
```

Desse jeito o código ficaria mais enxuto e não precisaríamos criar o DTO no projeto. Mas, será que isso realmente é uma boa ideia?


#### Os problemas de receber/devolver entidades JPA

De fato é muito mais simples e cômodo não utilizar DTOs e sim lidar diretamente com as entidades JPA nos controllers. Porém, essa abordagem tem algumas desvantagens, inclusive causando vulnerabilidade na aplicação para ataques do tipo Mass Assignment.

Um dos problemas consiste no fato de que, ao retornar uma entidade JPA em um método de um Controller, o Spring vai gerar o JSON contendo todos os atributos dela, sendo que nem sempre esse é o comportamento que desejamos.

Eventualmente podemos ter atributos que não desejamos que sejam devolvidos no JSON, seja por motivos de segurança, no caso de dados sensíveis, ou mesmo por não serem utilizados pelos clientes da API.

#### Utilização da anotação @JsonIgnore

Nessa situação, poderíamos utilizar a anotação @JsonIgnore, que nos ajuda a ignorar certas propriedades de uma classe Java quando ela for serializada para um objeto JSON.

Sua utilização consiste em adicionar a anotação nos atributos que desejamos ignorar quando o JSON for gerado. Por exemplo, suponha que em um projeto exista uma entidade JPA Funcionario, na qual desejamos ignorar o atributo salario:

```
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity(name = "Funcionario")
@Table(name = "funcionarios")
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;

    @JsonIgnore
    private BigDecimal salario;

    //restante do código omitido…
}
```

No exemplo anterior, o atributo salario da classe Funcionario não será exibido nas respostas JSON e o problema estaria solucionado.

Entretanto, pode acontecer de existir algum outro endpoint da API na qual precisamos enviar no JSON o salário dos funcionários, sendo que nesse caso teríamos problemas, pois com a anotação @JsonIgnore tal atributo nunca será enviado no JSON, e ao remover a anotação o atributo sempre será enviado. Perdemos, com isso, a flexibilidade de controlar quando determinados atributos devem ser enviados no JSON e quando não.

#### DTO

O padrão DTO (Data Transfer Object) é um padrão de arquitetura que era bastante utilizado antigamente em aplicações Java distribuídas (arquitetura cliente/servidor) para representar os dados que eram enviados e recebidos entre as aplicações cliente e servidor.

O padrão DTO pode (e deve) ser utilizado quando não queremos expor todos os atributos de alguma entidade do nosso projeto, situação igual a dos salários dos funcionários mostrado no exemplo de código anterior. Além disso, com a flexibilidade e a opção de filtrar quais dados serão transmitidos, podemos poupar tempo de processamento.

#### Loop infinito causando StackOverflowError

Outro problema muito recorrente ao se trabalhar diretamente com entidades JPA acontece quando uma entidade possui algum autorrelacionamento ou relacionamento bidirecional. Por exemplo, considere as seguintes entidades JPA:

```
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity(name = "Produto")
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;

    @ManyToOne
    @JoinColumn(name = “id_categoria”)
    private Categoria categoria;

    //restante do código omitido…
}

@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity(name = "Categoria")
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;

    @OneToMany(mappedBy = “categoria”)
    private List<Produto> produtos = new ArrayList<>();

    //restante do código omitido…
}
```

Ao retornar um objeto do tipo Produto no Controller, o Spring teria problemas para gerar o JSON desse objeto, causando uma exception do tipo StackOverflowError. Esse problema ocorre porque o objeto produto tem um atributo do tipo Categoria, que por sua vez tem um atributo do tipo List<Produto>, causando assim um loop infinito no processo de serialização para JSON.

Tal problema pode ser resolvido com a utilização da anotação @JsonIgnore ou com a utilização das anotações @JsonBackReference e @JsonManagedReference, mas também poderia ser evitado com a utilização de um DTO que representa apenas os dados que devem ser devolvidos no JSON.

## Aula 5: Requisições PUT e DELETE

- Mapear requisições PUT com a anotação @PutMapping;
- Escrever um código para atualizar informações de um registro no banco de dados;
- Mapear requisições DELETE com a anotação @DeleteMapping;
- Mapear parâmetros dinâmicos em URL com a anotação @PathVariable;
    Implementar o conceito de exclusão lógica com o uso de um atributo booleano.

### Mass Assignment Attack

Mass Assignment Attack ou Ataque de Atribuição em Massa, em português, ocorre quando um usuário é capaz de inicializar ou substituir parâmetros que não deveriam ser modificados na aplicação. Ao incluir parâmetros adicionais em uma requisição, sendo tais parâmetros válidos, um usuário mal-intencionado pode gerar um efeito colateral indesejado na aplicação.

O conceito desse ataque refere-se a quando você injeta um conjunto de valores diretamente em um objeto, daí o nome atribuição em massa, que sem a devida validação pode causar sérios problemas.

Vamos a um exemplo prático. Suponha que você tem o seguinte método, em uma classe Controller, utilizado para cadastrar um usuário na aplicação:

```
@PostMapping
@Transactional
public void cadastrar(@RequestBody @Valid Usuario usuario) {
    repository.save(usuario);
}
```

E a entidade JPA que representa o usuário:

```
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity(name = "Usuario")
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;
    private Boolean admin = false;

    //restante do código omitido…
}

Repare que o atributo admin da classe Usuario é inicializado como false, indicando que um usuário deve sempre ser cadastrado como não sendo um administrador. Porém, se na requisição for enviado o seguinte JSON:

{
    “nome” : “Rodrigo”,
    “email” : “rodrigo@email.com”,
    “admin” : true
}
```

O usuário será cadastrado com o atributo admin preenchido como true. Isso acontece porque o atributo admin enviado no JSON existe na classe que está sendo recebida no Controller, sendo considerado então um atributo válido e que será preenchido no objeto Usuario que será instanciado pelo Spring.

Então, como fazemos para prevenir esse problema?

#### Prevenção

O uso do padrão DTO nos ajuda a evitar esse problema, pois ao criar um DTO definimos nele apenas os campos que podem ser recebidos na API, e no exemplo anterior o DTO não teria o atributo admin.

Novamente, vemos mais uma vantagem de se utilizar o padrão DTO para representar os dados que chegam e saem da API.