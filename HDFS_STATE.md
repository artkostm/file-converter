# Task 2
## Acceptance criteria
- Check current state of hdfs via CLI, describe the state and make a screenshot.
- Perform checking file system, make a screenshot.
- Move stored dataset (or rename it) via CLI and make screenshot.

## HDFS state check

To get overall status of the HDFS cluster and each namenode/datanode use ```hdfs dfsadmin -report  ```

Console output:
```shell
[hdfs@sandbox-hdp root]$ hdfs dfsadmin -report                                                                                                                                                                
Configured Capacity: 113791799296 (105.98 GB)                                                                                                                                                                 
Present Capacity: 84536938496 (78.73 GB)                                                                                                                                                                      
DFS Remaining: 81972592640 (76.34 GB)                                                                                                                                                                         
DFS Used: 2564345856 (2.39 GB)                                                                                                                                                                                
DFS Used%: 3.03%                                                                                                                                                                                              
Under replicated blocks: 0                                                                                                                                                                                    
Blocks with corrupt replicas: 0                                                                                                                                                                               
Missing blocks: 0                                                                                                                                                                                             
Missing blocks (with replication factor 1): 0                                                                                                                                                                 
                                                                                                                                                                                                              
-------------------------------------------------                                                                                                                                                             
Live datanodes (1):                                                                                                                                                                                           
                                                                                                                                                                                                              
Name: 172.18.0.2:50010 (sandbox-hdp.hortonworks.com)                                                                                                                                                          
Hostname: sandbox-hdp.hortonworks.com                                                                                                                                                                         
Decommission Status : Normal                                                                                                                                                                                  
Configured Capacity: 113791799296 (105.98 GB)                                                                                                                                                                 
DFS Used: 2564345856 (2.39 GB)                                                                                                                                                                                
Non DFS Used: 23263436800 (21.67 GB)                                                                                                                                                                          
DFS Remaining: 81972592640 (76.34 GB)                                                                                                                                                                         
DFS Used%: 2.25%                                                                                                                                                                                              
DFS Remaining%: 72.04%                                                                                                                                                                                        
Configured Cache Capacity: 0 (0 B)                                                                                                                                                                            
Cache Used: 0 (0 B)                                                                                                                                                                                           
Cache Remaining: 0 (0 B)                                                                                                                                                                                      
Cache Used%: 100.00%                                                                                                                                                                                          
Cache Remaining%: 0.00%                                                                                                                                                                                       
Xceivers: 4                                                                                                                                                                                                   
Last contact: Mon Nov 05 10:10:08 UTC 2018                                                                                                                                                                    
Last Block Report: Mon Nov 05 06:57:47 UTC 2018                                                   
```

From the console output, we can see the state of cluster memory: capacity, remaining and used memory, blocks information.

The last ones are very important metrics. **Under-replicated blocks** number shows us the number of blocks with insufficient replication. Hadoop’s replication factor is configurable on a per-client or per-file basis. The default replication factor is 3, meaning that each block will be stored on three DataNodes. If you see growing number of under-replicated blocks, it is likely that a DataNode has died. 

A **missing block** cannot be recovered by copying a replica. A missing block represents a block for which no known copy exists in the cluster. That’s not to say that the block no longer exists—if a series of DataNodes were taken offline for maintenance, missing blocks could be reported until they are brought back up.

The second section of the console output shows us how many live datanode exist in the cluster, their physical address, and memory state for each of these.

## Checking file system

To check if your cluster is working or not by writing/reading files:
```
hdfs dfs -put [input file] [output file]
hdfs dfs -cat [output file]
```
Console output:
```shell
[hdfs@sandbox-hdp fileConverter]$ hdfs dfs -put /tmp/fileConverter/destinations.avsc /tmp/test/destinations.avsc                                                                                              
[hdfs@sandbox-hdp fileConverter]$ hdfs dfs -cat /tmp/test/destinations.avsc                                                                                                                                   
{                                                                                                                                                                                                             
  "type" : "record",                                                                                                                                                                                          
  "name" : "destinations",                                                                                                                                                                                    
  "fields" : [                                                                                                                                                                                                
          {                                                                                                                                                                                                   
                "name" : "srch_destination_id",                                                                                                                                                               
                "type" : [                                                                                                                                                                                    
                  "string"                                                                                                                                                                                    
                ]                                                                                                                                                                                             
          },                                                                                                                                                                                                  
          {                                                                                                                                                                                                   
                "name": "d1",                                                                                                                                                                                 
                "type": [                                                                                                                                                                                     
                  "string",                                                                                                                                                                                   
                  "null"                                                                                                                                                                                      
                ]                                                                                                                                                                                             
          }, ...                                                                                                                                                                                                
```
