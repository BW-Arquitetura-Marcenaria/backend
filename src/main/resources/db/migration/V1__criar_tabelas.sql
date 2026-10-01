-- =====================================================
-- Banco de dados
-- =====================================================
CREATE
DATABASE IF NOT EXISTS bw_marcenaria;

USE bw_marcenaria;

-- =====================================================
-- Tabela usuario
-- =====================================================
CREATE TABLE usuario
(
    id              INT AUTO_INCREMENT PRIMARY KEY,
    tipo_cliente    VARCHAR(20)  NOT NULL,
    nome            VARCHAR(100) NOT NULL,
    sobrenome       VARCHAR(100) NOT NULL,
    cpf             VARCHAR(20),
    cnpj            VARCHAR(20),
    data_nascimento DATE,
    email           VARCHAR(150) NOT NULL,
    telefone        VARCHAR(20)  NOT NULL,
    etapa_funil     VARCHAR(20) DEFAULT 'Novo Lead',
    data_cadastro   TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_usuario_tipo_cliente
        CHECK (tipo_cliente IN ('Pessoa Fisica', 'Empresa')),

    CONSTRAINT chk_usuario_etapa_funil
        CHECK (
            etapa_funil IN (
                            'Novo Lead',
                            'Em Qualificacao',
                            'Qualificado',
                            'Apresentacao',
                            'Em Negociacao',
                            'Fechado'
                )
            )
)

-- =====================================================
-- Tabela projetos
-- =====================================================
CREATE TABLE projetos
(
    id             INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id     INT          NOT NULL,
    titulo_projeto VARCHAR(150) NOT NULL,
    valor_total    DECIMAL(10, 2),
    status         VARCHAR(20) DEFAULT 'Planejamento',

    CONSTRAINT chk_projetos_status
        CHECK (
            status IN (
                       'Planejamento',
                       'Em Execucao',
                       'Concluido',
                       'Cancelado'
                )
            ),

    CONSTRAINT fk_projetos_cliente
        FOREIGN KEY (cliente_id)
            REFERENCES usuario (id)
            ON DELETE CASCADE
            ON UPDATE CASCADE
)


-- =====================================================
-- Tabela calendario_eventos
-- =====================================================
CREATE TABLE calendario_eventos
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    projeto_id  INT         NOT NULL,
    tipo_evento VARCHAR(20) NOT NULL,
    data_inicio TIMESTAMP   NOT NULL,
    data_fim    TIMESTAMP   NOT NULL,
    observacoes TEXT,

    CONSTRAINT chk_calendario_tipo_evento
        CHECK (
            tipo_evento IN (
                            'Entrega',
                            'Montagem',
                            'Visita'
                )
            ),

    CONSTRAINT fk_calendario_projeto
        FOREIGN KEY (projeto_id)
            REFERENCES projetos (id)
            ON DELETE CASCADE
            ON UPDATE CASCADE
)


-- =====================================================
-- Tabela chatbot_logs
-- =====================================================
CREATE TABLE chatbot_logs
(
    id               INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id       INT,
    mensagem_usuario TEXT NOT NULL,
    resposta_ia      TEXT NOT NULL,
    data_interacao   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_chatbot_cliente
        FOREIGN KEY (cliente_id)
            REFERENCES usuario (id)
            ON DELETE SET NULL
            ON UPDATE CASCADE
)


-- =====================================================
-- Tabela fluxo_financeiro
-- =====================================================
CREATE TABLE fluxo_financeiro
(
    id             INT AUTO_INCREMENT PRIMARY KEY,
    projeto_id     INT            NOT NULL,
    tipo_transacao VARCHAR(10)    NOT NULL,
    valor          DECIMAL(10, 2) NOT NULL,
    data_transacao DATE           NOT NULL,
    categoria      VARCHAR(100),
    descricao      TEXT,
    status         VARCHAR(20) DEFAULT 'Pendente',

    CONSTRAINT chk_financeiro_tipo
        CHECK (
            tipo_transacao IN (
                               'Receita',
                               'Despesa'
                )
            ),

    CONSTRAINT chk_financeiro_status
        CHECK (
            status IN (
                       'Pendente',
                       'Pago',
                       'Atrasado',
                       'Cancelado'
                )
            ),

    CONSTRAINT fk_financeiro_projeto
        FOREIGN KEY (projeto_id)
            REFERENCES projetos (id)
            ON DELETE CASCADE
            ON UPDATE CASCADE
)


-- =====================================================
-- Tabela rastreabilidade_madeira
-- =====================================================
CREATE TABLE rastreabilidade_madeira
(
    id            INT AUTO_INCREMENT PRIMARY KEY,
    projeto_id    INT          NOT NULL,
    lote          VARCHAR(100) NOT NULL,
    tipo_madeira  VARCHAR(100) NOT NULL,
    fornecedor    VARCHAR(150) NOT NULL,
    origem        VARCHAR(150),
    certificacoes VARCHAR(255),
    data_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_madeira_projeto
        FOREIGN KEY (projeto_id)
            REFERENCES projetos (id)
            ON DELETE CASCADE
            ON UPDATE CASCADE
)

-- =====================================================
-- Tabela simulacoes_ia
-- =====================================================
CREATE TABLE simulacoes_ia
(
    id                INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id        INT         NOT NULL,
    descricao_prompt  TEXT        NOT NULL,
    ambiente          VARCHAR(20) NOT NULL,
    estilo_desejado   VARCHAR(20) NOT NULL,
    acabamento        VARCHAR(100),
    imagem_gerada_url VARCHAR(255),
    data_simulacao    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_simulacoes_ambiente
        CHECK (
            ambiente IN (
                         'Cozinha',
                         'Quarto',
                         'Sala',
                         'Banheiro',
                         'Escritorio',
                         'Outro'
                )
            ),

    CONSTRAINT chk_simulacoes_estilo
        CHECK (
            estilo_desejado IN (
                                'Moderno',
                                'Rustico',
                                'Minimalista',
                                'Industrial',
                                'Classico',
                                'Outro'
                )
            ),

    CONSTRAINT fk_simulacoes_cliente
        FOREIGN KEY (cliente_id)
            REFERENCES usuario (id)
            ON DELETE CASCADE
            ON UPDATE CASCADE
)

-- =====================================================
-- Tabela tabela_precos_referencia
-- =====================================================
CREATE TABLE tabela_precos_referencia
(
    id               INT AUTO_INCREMENT PRIMARY KEY,
    item_servico     VARCHAR(150)   NOT NULL,
    preco_referencia DECIMAL(10, 2) NOT NULL,
    descricao        TEXT,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)