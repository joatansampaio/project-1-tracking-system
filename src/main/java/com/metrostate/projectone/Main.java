package com.metrostate.projectone;

//Intra-project imports

import com.metrostate.projectone.controllers.DealershipController;
import com.metrostate.projectone.models.Vehicle;
import com.metrostate.projectone.utils.Printer;

//Java API imports
import java.io.File;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {

	static Scanner scan = new Scanner(System.in);
	static DealershipController controller = new DealershipController();

	public static void main(String[] args) {

		while (true) {
			showMainMenu();
			String option = scan.nextLine();
			switch (option) {
				case "1":
					listAllVehicles();
					break;
				case "2":
					break;
				case "3":
					break;
				case "4":
					handleDealerAcquisition(true);
					break;
				case "5":
					handleDealerAcquisition(false);
					break;
				case "6":
					importFile();
					break;
				case "7":
					handleDealerExport();
					break;
				case "0":
					System.exit(0);
				default:
					Printer.println("Invalid option! Enter one of the available options.");
			}
			waitUser();
		}
	}



	//Todo: Reorder menu methods in their menu order when presented to user
	public static void showMainMenu() {
		Printer.println("Tracking system v1");
		Printer.println("-".repeat(30), Printer.Color.BLUE, false);
		Printer.println("1 - List All Vehicles");
		Printer.println("2 - List Vehicles By Dealership ID");
		Printer.println("3 - Add Incoming Vehicle");
		Printer.println("4 - Enable Vehicle Acquisition");
		Printer.println("5 - Disable Vehicle Acquisition");
		Printer.println("-".repeat(30));
		Printer.println("6 - Import Json File", Printer.Color.CYAN, false);
		Printer.println("7 - Export Dealer Vehicles To Json");
		Printer.println("-".repeat(30));
		Printer.println("0 - Exit", Printer.Color.PURPLE);
		Printer.print("Enter an option: ");
	}

	public static void listAllVehicles() {
		List<Vehicle> vehicleList = controller.getVehicles();
		if (vehicleList.isEmpty()) {
			Printer.println("No vehicles registered yet.");
			return;
		}
		for (Vehicle v : vehicleList) {
			Printer.println(v.toString());
		}
	}

	public static void importFile() {
		Printer.println("\nSelect a file to import: ");

		File folder = new File("src/main/resources");
		File[] jsonFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));

		if (jsonFiles == null || jsonFiles.length == 0) {
			Printer.println("No JSON files were found in the resources folder.");
			return;
		}

		int counter = 1;
		for (File file : jsonFiles) {
			Printer.println(counter + " - " + file.getName(), Printer.Color.YELLOW);
			counter++;
		}

		Printer.print("Select a file number to import: ");
		// Read the file number

		int fileNumber;
		try {
			int option = scan.nextInt();
			if (option <= 0 || option > jsonFiles.length) {
				Printer.println("Invalid file number, try again...", Printer.Color.RED);
				scan.nextLine();
				return;
			}
			fileNumber = option;
		} catch (InputMismatchException e) {
			Printer.println("Invalid file number, try again...", Printer.Color.RED);
			scan.nextLine();
			return;
		}

		if (controller.importJsonFile(jsonFiles[fileNumber - 1].getName())) {
			Printer.println("File loaded", Printer.Color.YELLOW);
		} else {
			Printer.println("Error loading file", Printer.Color.RED);
		}
		scan.nextLine();
	}
	private static void handleDealerExport() {
		//  Not working yet: TODO: Test with exportDealerToJson() after getVehiclesByDealershipId() is implemented
		// supply String dealershipID to this method: controller.exportDealerToJson();

		System.out.println("Exporting Dealer Vehicles To Json");
		// for testing -
		controller.exportDealerToJson("12513");
	}



	private static void handleDealerAcquisition(boolean isEnable) {
		Printer.print("Enter Dealership ID: ");
		String dealershipId = scan.next();
		if (isEnable) {
			// to be implemented
			return;
		}
		if (!controller.disableAcquisition(dealershipId)) {
			Printer.println("Dealership ID not found.", Printer.Color.RED);
		} else {
			Printer.println("Acquisition successfully disabled for Dealership ID: " + dealershipId);
		}
		scan.nextLine();
	}

	private static void waitUser() {
		Printer.println("Press \"ENTER\" to continue...");
		scan.nextLine();
	}
}