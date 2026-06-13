-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 12-06-2026 a las 19:08:41
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `medialab_db`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `docentes`
--

CREATE TABLE `docentes` (
  `id` bigint(20) NOT NULL,
  `dni` varchar(8) NOT NULL,
  `nombres` varchar(100) NOT NULL,
  `facultad` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `docentes`
--

INSERT INTO `docentes` (`id`, `dni`, `nombres`, `facultad`) VALUES
(1, '44556677', 'Ing. Carlos Mendoza', 'Ingeniería de Sistemas'),
(2, '32451426', 'Ing. Martina Torres ', 'Ingeniería Civil'),
(3, '45781234', 'Lic. Juan Pérez Ramírez', 'Ingeniería de Sistemas'),
(4, '70451298', 'Dra. María Gómez Torres', 'Ingeniería Industrial'),
(5, '10457823', 'Ing. Carlos López Mendoza', 'Arquitectura'),
(6, '25896314', 'Dr. Alejandro Peralta Ruiz', 'Derecho y Ciencias Políticas'),
(7, '32987456', 'Msc. Elena Rostworowski S.', 'Ciencias de la Comunicación'),
(8, '41235678', 'Ing. Ricardo Gareca N.', 'Administración y Negocios'),
(9, '09876543', 'Lic. Sofía Benavides Prado', 'Psicología'),
(10, '72145639', 'Ing. Fernando Belaunde T.', 'Ingeniería Civil'),
(11, '42556677', 'Dra. Beatriz Merino Lucero', 'Contabilidad'),
(12, '12345678', 'Msc. Javier Pulgar Vidal', 'Ingeniería Ambiental');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `equipos`
--

CREATE TABLE `equipos` (
  `id` bigint(20) NOT NULL,
  `codigo_patrimonial` varchar(50) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `tipo` varchar(50) NOT NULL,
  `estado` varchar(20) DEFAULT 'DISPONIBLE'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `equipos`
--

INSERT INTO `equipos` (`id`, `codigo_patrimonial`, `nombre`, `tipo`, `estado`) VALUES
(1, 'UNS-001', 'Laptop HP ProBook', 'LAPTOP', 'DISPONIBLE'),
(2, 'UNS-002', 'Proyector Epson PowerLite', 'PROYECTOR', 'DISPONIBLE'),
(3, 'UNS-003', 'Proyector Epson PowerLite', 'PROYECTOR', 'DISPONIBLE'),
(4, 'UNS-004', 'Laptop HP ProBook 450 G9', 'LAPTOP', 'PRESTADO'),
(5, 'UNS-005', 'Laptop HP ProBook 450 G9', 'LAPTOP', 'DISPONIBLE'),
(6, 'UNS-006', 'Laptop HP ProBook 450 G9', 'LAPTOP', 'DISPONIBLE'),
(7, 'UNS-007', 'Laptop HP ProBook 450 G9', 'LAPTOP', 'DISPONIBLE'),
(8, 'UNS-008', 'Laptop HP ProBook 450 G9', 'LAPTOP', 'DISPONIBLE'),
(9, 'UNS-009', 'Proyector Epson PowerLite E20', 'PROYECTOR', 'DISPONIBLE'),
(10, 'UNS-010', 'Proyector Epson PowerLite E20', 'PROYECTOR', 'DISPONIBLE'),
(11, 'UNS-011', 'Proyector Epson PowerLite E20', 'PROYECTOR', 'DISPONIBLE'),
(12, 'UNS-012', 'Proyector Epson PowerLite E20', 'PROYECTOR', 'DISPONIBLE'),
(13, 'UNS-013', 'Proyector Epson PowerLite E20', 'PROYECTOR', 'DISPONIBLE');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `prestamos`
--

CREATE TABLE `prestamos` (
  `id` bigint(20) NOT NULL,
  `equipo_id` bigint(20) NOT NULL,
  `docente_id` bigint(20) NOT NULL,
  `fecha_salida` date NOT NULL,
  `fecha_limite` date NOT NULL,
  `fecha_devolucion` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `prestamos`
--

INSERT INTO `prestamos` (`id`, `equipo_id`, `docente_id`, `fecha_salida`, `fecha_limite`, `fecha_devolucion`) VALUES
(1, 4, 2, '2026-06-12', '2026-06-13', NULL);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `docentes`
--
ALTER TABLE `docentes`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `dni` (`dni`);

--
-- Indices de la tabla `equipos`
--
ALTER TABLE `equipos`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `codigo_patrimonial` (`codigo_patrimonial`);

--
-- Indices de la tabla `prestamos`
--
ALTER TABLE `prestamos`
  ADD PRIMARY KEY (`id`),
  ADD KEY `equipo_id` (`equipo_id`),
  ADD KEY `docente_id` (`docente_id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `docentes`
--
ALTER TABLE `docentes`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT de la tabla `equipos`
--
ALTER TABLE `equipos`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT de la tabla `prestamos`
--
ALTER TABLE `prestamos`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `prestamos`
--
ALTER TABLE `prestamos`
  ADD CONSTRAINT `prestamos_ibfk_1` FOREIGN KEY (`equipo_id`) REFERENCES `equipos` (`id`),
  ADD CONSTRAINT `prestamos_ibfk_2` FOREIGN KEY (`docente_id`) REFERENCES `docentes` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
