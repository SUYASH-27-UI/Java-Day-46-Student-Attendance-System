import java.util.*;
import java.io.*;

class Student {
    int id;
    String name;
    int totalClasses;
    int presentClasses;

    Student(int id, String name, int totalClasses, int presentClasses) {
        this.id = id;
        this.name = name;
        this.totalClasses = totalClasses;
        this.presentClasses = presentClasses;
    }

    double getPercentage() {
        if (totalClasses == 0) {
            return 0;
        }

        return (presentClasses * 100.0) / totalClasses;
    }

    void display() {
        System.out.printf(
            "ID: %d | Name: %s | Present: %d/%d | Attendance: %.2f%%%n",
            id,
            name,
            presentClasses,
            totalClasses,
            getPercentage()
        );
    }
}

public class Main {

    static ArrayList<Student> students = new ArrayList<>();

    static final String FILE_NAME = "students.csv";

    static void loadStudents() {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return;
        }

        try (Scanner reader = new Scanner(file)) {

            if (reader.hasNextLine()) {
                reader.nextLine();
            }

            while (reader.hasNextLine()) {
                String[] data = reader.nextLine().split(",", -1);

                if (data.length == 4) {
                    try {
                        int id = Integer.parseInt(data[0]);
                        int total = Integer.parseInt(data[2]);
                        int present = Integer.parseInt(data[3]);

                        if (id > 0 && total >= 0
                                && present >= 0 && present <= total
                                && !data[1].trim().isEmpty()) {

                            students.add(
                                new Student(id, data[1], total, present)
                            );
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Skipping invalid student record.");
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error loading student data!");
        }
    }

    static void saveStudents() {
        try (FileWriter writer = new FileWriter(FILE_NAME)) {

            writer.write("ID,Name,TotalClasses,PresentClasses\n");

            for (Student s : students) {
                writer.write(
                    s.id + "," +
                    s.name + "," +
                    s.totalClasses + "," +
                    s.presentClasses + "\n"
                );
            }

        } catch (IOException e) {
            System.out.println("Error saving data!");
        }
    }

    static Student findStudent(int id) {
        for (Student s : students) {
            if (s.id == id) {
                return s;
            }
        }

        return null;
    }

    static void addStudent(Scanner sc) {
        System.out.print("Enter Student ID: ");

        int id;

        try {
            id = Integer.parseInt(sc.nextLine());

            if (id <= 0 || findStudent(id) != null) {
                System.out.println("Invalid or duplicate ID!");
                return;
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid ID!");
            return;
        }

        System.out.print("Enter Student Name: ");
        String name = sc.nextLine().trim();

        if (name.isEmpty() || name.contains(",")) {
            System.out.println("Invalid name!");
            return;
        }

        students.add(new Student(id, name, 0, 0));

        saveStudents();

        System.out.println("Student registered successfully!");
    }

    static void markAttendance(Scanner sc) {
        if (students.isEmpty()) {
            System.out.println("No students registered!");
            return;
        }

        System.out.println("\n--- Mark Attendance ---");
        System.out.println("Enter P for Present or A for Absent.");

        for (Student s : students) {

            while (true) {
                System.out.print(s.name + ": ");

                String status = sc.nextLine().trim();

                if (status.equalsIgnoreCase("P")) {
                    s.totalClasses++;
                    s.presentClasses++;
                    break;
                }
                else if (status.equalsIgnoreCase("A")) {
                    s.totalClasses++;
                    break;
                }
                else {
                    System.out.println("Enter only P or A!");
                }
            }
        }

        saveStudents();

        System.out.println("Attendance saved successfully!");
    }

    static void showAttendance() {
        if (students.isEmpty()) {
            System.out.println("No records found.");
            return;
        }

        System.out.println("\n--- Attendance Records ---");

        for (Student s : students) {
            s.display();
        }
    }

    static void showLowAttendance() {
        boolean found = false;

        System.out.println("\n--- Attendance Below 75% ---");

        for (Student s : students) {
            if (s.totalClasses > 0 && s.getPercentage() < 75) {
                s.display();
                found = true;
            }
        }

        if (!found) {
            System.out.println("No students below 75%.");
        }
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        loadStudents();

        while (true) {
            System.out.println("\n===== ATTENDANCE MANAGEMENT =====");
            System.out.println("1. Add Student");
            System.out.println("2. Mark Attendance");
            System.out.println("3. Show Attendance");
            System.out.println("4. Show Low Attendance Students");
            System.out.println("5. Exit");

            System.out.print("Enter choice: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    addStudent(sc);
                    break;

                case "2":
                    markAttendance(sc);
                    break;

                case "3":
                    showAttendance();
                    break;

                case "4":
                    showLowAttendance();
                    break;

                case "5":
                    System.out.println("Program closed.");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}
