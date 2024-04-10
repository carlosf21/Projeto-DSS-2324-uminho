-- DROP SCHEMA IF EXISTS `Oficina`;
CREATE SCHEMA IF NOT EXISTS `Oficina`;
USE `Oficina`;

-- -----------------------------------------------------
-- ISTO FAZ PARTE DO SUBCLIENTES MAS TÁ NO INICIO PARA RESPEITAR A ORDEM DAS FK
CREATE TABLE IF NOT EXISTS `Oficina`.`TipoMotor` (
  `IdTipo` INT NOT NULL AUTO_INCREMENT,
  `Descricao` ENUM('Eletrico', 'Gasolina','Diesel','Combustao','Universal') NOT NULL,
  PRIMARY KEY (`IdTipo`))
ENGINE = InnoDB;
-- 
-- -----------------------------------------------------

-- -----------------------------------------------------
-- SUBSISTEMA 'SUBPOSTO'
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS `Oficina`.`Posto` (
  `IdPosto` INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`IdPosto`))
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `Oficina`.`Servico` (
  `IdServico` INT NOT NULL AUTO_INCREMENT,
  `Duracao` INT NOT NULL,
  `Descricao` VARCHAR(100) NOT NULL,
  `IdTM` INT NOT NULL,
  PRIMARY KEY (`IdServico`),
    CONSTRAINT `fk_Servico_TipoMotor1`
    FOREIGN KEY (`IdTM`)
    REFERENCES `TipoMotor` (`IdTipo`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `Oficina`.`PostoServico` (
  `IdPosto` INT NOT NULL,
  `IdServico` INT NOT NULL,

  PRIMARY KEY (`IdPosto`,`IdServico`),
  CONSTRAINT `fk_PostoServico_Posto1`
    FOREIGN KEY (`IdPosto`)
    REFERENCES `Posto` (`IdPosto`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
   CONSTRAINT `fk_PostoServico_Servico1`
    FOREIGN KEY (`IdServico`)
    REFERENCES `Servico` (`IdServico`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- SUBSISTEMA 'SUBCLIENTES'
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Oficina`.`Cliente` (
  `Nif` CHAR(9) NOT NULL,
  `Nome` VARCHAR(50) NOT NULL,
  `Morada` VARCHAR(100) NULL,
  `Telefone` CHAR(12) NOT NULL, 
  `Email` VARCHAR(100) NULL,
  PRIMARY KEY (`Nif`))
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `Oficina`.`Veiculo` (
  `Matricula` CHAR(6) NOT NULL,
  `Modelo` VARCHAR(30) NOT NULL,
  `IdTipoMotor` INT NOT NULL,
  `IdTipoMotor2` INT NULL,
  `NifCliente` CHAR(9) NULL,
  PRIMARY KEY (`Matricula`),
  CONSTRAINT `fk_Veiculo_TipoMotor1`
    FOREIGN KEY (`IdTipoMotor`)
    REFERENCES `Oficina`.`TipoMotor` (`IdTipo`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Veiculo_Cliente1`
    FOREIGN KEY (`NifCliente`)
    REFERENCES `Oficina`.`Cliente` (`Nif`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `Oficina`.`ServicosRecomendados` (
  `MatrVeiculo` CHAR(6) NOT NULL,
  `IdServico` INT NOT NULL,
  PRIMARY KEY (`matrVeiculo`,`IdServico`),
  CONSTRAINT `fk_ServRecomendados_Veiculo1`
    FOREIGN KEY (`MatrVeiculo`)
    REFERENCES `Oficina`.`Veiculo` (`Matricula`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ServRecomendados_Servico1`
    FOREIGN KEY (`IdServico`)
    REFERENCES `Oficina`.`Servico` (`IdServico`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- SUBSISTEMA 'SUBFUNCIONARIOS'
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS `Oficina`.`Funcionario` (
  `IdFunc` INT NOT NULL AUTO_INCREMENT,
  `Nome` VARCHAR(50) NOT NULL,
  `TipoFunc` ENUM('Mecanico', 'Administrativo') NOT NULL,
  PRIMARY KEY (`IdFunc`))
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `Oficina`.`Mecanico` (
  `IdMecanico` INT NOT NULL,
  `IdTipoMotor` INT NOT NULL,

  PRIMARY KEY (`IdMecanico`),
  CONSTRAINT `fk_Mecanico_Funcionario1`
    FOREIGN KEY (`IdMecanico`)
    REFERENCES `Funcionario` (`IdFunc`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Mecanico_Posto1`
    FOREIGN KEY (`IdTipoMotor`)
    REFERENCES `TipoMotor` (`IdTipo`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `Oficina`.`Administrativo` (
  `IdAdministrativo` INT NOT NULL,
  PRIMARY KEY (`IdAdministrativo`),
  CONSTRAINT `fk_Administrativo_Funcionario1`
    FOREIGN KEY (`IdAdministrativo`)
    REFERENCES `Funcionario` (`IdFunc`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `Oficina`.`Registo` (
  `IdRegisto` INT NOT NULL AUTO_INCREMENT,
  `HoraEntrada` DATETIME NOT NULL,
  `HoraSaida` DATETIME NULL,
  `IdMecanico` INT NOT NULL,
  PRIMARY KEY (`IdRegisto`),
  CONSTRAINT `fk_Registo_Mecanico1`
    FOREIGN KEY (`IdMecanico`)
    REFERENCES `Mecanico` (`IdMecanico`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `Oficina`.`Horario` (
	`IdHorario`INT NOT NULL AUTO_INCREMENT,
    `HoraAbertura` TIME NOT NULL,
    `HoraFecho` TIME NOT NULL,
    PRIMARY KEY (`IdHorario`)
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Isto está no subsistema SSPostos, mas está aqui devido à ordem de criação das FK
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS `Oficina`.`Agendamento` (
  `IdAgendamento` INT NOT NULL,
  `HoraInicio` DATETIME NOT NULL,
  `HoraFim` DATETIME NOT NULL,
  `Notificar` BOOLEAN NOT NULL,
  `Concluido` BOOLEAN NOT NULL,
  `Motivo` VARCHAR(50) NULL,
  `IdServico` INT NOT NULL,
  `IdCliente` CHAR(9) NOT NULL,
  `IdVeiculo` CHAR(6) NOT NULL,
  `IdPosto` INT NOT NULL,
  PRIMARY KEY (`IdAgendamento`),
  CONSTRAINT `fk_Agendamento_Servico1`
    FOREIGN KEY (`IdServico`)
    REFERENCES `Servico` (`IdServico`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Agendamento_Cliente1`
    FOREIGN KEY (`IdCliente`)
    REFERENCES `Cliente` (`Nif`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Agendamento_Veiculo1`
    FOREIGN KEY (`IdVeiculo`)
    REFERENCES `Veiculo` (`Matricula`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Agendamento_Posto1`
    FOREIGN KEY (`IdPosto`)
    REFERENCES `Posto` (`IdPosto`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


