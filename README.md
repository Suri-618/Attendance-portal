# 🎓 Attendance Portal

A desktop-based Attendance Management System built using **Java Swing**, **JDBC**, and **MySQL**.  
This system allows faculty to manage attendance records and students to view their attendance details through secure login.

---

## 🚀 Features

### 👨‍🏫 Faculty
- Faculty Login
- Create Attendance Sessions
- Mark Student Attendance
- View Attendance Records

### 👨‍🎓 Student
- Student Registration
- Student Login
- View Attendance Details
- Track Attendance History

---

## 🛠 Tech Stack

- **Java (Core Java)**
- **Java Swing (GUI)**
- **JDBC (Database Connectivity)**
- **MySQL (Database)**
- **IDE:** IntelliJ / Eclipse / NetBeans

---

## 🏗 Project Structure

- **Attendance-portal/**
- **│**
-**├── DBConnection.java**
- **├── StudentLogin.java**
- **├── FacultyLogin.java**
- **├── StudentDashboard.java**
- **├── FacultyDashboard.java**
- **├── StudentRegister.java**
- **├── MarkAttendance.java**
- **├── ViewAttendance.java**
- **├── CreateSession.java**
- **└── *.java**

---

## 🗄 Database Schema

### 1️⃣ Users Table
| Column | Type |
|--------|------|
| id | INT (PK) |
| name | VARCHAR |
| email | VARCHAR |
| password | VARCHAR |
| role | VARCHAR (student/faculty) |

### 2️⃣ Attendance Table
| Column | Type |
|--------|------|
| id | INT (PK) |
| student_id | INT (FK) |
| date | DATE |
| status | VARCHAR (Present/Absent) |

