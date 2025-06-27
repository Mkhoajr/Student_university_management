## STUDENT UNIVERSITY MANAGEMENT PROJECT 

A Java-based desktop application designed to help manage Student, Teacher, and academic information efficiently within a university environment.

## Features

_ User Authentication (Admin, Teacher, Student)  
_ Role-based access and permissions  
_ Using SHA-256 to hash the Password  
_ OTP Email Verification (JavaMail) in Login Form

**1. Admin**    

_ A dashboard has charts (Bar chart & Pie chart) and labels to statistics for all Teachers and students in the University  
_ Student and Teacher Management  
_ Course and Subject management  
_ Send and receive notifications/messages to All Teachers and Students

**2. Teacher**  

_ The teacher has the schedule that the students have registered for the course  
_ The teacher can grade all the students in the course  
_ Student Grade Management (GPA calculation and classification)  
_ Export the student's grade to an Excel file  
_ Send/receive messages from the Admin  

**3. Student**    

_ Student can register and grade results for the Course with the teacher in charge of that class  
_ Export the student's GPA to PDF    
_ Send/receive messages from the Admin

## Technologies Used    

_ Java 22  
_ JavaFX (UI Framework)  
_ Hibernate (ORM for database interaction)  
_ Maven (Build tool)  
_ MySQL (Database)  
_ JavaMail API   
_ Apache POI (For exporting to Excel)  
_ iText7 (For exporting to PDF)  
_ Socket programming  

## Database  
<img src="https://github.com/user-attachments/assets/ffe444cf-800c-4aee-88be-e99e44171e38" width="600" alt="Database" />


## Project Structure  

<img src="https://github.com/user-attachments/assets/3033aed8-bfd8-49e7-a08a-62f62de586be" width="600" alt="Project structure" />

### Explanation of Key Packages:

- **controller/**: Handles interaction between the UI (JavaFX) and business logic.
- **dao/**: Provides CRUD operations with the database using Hibernate.
- **main/**: Entry point of the application (`Main.java`).
- **model/**: Represents the system's data models (e.g., Student, Class, Grade).
- **util/**: Contains reusable helper methods and database config utilities.

### Resources

- **`resources/`**:
  - `*.fxml`: UI layout files for JavaFX
  - `*.css`: Custom styles
  - Image and configuration assets

### Tests

- **`src/test/java/`**: Includes unit tests to validate the correctness of DAO and utility logic.

### Dependencies

- **`pom.xml`**: Defines project dependencies, plugins, and build lifecycle using Maven.
- **JRE System Library [JavaSE-1.8] & Maven Dependencies**: Provides Java standard libraries and external libraries like JavaFX, Hibernate, JavaMail, etc.

### UI Screenshots  

<figure>
  <img src="https://github.com/user-attachments/assets/8c616cf8-71a1-4d80-a4b6-1107610eb388" width="600" alt="Login Form" />
</figure>  

<figure>  
  <img src="https://github.com/user-attachments/assets/6a79c828-096d-43b9-9518-827d28772b9c" width="600" alt="Forget Pass Form" />
</figure>  

<figure>
  <img src="https://github.com/user-attachments/assets/b5e471dd-a460-4ee9-8a47-769589888397" width="600" alt="Verify Pass Form" />
</figure>  

### Admin UI

<figure>
  <img src="https://github.com/user-attachments/assets/3846ec57-8185-41e5-959e-f080556c8c27" width="600" alt="Admin UI" />  
</figure>

<figure>
  <img src="https://github.com/user-attachments/assets/b3eed321-d058-4382-8cb1-373ea589bf88" width="600" alt="Admin Dashboard 1" />  
</figure>

<figure>
  <img src="https://github.com/user-attachments/assets/301c13e6-22f9-4ee9-8388-75207627acd6" width="600" alt="Admin Dashboard 2" />  
</figure>

<figure>
  <img src="https://github.com/user-attachments/assets/ed0fbf65-1736-4323-abbe-e695b6ce78fd" width="600" alt="Student Management 1" />  
</figure>

<figure>
  <img src="https://github.com/user-attachments/assets/7e5032c6-0d04-49cb-905c-2519e201ba5b" width="600" alt="Student Management 2" />  
</figure>

---

### Teacher UI

<figure>
  <img src="https://github.com/user-attachments/assets/982eb237-7833-4a4d-9bea-a89a0a463307" width="600" alt="Teacher Dashboard" />  
</figure>

<figure>
  <img src="https://github.com/user-attachments/assets/c141f10a-f7dc-43ec-ba18-c1867cb33762" width="600" alt="Teacher Schedule" />  
</figure>

<figure>
  <img src="https://github.com/user-attachments/assets/d17be737-0d66-4ac7-ac35-589e61f74a56" width="600" alt="Teacher Grading Export Excel" />  
</figure>

<figure>
  <img src="https://github.com/user-attachments/assets/cbfd1826-fd0d-4520-b4a1-6e31d10fc8e5" width="600" alt="Grading 1" />  
</figure>

<figure>
  <img src="https://github.com/user-attachments/assets/a03fbef3-eb16-44e8-b538-8041277ef2d0" width="600" alt="Grading 2" />  
</figure>

---

### Student UI

<figure>
  <img src="https://github.com/user-attachments/assets/aa32077d-9e80-445d-9e4b-a71b0dc6f052" width="600" alt="Student Dashboard" />  
</figure>

<figure>
  <img src="https://github.com/user-attachments/assets/54d03872-26e9-43b8-baaf-9018ef9e9040" width="600" alt="Course Registration" />  
</figure>

<figure>
  <img src="https://github.com/user-attachments/assets/94acedb1-df89-4524-9618-f925c008a723" width="600" alt="Export to PDF" />  
</figure>

<figure>
  <img src="https://github.com/user-attachments/assets/1938ccc1-e51f-4f31-9b09-af5f8f868a9c" width="600" alt="Registered Course" />  
</figure>

<figure>
  <img src="https://github.com/user-attachments/assets/c2ef6e45-1ae7-4f63-bdd8-de52aac87fd6" width="600" alt="Course GPA 1" />  
</figure>

<figure>
  <img src="https://github.com/user-attachments/assets/96009cb9-7ab0-447a-8004-ed54d22f05d9" width="600" alt="Course GPA 2" />  
</figure>  


### About Information  
- Name: Cát Minh Khoa
- Student ID: 24IT117
- Class: 24GIT1
- University: VKU
- Academic Year: 2024–2025
- Course: Java OOP Project — Final Project for Year 1

 















