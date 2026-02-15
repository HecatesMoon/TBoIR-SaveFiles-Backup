# TBoIR Backup manager

![Java](https://img.shields.io/badge/Java-25-orange) ![License](https://img.shields.io/badge/license-MIT-blue) ![Windows](https://img.shields.io/badge/Windows-0078D6?logo=windows&logoColor=white) ![Linux](https://img.shields.io/badge/Linux-FCC624?logo=linux&logoColor=black)  ![macOS](https://img.shields.io/badge/macOS-000000?logo=apple&logoColor=white)

CLI backup manager that saves/restores game files across TBoI versions, with batch operations and cloud disable.

## What can it do?
- Make backups for each TBoIR version (Rebirth, Afterbirth, Afterbirth+, Repentance, Repentance+)
- Restore the made backups
- Make a batch backup or restore on installed versions of TBoIR
- Disable steam cloud from the program
- Change backup filepaths from the program and persist them
- Is compatible with mac, windows and linux

[<img src="screenshots/menu.png" width="400">](screenshots/menu.png)

[<img src="screenshots/backup.png" width="620">](screenshots/backup.png)

## Requirements
- Windows, Linux or MacOS

**Optional (for building a native-image):**
- Maven (for compilation)
- GraalVM (optional, for native-image)
**Optional (To compile it yourself):**
- Java 25 or higher

## 📦 Installation

### Quick start (recommended)
1. Download the native-image for your OS from [Releases](https://github.com/HecatesMoon/TBoIR-Backup-Manager/releases)
2. Run it:
   - **Linux/macOS**: `chmod +x isaac-backup-manager-* && ./isaac-backup-manager-*`
   - **Windows**: Double-click the `.exe` file

### Build from source
<details>
<summary>Click to expand compilation options</summary>

**With Java only:**
```bash
git clone https://github.com/HecatesMoon/TBoIR-Backup-Manager.git
cd src/main/java
javac com/hecatesmoon/isaacbackupmanager/App.java
cd ../../..
java -cp src/main/java com.hecatesmoon.isaacbackupmanager.App
```

**With Maven (for native-image):**
```bash
git clone https://github.com/HecatesMoon/TBoIR-Backup-Manager.git
cd TBoIR-Backup-Manager
mvn clean package
# Native image available in ./target/
```
</details>

## Tech Stack
- Java 25
- Maven
- GraalVM native-image
- Github actions

## Why did I do it
I made this project because I found a great opportunity to practice Java, I always wanted to do a useful app from zero, and wanted to get comfortable with Java. 
I also was playing The Binding of Isaac Rebirth and steam was overwriting my saves with empty saves, so I wanted something to save my games while not using steam cloud.

## Updates
I plan on updating the program, but It will take a while because I have to focus on other projects right now.
