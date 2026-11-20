-- =============================================================
-- LEADWAY | BANCO 
-- =============================================================

CREATE DATABASE IF NOT EXISTS leadway CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE leadway;

CREATE TABLE cargo (
 id INT AUTO_INCREMENT PRIMARY KEY,
 nome VARCHAR(90) NOT NULL UNIQUE,
 descricao VARCHAR(145)
) ENGINE=InnoDB;

CREATE TABLE setor (
id INT AUTO_INCREMENT PRIMARY KEY, 
nome VARCHAR(45) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE endereco (
 id INT AUTO_INCREMENT PRIMARY KEY, rua VARCHAR(200) NOT NULL, cep VARCHAR(20) NOT NULL,
 numero VARCHAR(20) NOT NULL, bairro VARCHAR(100) NOT NULL, cidade VARCHAR(100) NOT NULL,
 estado VARCHAR(100) NOT NULL, pais VARCHAR(100) NOT NULL, complemento VARCHAR(100)
) ENGINE=InnoDB;

CREATE TABLE etapa (
id INT AUTO_INCREMENT PRIMARY KEY, 
nome VARCHAR(50) NOT NULL, 
ordem TINYINT NOT NULL
) ENGINE=InnoDB;

CREATE TABLE usuario (
 id INT AUTO_INCREMENT PRIMARY KEY, 
 cargo_id INT NOT NULL, 
 lider_id INT NULL,
 cod_vendedor VARCHAR(30), 
 nome VARCHAR(190) NOT NULL, 
 email VARCHAR(255) NOT NULL UNIQUE,
 senha VARCHAR(50) NOT NULL, 
 telefone CHAR(11) NOT NULL, 
 regiao VARCHAR(90),
 tentativas_login INT NOT NULL DEFAULT 0, 
 bloqueado_ate DATETIME NULL,
 ativo TINYINT(1) NOT NULL DEFAULT 1,
 CONSTRAINT fk_usuario_cargo FOREIGN KEY (cargo_id) REFERENCES cargo(id),
 CONSTRAINT fk_usuario_lider FOREIGN KEY (lider_id) REFERENCES usuario(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE `lead` (
 id INT AUTO_INCREMENT PRIMARY KEY, 
 setor_id INT NOT NULL, 
 endereco_id INT NOT NULL,
 responsavel_id INT NOT NULL, 
 etapa_id INT NOT NULL,
 cnpj CHAR(14) NOT NULL UNIQUE, 
 razao_social VARCHAR(190) NOT NULL,
 nome_fantasia VARCHAR(190) NOT NULL, 
 email VARCHAR(255) NOT NULL UNIQUE,
 telefone CHAR(11) NOT NULL,
 prioridade ENUM('ALTA','MEDIA','BAIXA') NULL,
 potencial TINYINT UNSIGNED NOT NULL,
 data_primeiro_contato DATETIME NULL, 
 data_primeira_visita DATETIME NULL,
 criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
 atualizado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 motivo_perda TEXT NULL, ativo TINYINT(1) NOT NULL DEFAULT 1,
 CONSTRAINT chk_lead_potencial CHECK(potencial BETWEEN 1 AND 5),
 CONSTRAINT fk_lead_setor FOREIGN KEY(setor_id) REFERENCES setor(id),
 CONSTRAINT fk_lead_endereco FOREIGN KEY(endereco_id) REFERENCES endereco(id),
 CONSTRAINT fk_lead_responsavel FOREIGN KEY(responsavel_id) REFERENCES usuario(id) ON DELETE RESTRICT,
 CONSTRAINT fk_lead_etapa FOREIGN KEY(etapa_id) REFERENCES etapa(id)
) ENGINE=InnoDB;

CREATE TABLE contato (
 id INT AUTO_INCREMENT PRIMARY KEY, 
 lead_id INT NOT NULL,
 nome VARCHAR(190) NOT NULL, 
 cargo VARCHAR(45) NOT NULL,
 email VARCHAR(255) NOT NULL UNIQUE, 
 telefone VARCHAR(11), whatsapp CHAR(11) NOT NULL,
 classificacao ENUM('PADRAO','SECUNDARIO','PRINCIPAL'),
 criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
 atualizado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 ativo TINYINT(1) NOT NULL DEFAULT 1,
 CONSTRAINT fk_contato_lead FOREIGN KEY(lead_id) REFERENCES `lead`(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE lead_etapa_historico (
 id INT AUTO_INCREMENT PRIMARY KEY, 
 etapa_id INT NOT NULL, 
 lead_id INT NOT NULL,
 criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT fk_historico_etapa FOREIGN KEY(etapa_id) REFERENCES etapa(id),
 CONSTRAINT fk_historico_lead FOREIGN KEY(lead_id) REFERENCES `lead`(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE interacao (
 id INT AUTO_INCREMENT PRIMARY KEY, 
 lead_id INT NOT NULL, usuario_id INT NULL,
 tipo VARCHAR(45) NOT NULL, 
 data_hora DATETIME NULL, 
 descricao TEXT,
 criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
 atualizado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 CONSTRAINT fk_interacao_lead FOREIGN KEY(lead_id) REFERENCES `lead`(id) ON DELETE CASCADE,
 CONSTRAINT fk_interacao_usuario FOREIGN KEY(usuario_id) REFERENCES usuario(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE tarefa (
 id INT AUTO_INCREMENT PRIMARY KEY, 
 contato_id INT NOT NULL, 
 lead_id INT NOT NULL,
 responsavel_id INT NULL, 
 tipo VARCHAR(50) NOT NULL, descricao TEXT NOT NULL,
 data_prazo DATETIME NOT NULL, 
 status ENUM('ABERTA','CONCLUIDA','CANCELADA') DEFAULT 'ABERTA',
 concluido_em DATETIME NULL, 
 lembrete_em DATETIME NULL,
 criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
 atualizado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 CONSTRAINT fk_tarefa_contato FOREIGN KEY(contato_id) REFERENCES contato(id) ON DELETE RESTRICT,
 CONSTRAINT fk_tarefa_lead FOREIGN KEY(lead_id) REFERENCES `lead`(id) ON DELETE CASCADE,
 CONSTRAINT fk_tarefa_responsavel FOREIGN KEY(responsavel_id) REFERENCES usuario(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE cadencia (
 id INT AUTO_INCREMENT PRIMARY KEY, 
 contato_id INT NULL, 
 lead_id INT NOT NULL,
 responsavel_id INT NULL, 
 frequencia ENUM('DIARIA','SEMANAL','QUINZENAL','MENSAL', 'PERSONALIZADA') NOT NULL,
 intervalo_dias INT NULL, data_inicio DATETIME NULL, data_fim DATETIME NULL,
 status ENUM('ATIVA','PAUSADA','CONCLUIDA','CANCELADA') NOT NULL DEFAULT 'ATIVA',
 criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
 atualizado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 CONSTRAINT fk_cadencia_contato FOREIGN KEY(contato_id) REFERENCES contato(id) ON DELETE RESTRICT,
 CONSTRAINT fk_cadencia_lead FOREIGN KEY(lead_id) REFERENCES `lead`(id) ON DELETE CASCADE,
 CONSTRAINT fk_cadencia_responsavel FOREIGN KEY(responsavel_id) REFERENCES usuario(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE log (
 id INT AUTO_INCREMENT PRIMARY KEY, tipo VARCHAR(45) NOT NULL,
 mensagem VARCHAR(100) NOT NULL, criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Integração externa: estrutura mantida do SQL anterior.
CREATE TABLE cal_integracao (
 usuario_id INT PRIMARY KEY,
 cal_username VARCHAR(100) NOT NULL,
 cal_api_key VARCHAR(255) NOT NULL,
 conectado_em DATETIME NOT NULL,
 CONSTRAINT fk_cal_integracao_usuario FOREIGN KEY (usuario_id)
   REFERENCES usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- =============================================================
-- DADOS INICIAIS ESSENCIAIS (SEM SENHAS EM TEXTO PURO)
-- =============================================================
INSERT INTO cargo (id, nome, descricao) VALUES
 (1, 'ADMINISTRADOR', 'Administrador da organização'),
 (2, 'GERENTE', 'Gerencia equipe de vendedores'),
 (3, 'VENDEDOR', 'Responsável pelo atendimento comercial');

INSERT INTO setor (id, nome) VALUES
 (1, 'Tecnologia'),
 (2, 'Saúde'),
 (3, 'Educação'),
 (4, 'Comércio'),
 (5, 'Indústria');

INSERT INTO etapa (id, nome, ordem) VALUES
 (1, 'Prospecção / Novo Lead', 1),
 (2, 'Qualificação', 2),
 (3, 'Apresentação / Proposta', 3),
 (4, 'Negociação', 4),
 (5, 'Fechado / Ganho', 5);

INSERT INTO log (tipo, mensagem) VALUES
 ('SISTEMA', 'Estrutura inicial do LeadWay criada');

-- =============================================================
-- CONSULTAS DE VERIFICAÇÃO
-- =============================================================
SHOW TABLES; -- esperado: 13 tabelas
SELECT COUNT(*) AS quantidade_cargos FROM cargo; -- 3
SELECT COUNT(*) AS quantidade_setores FROM setor; -- 5
SELECT COUNT(*) AS quantidade_etapas FROM etapa; -- 5
SELECT COUNT(*) AS quantidade_usuarios FROM usuario; -- 0 (criar pelo cadastro)
SELECT COUNT(*) AS quantidade_leads FROM `lead`; -- 0
