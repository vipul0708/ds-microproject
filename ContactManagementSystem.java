import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Contact Management System using a custom singly linked list.
 * Includes phone number and email validation.
 */
public class ContactManagementSystem {

    private static final Scanner INPUT = new Scanner(System.in);
    private static final ContactList CONTACTS = new ContactList();

    // Phone number validation: exactly 10 digits
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[0-9]{10}$");

    // Email validation
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public static void main(String[] args) {

        boolean running = true;

        System.out.println("====================================");
        System.out.println("       CONTACT MANAGEMENT SYSTEM   ");
        System.out.println("====================================");

        while (running) {

            showMenu();

            int choice = readInt("Choose an option: ");

            switch (choice) {

                case 1:
                    addContact();
                    break;

                case 2:
                    CONTACTS.displayAll();
                    break;

                case 3:
                    searchContact();
                    break;

                case 4:
                    updateContact();
                    break;

                case 5:
                    deleteContact();
                    break;

                case 6:
                    System.out.println(
                            "Total contacts: " + CONTACTS.size());
                    break;

                case 0:
                    running = false;
                    System.out.println("Goodbye.");
                    break;

                default:
                    System.out.println(
                            "Please choose a number from 0 to 6.");
            }
        }

        INPUT.close();
    }

    private static void showMenu() {

        System.out.println("\n1. Add contact");
        System.out.println("2. Display all contacts");
        System.out.println("3. Search contact");
        System.out.println("4. Update contact");
        System.out.println("5. Delete contact");
        System.out.println("6. Count contacts");
        System.out.println("0. Exit");
    }

    // Add contact
    private static void addContact() {

        System.out.println("\n-- Add Contact --");

        String name = readRequired("Name: ");
        String phone = readValidPhone();
        String email = readValidEmail();
        String address = readRequired("Address: ");

        if (CONTACTS.add(name, phone, email, address)) {
            System.out.println("Contact added successfully.");
        } else {
            System.out.println(
                    "A contact with that phone number already exists.");
        }
    }

    // Validate phone number
    private static String readValidPhone() {

        while (true) {

            System.out.print("Phone (10 digits): ");
            String phone = INPUT.nextLine().trim();

            if (!PHONE_PATTERN.matcher(phone).matches()) {
                System.out.println(
                        "Invalid phone number! Enter exactly 10 digits.");
                continue;
            }

            if (CONTACTS.findByPhone(phone) != null) {
                System.out.println(
                        "This phone number already exists.");
                continue;
            }

            return phone;
        }
    }

    // Validate email
    private static String readValidEmail() {

        while (true) {

            System.out.print("Email: ");
            String email = INPUT.nextLine().trim();

            if (!EMAIL_PATTERN.matcher(email).matches()) {
                System.out.println(
                        "Invalid email! Enter a valid email address.");
            } else {
                return email;
            }
        }
    }

    // Search contact
    private static void searchContact() {

        System.out.println("\n-- Search Contact --");
        System.out.println("1. Search by phone number");
        System.out.println("2. Search by name");

        int choice = readInt("Choose search method: ");

        if (choice == 1) {

            String phone = readValidPhoneForSearch();

            Contact found = CONTACTS.findByPhone(phone);

            if (found == null) {
                System.out.println("No contact found.");
            } else {
                printContact(found);
            }

        } else if (choice == 2) {

            String name = readRequired("Name: ");
            CONTACTS.displayMatchesByName(name);

        } else {

            System.out.println("Please choose 1 or 2.");
        }
    }

    // Phone validation for search
    private static String readValidPhoneForSearch() {

        while (true) {

            System.out.print("Phone (10 digits): ");
            String phone = INPUT.nextLine().trim();

            if (PHONE_PATTERN.matcher(phone).matches()) {
                return phone;
            }

            System.out.println(
                    "Invalid phone number! Enter exactly 10 digits.");
        }
    }

    // Update contact
    private static void updateContact() {

        System.out.println("\n-- Update Contact --");

        String phone = readValidPhoneForSearch();

        Contact contact = CONTACTS.findByPhone(phone);

        if (contact == null) {
            System.out.println(
                    "No contact found with that phone number.");
            return;
        }

        System.out.println("Enter replacement details:");

        String name = readRequired("Name: ");
        String newPhone = readValidPhoneForUpdate(phone);
        String email = readValidEmail();
        String address = readRequired("Address: ");

        if (CONTACTS.update(
                phone, name, newPhone, email, address)) {

            System.out.println("Contact updated successfully.");

        } else {

            System.out.println("Update not completed.");
        }
    }

    // Validate new phone number during update
    private static String readValidPhoneForUpdate(String oldPhone) {

        while (true) {

            System.out.print("New Phone (10 digits): ");
            String phone = INPUT.nextLine().trim();

            if (!PHONE_PATTERN.matcher(phone).matches()) {
                System.out.println(
                        "Invalid phone number! Enter exactly 10 digits.");
                continue;
            }

            Contact duplicate = CONTACTS.findByPhone(phone);

            if (duplicate != null &&
                    !duplicate.phone.equalsIgnoreCase(oldPhone)) {

                System.out.println(
                        "This phone number already belongs to another contact.");
                continue;
            }

            return phone;
        }
    }

    // Delete contact
    private static void deleteContact() {

        System.out.println("\n-- Delete Contact --");

        String phone = readValidPhoneForSearch();

        if (CONTACTS.deleteByPhone(phone)) {
            System.out.println("Contact deleted successfully.");
        } else {
            System.out.println("No contact found with that phone number.");
        }
    }

    // Read integer safely
    private static int readInt(String prompt) {

        while (true) {

            System.out.print(prompt);
            String line = INPUT.nextLine().trim();

            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException ex) {
                System.out.println("Enter a whole number.");
            }
        }
    }

    // Read non-empty input
    private static String readRequired(String prompt) {

        while (true) {

            System.out.print(prompt);
            String value = INPUT.nextLine().trim();

            if (!value.isEmpty()) {
                return value;
            }

            System.out.println("This field cannot be empty.");
        }
    }

    // Print contact details
    private static void printContact(Contact contact) {

        System.out.println("Name    : " + contact.name);
        System.out.println("Phone   : " + contact.phone);
        System.out.println("Email   : " + contact.email);
        System.out.println("Address : " + contact.address);
    }

    // Contact class
    private static class Contact {

        private String name;
        private String phone;
        private String email;
        private String address;

        private Contact(String name, String phone,
                        String email, String address) {

            this.name = name;
            this.phone = phone;
            this.email = email;
            this.address = address;
        }
    }

    // Node class
    private static class Node {

        private Contact data;
        private Node next;

        private Node(Contact data) {
            this.data = data;
        }
    }

    // Custom singly linked list
    private static class ContactList {

        private Node head;
        private Node tail;
        private int count;

        // Add contact
        private boolean add(String name, String phone,
                            String email, String address) {

            if (findByPhone(phone) != null) {
                return false;
            }

            Node node = new Node(
                    new Contact(name, phone, email, address));

            if (head == null) {

                head = node;
                tail = node;

            } else {

                tail.next = node;
                tail = node;
            }

            count++;
            return true;
        }

        // Find contact by phone
        private Contact findByPhone(String phone) {

            Node current = head;

            while (current != null) {

                if (current.data.phone.equalsIgnoreCase(phone)) {
                    return current.data;
                }

                current = current.next;
            }

            return null;
        }

        // Search contacts by name
        private void displayMatchesByName(String name) {

            Node current = head;
            int matches = 0;

            while (current != null) {

                if (current.data.name.toLowerCase()
                        .contains(name.toLowerCase())) {

                    printContact(current.data);
                    System.out.println("------------------------------");
                    matches++;
                }

                current = current.next;
            }

            if (matches == 0) {
                System.out.println("No matching contact found.");
            } else {
                System.out.println("Matches found: " + matches);
            }
        }

        // Update contact
        private boolean update(String oldPhone, String name,
                               String newPhone, String email,
                               String address) {

            Contact target = findByPhone(oldPhone);

            if (target == null) {
                return false;
            }

            Contact duplicate = findByPhone(newPhone);

            if (duplicate != null && duplicate != target) {
                return false;
            }

            target.name = name;
            target.phone = newPhone;
            target.email = email;
            target.address = address;

            return true;
        }

        // Delete contact
        private boolean deleteByPhone(String phone) {

            Node current = head;
            Node previous = null;

            while (current != null) {

                if (current.data.phone.equalsIgnoreCase(phone)) {

                    if (previous == null) {
                        head = current.next;
                    } else {
                        previous.next = current.next;
                    }

                    if (current == tail) {
                        tail = previous;
                    }

                    count--;

                    if (head == null) {
                        tail = null;
                    }

                    return true;
                }

                previous = current;
                current = current.next;
            }

            return false;
        }

        // Display all contacts
        private void displayAll() {

            if (head == null) {
                System.out.println("The contact list is empty.");
                return;
            }

            System.out.println(
                    "\n-- Contact List (" + count + ") --");

            Node current = head;
            int number = 1;

            while (current != null) {

                System.out.println("Contact " + number++);

                printContact(current.data);

                System.out.println("------------------------------");

                current = current.next;
            }
        }

        // Count contacts
        private int size() {
            return count;
        }
    }
}
