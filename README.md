# 📌 JUNO -- QR Code Based Attendance System

JUNO is a **Java-based QR Code Attendance Management System** designed
to simplify and automate the attendance process for educational
institutions. The system allows faculty to create attendance sessions
and students to mark attendance using QR code scanning.

This project reduces manual work, prevents proxy attendance, and enables
easy attendance tracking and export.

------------------------------------------------------------------------

# 🚀 Features

✅ Faculty & Student Authentication
- Faculty and students can register and login securely.

✅ QR Code Based Attendance
- Faculty generates a session QR code.
- Students scan the QR code to mark attendance.

✅ Session Management
- Faculty can create attendance sessions for classes.

✅ Attendance Tracking
- Attendance is stored and managed in the database.

✅ Excel Export
- Faculty can download attendance reports in Excel format.

✅ Student Dashboard
- Students can view their attendance records.

✅ Faculty Dashboard
- Faculty can manage sessions and view attendance.

------------------------------------------------------------------------

# 🛠️ Tech Stack

**Language** - Java

**GUI Framework** - Java Swing

**Database** - MySQL

**Libraries Used** - ZXing (QR Code generation & scanning) - Apache POI
(Excel export) - Commons Collections - XMLBeans

------------------------------------------------------------------------

# 📂 Project Structure

    JUNO/
    │
    ├── CreateSession.java
    ├── DBConnection.java
    ├── FacultyLogin.java
    ├── FacultyRegister.java
    ├── FacultyDashboard.java
    │
    ├── StudentLogin.java
    ├── StudentRegister.java
    ├── StudentDashboard.java
    │
    ├── ScanQRAttendance.java
    ├── MarkAttendance.java
    ├── ViewAttendance.java
    │
    ├── ExcelExporter.java
    ├── PasswordUtils.java
    │
    └── lib/

------------------------------------------------------------------------

# ⚙️ Installation & Setup

## 1️⃣ Clone the Repository

    git clone https://github.com/your-username/JUNO.git
    cd JUNO

## 2️⃣ Setup MySQL Database

Create a database:

    CREATE DATABASE juno_attendance;

Update database credentials in:

`DBConnection.java`

Example:

    String url = "jdbc:mysql://localhost:3306/juno_attendance";
    String user = "root";
    String password = "your_password";

## 3️⃣ Add Required Libraries

Make sure the following libraries are included:

-   ZXing Core
-   ZXing JavaSE
-   Apache POI
-   Commons Collections
-   XMLBeans

## 4️⃣ Compile the Project

    javac *.java

## 5️⃣ Run the Application

    java MainFSA

------------------------------------------------------------------------

# 📊 How It Works

1.  Faculty logs into the system\
2.  Faculty creates an attendance session\
3.  System generates a QR Code\
4.  Students scan the QR Code\
5.  Attendance is recorded in the database\
6.  Faculty can export attendance reports in Excel

------------------------------------------------------------------------

# 🎯 Future Improvements

-   Web version of the application
-   Mobile QR scanner integration
-   QR code expiry (30 seconds)
-   GPS verification
-   Email notifications
-   Admin dashboard

------------------------------------------------------------------------

# 👨‍💻 Author

**Suri**\
Computer Science Student \| Java Developer
