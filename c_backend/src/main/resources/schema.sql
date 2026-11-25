CREATE DATABASE leadway CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE leadway;

-- 1. Cargos
CREATE TABLE cargo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(30) NOT NULL UNIQUE
) ENGINE=InnoDB;

-- 2. Setores
CREATE TABLE setor (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(45) NOT NULL UNIQUE
) ENGINE=InnoDB;

-- 3. Endereços
CREATE TABLE endereco (
    id INT AUTO_INCREMENT PRIMARY KEY,
    rua VARCHAR(200) NOT NULL,
    cep VARCHAR(20) NOT NULL,
    numero VARCHAR(20) NOT NULL,
    bairro VARCHAR(100) NOT NULL,
    cidade VARCHAR(100) NOT NULL,
    estado VARCHAR(100) NOT NULL,
    pais VARCHAR(100) NOT NULL,
    complemento VARCHAR(100) NULL
) ENGINE=InnoDB;

-- 4. Etapas do funil
CREATE TABLE etapa (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(50) NOT NULL,
    ordem TINYINT NOT NULL UNIQUE
) ENGINE=InnoDB;

-- 5. Funcionários e contas administradoras
-- administrador_id: organização/conta-pai; lider_id: liderança direta.
-- Administrador raiz: administrador_id aponta para seu próprio id, lider_id é NULL.
CREATE TABLE vendedor (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cargo_id INT NOT NULL,
    administrador_id INT NULL,
    lider_id INT NULL,
    cod_vendedor VARCHAR(30) NULL,
    regiao VARCHAR(100) NULL,
    nome VARCHAR(190) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    telefone CHAR(11) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_vendedor_cargo FOREIGN KEY (cargo_id) REFERENCES cargo(id),
    CONSTRAINT fk_vendedor_administrador FOREIGN KEY (administrador_id)
        REFERENCES vendedor(id) ON DELETE RESTRICT,
    CONSTRAINT fk_vendedor_lider FOREIGN KEY (lider_id)
        REFERENCES vendedor(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 6. Leads (sem farol; potencial 1-5; prioridade ALTA/MEDIA/BAIXA)
CREATE TABLE leads (
    id INT AUTO_INCREMENT PRIMARY KEY,
    setor_id INT NOT NULL,
    endereco_id INT NOT NULL,
    vendedor_id INT NULL,
    etapa_id INT NOT NULL,
    cnpj CHAR(14) NOT NULL UNIQUE,
    razao_social VARCHAR(190) NOT NULL,
    nome_fantasia VARCHAR(190) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    telefone CHAR(11) NOT NULL,
    prioridade VARCHAR(30) NULL,
    potencial INT NOT NULL,
    data_primeiro_contato DATETIME NULL,
    data_primeira_visita DATETIME NULL,
    vinculado_em DATETIME NULL,
    criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    motivo_perda TEXT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_leads_potencial CHECK (potencial BETWEEN 1 AND 5),
    CONSTRAINT chk_leads_prioridade CHECK (prioridade IS NULL OR prioridade IN ('ALTA','MEDIA','BAIXA')),
    CONSTRAINT fk_leads_setor FOREIGN KEY (setor_id) REFERENCES setor(id),
    CONSTRAINT fk_leads_endereco FOREIGN KEY (endereco_id) REFERENCES endereco(id),
    -- RESTRICT impede exclusão física de funcionário com leads vinculados.
    CONSTRAINT fk_leads_vendedor FOREIGN KEY (vendedor_id) REFERENCES vendedor(id) ON DELETE RESTRICT,
    CONSTRAINT fk_leads_etapa FOREIGN KEY (etapa_id) REFERENCES etapa(id)
) ENGINE=InnoDB;

-- 7. Contatos
CREATE TABLE contato (
    id INT AUTO_INCREMENT PRIMARY KEY,
    lead_id INT NULL,
    nome VARCHAR(190) NOT NULL,
    cargo VARCHAR(45) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    telefone VARCHAR(11) NULL,
    whatsapp CHAR(11) NOT NULL,
    classificacao ENUM('PADRAO','INFLUENCIADOR','DECISOR') NULL,
    criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_contato_lead FOREIGN KEY (lead_id) REFERENCES leads(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 8. Histórico das etapas
CREATE TABLE lead_etapa_historico (
    id INT AUTO_INCREMENT PRIMARY KEY,
    etapa_id INT NOT NULL,
    lead_id INT NOT NULL,
    criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_historico_etapa FOREIGN KEY (etapa_id) REFERENCES etapa(id),
    CONSTRAINT fk_historico_lead FOREIGN KEY (lead_id) REFERENCES leads(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 9. Interações
CREATE TABLE interacao (
    id INT AUTO_INCREMENT PRIMARY KEY,
    contato_id INT NULL,
    lead_id INT NULL,
    tipo VARCHAR(45) NOT NULL,
    data_hora DATETIME NULL,
    descricao TEXT NULL,
    criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_interacao_contato FOREIGN KEY (contato_id) REFERENCES contato(id) ON DELETE SET NULL,
    CONSTRAINT fk_interacao_lead FOREIGN KEY (lead_id) REFERENCES leads(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 10. Tarefas
CREATE TABLE tarefa (
    id INT AUTO_INCREMENT PRIMARY KEY,
    contato_id INT NOT NULL,
    lead_id INT NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    descricao TEXT NOT NULL,
    data_prazo DATETIME NOT NULL,
    status ENUM('ABERTA','CONCLUIDA','CANCELADA') NOT NULL DEFAULT 'ABERTA',
    criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_tarefa_contato FOREIGN KEY (contato_id) REFERENCES contato(id) ON DELETE RESTRICT,
    CONSTRAINT fk_tarefa_lead FOREIGN KEY (lead_id) REFERENCES leads(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 11. Preferências de notificações
CREATE TABLE preferencias_vendedor (
    vendedor_id INT PRIMARY KEY,
    new_leads BOOLEAN NOT NULL DEFAULT TRUE,
    appointment_reminders BOOLEAN NOT NULL DEFAULT TRUE,
    late_cadence_alerts BOOLEAN NOT NULL DEFAULT TRUE,
    weekly_summary_email BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_preferencias_vendedor FOREIGN KEY (vendedor_id)
        REFERENCES vendedor(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 12. Integração com calendário
CREATE TABLE cal_integracao (
    vendedor_id INT PRIMARY KEY,
    cal_username VARCHAR(100) NOT NULL,
    cal_api_key VARCHAR(255) NOT NULL,
    conectado_em DATETIME NOT NULL,
    CONSTRAINT fk_cal_integracao_vendedor FOREIGN KEY (vendedor_id)
        REFERENCES vendedor(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ========================================================
-- DADOS INICIAIS
-- ========================================================
INSERT INTO cargo (id, nome) VALUES
(1, 'ADMINISTRADOR'), (2, 'GERENTE'), (3, 'VENDEDOR');

INSERT INTO setor (id, nome) VALUES
(1, 'Tecnologia'), (2, 'Saúde e Farmacêutica'), (3, 'Educação'),
(4, 'Varejo e E-commerce'), (5, 'Indústria e Manufatura');

INSERT INTO etapa (id, nome, ordem) VALUES
(1, 'Prospecção / Novo Lead', 1),
(2, 'Qualificação', 2),
(3, 'Apresentação / Proposta', 3),
(4, 'Negociação', 4),
(5, 'Fechado / Ganho', 5);

-- Contas demonstrativas: senhas em texto simples SOMENTE para testes locais.
-- O login real exige que o backend suporte o formato de senha usado.
-- O cadastro público deve definir cargo ADMINISTRADOR no Java, não no frontend.
INSERT INTO vendedor
(id, cargo_id, administrador_id, lider_id, cod_vendedor, regiao, nome, email, senha, telefone, ativo)
VALUES
(1, 1, NULL, NULL, 'ADM-001', 'São Paulo', 'Lucas Martins', 'lucas@leadway.com', '123456', '11999990001', TRUE);
-- Vincular o administrador a sua própria organização.
UPDATE vendedor SET administrador_id = 1 WHERE id = 1;

INSERT INTO vendedor
(id, cargo_id, administrador_id, lider_id, cod_vendedor, regiao, nome, email, senha, telefone, ativo)
VALUES
(2, 2, 1, 1, 'GER-001', 'Grande SP', 'Maria Souza', 'maria@leadway.com', '123456', '11999990002', TRUE),
(3, 2, 1, 1, 'GER-002', 'Interior SP', 'Carlos Oliveira', 'carlos@leadway.com', '123456', '11999990003', TRUE),
(4, 3, 1, 2, 'VEN-001', 'Grande SP', 'João Silva', 'joao@leadway.com', '123456', '11999990004', TRUE),
(5, 3, 1, 2, 'VEN-002', 'Grande SP', 'Ana Costa', 'ana@leadway.com', '123456', '11999990005', TRUE),
(6, 3, 1, 3, 'VEN-003', 'Interior SP', 'Pedro Lima', 'pedro@leadway.com', '123456', '11999990006', TRUE);

INSERT INTO endereco
(id, rua, cep, numero, bairro, cidade, estado, pais, complemento)
VALUES
(1, 'Avenida Paulista', '01311000', '1000', 'Bela Vista', 'São Paulo', 'São Paulo', 'Brasil', 'Conjunto 101'),
(2, 'Rua das Flores', '13010000', '250', 'Centro', 'Campinas', 'São Paulo', 'Brasil', NULL),
(3, 'Avenida Brasil', '04530000', '500', 'Jardim Paulista', 'São Paulo', 'São Paulo', 'Brasil', NULL),
(4, 'Rua da Educação', '04001000', '120', 'Vila Mariana', 'São Paulo', 'São Paulo', 'Brasil', NULL),
(5, 'Avenida Industrial', '09080000', '800', 'Centro', 'Santo André', 'São Paulo', 'Brasil', NULL);

-- Empresas fictícias com CNPJs de teste de dígitos verificadores válidos.
-- Não correspondem necessariamente a empresas reais.
INSERT INTO leads
(id, setor_id, endereco_id, vendedor_id, etapa_id, cnpj, razao_social, nome_fantasia,
 email, telefone, prioridade, potencial, data_primeiro_contato, data_primeira_visita, vinculado_em, ativo)
VALUES
(1, 1, 1, 4, 1, '00000001000136', 'TecNova Sistemas LTDA', 'TecNova Sistemas', 'contato@tecnova.com', '11988880001', 'ALTA', 5, '2026-11-01 09:00:00', NULL, '2026-11-01 08:00:00', TRUE),
(2, 2, 2, 5, 4, '00000002000180', 'Vida Mais Saúde LTDA', 'Vida Mais', 'contato@vidamais.com', '19988880002', 'MEDIA', 4, '2026-11-02 10:00:00', '2026-11-05 14:00:00', '2026-11-02 09:00:00', TRUE),
(3, 3, 4, 6, 3, '00000003000125', 'Educa Brasil LTDA', 'Educa Brasil', 'contato@educabrasil.com', '11988880003', 'ALTA', 5, '2026-11-03 11:00:00', '2026-11-06 15:00:00', '2026-11-03 10:00:00', TRUE),
(4, 4, 3, 4, 4, '00000004000170', 'Digital Express Comércio LTDA', 'Digital Express', 'contato@digitalexpress.com', '11988880004', 'BAIXA', 3, '2026-11-04 09:30:00', NULL, '2026-11-04 09:00:00', TRUE),
(5, 5, 5, 5, 5, '00000005000114', 'Metal Forte Indústria LTDA', 'Metal Forte', 'contato@metalforte.com', '11988880005', 'MEDIA', 4, '2026-11-05 08:30:00', '2026-11-07 13:00:00', '2026-11-05 08:00:00', TRUE);

INSERT INTO contato
(id, lead_id, nome, cargo, email, telefone, whatsapp, classificacao, ativo)
VALUES
(1, 1, 'Mariana Costa', 'Diretora de Operações', 'mariana@tecnova.com', '11977770001', '11977770001', 'DECISOR', TRUE),
(2, 1, 'Pedro Almeida', 'Analista de TI', 'pedro@tecnova.com', '11977770002', '11977770002', 'INFLUENCIADOR', TRUE),
(3, 2, 'Ana Paula', 'Gerente Comercial', 'ana@vidamais.com', '19977770003', '19977770003', 'DECISOR', TRUE),
(4, 3, 'Roberto Lima', 'Diretor', 'roberto@educabrasil.com', '11977770004', '11977770004', 'DECISOR', TRUE),
(5, 4, 'Fernanda Rocha', 'Coordenadora', 'fernanda@digitalexpress.com', '11977770005', '11977770005', 'INFLUENCIADOR', TRUE),
(6, 5, 'Ricardo Mendes', 'Diretor Comercial', 'ricardo@metalforte.com', '11977770006', '11977770006', 'DECISOR', TRUE);

INSERT INTO lead_etapa_historico (etapa_id, lead_id, criado_em) VALUES
(1, 1, '2026-11-01 09:00:00'),
(1, 2, '2026-11-02 10:00:00'), (2, 2, '2026-11-03 10:00:00'), (3, 2, '2026-11-05 14:00:00'), (4, 2, '2026-11-10 11:00:00'),
(1, 3, '2026-11-03 11:00:00'), (2, 3, '2026-11-04 09:00:00'), (3, 3, '2026-11-08 14:00:00'),
(1, 4, '2026-11-04 09:30:00'), (2, 4, '2026-11-05 10:00:00'), (3, 4, '2026-11-07 11:00:00'), (4, 4, '2026-11-11 15:00:00'),
(1, 5, '2026-11-05 08:30:00'), (2, 5, '2026-11-06 09:00:00'), (3, 5, '2026-11-07 13:00:00'), (4, 5, '2026-11-09 14:00:00'), (5, 5, '2026-11-12 16:00:00');

INSERT INTO interacao (contato_id, lead_id, tipo, data_hora, descricao) VALUES
(1, 1, 'WHATSAPP', '2026-11-15 09:30:00', 'Follow-up realizado. Cliente solicitou retorno.'),
(1, 1, 'EMAIL', '2026-11-12 14:20:00', 'Catálogo de produtos enviado por e-mail.'),
(2, 1, 'LIGACAO', '2026-11-10 10:00:00', 'Primeiro contato com a equipe técnica.'),
(3, 2, 'REUNIAO', '2026-11-13 15:00:00', 'Reunião de negociação comercial.'),
(4, 3, 'EMAIL', '2026-11-12 11:00:00', 'Proposta comercial encaminhada.'),
(5, 4, 'WHATSAPP', '2026-11-14 09:00:00', 'Cliente solicitou revisão da proposta.'),
(6, 5, 'REUNIAO', '2026-11-12 16:00:00', 'Negociação concluída com sucesso.');

INSERT INTO tarefa (contato_id, lead_id, tipo, descricao, data_prazo, status) VALUES
(1, 1, 'REUNIAO', 'Apresentar proposta comercial.', '2026-11-18 10:00:00', 'ABERTA'),
(3, 2, 'LIGACAO', 'Confirmar condições da negociação.', '2026-11-17 14:00:00', 'ABERTA'),
(4, 3, 'EMAIL', 'Enviar proposta revisada.', '2026-11-19 09:00:00', 'ABERTA'),
(5, 4, 'REUNIAO', 'Reunião de negociação.', '2026-11-20 15:00:00', 'ABERTA'),
(6, 5, 'LIGACAO', 'Confirmar encerramento comercial.', '2026-11-12 16:00:00', 'CONCLUIDA');

INSERT INTO preferencias_vendedor
(vendedor_id, new_leads, appointment_reminders, late_cadence_alerts, weekly_summary_email)
VALUES
(1, TRUE, TRUE, TRUE, TRUE),
(2, TRUE, TRUE, TRUE, FALSE),
(3, TRUE, TRUE, TRUE, FALSE),
(4, TRUE, TRUE, TRUE, FALSE),
(5, TRUE, TRUE, TRUE, FALSE),
(6, TRUE, TRUE, TRUE, FALSE);


-- ========================================================
-- CONFERÊNCIA
-- ========================================================
SHOW TABLES;
SELECT v.id, v.nome, c.nome AS cargo, a.nome AS administrador, l.nome AS lider
FROM vendedor v
JOIN cargo c ON c.id = v.cargo_id
LEFT JOIN vendedor a ON a.id = v.administrador_id
LEFT JOIN vendedor l ON l.id = v.lider_id
ORDER BY v.id;

SELECT l.id, l.nome_fantasia, l.prioridade, l.potencial,
       v.nome AS responsavel, e.nome AS etapa
FROM leads l
LEFT JOIN vendedor v ON v.id = l.vendedor_id
JOIN etapa e ON e.id = l.etapa_id
ORDER BY l.id;

SELECT 'cargo' AS tabela, COUNT(*) AS total FROM cargo
UNION ALL SELECT 'setor', COUNT(*) FROM setor
UNION ALL SELECT 'etapa', COUNT(*) FROM etapa
UNION ALL SELECT 'vendedor', COUNT(*) FROM vendedor
UNION ALL SELECT 'leads', COUNT(*) FROM leads
UNION ALL SELECT 'contato', COUNT(*) FROM contato
UNION ALL SELECT 'interacao', COUNT(*) FROM interacao
UNION ALL SELECT 'tarefa', COUNT(*) FROM tarefa;
