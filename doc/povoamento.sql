USE `Oficina`;

INSERT INTO `TipoMotor` VALUES 
    (1, 'Gasolina'),
    (2, 'Diesel'),
	(3, 'Eletrico'),
    (4, 'Combustao'),
    (5, 'Universal');

INSERT INTO `Posto` VALUES
	(1),
    (2),
    (3),
	(4),
    (5);
-- 2 motores a gasolina
-- 2 motores de diesel
-- 2 motores eletricos
-- 2 motores de combustão
-- 2 universais
INSERT INTO `Servico` VALUES
    (1,70,'Substituição da válvula do acelerador', 1),
    (2,120, 'Substituição das velas de ignição', 1),

    (3,80,'Substituição das velas de incandescência', 2),
    (4,45,'Regeneração ou substituição do filtro de partículas', 2),
    
    (5,60,'Substituição da bateria', 3),
    (6,30,'Avaliação do desempenho da bateria', 3),

    (7,60,'Substituição do conversor catalítico', 4),
    (8,60,'Mudança de óleo do motor', 4),
    
	(9,30,'Substituição de pneus', 5),
    (10,45,'Calibragem de rodas', 5);
    
INSERT INTO `PostoServico` VALUES
	(1,1),
    (1,3),
    (1,5),
	(2,1), -- posto 2 so faz servicos para gasolina (1)
    (2,2),
	(3,5),
    (3,6),
    (4,7),
    (4,8),
    (5,5),
    (5,9);

INSERT INTO `Cliente` VALUES
	('123345709', 'Dora Matagoal', 'Rua de Cima, 150', '351938499232','dora@gmail.com'),
    ('156232512', 'Joao Malhao', 'Rua de Baixo, 123', '351293125123', 'malhao@gmail.com'),
    ('352315262', 'Antonio Assis', 'Rua de rua, 12, 1esq', '351876245678','antss@outlook.com'),
    ('125125231', 'Inês de Gusmão', 'Rua do beco, 11, 1dir', '351876245678','ig123@gmail.com');

INSERT INTO `Veiculo` VALUES
	('0GYAT3','Fiat Punto',1,null,'123345709'),
    ('AA11BB','Mclaren p1',1,3,'156232512'),
    ('98ALGT','BMW E30 M3',1,null,'156232512'),
    ('12CD34','Porsche 911 GT3 RS',1,null,'352315262'),
    ('XZ99ZX','Koenigsegg Gemera',1,null,'125125231'),
    ('OL11AA','Lamborghini Spark R',2,null,'125125231');
    
INSERT INTO `ServicosRecomendados` VALUES
	('0GYAT3',1),
	('0GYAT3',7),
    ('AA11BB',5),
    ('AA11BB',2),
    ('98ALGT',8),
    ('12CD34',1),
    ('OL11AA',3);

INSERT INTO `Horario` VALUES
	(1,'09:00:00','18:00:00');

INSERT INTO `Funcionario` VALUES
	(1,'Rui Ricardes','Mecanico'),
    (2,'Roberto Real','Mecanico'),
    (3,'Antonio Antunes', 'Mecanico'),
    (4,'André de Andrade', 'Mecanico'),
	(5,'Joana Joanes','Administrativo'),
    (6,'Rute Ruterdão','Administrativo');

INSERT INTO `Mecanico` VALUES
	(1,1),
    (2,2),
    (3,3),
    (4,4);

INSERT INTO `Administrativo` VALUES
	(5),
    (6);
    
INSERT INTO `Registo` VALUES
	(1,'2024-01-06 09:30:12',null,1),
	(2,'2024-01-06 09:30:15','2024-01-06 18:30:15',2),
	(3,'2024-01-06 08:30:00','2024-01-06 17:30:00',3),
	(4,'2024-01-07 13:30:02','2024-01-07 20:30:02',1),
	(5,'2024-01-07 13:30:30','2024-01-07 18:30:02',2),
	(6,'2024-01-07 14:30:04','2024-01-07 18:30:14',3),
	(7,'2024-01-08 15:30:20','2024-01-08 16:30:20',3),
	(8,'2024-01-08 16:30:50',null,2);

INSERT INTO `Agendamento` VALUES
	(1,'2024-01-04 09:30:00','2024-01-04 10:40:00',TRUE,FALSE,NULL, 1, '123345709','0GYAT3',1),
    (2,'2024-01-05 10:00:30','2024-01-05 10:30:30',FALSE,TRUE,NULL, 6,'156232512','AA11BB',2),
	(3,'2024-01-05 13:00:05','2024-01-05 13:30:05',FALSE,FALSE,'Bolor no carro', 6,'156232512','AA11BB',3),
	(4,'2024-01-05 15:00:05','2024-01-05 15:30:05',FALSE,FALSE,NULL, 6,'156232512','AA11BB',2);

    
