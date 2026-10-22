package br.com.bw.backend.dto.response;

public record ContaAdminGetResponseDTO(
        EmpresaDados empresa,
        EnderecoDados endereco
) {
    public record EmpresaDados(
            String nome,
            String nomeFantasia,
            String responsaveis,
            String cnpj,
            String email,
            String telefone
    ) {}

    public record EnderecoDados(
            String cep,
            String logradouro,
            String numero,
            String complemento,
            String bairro,
            String cidade,
            String uf
    ) {}
}