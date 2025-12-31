# Multi-Level OOP Parking Lot System

### 🚩 Problem Statement
Managing a physical parking lot manually is prone to errors such as overfilling, incorrect billing, and inefficient spot allocation. This project provides an automated backend solution to:
- Manage multi-floor parking availability.
- Support multiple vehicle types (Bike, Car, Truck) with specific size constraints.
- Automate real-time fee calculation based on stay duration.
- Track all transactions for revenue reporting.

### 🛠️ Tech Stack
- **Language:** Java 17+
- **Architecture:** Object-Oriented Programming (OOP)
- **Design Patterns:** Singleton Pattern (ParkingLot Service)
- **Features:** Java Streams for analytics, Scanner for interactive CLI.

### 💡 Design & SOLID Principles
This project demonstrates clean code practices by adhering to SOLID principles:
- **Single Responsibility:** Separate classes for Models (Vehicle, Spot), Services (ParkingLot), and Tracking (Transaction).
- **Open/Closed Principle:** New vehicle types or spot types can be added with minimal changes to the core logic.
- **Liskov Substitution:** Different vehicle types (Car, Bike, Truck) can be used interchangeably where a base Vehicle is expected.

### ✨ Key Features
- **Smart Allocation:** Logic ensures a Truck cannot park in a Small/Medium spot, while a Bike can park anywhere.
- **Interactive Console:** A menu-driven interface allows users to Park, Exit, Search, and check Status in real-time.
- **Dynamic Pricing:** Automated billing with different rates per vehicle type (e.g., Trucks pay a higher hourly rate).
- **Revenue Dashboard:** Provides a summary of total earnings and a count of vehicles processed.
- **Search Capability:** Find exactly where a vehicle is parked (Floor and Spot ID) using its license plate.

### 🧪 Use Case (Simulation)
1. **Initialise:** System starts with defined spots on Floor 1 and Floor 2.
2. **Action - Park:** Register `CAR-555` as a `CAR`.
   - *Result:* System assigns Floor 1, Spot 102 (Medium).
3. **Action - Exit:** Exit `CAR-555` after a simulated stay.
   - *Result:* System displays total fee and marks the spot as "Free" for the next user.
4. **Action - Revenue:** View the end-of-day report showing total money collected.

### 🛠️ How to Run
1. Clone the repository: `git clone https://github.com/givijain1234/parking-lot-system.git`
2. Open the project in IntelliJ IDEA.
3. Run the `Main.java` file.
4. Follow the on-screen menu to manage the lot.
