-- USUARIOS
INSERT INTO usuario (nombre, apellidos, email, contrasenia, tipo_usuario, telefono) VALUES
                                                                                        ('ADMIN','ADMIN ADMIN','admin@gym.test','admin','ADMIN','111111111'),
                                                                                        ('Mario','López Ruiz','mario@gym.test','1234','CLIENTE','600333444'),
                                                                                        ('Sofía','Santos Díaz','sofia@gym.test','1234','ENTRENADOR','600555666'),
                                                                                        ('David','Muñoz León','david@gym.test','1234','ENTRENADOR','600777888');

-- ENTRENADORES (id = id_usuario)
INSERT INTO entrenador (id_entrenador, especialidad, experiencia, hora_inicio_trabajo, hora_fin_trabajo) VALUES
                                                                                                             ((SELECT id_usuario FROM usuario WHERE email='sofia@gym.test'),'Yoga y Pilates',6,'08:00','14:00'),
                                                                                                             ((SELECT id_usuario FROM usuario WHERE email='david@gym.test'),'Crossfit',4,'14:00','20:00');

-- CLIENTES (id = id_usuario)
INSERT INTO cliente (id_cliente, fecha_nacimiento, genero, objetivos) VALUES
                                                                          ((SELECT id_usuario FROM usuario WHERE email='admin@gym.test'),'1990-01-01','MASCULINO','Admin demo'),
                                                                          ((SELECT id_usuario FROM usuario WHERE email='mario@gym.test'),'1988-11-02','MASCULINO','Perder peso y ganar resistencia');

-- ACTIVIDADES
INSERT INTO actividad (nombre, descripcion, tipo_actividad, nivel) VALUES
                                                                       ('Yoga Vinyasa','Sesión fluida de yoga','FLEXIBILIDAD','AMATEUR'),
                                                                       ('HIIT','Intervalos de alta intensidad','CARDIO','NORMAL');

-- SALAS
INSERT INTO sala (nombre, capacidad, ubicacion, descripcion) VALUES
                                                                 ('Sala A',20,'Planta 1','Sala con espejos'),
                                                                 ('Sala B',15,'Planta 2','Sala funcional');

-- MAQUINARIA
INSERT INTO maquinaria (nombre, cantidad_total, tipo_actividad, descripcion) VALUES
                                                                                 ('CintaCorrer',8,'CARDIO','Cintas Technogym'),
                                                                                 ('Mancuernas',20,'FUERZA','Juego 2–20kg'),
                                                                                 ('Esterillas',30,'COMPLETA','Esterillas antideslizantes');

-- RESERVAS
INSERT INTO reserva
(fecha_hora_inicio, fecha_hora_fin, estado, comentarios, id_cliente, id_entrenador, id_actividad, id_sala)
VALUES
    ('2025-10-21 09:00:00','2025-10-21 10:00:00','Completada','Primera clase',
     (SELECT id_usuario FROM usuario WHERE email='admin@gym.test'),
     (SELECT id_usuario FROM usuario WHERE email='sofia@gym.test'),
     (SELECT id_actividad FROM actividad WHERE nombre='Yoga Vinyasa'),
     (SELECT id_sala FROM sala WHERE nombre='Sala A')
    ),
    ('2025-10-22 18:00:00','2025-10-22 19:00:00','Pendiente',NULL,
     (SELECT id_usuario FROM usuario WHERE email='mario@gym.test'),
     (SELECT id_usuario FROM usuario WHERE email='david@gym.test'),
     (SELECT id_actividad FROM actividad WHERE nombre='HIIT'),
     (SELECT id_sala FROM sala WHERE nombre='Sala B')
    );

-- RESERVA ↔ MAQUINARIA
INSERT INTO reserva_maquinaria (id_reserva, id_maquinaria, cantidad) VALUES
                                                                         (
                                                                             (SELECT r.id_reserva FROM reserva r
                                                                                                           JOIN usuario u ON r.id_cliente=u.id_usuario AND u.email='admin@gym.test'
                                                                              WHERE r.id_actividad=(SELECT id_actividad FROM actividad WHERE nombre='Yoga Vinyasa')
                                                                             ),
                                                                             (SELECT id_maquinaria FROM maquinaria WHERE nombre='Esterillas'),
                                                                             10
                                                                         ),
                                                                         (
                                                                             (SELECT r.id_reserva FROM reserva r
                                                                                                           JOIN usuario u ON r.id_cliente=u.id_usuario AND u.email='mario@gym.test'
                                                                              WHERE r.id_actividad=(SELECT id_actividad FROM actividad WHERE nombre='HIIT')
                                                                             ),
                                                                             (SELECT id_maquinaria FROM maquinaria WHERE nombre='Mancuernas'),
                                                                             4
                                                                         );