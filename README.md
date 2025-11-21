🚀 Running the Application           # for day 1 app - it will work in MCA lab at vsb college
1. Start the Server
Open Command Prompt/Terminal and run:

bash
java -jar ChatServer.jar
Expected Output:

text
Chat Server started on port 8080 - Binding to all network interfaces
Server ready for connections...
2. Find Your Server IP Address
On Windows:

cmd
ipconfig
Look for "IPv4 Address" under your network adapter (usually 192.168.x.x or 10.x.x.x)

On Linux/Mac:

bash
ifconfig
or

bash
ip addr show
3. Run Clients on Same Network
On other computers, run:

bash
java -jar ChatClient.jar <SERVER_IP> 8080
Example:

bash
java -jar ChatClient.jar 192.168.1.100 8080
java -jar ChatClient.jar 192.168.1.100 8080
🛠️ Quick Troubleshooting if Clients Can't Connect
1. Windows Firewall Fix (Run as Administrator)
cmd
# Allow Java through firewall
netsh advfirewall firewall add rule name="ChatServer" dir=in action=allow protocol=TCP localport=8080

# Or temporarily disable firewall for testing
netsh advfirewall set allprofiles state off
2. Test Connection Between Computers
cmd
# From client computer, test if server is reachable
ping 192.168.1.100
telnet 192.168.1.100 8080
3. Common Issues & Solutions
Issue: "Connection refused"

Server not running

Wrong IP address

Firewall blocking

Issue: "Connection timed out"

Different network/subnet

Router blocking traffic

Quick Fix: Use computer name instead of IP

bash
java -jar ChatClient.jar HOSTNAME 8080
📋 Step-by-Step Testing Guide
Step 1: Test on Single Computer First
bash
# Terminal 1 - Start Server
java -jar ChatServer.jar

# Terminal 2 - Start Client 1  
java -jar ChatClient.jar localhost 8080

# Terminal 3 - Start Client 2
java -jar ChatClient.jar localhost 8080
Step 2: Test Basic Features
Set usernames for both clients

Create room: /create General

Join room: /join General

Send messages between clients

List rooms: /rooms

List users: /who

Step 3: Test Advanced Features
Private chat: /private username

Typing indicator (start typing to see it work)

Leave room: /leave

Exit private: /exit

🔧 If JAR Files Don't Run
Check JAR Contents:
bash
# List JAR contents to verify main class
jar tf ChatClient.jar | grep META-INF
Manual Compilation Alternative:
bash
# If JARs don't work, run directly from class files:
java ChatServer
java ChatClient 192.168.1.100 8080
📱 Distribution to Other Computers
Method 1: Shared Folder
Create shared folder on server computer

Copy ChatClient.jar to shared folder

Other users download and run from shared location

Method 2: File Transfer
Email ChatClient.jar to other users

Use USB drive

Upload to local network share

Method 3: Quick Setup Script (Windows)
Create StartClient.bat:

bat
@echo off
echo Enter Server IP: 
set /p server_ip=
java -jar ChatClient.jar %server_ip% 8080
pause
🎯 Expected Successful Output
Server Console:

text
Client #1 connected from 192.168.1.101
Client #2 connected from 192.168.1.102
Client Console:

text
Connected to server!
Welcome to LAN Chat Server!
Enter your username: 
Username set to: Alice
Type /help for available commands

3. Run Clients on Same Network
On other computers, run:

bash
java -jar ChatClient.jar <SERVER_IP> 8080
Example:

bash
java -jar ChatClient.jar 192.168.1.100 8080
java -jar ChatClient.jar 192.168.1.100 8080
