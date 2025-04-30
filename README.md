# 🚗 Vehicle Inventory Management System v3 - Kotlin

## 📌 Overview

This is a JavaFX-based desktop application developed as part of the ICS 372 group project (Spring 2025). It provides a
simple solution for managing vehicle inventories across dealerships.

---

## 🛠️ Tech Stack & Dependencies

- **Java 20**
- **JavaFX** – UI framework
- **ControlsFX** – UI enhancements and real-time notifications
- **Gson** – JSON serialization/deserialization
- **Jackson FasterXML** – XML parsing and writing
- **JUnit 5** – Unit testing framework
- **Gradle** – Dependency and build management
- **Dokka** - API Documentation

---

## ✨ Features

- 📥 Import/export inventory in **JSON** and **XML** formats
- 💾 **Persistent local data storage**
- 🔄 Toggle vehicle **rental status** (disabled for sports cars)
- 🚫 Enable/disable **dealer acquisition**
- 🔁 **Transfer vehicles** between dealerships
- ➕➖ **Add** and **delete vehicles**
- ➕➖ **Add** and **delete dealers**
- 🧾 **Edit** dealership name (inline)
- 🔍 **Filter vehicles** by dealership
- 🆔 Prevent **duplicate vehicle IDs**
- 🔔 Real-time **notifications** using ControlsFX

---

## 🧑‍💻 Usage Guide

### 📂 Import/Export

- **Import JSON**: `File > Import JSON > Select File`
- **Import XML**: `File > Import XML > Select File`
- **Export JSON**: `File > Export JSON > Choose Destination & File Name`
- **Export XML**: `File > Export XML > Choose Destination & File Name`

### 🚘 Inventory Management

- **Add Vehicle**: `Vehicles Tab > Add Vehicle` or `File > Add Vehicle`
- **Delete Vehicle**: Select vehicle and press `Delete`
- **Transfer Vehicles**: `Select Dealer > Transfer Inventory > Enter Dealer ID`
- **Toggle Rent**: `Select Vehicle > Toggle Rent` (disabled for Sports Cars)
- **Filter by Dealer**: Use dropdown on Vehicles tab

### 🏢 Dealer Management

- **Add Dealer**: `Dealers Tab > Add Dealer`
- **Edit Dealer Name**: Double-click the dealer name
- **Delete Dealer**: Select dealer and press `Delete`
- **Toggle Acquisition**: Select dealer and click `Toggle Acquisition`

---

## 🔄 Kotlin Transition

Version 3 Transitioned to Kotlin.

## For InteliJ

If you have touble starting the application, create a gradle build `:run` to properly start it.

## 📑 Documentation

For more information see the documentation folder in the root folder of the project from the GitHub repository or archive.
Example: project root->documentation

Dokka generated html (Kotlin api style) pages are in the html folder inside the documentation folder.
Use IntelliJ to open index.html in your web browser for a better experience.
Example: project root->documentation->html->index.html

Dokka generated html pages (Java api style) pages are in the javadoc folder inside the documentation folder.
Example: project root->documentation->javadoc->index.html
