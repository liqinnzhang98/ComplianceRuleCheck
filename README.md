<br />
<div align="center">
  <a href="https://github.com/irgiob/apromore_compliance_centre">
    <img src="frontend/src/assets/logo-colour.svg" alt="Logo" height="80">
  </a>

  <h1 align="center">Compliance Centre</h1>

  <p align="center">
    Extending Apromore's process mining software for managing process compliance
    <br />
    <br />
    <a href="https://comply.cloud.ut.ee/">Demo</a>
    ·
    <a href="https://confluence.cis.unimelb.edu.au:8443/display/SWEN900172023AZ/Home">Confluence</a>
    ·
    <a href="https://jira.cis.unimelb.edu.au:8444/secure/RapidBoard.jspa?projectKey=SWEN900172023AZ">Jira</a>
  </p>
</div>

<details>
<summary>Table of Contents</summary>

-   [About The Project](#about-the-project)
-   [Getting Started](#getting-started)
    -   [Requirements](#requirements)
    -   [Quick Start](#quick-start)
    -   [Booting Up](#booting-up)
    -   [Connecting a MySQL database](#connecting-a-mysql-database)
-   [Project Structure](#project-structure)
    -   [Backend Structure](#backend-structure)
    -   [Frontend Structure](#frontend-structure)

</details>

<br/>

## About The Project

[![Risk & Obligation Register Page][ui-screenshot]](https://comply.cloud.ut.ee/register)

The compliance centre serves as a single access point for assessing business process compliance throughout its lifecycle. The goal is to create a comprehensive solution to address identified challenges related to business process compliance by developing a plugin for the Apromore portal. The plugin must prioritise user experience, be easily scalable, and enable users to pinpoint areas of non-compliance and develop effective solutions based on a complete log of activities. The effectiveness of the solution will be tested through a case study conducted by one of Australia's prominent banks, and Apromore clients will have the opportunity to directly test and experiment with the solution.

Our role in this larger project is to work with the Apromore team to develop a proof of concept (PoC) prototype for the compliance centre that will assist a business analyst in completing compliance checks on a set of event logs for a specific set of processes. These compliance checks are done by creating a set of compliance rules, which in turn are generated from a set of risks and obligations that are related to the process in some way. These risks and obligations contain control descriptions that need to be mapped to pre-defined compliance templates, to generate the programmatic compliance rules that will be checked against the event log data. The compliance checks and any associated violations must then must be outputted as an aggregated report to the analyst detailing which compliance rules were violated and where specifically in the process they were violated.

[![Project Visual Diagram][visual-diagram]]()

<br/>

## Getting Started

This repository packages a React frontend (powered by Vite) with a Spring Boot backend into a single WAR file, as such the strucutre has been optimised for deployment, and development by default is not straightforward. In order to support automatic reloading both for frontend and backend, 3 seperate processes need to be run simultaneously. To simplify the developer experience. A set of VSCode tasks have been to automatically start all 3 processes. Read below for how to get started.

### Requirements

#### Before you begin: check your Java version

-   VSCode (+ Prettier extension for code formatting)
-   MySQL database (instructions below)
-   Python & Pip
-   Java (Check your java version by going to your terminal and typing `java --version`, if you don't have a version between 17 and 19, follow the steps outlined in [this guide](https://www3.cs.stonybrook.edu/~amione/CSE114_Course/materials/resources/InstallingJava17.pdf))

### Quick Start

1. Clone this repository to your device and open it in VSCode
2. Run the `Init` task once after initial clone (instructions for running tasks are in the **Booting Up** section below)
3. Go to `datasource.properties` and replace the placeholders with your local database credentials (instructions for setting up a local database are in the **Connecting a local database** section below)
4. Run any of the `Start X Dev` tasks to begin development
5. Run the `Stop All Tasks` task to stop development

### Booting Up

Activating development mode is done using VSCode Tasks. After opening this repository in VSCode, type ⌘ + Shift + P on Mac or CTRL + Shift + P on Windows to open the Command Palette, type `task` and select `Tasks: Run Task`. This will bring up a list of defined starting scripts, outlined below. Select the task you want to run to start it.
| Task Name | Description | Access URL |
| ------------- | ------------- | ------------- |
| Init | initialise repository with all dependencies | N/A |
| Stop All Tasks | Use this to terminate all the processes | N/A |
| Start Non-Dev Server | Build and run a production instance of the system (equivalent to running ./gradlew bootRun) | localhost:8080 |
| Start Fullstack Dev | Build and run system with automatic reload for both frontend and backend | localhost:5173 |
| Start Backend Dev | Build and run system with backend automatic reload | localhost:8080 |
| Start Frontend Dev | Build and run system with frontend automatic reload | localhost:5173 |
| Start Backend Debug | Build and run system with debugger attached to backend server | localhost:8080 |

Select which task most applies to your development work. The URL you use to access the frontend component depends on what start script you run. After you're done developing, run the `Stop All Tasks` script to terminate all the processes.

### Connecting a MySQL database

1. To connect the project with a database, you first need a running MySQL instance by setting up a local MySQL server on your device. As systems can vary widely, specific installation instructions cannot be specified here. Instead, follow the instructions on the (MySQL Documentation Website)[https://dev.mysql.com/doc/mysql-installation-excerpt/5.7/en/] that are relevant for your specific device. If you followed all the steps correctly, you should be able to access MySQL from the command line.

2. After setting up your database, take your database name, username, and password, and add it to the `datasource.properties` file. Although it varies, the file will tend to look like the one below (you only need to change the first 3 lines):

    - spring.datasource.url=jdbc:mysql://HOSTNAME:PORT/DATABASE_NAME
    - spring.datasource.username=YOUR_USERNAME
    - spring.datasource.password=YOUR_PASSWORD

3. Since we're using JPA, our system will automatically generate the tables for you, so all you need to do is run the system at least once so Spring Boot can generate the tables.

<br>

## Project Structure

The project comprises of Java REST API backend powered by Spring Boot, plus a React frontend powered by Vite. The entire project is compiled entirely using Gradle to a WAR file, though NPM is also used for frontend package management. Prettier is used to format code across both the frontend and backend, and finally VSCode workspace files are used to for developer configurations. All these components are accessible from the root directory of the project, with the main sub-directories of `frontend` and `backend` being detailed below:

### Backend Structure

<div align="center">

<a href="">![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)</a>
<a href="">![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)</a>
<a href="">![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)</a>
<a href="">![MySQL](https://img.shields.io/badge/mysql-%2300f.svg?style=for-the-badge&logo=mysql&logoColor=white)</a>
<a href="">![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)</a>

</div>

The backend Java REST API powered by Spring Boot is relatively straight forward and doesn't have many dependencies, but the ones that are installed are detailed below:

-   Spring Boot for REST API interface abstraction
-   JDBC for connecting to and querying the application database
-   Postgresql for acting as the driver to a external Postgres instance
-   Jupiter, Hamcrest and Mockito are all included for testing
-   Lombok to reduce boilerplate using annotations
-   Spring Boot DevTools for automatic reload of backend

All backend code is contained within the source folder and is split between two subfolders: `main` and `test`. Both folder's should be structured by layer to the packages below:

-   model: the different resources/domain objects available in our rest API
-   controller: the REST API endpoints for each model
-   dto: the data transfer objects for each model/endpoint
-   service: the business logic for each model
-   repository: the data access layer for each model
-   other folders can include config, util, and exception

### Frontend Structure

<div align="center">

<a href="">![React](https://img.shields.io/badge/react-%2320232a.svg?style=for-the-badge&logo=react&logoColor=%2361DAFB)</a>
<a href="">![TypeScript](https://img.shields.io/badge/typescript-%23007ACC.svg?style=for-the-badge&logo=typescript&logoColor=white)</a>
<a href="">![TailwindCSS](https://img.shields.io/badge/tailwindcss-%2338B2AC.svg?style=for-the-badge&logo=tailwind-css&logoColor=white)</a>
<a href="">![React Query](https://img.shields.io/badge/-React%20Query-FF4154?style=for-the-badge&logo=react%20query&logoColor=white)</a>

</div>

The frontend is a React web appliction written in TypeScript and powered by Vite. It connects to the backend by interacting with Spring Boot's REST API interface. Unlike for the backend, the frontend contains many more dependenices and external packages. Without outlining every single package, the architectually significant dependencies are outlined below:

-   ESLint and Prettier for code linting and formatting
-   Vitest for testing
-   React Router for routing
-   Tailwind CSS + Headless UI for styling (it is recommended you install the Tailwind CSS IntelliSense & Tailwind Docs VSCode extensions if you will be working with Tailwind)
-   React Query + Axios + Zod for data fetching & input validation

All frontend code is divided into two main folders: `src` and `test`. While the directory structure isn't fully finalised, this is the current plan for dividing elements:

-   assets: all non-code files such as images that are used in the web app
-   pages: components representing entire pages of the web app
-   popups: components representing entire popups of the web app
-   components: smaller single-purpose components that are used across multiple pages or popups
-   other folders can include hooks, utils, and context

[ui-screenshot]: frontend/src/assets/ui_example.png
[visual-diagram]: frontend/src/assets/project_visual_diagram.png
