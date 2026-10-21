
DROP DATABASE IF EXISTS publicidad;
CREATE DATABASE publicidad CHARACTER SET utf8mb4;
USE publicidad;



-- Los dos perfiles del sistema estan en la base y no como constantes en Java.
-- Cada perfil dice a que pagina va al entrar (url_inicio), asi el codigo no compara nombres de perfil.
CREATE TABLE perfil (
    nombre VARCHAR(30) PRIMARY KEY,
    url_inicio VARCHAR(50) NOT NULL
);

-- Las opciones del menu. El menu y los permisos salen de estas dos tablas, no de ifs en Java.
CREATE TABLE opcion_menu (
    url VARCHAR(50) PRIMARY KEY,
    texto VARCHAR(40) NOT NULL
);

-- Que opciones puede ver y usar cada perfil.
CREATE TABLE perfil_opcion (
    perfil VARCHAR(30),
    url VARCHAR(50),
    PRIMARY KEY (perfil, url),
    FOREIGN KEY (perfil) REFERENCES perfil(nombre),
    FOREIGN KEY (url) REFERENCES opcion_menu(url)
);

CREATE TABLE especializacion (
    nombre VARCHAR(30) PRIMARY KEY
);

-- Los cinco tipos de contenido, con las horas de produccion y el factor de la tabla del enunciado
CREATE TABLE tipo_contenido (
    nombre VARCHAR(30) PRIMARY KEY,
    horas_produccion INT NOT NULL,
    factor DECIMAL(3,1) NOT NULL
);

CREATE TABLE estado_campania (
    nombre VARCHAR(20) PRIMARY KEY
);



-- Clientes de la agencia. El diseñador puede estar autorizado para varias cuentas.
CREATE TABLE cuenta (
    nombre VARCHAR(50) PRIMARY KEY
);

-- La clave es el legajo. El dni es un dato mas (unico), no una segunda clave
CREATE TABLE disenador (
    legajo INT PRIMARY KEY,
    nombre VARCHAR(40) NOT NULL,
    apellido VARCHAR(40) NOT NULL,
    dni VARCHAR(10) NOT NULL UNIQUE,
    fecha_nacimiento DATE NOT NULL,
    especializacion VARCHAR(30) NOT NULL,
    celular VARCHAR(20) NOT NULL,
    FOREIGN KEY (especializacion) REFERENCES especializacion(nombre)
);

-- La clave es el nombre de la plataforma
CREATE TABLE plataforma (
    nombre VARCHAR(40) PRIMARY KEY,
    formato_principal VARCHAR(40) NOT NULL,
    alcance_maximo DECIMAL(8,2) NOT NULL,
    costo_fijo DECIMAL(12,2) NOT NULL,
    cpc DECIMAL(8,2) NOT NULL
);

-- Un diseñador puede estar autorizado para muchas cuentas, y una cuenta tiene muchos diseñadores
CREATE TABLE disenador_cuenta (
    legajo INT,
    cuenta VARCHAR(50),
    PRIMARY KEY (legajo, cuenta),
    FOREIGN KEY (legajo) REFERENCES disenador(legajo),
    FOREIGN KEY (cuenta) REFERENCES cuenta(nombre)
);

-- Una plataforma la pueden usar muchos diseñadores, y un diseñador usa muchas plataformas
CREATE TABLE disenador_plataforma (
    legajo INT,
    plataforma VARCHAR(40),
    PRIMARY KEY (legajo, plataforma),
    FOREIGN KEY (legajo) REFERENCES disenador(legajo),
    FOREIGN KEY (plataforma) REFERENCES plataforma(nombre)
);

-- La clave es el nombre de usuario. Si el usuario es un diseñador, guarda su legajo

CREATE TABLE usuario (
    usuario VARCHAR(30) PRIMARY KEY,
    password VARCHAR(30) NOT NULL,
    perfil VARCHAR(30) NOT NULL,
    legajo INT UNIQUE,
    FOREIGN KEY (perfil) REFERENCES perfil(nombre),
    FOREIGN KEY (legajo) REFERENCES disenador(legajo)
);

-- Campaña: es la unica tabla con id autonumerico, porque no tiene un dato natural que la identifique.
CREATE TABLE campania (
    id INT AUTO_INCREMENT PRIMARY KEY,
    legajo INT NOT NULL,
    cuenta VARCHAR(50) NOT NULL,
    plataforma VARCHAR(40) NOT NULL,
    tipo_contenido VARCHAR(30) NOT NULL,
    -- Guardo el costo calculado al asignar: si despues cambia el CPC de la plataforma,
    -- la pauta ya asignada no cambia. Las horas NO las guardo: salen del tipo de contenido.
    costo_total DECIMAL(14,2) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    FOREIGN KEY (legajo) REFERENCES disenador(legajo),
    FOREIGN KEY (cuenta) REFERENCES cuenta(nombre),
    FOREIGN KEY (plataforma) REFERENCES plataforma(nombre),
    FOREIGN KEY (tipo_contenido) REFERENCES tipo_contenido(nombre),
    FOREIGN KEY (estado) REFERENCES estado_campania(nombre)
);

-- =====================================================================================
-- UNICO PROCEDURE DEL SCRIPT. Lo agrego SOLO para cumplir el punto del enunciado que pide
-- usar CallableStatement (CallableStatement sirve para llamar procedures). Todo lo demas
-- del sistema usa Statement y PreparedStatement.
-- Se llama desde Java en CampaniaDAOJdbc.agregar(...): cuenta cuantas campañas activas
-- (PENDIENTE o INICIADA) tiene una plataforma. Recibe la plataforma (IN) y devuelve la
-- cantidad (OUT).
-- =====================================================================================
DELIMITER //
CREATE PROCEDURE sp_contar_campanias_activas(IN p_plataforma VARCHAR(40), OUT p_cantidad INT)
BEGIN
    SELECT COUNT(*) INTO p_cantidad
    FROM campania
    WHERE plataforma = p_plataforma AND estado IN ('PENDIENTE', 'INICIADA');
END //
DELIMITER ;

-- ---------- Datos ----------

INSERT INTO perfil VALUES
    ('Ejecutivo de cuentas', '/campanias'),
    ('Diseñador', '/mis-campanias');

INSERT INTO opcion_menu VALUES
    ('/campanias', 'Campañas'),
    ('/disenadores', 'Diseñadores'),
    ('/plataformas', 'Plataformas'),
    ('/mis-campanias', 'Mis campañas');

INSERT INTO perfil_opcion VALUES
    ('Ejecutivo de cuentas', '/campanias'),
    ('Ejecutivo de cuentas', '/disenadores'),
    ('Ejecutivo de cuentas', '/plataformas'),
    ('Diseñador', '/mis-campanias');

INSERT INTO especializacion VALUES ('Gráfico'), ('Audiovisual'), ('Digital');

INSERT INTO tipo_contenido VALUES
    ('Banner Estático', 2, 1.0),
    ('Video Corto', 8, 3.0),
    ('Carrusel', 4, 1.5),
    ('Audio Spot', 3, 1.2),
    ('Artículo de Blog', 6, 0.8);

-- Van en mayuscula porque son los mismos nombres que el enum EstadoCampania de Java.
INSERT INTO estado_campania VALUES ('PENDIENTE'), ('INICIADA'), ('FINALIZADA');

INSERT INTO cuenta VALUES ('Coca Cola'), ('Nike'), ('Personal');

INSERT INTO disenador VALUES
    (1001, 'Lucía', 'Fernández', '30111222', '1992-03-14', 'Gráfico', '1155550001'),
    (1002, 'Martín', 'Gómez', '31222333', '1990-07-02', 'Audiovisual', '1155550002'),
    (1003, 'Sofía', 'Ramírez', '32333444', '1995-11-23', 'Digital', '1155550003');

INSERT INTO plataforma VALUES
    ('Google Ads', 'Banner digital', 50.00, 1500.00, 0.35),
    ('Facebook', 'Carrusel', 30.00, 1000.00, 0.25),
    ('TV Cable', 'Video corto', 8.00, 5000.00, 1.20),
    ('Radio FM', 'Audio spot', 5.00, 800.00, 0.50);

INSERT INTO disenador_cuenta VALUES
    (1001, 'Coca Cola'), (1001, 'Nike'),
    (1002, 'Coca Cola'),
    (1003, 'Nike'), (1003, 'Personal');

INSERT INTO disenador_plataforma VALUES
    (1001, 'Google Ads'), (1001, 'Facebook'),
    (1002, 'TV Cable'), (1002, 'Google Ads'),
    (1003, 'Facebook'), (1003, 'Radio FM');

INSERT INTO usuario VALUES
    ('ejecutivo', 'ejecutivo123', 'Ejecutivo de cuentas', NULL),
    ('lucia', 'lucia123', 'Diseñador', 1001),
    ('martin', 'martin123', 'Diseñador', 1002),
    ('sofia', 'sofia123', 'Diseñador', 1003);
