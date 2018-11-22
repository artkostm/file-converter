# HDFS State
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

The first section of the output shows the summary of the HDFS cluster, including the configured capacity, present capacity, remaining capacity, used space, number of under-replicated data blocks, number of data blocks with corrupted replicas, and number of missing blocks.

The last few are very important metrics. **Under-replicated blocks** number shows us the number of blocks with insufficient replication. Hadoop’s replication factor is configurable on a per-client or per-file basis. The default replication factor is 3, meaning that each block will be stored on three DataNodes. If you see growing number of under-replicated blocks, it is likely that a DataNode has died. 

A **missing block** cannot be recovered by copying a replica. A missing block represents a block for which no known copy exists in the cluster. That’s not to say that the block no longer exists—if a series of DataNodes were taken offline for maintenance, missing blocks could be reported until they are brought back up.

The following sections of the output information show the status of each HDFS slave node, including the name (ip:port) of the DataNode machine, commission status, configured capacity, HDFS and non-HDFS used space amount, HDFS remaining space, and the time that the slave node contacted the master.

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
Fsck HDFS filesystem see if it is healthy ```hdfs fsck / -files -blocks -locations > dfs-fsck.log```:

```shell
[hdfs@sandbox-hdp fileConverter]$ hdfs fsck / -files -blocks -locations > dfs-fsck.log                                                                                                                        
Connecting to namenode via http://sandbox-hdp.hortonworks.com:50070/fsck?ugi=hdfs&files=1&blocks=1&locations=1&path=%2F
[hdfs@sandbox-hdp fileConverter]$ less dfs-fsck.log                                                                                                                                                           
...
/user/zeppelin/notebook/2CBPZJDB7 <dir>                                                                                                                                                                       
/user/zeppelin/notebook/2CBPZJDB7/note.json 59084 bytes, 1 block(s):  OK                                                                                                                                      
0. BP-243674277-172.17.0.2-1529333510191:blk_1073741837_1013 len=59084 repl=1 [DatanodeInfoWithStorage[172.18.0.2:50010,DS-ab75b94d-c6f2-4415-8639-1aaec2609e13,DISK]]                                        
                                                                                                                                                                                                              
/user/zeppelin/notebook/2CBTZPY14 <dir>                                                                                                                                                                       
/user/zeppelin/notebook/2CBTZPY14/note.json 66945 bytes, 1 block(s):  OK                                                                                                                                      
0. BP-243674277-172.17.0.2-1529333510191:blk_1073741836_1012 len=66945 repl=1 [DatanodeInfoWithStorage[172.18.0.2:50010,DS-ab75b94d-c6f2-4415-8639-1aaec2609e13,DISK]]                                        
                                                                                                                                                                                                              
/user/zeppelin/notebook/2CCBNZ5YY <dir>                                                                                                                                                                       
/user/zeppelin/notebook/2CCBNZ5YY/note.json 64543 bytes, 1 block(s):  OK                                                                                                                                      
0. BP-243674277-172.17.0.2-1529333510191:blk_1073741843_1019 len=64543 repl=1 [DatanodeInfoWithStorage[172.18.0.2:50010,DS-ab75b94d-c6f2-4415-8639-1aaec2609e13,DISK]]                                        
                                                                                                                                                                                                              
/user/zeppelin/test <dir>                                                                                                                                                                                     
Status: HEALTHY                                                                                                                                                                                               
 Total size:    2537242881 B                                                                                                                                                                                  
 Total dirs:    286                                                                                                                                                                                           
 Total files:   1165                                                                                                                                                                                          
 Total symlinks:                0 (Files currently being written: 2)                                                                                                                                          
 Total blocks (validated):      1160 (avg. block size 2187278 B) (Total open file blocks (not validated): 1)                                                                                                  
 Minimally replicated blocks:   1160 (100.0 %)                                                                                                                                                                
 Over-replicated blocks:        0 (0.0 %)                                                                                                                                                                     
 Under-replicated blocks:       0 (0.0 %)                                                                                                                                                                     
 Mis-replicated blocks:         0 (0.0 %)                                                                                                                                                                     
 Default replication factor:    1                                                                                                                                                                             
 Average block replication:     1.0                                                                                                                                                                           
 Corrupt blocks:                0                                                                                                                                                                             
 Missing replicas:              0 (0.0 %)                                                                                                                                                                     
 Number of data-nodes:          1                                                                                                                                                                             
 Number of racks:               1                                                                                                                                                                             
FSCK ended at Mon Nov 05 10:41:35 UTC 2018 in 40 milliseconds                                                                                                                                                 
                                                                                                                                                                                                              
                                                                                                                                                                                                              
The filesystem under path '/' is HEALTHY                                                                                                                                                                      
(END)
```
The output shows some information for each file in HDFS (path to the file, file size, the number of blocks, replication factor, replicas). At the end of the output, we can see summary information similar to the ```hdfs dfsadmin -report``` output.

## Move files

Command to move files from source to destination: ```hdfs dfs -mv /path/to/file.ext /path/to/destination```

Here is the example:
```shell
[hdfs@sandbox-hdp fileConverter]$hdfs dfs -ls /tmp/test                                                                                                                                                                  
Found 2 items
-rw-r--r--   1 hdfs hdfs      10465 2018-11-05 10:38 /tmp/test/destinations.avsc
drwxr-xr-x   - root hdfs          0 2018-11-02 15:47 /tmp/test/dir
[hdfs@sandbox-hdp fileConverter]$ hdfs dfs -mkdir /tmp/test2                                                                                                                                                  
[hdfs@sandbox-hdp fileConverter]$ hdfs dfs -mv /tmp/test/destinations.avsc /tmp/test2                                                                                                                         
[hdfs@sandbox-hdp fileConverter]$ hdfs dfs -ls /tmp/test2
Found 1 items
-rw-r--r--   1 hdfs hdfs      10465 2018-11-05 10:38 /tmp/test2/destinations.avsc
```
For file renaiming, let's say we have already dumped the file in HDFS environment under folder (for e.g.) test/Xyz.txt

```hdfs dfs -mv 'old file with path' ' New file name with path'```

Console example:

```shell
[hdfs@sandbox-hdp fileConverter]$hdfs dfs -ls /tmp/test                                                                                                                                                                  
Found 3 items
-rw-r--r--   1 hdfs hdfs      10465 2018-11-05 10:38 /tmp/test/destinations.avsc
-rw-r--r--   1 hdfs hdfs        314 2018-11-03 14:11 /tmp/test/Xyz.txt
drwxr-xr-x   - root hdfs          0 2018-11-02 15:47 /tmp/test/dir
[hdfs@sandbox-hdp fileConverter]$hdfs dfs -mv /tmp/test/Xyz.txt /tmp/test/new_xyz.txt
[hdfs@sandbox-hdp fileConverter]$hdfs dfs -ls /tmp/test                                                                                                                                                                  
Found 3 items
-rw-r--r--   1 hdfs hdfs      10465 2018-11-05 10:38 /tmp/test/destinations.avsc
-rw-r--r--   1 hdfs hdfs        314 2018-11-03 14:11 /tmp/test/new_xyz.txt
drwxr-xr-x   - root hdfs          0 2018-11-02 15:47 /tmp/test/dir
```
