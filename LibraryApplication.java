import java.io.*;
import java.util.*;

class Book implements Comparable<Book> {
    private int bookId;
    private String title;
    private String author;
    private String category;
    private boolean isIssued;

    public Book(int bookId, String title, String author, String category) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.isIssued = false;
    }

    public int getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    public boolean getIsIssued() { return isIssued; }

    public void markAsIssued() { isIssued = true; }
    public void markAsReturned() { isIssued = false; }

    public void displayBookDetails() {
        System.out.println("Book ID: " + bookId);
        System.out.println("Title: " + title);
        System.out.println("Author: " + author);
        System.out.println("Category: " + category);
        System.out.println("Issued: " + isIssued);
    }

    @Override
    public int compareTo(Book other) {
        return this.title.compareToIgnoreCase(other.title);
    }
}

class Member {
    private int memberId;
    private String name;
    private String email;
    private List<Integer> issuedBooks;

    public Member(int memberId, String name, String email) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.issuedBooks = new ArrayList<>();
    }

    public int getMemberId() { return memberId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public List<Integer> getIssuedBooks() { return issuedBooks; }

    public void addIssuedBook(int bookId) { issuedBooks.add(bookId); }
    public void returnIssuedBook(int bookId) { issuedBooks.remove(Integer.valueOf(bookId)); }

    public void displayMemberDetails() {
        System.out.println("Member ID: " + memberId);
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Issued Books: " + issuedBooks);
    }
}

public class LibraryApplication {
    private Map<Integer, Book> books;
    private Map<Integer, Member> members;
    private Scanner scanner;

    public LibraryApplication() {
        books = new HashMap<>();
        members = new HashMap<>();
        scanner = new Scanner(System.in);
        loadFromFile();
    }

    public void addBook() {
        System.out.print("Enter Book ID: ");
        int id = scanner.nextInt(); scanner.nextLine();
        System.out.print("Enter Book Title: ");
        String title = scanner.nextLine();
        System.out.print("Enter Author: ");
        String author = scanner.nextLine();
        System.out.print("Enter Category: ");
        String category = scanner.nextLine();
        if (books.containsKey(id)) {
            System.out.println("Book ID already exists.");
            return;
        }
        Book b = new Book(id, title, author, category);
        books.put(id, b);
        saveToFile();
        System.out.println("Book added successfully with ID: " + id);
    }

    public void addMember() {
        System.out.print("Enter Member ID: ");
        int id = scanner.nextInt(); scanner.nextLine();
        System.out.print("Enter Member Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        if (members.containsKey(id)) {
            System.out.println("Member ID already exists.");
            return;
        }
        Member m = new Member(id, name, email);
        members.put(id, m);
        saveToFile();
        System.out.println("Member added successfully with ID: " + id);
    }

    public void issueBook() {
        System.out.print("Enter Book ID: ");
        int bookId = scanner.nextInt(); scanner.nextLine();
        System.out.print("Enter Member ID: ");
        int memberId = scanner.nextInt(); scanner.nextLine();
        Book b = books.get(bookId);
        Member m = members.get(memberId);
        if (b == null || m == null) {
            System.out.println("Invalid Book or Member ID.");
            return;
        }
        if (b.getIsIssued()) {
            System.out.println("Book already issued.");
            return;
        }
        b.markAsIssued();
        m.addIssuedBook(bookId);
        saveToFile();
        System.out.println("Book issued successfully.");
    }

    public void returnBook() {
        System.out.print("Enter Book ID: ");
        int bookId = scanner.nextInt(); scanner.nextLine();
        System.out.print("Enter Member ID: ");
        int memberId = scanner.nextInt(); scanner.nextLine();
        Book b = books.get(bookId);
        Member m = members.get(memberId);
        if (b == null || m == null) {
            System.out.println("Invalid Book or Member ID.");
            return;
        }
        if (!b.getIsIssued()) {
            System.out.println("Book is not issued.");
            return;
        }
        b.markAsReturned();
        m.returnIssuedBook(bookId);
        saveToFile();
        System.out.println("Book returned successfully.");
    }

    public void searchBooks() {
        System.out.print("Enter search keyword: ");
        String keyword = scanner.nextLine().toLowerCase();
        for (Book b : books.values()) {
            if (b.getTitle().toLowerCase().contains(keyword) ||
                b.getAuthor().toLowerCase().contains(keyword) ||
                b.getCategory().toLowerCase().contains(keyword)) {
                b.displayBookDetails();
                System.out.println();
            }
        }
    }

    public void sortBooks() {
        System.out.println("Sort by: 1. Title 2. Author 3. Category");
        int choice = scanner.nextInt(); scanner.nextLine();
        List<Book> list = new ArrayList<>(books.values());
        if (choice == 1) Collections.sort(list);
        else if (choice == 2) list.sort(Comparator.comparing(Book::getAuthor));
        else if (choice == 3) list.sort(Comparator.comparing(Book::getCategory));
        for (Book b : list) {
            b.displayBookDetails();
            System.out.println();
        }
    }

    private void loadFromFile() {
        try {
            File bookFile = new File("books.txt");
            File memberFile = new File("members.txt");
            if (bookFile.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(bookFile));
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 5) {
                        int id = Integer.parseInt(parts[0]);
                        Book b = new Book(id, parts[1], parts[2], parts[3]);
                        if (Boolean.parseBoolean(parts[4])) b.markAsIssued();
                        books.put(id, b);
                    }
                }
                br.close();
            }
            if (memberFile.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(memberFile));
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 3) {
                        int id = Integer.parseInt(parts[0]);
                        Member m = new Member(id, parts[1], parts[2]);
                        for (int i = 3; i < parts.length; i++) {
                            m.addIssuedBook(Integer.parseInt(parts[i]));
                        }
                        members.put(id, m);
                    }
                }
                br.close();
            }
        } catch (Exception e) {
            System.out.println("Error loading files: " + e.getMessage());
        }
    }

    private void saveToFile() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("books.txt"));
            for (Book b : books.values()) {
                bw.write(b.getBookId() + "," + b.getTitle() + "," + b.getAuthor() + "," + b.getCategory() + "," + b.getIsIssued());
                bw.newLine();
            }
            bw.close();
            BufferedWriter bw2 = new BufferedWriter(new FileWriter("members.txt"));
            for (Member m : members.values()) {
                bw2.write(m.getMemberId() + "," + m.getName() + "," + m.getEmail());
                for (int bookId : m.getIssuedBooks()) {
                    bw2.write("," + bookId);
                }
                bw2.newLine();
            }
            bw2.close();
        } catch (Exception e) {
            System.out.println("Error saving files: " + e.getMessage());
        }
    }

        public void mainMenu() {
        System.out.println("Welcome to City Library Digital Management System");
        while (true) {
            System.out.println("1. Add Book");
            System.out.println("2. Add Member");
            System.out.println("3. Issue Book");
            System.out.println("4. Return Book");
            System.out.println("5. Search Books");
            System.out.println("6. Sort Books");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: addBook(); break;
                case 2: addMember(); break;
                case 3: issueBook(); break;
                case 4: returnBook(); break;
                case 5: searchBooks(); break;
                case 6: sortBooks(); break;
                case 7:
                    saveToFile();
                    System.out.println("Exiting application.");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        new LibraryApplication().mainMenu();
    }
}
