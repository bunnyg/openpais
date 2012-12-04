Instructions on setup DB2 DPF

Installation binaries:
=========================================
DB2 download folder: /data2/fwang/db2dpf
 server: infosphere warehouse edtion
 gse: db2 spatial
 universal: universal fixpack (9.7.6)
 license2: license
 
 To setup license:
 Copy license file to the install folder:
 cp /data2/fwang/db2dpf/license2/*.lic /data2/fwang/db2dpf/server/isw/Ese/db2/license

 
Layout
=========================================
Two nodes:
pais1.cci.emory.edu (public accessible) 
node40.clus.cci.emory.edu node40
 
pais2.cci.emory.edu (public accessible) 
node41.clus.cci.emory.edu node41

Folders:
node40: /shared: local, also exported as /shared on node41
node41: /db: local, has access to shared folder /shared

DB2 accounts (excluding db2admin user) and db2 binary will be installed in a shared 
folder: /shared/database
  e.g.,: /shared/database/db2inst
  
DB2 databases will be installed locally: 
node40: /shared/database (or soft link: /db/shared/database)
node41: /db/shared/database
  /data will be for data
  /log will be for log


DB2 Users
=========================================
Create db2 users on both master and slave nodes, with same login information.
The users should be local users and not NIS or NIS+ users. db2admin user(dasuser1) 
should have local home, and db2inst1 and db2fenc1 should have shared home folders.

sudo /usr/sbin/groupadd -g 999 db2iadm1
sudo /usr/sbin/groupadd -g 998 db2fadm1
sudo /usr/sbin/groupadd -g 997 dasadm1
sudo /usr/sbin/useradd -u 1104 -g db2iadm1 -m -d /shared/database/db2inst1 db2inst1
sudo /usr/sbin/useradd -u 1103 -g db2fadm1 -m -d /shared/database/db2fenc1 db2fenc1
sudo /usr/sbin/useradd -u 1102 -g dasadm1  -m  -d  /home/dasusr1 dasusr1
sudo /usr/sbin/usermod  -a -G dasadm1 db2inst1 

Setup DB2:
==================================

sudo /data2/fwang/db2dpf/server/isw/Ese/db2setup 

Save response file: /data2/fwang/rsp/db2ese.rsp, db2ese_slave.rsp

(Check if license is right if neede. Ignore this if license file copied before.)
/shared/database/ibm/db2/V9.7/adm:
sudo ./db2licm -l
If license file not copied:
sudo /shared/database/db2inst1/sqllib/adm/db2licm -a /data2/fwang/db2dpf/license2/iwebe.lic



Setup public key authentication (avoid login between nodes):
===========================================
Run as db2instance user:
 su -l db2inst1

-----------------------------------
Do this on both master/slave nodes:

ssh-keygen -t rsa
cd ~/.ssh
mv id_rsa identity
chmod 600 identity
cat id_rsa.pub >> authorized_keys
chmod 644 authorized_keys
rm id_rsa.pub

ssh-keyscan -t rsa node40 node40.clus.cci.emory.edu,192.168.1.159 >> ~/.ssh/known_hosts
ssh-keyscan -t rsa node41 node41.clus.cci.emory.edu,192.168.1.160 >> ~/.ssh/known_hosts



Do this on master node:
-----------------------------------
sudo nano /etc/ssh/sshd_config (adding entry and update entry):

#    HostbasedAuthentication no 
->
    HostbasedAuthentication yes


Do this on both master/slave nodes:
-----------------------------------
sudo nano /etc/ssh/shosts.equiv:
Add following:

node40
node40.clus.cci.emory.edu
node41
node41.clus.cci.emory.edu
pais1
pais1.cci.emory.edu
pais2
pais2.cci.emory.edu


Go to: /etc/ssh:

sudo ssh-keyscan -t rsa node40 node40.clus.cci.emory.edu,192.168.1.159 >> /tmp/ssh_known_hosts
sudo ssh-keyscan -t rsa node41 node41.clus.cci.emory.edu,192.168.1.160  >> /tmp/ssh_known_hosts
sudo cp /tmp/ssh_known_hosts .

On slave nodes (node41):
-----------------------------------
sudo nano /etc/ssh/ssh_config
#HostbasedAuthentication no
->
HostbasedAuthentication yes
#added:
EnableSSHKeysign yes


Restart ssh on both nodes:
-----------------------------------

sudo /sbin/service sshd restart



Enable ssh for DB2 on both nodes:
-----------------------------------

db2set DB2RSHCMD=/usr/bin/ssh


Fix if wrong:
====================================================
Clean all db2 processes:
/sqllib/bin/db2_kill 

Stop fault monitor daemon (db2fmcd):
/sqllib/bin:
sudo ./db2fmcu -d

sudo /shared/database/ibm/db2/V9.7/bin/db2val
or db2val -o
(missing lib)


Install DB2 from cmd with response file:
=====================================================

Master node:
-----------------------------------
sudo ./db2setup   -u /data2/fwang/db2dpf/rsp/db2ese.rsp 

Find error, fix it:
db2set DB2COMM = TCPIP
db2set DB2_ENABLE_LDAP=no
db2set -all DB2COMM

Setup DB2 slave:
-----------------------------------
sudo ./db2setup -u /data2/fwang/db2dpf/rsp/db2ese_addpart.rsp 





Install DB2 Spatial Extender on Master node:
===========================================
db2stop first.
Setup DB2 Spatial Extender on master node
Install DB2 spatial extender on master node, by running db2setup from DB2GSE 
installation file path:

sudo ./db2setup
Choose to generate response files only, and reuse existing instance.

Response file generated: db2gse.rsp                               

sudo ./db2setup -u ../rsp/db2gse.rsp 


Apply fixpack (master node):
===========================================
db2stop; db2admin stop
Under universal:
sudo ./installFixPack -b /shared/database/ibm/db2/V9.7



db2 instance update ((master node):
========================================
cd /shared/database/ibm/db2/V9.7/instance/
sudo ./db2iupdt db2inst1



Setup physical partitioning information on both master and slave nodes:
========================================
Shut down db2 first: db2stop

16 cores, 15 partition per core
Create a file db2nodes.cfg in /home/db2inst1/sqllib, with following entries:
db2nodes.cfg:
0 node40 0
1 node40 1
2 node40 2
3 node40 3
4 node40 4
5 node40 5
6 node40 6
7 node40 7
8 node40 8
9 node40 9
10 node40 10
11 node40 11
12 node40 12
13 node40 13
14 node40 14
15 node41 0
16 node41 1
17 node41 2
18 node41 3
19 node41 4
20 node41 5
21 node41 6
22 node41 7
23 node41 8
24 node41 9
25 node41 10
26 node41 11
27 node41 12
28 node41 13
29 node41 14


Manual add a partition:
========================================

db2start DBPARTITIONNUM 1 ADD  DBPARTITIONNUM HOSTNAME node40.clus.cci.emory.edu PORT 1
=> failure.

Look at system resources:

numactl --hardware


IO Consideration:
========================================
DB2 doc: "Optimizing table space performance when data is on RAID devices"
Linux article: "A Comparison of Chunk Size for Software RAID-5"

RAID strip size = chunk size * disks in an array (e.g., 64K x 5 = 320K for our case.)
Chunk size is also "segment size of disk".
RAID5 chunk size: 64K
Disk segment size: 64K
RAID5 Strip size: 320K

The DB2 extent size for a tablespace is the amount of data that the database manager
writes to a container before writing to the next container.

The extent size should be a multiple of the underlying segment size of the disks. Also a multiply
of the page size.
Page size (large table): 32KB

Extent size = 256K

Prefetch size 




Check raid level:
/proc/mdstat

md0 : active raid5 sdd1[4] sdc1[3] sdb1[2] sda1[1] hdb1[0]
      11721065472 blocks super 1.0 level 5, 64k chunk, algorithm 2 [5/5] [UUUUU]

http://alephnull.com/benchmarks/sata2009/chunksize.html
