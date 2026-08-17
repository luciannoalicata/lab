<div align="center">

<img src="src/reportes/img/biotec_logo.png" alt="BIOTEC Logo" width="180"/>
---
### Laboratory Information System

**Sistema de Gestión Integral para Laboratorios de Análisis Clínicos**

[![Java](https://img.shields.io/badge/Java-21_LTS-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://adoptium.net)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://dev.mysql.com)
[![JasperReports](https://img.shields.io/badge/JasperReports-6.21.5-red?style=for-the-badge)](https://community.jaspersoft.com)
[![License](https://img.shields.io/badge/License-Proprietary-lightgrey?style=for-the-badge)](LICENSE)
[![Version](https://img.shields.io/badge/Version-1.0.0_RC-brightgreen?style=for-the-badge)](CHANGELOG.md)

</div>

---
## 👨‍💻 Autor

**Luciano Alicata**
Estudiante de Ingeniería en Sistemas de Información 
## 📋 Tabla de Contenidos

- [Descripción](#-descripción)
- [Características](#-características-principales)
- [Tecnologías](#-tecnologías-utilizadas)
- [Arquitectura](#-arquitectura)
- [Requisitos](#-requisitos-del-sistema)
- [Instalación](#-instalación)
- [Configuración](#-configuración)
- [Capturas](#-capturas-de-pantalla)
- [Autor](#-autor)

---

## 📌 Descripción

**BIOTEC LIS** es un sistema de información de laboratorio clínico desarrollado como proyecto de escritorio en Java. Cubre el ciclo operativo completo de un laboratorio de análisis clínicos: desde el registro del paciente hasta la generación y entrega del informe médico en PDF con firma bioquímica y valores de referencia.

> ⚠️ **v1.0 Release Candidate** — Sistema funcional y estable, actualmente en fase de validación clínica.

---

## ✨ Características Principales

### 👥 Gestión de Pacientes
- CRUD completo con búsqueda en tiempo real (por apellido, nombre o DNI)
- Visualización de estudios realizados
- Validación de campos con restricciones por tipo de dato

### 🔬 Gestión de Análisis
- Flujo guiado de 3 pasos: selección de prácticas → carga de resultados → impresión
- Cálculo automático de precio según UB × arancel de obra social

### 📋 Nomenclador Bioquímico Único (NBU)
- Catálogo de prácticas con estructura padre-hijo (determinaciones compuestas)
- Prioridades absolutas para ordenamiento en el informe PDF
- Separadores visuales por sección (Hemograma, Eritrosedimentación, etc.)
- Reordenamiento con botones ▲/▼ persistido en base de datos

### 📄 Generación de Informes PDF
- JasperReports con 4 formatos: A4/A5 × Vertical/Horizontal
- Logo institucional y firma bioquímica configurables desde el software

### 🏥 Módulos Adicionales
- **Gestión de Médicos** — CRUD con especialidades y matrícula
- **Obras Sociales** — CRUD con código y arancel por UB
- **Gestión de Usuarios** — 4 roles con permisos diferenciados
- **Auditoría** — Log completo de todas las operaciones del sistema
- **Ajustes** — Configuración institucional, rutas, valor UB y formato de impresión

---

## 🛠 Tecnologías Utilizadas

| Tecnología | Versión | Uso |
|---|---|---|
| **Java SE** | 21 LTS | Lenguaje principal y plataforma de ejecución |
| **Java Swing** | JDK 21 | Interfaz gráfica de escritorio |
| **MySQL** | 8.0.x | Base de datos relacional (InnoDB) |
| **JasperReports** | 6.21.5 | Generación de informes PDF |
| **mysql-connector-j** | 8.0.31 | Driver JDBC para MySQL |
| **jcalendar** | 1.4 | Componente de selección de fechas |
| **openpdf** | 1.3.32 | Motor PDF subyacente de JasperReports |
| **Apache Commons** | varios | Soporte interno de JasperReports |
| **Apache NetBeans** | 21+ | IDE de desarrollo |

---

## 🏛 Arquitectura

BIOTEC implementa el patrón **MVP (Model-View-Presenter)** con navegación de ventana única basada en **CardLayout**. Esta arquitectura garantiza que la capa de presentación sea completamente intercambiable sin modificar la lógica de negocio.
