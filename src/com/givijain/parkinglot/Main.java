package com.givijain.parkinglot;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.*;

// --- ENUMS ---
enum VehicleType { BIKE, CAR, TRUCK }
enum SpotType { SMALL, MEDIUM, LARGE }

// --- MODELS ---
abstract class Vehicle {
    private String plate;
    private VehicleType type;
    public Vehicle(String plate, VehicleType type) { this.plate = plate; this.type = type; }
    public VehicleType getType() { return type; }
    public String getPlate() { return plate; }
}

class Car extends Vehicle { public Car(String plate) { super(plate, VehicleType.CAR); } }
class Bike extends Vehicle { public Bike(String plate) { super(plate, VehicleType.BIKE); } }
class Truck extends Vehicle { public Truck(String plate) { super(plate, VehicleType.TRUCK); } }

record Transaction(String plate, VehicleType type, double fee) {}

class ParkingSpot {
    private int id;
    private int floor;
    private SpotType type;
    private boolean isFree = true;

    public ParkingSpot(int id, int floor, SpotType type) {
        this.id = id; this.floor = floor; this.type = type;
    }

    public boolean canFit(Vehicle v) {
        if (!isFree) return false;
        return switch (v.getType()) {
            case BIKE -> true;
            case CAR -> type == SpotType.MEDIUM || type == SpotType.LARGE;
            case TRUCK -> type == SpotType.LARGE;
        };
    }

    public void occupy() { isFree = false; }
    public void release() { isFree = true; }
    public boolean isFree() { return isFree; }
    public int getId() { return id; }
    public int getFloor() { return floor; }
}

class Ticket {
    private LocalDateTime start = LocalDateTime.now();
    private ParkingSpot spot;
    private Vehicle vehicle;

    public Ticket(ParkingSpot spot, Vehicle vehicle) { this.spot = spot; this.vehicle = vehicle; }
    public ParkingSpot getSpot() { return spot; }
    public Vehicle getVehicle() { return vehicle; }
    public double calculateFee() {
        long mins = Duration.between(start, LocalDateTime.now()).toMinutes();
        double rate = switch (vehicle.getType()) { case BIKE -> 5.0; case CAR -> 10.0; case TRUCK -> 20.0; };
        // Minimum fee of 1 hour (rate) even if parked for 0 mins
        return Math.max(rate, (mins / 60.0) * rate);
    }
}

// --- SERVICE ---
class ParkingLot {
    private List<ParkingSpot> spots = new ArrayList<>();
    private Map<String, Ticket> activeTickets = new HashMap<>();
    private List<Transaction> history = new ArrayList<>();
    private static ParkingLot instance;

    private ParkingLot() {}
    public static ParkingLot getInstance() {
        if (instance == null) instance = new ParkingLot();
        return instance;
    }

    public void addSpot(ParkingSpot s) { spots.add(s); }

    public void park(Vehicle v) {
        // Prevent duplicate plates
        if (activeTickets.containsKey(v.getPlate())) {
            System.out.println("❌ Error: Vehicle [" + v.getPlate() + "] is already in the lot!");
            return;
        }

        for (ParkingSpot s : spots) {
            if (s.canFit(v)) {
                s.occupy();
                Ticket ticket = new Ticket(s, v);
                activeTickets.put(v.getPlate(), ticket);
                System.out.println("✅ Parked " + v.getType() + " at Floor " + s.getFloor() + ", Spot " + s.getId());
                return;
            }
        }
        System.out.println("❌ No suitable space available for " + v.getType());
    }

    public void exit(String plate) {
        Ticket t = activeTickets.remove(plate);
        if (t != null) {
            double fee = t.calculateFee();
            t.getSpot().release();
            history.add(new Transaction(plate, t.getVehicle().getType(), fee));
            System.out.println("🏁 Exit Successful for " + plate + ". Fee: $" + String.format("%.2f", fee));
        } else {
            System.out.println("⚠️ Error: Plate [" + plate + "] not found.");
        }
    }

    public void searchVehicle(String plate) {
        Ticket t = activeTickets.get(plate);
        if (t != null) {
            System.out.println("🔍 Vehicle [" + plate + "] is at Floor " + t.getSpot().getFloor() + ", Spot " + t.getSpot().getId());
        } else {
            System.out.println("🔍 Vehicle not found in the lot.");
        }
    }

    public void showStatus() {
        long free = spots.stream().filter(ParkingSpot::isFree).count();
        System.out.println("\n--- LOT STATUS: " + free + "/" + spots.size() + " SPOTS FREE ---");
        activeTickets.forEach((plate, ticket) -> {
            System.out.println("• " + plate + " (" + ticket.getVehicle().getType() + ") at Spot " + ticket.getSpot().getId());
        });
    }

    public void showRevenue() {
        double total = history.stream().mapToDouble(Transaction::fee).sum();
        System.out.println("\n💰 --- REVENUE REPORT ---");
        System.out.println("Total Transactions: " + history.size());
        System.out.println("Total Earnings: $" + String.format("%.2f", total));
        System.out.println("--------------------------");
    }
}

// --- MAIN RUNNER ---
public class Main {
    public static void main(String[] args) {
        ParkingLot lot = ParkingLot.getInstance();

        // Initializing the Lot
        lot.addSpot(new ParkingSpot(101, 1, SpotType.SMALL));
        lot.addSpot(new ParkingSpot(102, 1, SpotType.MEDIUM));
        lot.addSpot(new ParkingSpot(201, 2, SpotType.LARGE));

        Scanner scanner = new Scanner(System.in);
        System.out.println("=== PARKING SYSTEM INITIALIZED ===");

        while (true) {
            try {
                System.out.println("\n1. Park | 2. Exit | 3. Search | 4. Status | 5. Revenue | 6. Quit");
                System.out.print("Action: ");

                // Read the whole line and convert to int to prevent Scanner bugs
                String input = scanner.nextLine();
                int choice = Integer.parseInt(input);

                if (choice == 6) break;

                switch (choice) {
                    case 1 -> {
                        System.out.print("Enter Plate: ");
                        String plate = scanner.nextLine();
                        System.out.print("Type (1: Bike, 2: Car, 3: Truck): ");
                        int typeIdx = Integer.parseInt(scanner.nextLine());

                        Vehicle v = switch (typeIdx) {
                            case 1 -> new Bike(plate);
                            case 2 -> new Car(plate);
                            case 3 -> new Truck(plate);
                            default -> null;
                        };

                        if (v != null) lot.park(v);
                        else System.out.println("⚠️ Invalid type. Use 1, 2, or 3.");
                    }
                    case 2 -> {
                        System.out.print("Enter Plate to exit: ");
                        lot.exit(scanner.nextLine());
                    }
                    case 3 -> {
                        System.out.print("Enter Plate to search: ");
                        lot.searchVehicle(scanner.nextLine());
                    }
                    case 4 -> lot.showStatus();
                    case 5 -> lot.showRevenue();
                    default -> System.out.println("⚠️ Please choose 1-6.");
                }
            } catch (Exception e) {
                System.out.println("❌ Input Error: Please enter numbers only for menu choices.");
            }
        }
        System.out.println("System Shutting Down. Goodbye!");
        scanner.close();
    }
}