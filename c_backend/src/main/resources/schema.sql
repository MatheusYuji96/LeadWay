-- ========================================================
-- CREATION SCRIPT - LEADWAY DATABASE (MySQL)
-- VERSÃO ATUALIZADA
-- ========================================================

CREATE DATABASE IF NOT EXISTS leadway
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE leadway;

-- ========================================================
-- RECRIAÇÃO DAS TABELAS
-- ATENÇÃO: APAGA TODOS OS DADOS EXISTENTES
-- ========================================================

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS preferencias_vendedor;
DROP TABLE IF EXISTS tarefa;
DROP TABLE IF EXISTS interacao;
DROP TABLE IF EXISTS contato;
DROP TABLE IF EXISTS lead_etapa_historico;
DROP TABLE IF EXISTS leads;
DROP TABLE IF EXISTS vendedor;
DROP TABLE IF EXISTS etapa;
DROP TABLE IF EXISTS endereco;
DROP TABLE IF EXISTS setor;

SET FOREIGN_KEY_CHECKS = 1;

-- ========================================================
-- 1. TABELA SETOR
-- ========================================================

CREATE TABLE setor (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(45) NOT NULL
) ENGINE=InnoDB;

-- ========================================================
-- 2. TABELA ENDERECO
-- ========================================================

CREATE TABLE endereco (
    id INT AUTO_INCREMENT PRIMARY KEY,
    rua VARCHAR(200) NOT NULL,
    cep VARCHAR(20) NOT NULL,
    numero VARCHAR(20) NOT NULL,
    bairro VARCHAR(100) NOT NULL,
    cidade VARCHAR(100) NOT NULL,
    estado VARCHAR(100) NOT NULL,
    pais VARCHAR(100) NOT NULL,
    complemento VARCHAR(100)
) ENGINE=InnoDB;

-- ========================================================
-- 3. TABELA ETAPA
-- ========================================================

CREATE TABLE etapa (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(50) NOT NULL,
    ordem TINYINT NOT NULL
) ENGINE=InnoDB;

-- ========================================================
-- 4. TABELA VENDEDOR
-- ========================================================

CREATE TABLE vendedor (
    id INT AUTO_INCREMENT PRIMARY KEY,
    lider_id INT,
    cod_vendedor VARCHAR(30),
    nome VARCHAR(190) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(50) NOT NULL,
    telefone CHAR(11) NOT NULL,
    regiao VARCHAR(100),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_vendedor_lider
        FOREIGN KEY (lider_id)
        REFERENCES vendedor(id)
        ON DELETE SET NULL
) ENGINE=InnoDB;

-- ========================================================
-- 5. TABELA LEADS
-- ========================================================

CREATE TABLE leads (
    id INT AUTO_INCREMENT PRIMARY KEY,

    setor_id INT NOT NULL,
    endereco_id INT NOT NULL,
    vendedor_id INT,
    etapa_id INT NOT NULL,

    cnpj CHAR(14) NOT NULL UNIQUE,
    razao_social VARCHAR(190) NOT NULL,
    nome_fantasia VARCHAR(190) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    telefone CHAR(11) NOT NULL,

    prioridade VARCHAR(30) NULL DEFAULT NULL,
    potencial VARCHAR(30) NOT NULL,
    farol VARCHAR(20) NULL DEFAULT NULL,

    data_primeiro_contato DATETIME,
    data_primeira_visita DATETIME,

    vinculado_em DATETIME NULL,

    criado_em DATETIME NOT NULL
        DEFAULT CURRENT_TIMESTAMP,

    atualizado_em DATETIME NOT NULL
        DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    motivo_perda TEXT,

    ativo BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_leads_setor
        FOREIGN KEY (setor_id)
        REFERENCES setor(id),

    CONSTRAINT fk_leads_endereco
        FOREIGN KEY (endereco_id)
        REFERENCES endereco(id),

    CONSTRAINT fk_leads_vendedor
        FOREIGN KEY (vendedor_id)
        REFERENCES vendedor(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_leads_etapa
        FOREIGN KEY (etapa_id)
        REFERENCES etapa(id)

) ENGINE=InnoDB;

-- ========================================================
-- 6. TABELA CONTATO
-- ========================================================

CREATE TABLE contato (
    id INT AUTO_INCREMENT PRIMARY KEY,

    lead_id INT,

    nome VARCHAR(190) NOT NULL,
    cargo VARCHAR(45) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    telefone VARCHAR(11),
    whatsapp CHAR(11) NOT NULL,

    classificacao ENUM(
        'PADRAO',
        'INFLUENCIADOR',
        'DECISOR'
    ),

    criado_em DATETIME NOT NULL
        DEFAULT CURRENT_TIMESTAMP,

    atualizado_em DATETIME NOT NULL
        DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    ativo BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_contato_lead
        FOREIGN KEY (lead_id)
        REFERENCES leads(id)
        ON DELETE CASCADE

) ENGINE=InnoDB;

-- ========================================================
-- 7. TABELA LEAD_ETAPA_HISTORICO
-- ========================================================

CREATE TABLE lead_etapa_historico (
    id INT AUTO_INCREMENT PRIMARY KEY,

    etapa_id INT NOT NULL,
    lead_id INT NOT NULL,

    criado_em DATETIME NOT NULL
        DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_historico_etapa
        FOREIGN KEY (etapa_id)
        REFERENCES etapa(id),

    CONSTRAINT fk_historico_lead
        FOREIGN KEY (lead_id)
        REFERENCES leads(id)
        ON DELETE CASCADE

) ENGINE=InnoDB;

-- ========================================================
-- 8. TABELA INTERACAO
-- ========================================================

CREATE TABLE interacao (
    id INT AUTO_INCREMENT PRIMARY KEY,

    contato_id INT,
    lead_id INT,

    tipo VARCHAR(45) NOT NULL,
    data_hora DATETIME,
    descricao TEXT,

    criado_em DATETIME NOT NULL
        DEFAULT CURRENT_TIMESTAMP,

    atualizado_em DATETIME NOT NULL
        DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_interacao_contato
        FOREIGN KEY (contato_id)
        REFERENCES contato(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_interacao_lead
        FOREIGN KEY (lead_id)
        REFERENCES leads(id)
        ON DELETE CASCADE

) ENGINE=InnoDB;

-- ========================================================
-- 9. TABELA TAREFA
-- ========================================================

CREATE TABLE tarefa (
    id INT AUTO_INCREMENT PRIMARY KEY,

    contato_id INT NOT NULL,
    lead_id INT NOT NULL,

    tipo VARCHAR(50) NOT NULL,
    descricao TEXT NOT NULL,
    data_prazo DATETIME NOT NULL,

    status ENUM(
        'ABERTA',
        'CONCLUIDA',
        'CANCELADA'
    ),

    criado_em DATETIME NOT NULL
        DEFAULT CURRENT_TIMESTAMP,

    atualizado_em DATETIME NOT NULL
        DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_tarefa_contato
        FOREIGN KEY (contato_id)
        REFERENCES contato(id),

    CONSTRAINT fk_tarefa_lead
        FOREIGN KEY (lead_id)
        REFERENCES leads(id)
        ON DELETE CASCADE

) ENGINE=InnoDB;

-- ========================================================
-- 10. TABELA PREFERENCIAS_VENDEDOR
-- ========================================================

CREATE TABLE preferencias_vendedor (
    vendedor_id INT NOT NULL PRIMARY KEY,

    new_leads BOOLEAN NOT NULL DEFAULT TRUE,

    appointment_reminders BOOLEAN NOT NULL DEFAULT TRUE,

    late_cadence_alerts BOOLEAN NOT NULL DEFAULT TRUE,

    weekly_summary_email BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_preferencias_vendedor
        FOREIGN KEY (vendedor_id)
        REFERENCES vendedor(id)
        ON DELETE CASCADE

) ENGINE=InnoDB;

-- ========================================================
-- INSERÇÃO DE SETORES
-- ========================================================

INSERT INTO setor (id, nome) VALUES
(1, 'Tecnologia'),
(2, 'Saúde e Farmacêutica'),
(3, 'Educação'),
(4, 'Varejo e E-commerce'),
(5, 'Indústria e Manufatura');

-- ========================================================
-- INSERÇÃO DAS ETAPAS DO FUNIL
-- ========================================================

INSERT INTO etapa (id, nome, ordem) VALUES
(1, 'Prospecção / Novo Lead', 1),
(2, 'Qualificação', 2),
(3, 'Apresentação / Proposta', 3),
(4, 'Negociação', 4),
(5, 'Fechado / Ganho', 5);

-- ========================================================
-- VERIFICAÇÃO DAS TABELAS
-- ========================================================

SHOW TABLES;

DESCRIBE setor;
DESCRIBE endereco;
DESCRIBE etapa;
DESCRIBE vendedor;
DESCRIBE leads;
DESCRIBE contato;
DESCRIBE lead_etapa_historico;
DESCRIBE interacao;
DESCRIBE tarefa;
DESCRIBE preferencias_vendedor;

-- ========================================================
-- CONSULTAR AS ETAPAS CADASTRADAS
-- ========================================================

SELECT *
FROM etapa
ORDER BY ordem;

-- ========================================================
-- CONSULTAR OS SETORES CADASTRADOS
-- ========================================================

SELECT *
FROM setor
ORDER BY id;
