
# Mini Project Assignment
![status](https://img.shields.io/badge/status-complete-brightgreen)
 

## 📌 Overview

This project is part of the **2C2P Job Assignment**.

- **Assignment 2:** Develop a batch processing utility that compares two financial datasets.

## 🏗️ How to Use the API
### 1. Create Postman Workspace

(optional)
![img.png](project-configuration/screen/1_postman_workspace.png)
(optional)
![img.png](project-configuration/screen/1_postman_workspace_ok.png)

### 2. Import Postman API Collection

![img.png](project-configuration/screen/0_postman_resource.png)

`project-configuration/collections/batch.postman_collection.json`

![img.png](project-configuration/screen/2_batch.postman_collection.png)
![img.png](project-configuration/screen/2_batch.postman_collection_import.png)
 

#### Usage Example

After importing the collection, verify that the following endpoints are available.

#### `/healthcheck`

![Health Check API](project-configuration/screen/3_healthcheck.png)

![Health Check API](project-configuration/screen/3_healthcheck_result.png)

#### `/api/v1/batch/run`

![batch_manually_run](project-configuration/screen/3_batch_manually_run.png)

### 3. Configure Inbound and Outbound Paths

The application supports two options for configuring the inbound (input) and outbound (output) folders:

1. **Local folders** — Recommended for easy setup and testing.

   ![Inbound and Outbound Path Configuration](project-configuration/screen/Inbound_Outbound_Local_Share.png)

2. **Shared drive (Optional)** — For environments where files are stored on a network share.
 
   ![batch_manually_run](project-configuration/screen/share_folder.png)

   ![batch_manually_run](project-configuration/screen/share_folder_input.png)


For the easiest setup, I recommend using **local folders**. If you use local folders, you can skip **Section 3.6 of the Assignment 2: Project Common Configuration** document.
    ![batch_manually_run](project-configuration/screen/ProjectCommonConfiguration_ShareDrive.png)

[//]: # (![Inbound and Outbound Path Configuration]&#40;project-configuration/screen/Inbound_Outbound_Local_Share.png&#41;)

##  🚀  How to Run the Batch

**There are 2 ways to run the batch job:**

### 1. Run the Job Manually

Call the following API endpoint:

`POST /api/v1/batch/run`

![batch_manually_run](project-configuration/screen/3_batch_manually_run.png)

### 2. Run the Job Using a Scheduler

The batch job can also be executed automatically using a Cron expression.

The Cron expression configuration can be found in [`application.properties`](src/main/resources/application.properties).

![Cron Application Configuration](project-configuration/screen/cron_application.png)

### 3. Result

![batch_manually_run](project-configuration/screen/share_folder_output.png)


## ▶️ Run the Project

For project setup and configuration instructions, please refer to **Section 3.5 of the Assignment 2: Project Common Configuration** document.

![batch_manually_run](project-configuration/screen/ProjectCommonConfiguration.png)


## 📝 Log History

### Log Configuration

The application stores log history in the configured log file.

![Log Configuration](project-configuration/screen/log_config.png)

### Log File

![Log File](project-configuration/screen/log_file.png)


## 🛠️ Tech Stack

- Java 17
- Spring Boot 4.1.0
- Spring Batch
- Spring Validation
- Maven
- Lombok
- REST API

## 🚧 Improvements

The following improvements can be considered for future development:

- **Summary / Error Report** — Include total, success, error, and error reasons for easier support and troubleshooting. This may require additional server storage.
- **Field Validation** — Add validation for input fields.
- **JUnit Tests** — Implement unit tests to ensure that new changes do not introduce errors.
















 
 