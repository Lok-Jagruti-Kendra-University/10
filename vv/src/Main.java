import java.util.*;

class TimetableGenerator {
    static class Faculty {
        String name;
        Faculty(String name) { this.name = name; }
    }

    static class Course {
        String name;
        Faculty faculty;
        int totalHoursPerWeek;
        int hoursAssigned = 0;

        Course(String name, Faculty faculty, int totalHoursPerWeek) {
            this.name = name;
            this.faculty = faculty;
            this.totalHoursPerWeek = totalHoursPerWeek;
        }
    }

    static class Room {
        String name;
        Room(String name) { this.name = name; }
    }

    static class Timeslot {
        String slotTime;
        Timeslot(String slotTime) { this.slotTime = slotTime; }
    }

    private List<Course> courses = new ArrayList<>();
    private List<Room> rooms = new ArrayList<>();
    private List<Timeslot> timeslots = new ArrayList<>();
    private List<Faculty> faculties = new ArrayList<>();
    private final String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private Map<String, Map<Timeslot, Map<Room, Course>>> timetable = new HashMap<>();
    private Scanner scanner = new Scanner(System.in);

    public void getUserInput() {
        System.out.print("Enter number of faculties: ");
        int facultyCount = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < facultyCount; i++) {
            System.out.print("Enter faculty name: ");
            faculties.add(new Faculty(scanner.nextLine()));
        }

        System.out.print("Enter number of courses: ");
        int courseCount = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < courseCount; i++) {
            System.out.print("Enter course name: ");
            String courseName = scanner.nextLine();
            int facultyIndex;
            while (true) {
                System.out.print("Assign faculty index (0 to " + (faculties.size() - 1) + "): ");
                facultyIndex = scanner.nextInt();
                if (facultyIndex >= 0 && facultyIndex < faculties.size()) break;
                System.out.println("Invalid index. Try again.");
            }
            System.out.print("Enter total hours per week for " + courseName + ": ");
            int totalHours = scanner.nextInt();
            scanner.nextLine();
            courses.add(new Course(courseName, faculties.get(facultyIndex), totalHours));
        }

        System.out.print("Enter number of rooms: ");
        int roomCount = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < roomCount; i++) {
            System.out.print("Enter room name: ");
            rooms.add(new Room(scanner.nextLine()));
        }

        System.out.print("Enter number of timeslots per day: ");
        int timeslotCount = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < timeslotCount; i++) {
            System.out.print("Enter timeslot (e.g., 9:00 AM - 10:00 AM): ");
            timeslots.add(new Timeslot(scanner.nextLine()));
        }
    }

    public void generateTimetable() {
        for (String day : days) {
            Map<Timeslot, Map<Room, Course>> dailySchedule = new LinkedHashMap<>();
            Map<Course, Integer> dailyCourseCount = new HashMap<>();

            // Initialize daily counters
            for (Course course : courses) {
                dailyCourseCount.put(course, 0);
            }

            // Shuffle courses daily for better distribution
            List<Course> shuffledCourses = new ArrayList<>(courses);
            Collections.shuffle(shuffledCourses);

            for (Timeslot slot : timeslots) {
                Map<Room, Course> roomAllocations = new LinkedHashMap<>();
                Set<Faculty> busyFaculty = new HashSet<>();

                for (Room room : rooms) {
                    // Find first available course for this room
                    for (Course course : shuffledCourses) {
                        if (course.hoursAssigned < course.totalHoursPerWeek &&
                                dailyCourseCount.get(course) < 2 &&
                                !busyFaculty.contains(course.faculty)) {

                            roomAllocations.put(room, course);
                            course.hoursAssigned++;
                            dailyCourseCount.put(course, dailyCourseCount.get(course) + 1);
                            busyFaculty.add(course.faculty);
                            break;
                        }
                    }
                }
                dailySchedule.put(slot, roomAllocations);
            }
            timetable.put(day, dailySchedule);
        }
    }

    public void printTimetable() {
        System.out.println("\n===== GENERATED TIMETABLE =====");
        for (String day : days) {
            System.out.println("\n📅 " + day.toUpperCase());
            Map<Timeslot, Map<Room, Course>> dailySlots = timetable.get(day);

            for (Timeslot slot : timeslots) {
                System.out.println("\n🕒 Time Slot: " + slot.slotTime);
                Map<Room, Course> roomsAllocation = dailySlots.get(slot);

                for (Room room : rooms) {
                    Course course = roomsAllocation.get(room);
                    String output = "🏫 " + room.name + ": ";
                    output += (course != null) ?
                            course.name + " (" + course.faculty.name + ")" :
                            "No class";
                    System.out.println(output);
                }
            }
        }
        System.out.println("\n===== END OF TIMETABLE =====");
    }

    public static void main(String[] args) {
        TimetableGenerator generator = new TimetableGenerator();
        generator.getUserInput();
        generator.generateTimetable();
        generator.printTimetable();
    }
}