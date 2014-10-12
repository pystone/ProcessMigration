ProcessMigration
================

Project 1 of 15-640. Design and implement a system that a process can be migrated between different hosts. Developed with @bbfeechen. 

***We proudly present it as we got full marks in grading.***

For more details, please refer to `./Material/Report of ProcessMigration.pdf`

## Special features

1. **Multiple clients supported**. One server can check the status of all clients and can control the process migration between them.2. **Process information check on client**. You can check out the process information on every client.3. **Function call to migratable process on demand**.You can invoke a function of a running migratable process with an input command.4. **Network communication scalable**. There are generalized message structure and dispatcher in the framework. You can add any type of message to the framework conveniently.5. **Load balancer friendly**. The control manager class (ClusterManager) is loosely decoupled from all other parts in server side. You can replace it with your own load balancer.