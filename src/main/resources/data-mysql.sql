-- sql
-- Archivo: `src/main/resources/data-mysql.sql`
-- Inserciones idempotentes: no fallarán ni duplicarán datos al ejecutarse varias veces.

-- USUARIOS (comprobar por email)
INSERT INTO usuario (nombre, apellidos, email, contrasenia, tipo_usuario, telefono)
SELECT 'ADMIN','ADMIN ADMIN','admin@gym.test','admin','ADMIN','111111111' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'admin@gym.test');

INSERT INTO usuario (nombre, apellidos, email, contrasenia, tipo_usuario, telefono)
SELECT 'Mario','López Ruiz','mario@gym.test','1234','CLIENTE','600333444' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'mario@gym.test');

INSERT INTO usuario (nombre, apellidos, email, contrasenia, tipo_usuario, telefono)
SELECT 'Sofía','Santos Díaz','sofia@gym.test','1234','ENTRENADOR','600555666' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'sofia@gym.test');

INSERT INTO usuario (nombre, apellidos, email, contrasenia, tipo_usuario, telefono)
SELECT 'David','Muñoz León','david@gym.test','1234','ENTRENADOR','600777888' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'david@gym.test');

-- ENTRENADORES (solo si existe el usuario y no existe el entrenador con ese id)
INSERT INTO entrenador (id_entrenador, especialidad, experiencia, hora_inicio_trabajo, hora_fin_trabajo)
SELECT u.id_usuario, 'Yoga y Pilates', 6, '08:00', '14:00'
FROM usuario u
WHERE u.email = 'sofia@gym.test'
  AND NOT EXISTS (SELECT 1 FROM entrenador e WHERE e.id_entrenador = u.id_usuario);

INSERT INTO entrenador (id_entrenador, especialidad, experiencia, hora_inicio_trabajo, hora_fin_trabajo)
SELECT u.id_usuario, 'Crossfit', 4, '14:00', '20:00'
FROM usuario u
WHERE u.email = 'david@gym.test'
  AND NOT EXISTS (SELECT 1 FROM entrenador e WHERE e.id_entrenador = u.id_usuario);

-- CLIENTES (solo si existe el usuario y no existe el cliente con ese id)
INSERT INTO cliente (id_cliente, fecha_nacimiento, genero, objetivos)
SELECT u.id_usuario, '1990-01-01', 'MASCULINO', 'Admin demo'
FROM usuario u
WHERE u.email = 'admin@gym.test'
  AND NOT EXISTS (SELECT 1 FROM cliente c WHERE c.id_cliente = u.id_usuario);

INSERT INTO cliente (id_cliente, fecha_nacimiento, genero, objetivos)
SELECT u.id_usuario, '1988-11-02', 'MASCULINO', 'Perder peso y ganar resistencia'
FROM usuario u
WHERE u.email = 'mario@gym.test'
  AND NOT EXISTS (SELECT 1 FROM cliente c WHERE c.id_cliente = u.id_usuario);

-- ACTIVIDADES (comprobar por nombre)
INSERT INTO actividad (nombre, descripcion, tipo_actividad, nivel)
SELECT 'Yoga Vinyasa','Sesión fluida de yoga','FLEXIBILIDAD','AMATEUR' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM actividad WHERE nombre = 'Yoga Vinyasa');

INSERT INTO actividad (nombre, descripcion, tipo_actividad, nivel)
SELECT 'HIIT','Intervalos de alta intensidad','CARDIO','NORMAL' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM actividad WHERE nombre = 'HIIT');

-- SALAS
INSERT INTO sala (nombre, capacidad, ubicacion, descripcion)
SELECT 'Sala A',20,'Planta 1','Sala con espejos' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sala WHERE nombre = 'Sala A');

INSERT INTO sala (nombre, capacidad, ubicacion, descripcion)
SELECT 'Sala B',15,'Planta 2','Sala funcional' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sala WHERE nombre = 'Sala B');

-- MAQUINARIA
INSERT INTO maquinaria (nombre, cantidad_total, tipo_actividad, descripcion)
SELECT 'CintaCorrer',8,'CARDIO','Cintas Technogym' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM maquinaria WHERE nombre = 'CintaCorrer');

INSERT INTO maquinaria (nombre, cantidad_total, tipo_actividad, descripcion)
SELECT 'Mancuernas',20,'FUERZA','Juego 2–20kg' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM maquinaria WHERE nombre = 'Mancuernas');

INSERT INTO maquinaria (nombre, cantidad_total, tipo_actividad, descripcion)
SELECT 'Esterillas',30,'COMPLETA','Esterillas antideslizantes' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM maquinaria WHERE nombre = 'Esterillas');

-- RESERVAS (solo si cliente/entrenador/actividad/sala existen y no hay reserva idéntica)
INSERT INTO reserva (fecha_hora_inicio, fecha_hora_fin, estado, comentarios, id_cliente, id_entrenador, id_actividad, id_sala)
SELECT
  '2025-10-21 09:00:00',
  '2025-10-21 10:00:00',
  'Completada',
  'Primera clase',
  uc.id_usuario,
  ue.id_usuario,
  a.id_actividad,
  s.id_sala
FROM usuario uc
JOIN usuario ue ON 1=1
JOIN actividad a ON 1=1
JOIN sala s ON 1=1
WHERE uc.email = 'mario@gym.test'
  AND ue.email = 'sofia@gym.test'
  AND a.nombre = 'Yoga Vinyasa'
  AND s.nombre = 'Sala A'
  AND NOT EXISTS (
    SELECT 1 FROM reserva r
    WHERE r.fecha_hora_inicio = '2025-10-21 09:00:00'
      AND r.id_cliente = uc.id_usuario
      AND r.id_actividad = a.id_actividad
  )
LIMIT 1;

INSERT INTO reserva (fecha_hora_inicio, fecha_hora_fin, estado, comentarios, id_cliente, id_entrenador, id_actividad, id_sala)
SELECT
  '2025-10-22 18:00:00',
  '2025-10-22 19:00:00',
  'Pendiente',
  NULL,
  uc.id_usuario,
  ue.id_usuario,
  a.id_actividad,
  s.id_sala
FROM usuario uc
JOIN usuario ue ON 1=1
JOIN actividad a ON 1=1
JOIN sala s ON 1=1
WHERE uc.email = 'mario@gym.test'
  AND ue.email = 'david@gym.test'
  AND a.nombre = 'HIIT'
  AND s.nombre = 'Sala B'
  AND NOT EXISTS (
    SELECT 1 FROM reserva r
    WHERE r.fecha_hora_inicio = '2025-10-22 18:00:00'
      AND r.id_cliente = uc.id_usuario
      AND r.id_actividad = a.id_actividad
  )
LIMIT 1;

-- RESERVA ↔ MAQUINARIA (solo si existe la reserva y la maquinaria y no existe la asociación)
INSERT INTO reserva_maquinaria (id_reserva, id_maquinaria, cantidad)
SELECT r.id_reserva, m.id_maquinaria, 10
FROM reserva r
JOIN usuario u ON r.id_cliente = u.id_usuario
JOIN actividad a ON r.id_actividad = a.id_actividad
JOIN maquinaria m ON m.nombre = 'Esterillas'
WHERE u.email = 'mario@gym.test'
  AND a.nombre = 'Yoga Vinyasa'
  AND NOT EXISTS (
    SELECT 1 FROM reserva_maquinaria rm
    WHERE rm.id_reserva = r.id_reserva
      AND rm.id_maquinaria = m.id_maquinaria
  )
LIMIT 1;

INSERT INTO reserva_maquinaria (id_reserva, id_maquinaria, cantidad)
SELECT r.id_reserva, m.id_maquinaria, 4
FROM reserva r
JOIN usuario u ON r.id_cliente = u.id_usuario
JOIN actividad a ON r.id_actividad = a.id_actividad
JOIN maquinaria m ON m.nombre = 'Mancuernas'
WHERE u.email = 'mario@gym.test'
  AND a.nombre = 'HIIT'
  AND NOT EXISTS (
    SELECT 1 FROM reserva_maquinaria rm
    WHERE rm.id_reserva = r.id_reserva
      AND rm.id_maquinaria = m.id_maquinaria
  )
LIMIT 1;