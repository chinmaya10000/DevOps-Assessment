# Deploy Java App

## Overview
This repository contains a comprehensive CI/CD pipeline setup for deploying a Java application using Jenkins. The setup includes infrastructure provisioning with Terraform, monitoring and logging, and a detailed pipeline configuration.

### Initial Setup

**Step 1: Launch EC2 (Amazon Linux 2023):**

- Provision an EC2 instance on AWS with Amazon Linux 2023.
- Connect to the instance using SSH.

**Step 2: Install Docker:**

- Set up Docker on the EC2 instance:

    ```bash
    sudo yum update -y
    sudo yum install docker.io -y
    sudo usermod -aG docker $USER  # Replace with your system's username, e.g., 'ec2-user'
    sudo systemctl enable --now docker
    ```

### Jenkins Setup

1. **Install Jenkins for Automation:**
    - Run Jenkins as a Docker Container on the EC2 instance to automate deployment:

    ```bash
    docker run -p 8080:8080 -p 5000:5000 -d \
    -v jenkins_home:/var/jenkins_home \
    -v /var/run/docker.sock:/var/run/docker.sock \
    -v $(which docker):/usr/bin/docker jenkins/jenkins:lts
    ``` 

- Permission to jenkins to run Container
   
   ```bash
   docker ps
   docker exec -u 0 -it <container-id> bash
   chmod 666 /var/run/docker.sock
   ```

   - Access Jenkins in a web browser using the public IP of your EC2 instance.
      
        publicIp:8080

2. **Install Necessary Plugins in Jenkins:**

    Goto Manage Jenkins →Plugins → Available Plugins →

    Install below plugins

    1 SonarQube Scanner (Install without restart)

    2 ssh agent 

### **Configure maven in Global Tool Configuration**

Goto Manage Jenkins → Tools → Install Maven → Click on Apply and Save

### SonarQube

Create the token

Goto Jenkins Dashboard → Manage Jenkins → Credentials → Add Secret Text. It should look like this

After adding sonar token

Click on Apply and Save

**The Configure System option** is used in Jenkins to configure different server

**Global Tool Configuration** is used to configure different tools that we install using Plugins

We will install a sonar scanner in the tools.

**Add DockerHub and ssh agent Credentials:**

- To securely handle DockerHub credentials in your Jenkins pipeline, follow these steps:
  - Go to "Dashboard" → "Manage Jenkins" → "Manage Credentials."
  - Click on "System" and then "Global credentials (unrestricted)."
  - Click on "Add Credentials" on the left side.


## Security

1. **Install SonarQube and Trivy:**


2. **Integrate SonarQube and Configure:**
    - Integrate SonarQube with your CI/CD pipeline.
    - Configure SonarQube to analyze code for quality and security issues.


## CI/CD Pipeline

### Jenkinsfile
- The pipeline configuration is defined in the `Jenkinsfile` located in the root directory.

### Pipeline Stages
1. **Source Code Management:** Clones the repository from GitHub.
2. **Build:** Compiles the Java application using Maven.
3. **Test:** Executes unit and integration tests.
4. **Configuration:** Sets up and deploys to Development, QA, UAT, and Production environments.
5. **Deploy:** Deploys the application and verifies the deployment.


### Infrastructure Setup

1. **Terraform Configuration**
   - The Terraform configuration for provisioning infrastructure can be found in the `terraform` directory. This includes configurations for AWS VPC, subnets, security groups, and EC2 instances.

2. **Ansible Playbook**
   - The Ansible playbook used for configuring the server and deploying Docker containers is located in the `ansible` directory. This playbook handles tasks such as installing Docker, Docker Compose, and deploying the Docker container from a `docker-compose.yml` file.


## Monitoring

1. **Install Prometheus and Grafana:**

   Set up Prometheus and Grafana to monitor your application.

2. **Configure Prometheus Plugin Integration:**

   Integrate Jenkins with Prometheus to monitor the CI/CD pipeline.

   **Prometheus Configuration:**

   To configure Prometheus to scrape metrics from Node Exporter and Jenkins, you need to modify the `prometheus.yml` file. Here is an example `prometheus.yml` configuration for your setup:

   ```yaml
   global:
     scrape_interval: 15s

   scrape_configs:
     - job_name: 'node_exporter'
       static_configs:
         - targets: ['localhost:9100']

     - job_name: 'jenkins'
       metrics_path: '/prometheus'
       static_configs:
         - targets: ['<your-jenkins-ip>:<your-jenkins-port>']
   ```

   Make sure to replace `<your-jenkins-ip>` and `<your-jenkins-port>` with the appropriate values for your Jenkins setup.

   Check the validity of the configuration file:

   ```bash
   promtool check config /etc/prometheus/prometheus.yml
   ```

   Reload the Prometheus configuration without restarting:

   ```bash
   curl -X POST http://localhost:9090/-/reload
   ```

   You can access Prometheus targets at:

   `http://<your-prometheus-ip>:9090/targets`

## Grafana

**Install Grafana on amazon linux 2023 and Set it up to Work with Prometheus**

**Step 1: Install Dependencies:**

First, ensure that all necessary dependencies are installed:

```bash
sudo apt-get update
sudo apt-get install -y apt-transport-https software-properties-common
```

**Step 2: Add the GPG Key:**

Add the GPG key for Grafana:

```bash
wget -q -O - https://packages.grafana.com/gpg.key | sudo apt-key add -
```

**Step 3: Add Grafana Repository:**

Add the repository for Grafana stable releases:

```bash
echo "deb https://packages.grafana.com/oss/deb stable main" | sudo tee -a /etc/apt/sources.list.d/grafana.list
```

**Step 4: Update and Install Grafana:**

Update the package list and install Grafana:

```bash
sudo apt-get update
sudo apt-get -y install grafana
```

**Step 5: Enable and Start Grafana Service:**

To automatically start Grafana after a reboot, enable the service:

```bash
sudo systemctl enable grafana-server
```

Then, start Grafana:

```bash
sudo systemctl start grafana-server
```

**Step 6: Check Grafana Status:**

Verify the status of the Grafana service to ensure it's running correctly:

```bash
sudo systemctl status grafana-server
```

**Step 7: Access Grafana Web Interface:**

Open a web browser and navigate to Grafana using your server's IP address. The default port for Grafana is 3000. For example:

`http://<your-server-ip>:3000`

You'll be prompted to log in to Grafana. The default username is "admin," and the default password is also "admin."

**Step 8: Change the Default Password:**

When you log in for the first time, Grafana will prompt you to change the default password for security reasons. Follow the prompts to set a new password.

**Step 9: Add Prometheus Data Source:**

To visualize metrics, you need to add a data source. Follow these steps:

- Click on the gear icon (⚙️) in the left sidebar to open the "Configuration" menu.

- Select "Data Sources."

- Click on the "Add data source" button.

- Choose "Prometheus" as the data source type.

- In the "HTTP" section:
  - Set the "URL" to `http://localhost:9090` (assuming Prometheus is running on the same server).
  - Click the "Save & Test" button to ensure the data source is working.

**Step 10: Import a Dashboard:**

To make it easier to view metrics, you can import a pre-configured dashboard. Follow these steps:

- Click on the "+" (plus) icon in the left sidebar to open the "Create" menu.

- Select "Dashboard."

- Click on the "Import" dashboard option.

- Enter the dashboard code you want to import (e.g., code 1860).

- Click the "Load" button.

- Select the data source you added (Prometheus) from the dropdown.

- Click on the "Import" button.

You should now have a Grafana dashboard set up to visualize metrics from Prometheus.

Grafana is a powerful tool for creating visualizations and dashboards, and you can further customize it to suit your specific monitoring needs.

That's it! You've successfully installed and set up Grafana to work with Prometheus for monitoring and visualization.

3. **Configure Prometheus Plugin Integration:**
    - Integrate Jenkins with Prometheus to monitor the CI/CD pipeline.
