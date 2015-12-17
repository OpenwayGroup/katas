Write a program makes a guaranteed deadlock of N threads:
thread 1 waits for thread 2, thread 2 waits for thread 3, ..., thread N waits for thread 1.
This should happen as soon as possible.

You can confirm that the program is in deadlock by taking a thread dump.
Successful deadlock of 3 threads looks like this (irrelevant parts omitted):

```
Found one Java-level deadlock:
=============================
"Thread-2":
  waiting to lock monitor 0x00007f8320001ec8 (object 0x00000000d741be48, a java.lang.Object),
  which is held by "Thread-0"
"Thread-0":
  waiting to lock monitor 0x00007f8344004e28 (object 0x00000000d741be58, a java.lang.Object),
  which is held by "Thread-1"
"Thread-1":
  waiting to lock monitor 0x00007f83440062c8 (object 0x00000000d741be68, a java.lang.Object),
  which is held by "Thread-2"

Java stack information for the threads listed above:
===================================================
"Thread-2":
	at Deadlock.lambda$main$1(Deadlock.java:32)
	- waiting to lock <0x00000000d741be48> (a java.lang.Object)
	- locked <0x00000000d741be68> (a java.lang.Object)
	at Deadlock$$Lambda$2/1149319664.run(Unknown Source)
	at java.lang.Thread.run(Thread.java:745)
"Thread-0":
	at Deadlock.lambda$main$1(Deadlock.java:32)
	- waiting to lock <0x00000000d741be58> (a java.lang.Object)
	- locked <0x00000000d741be48> (a java.lang.Object)
	at Deadlock$$Lambda$2/1149319664.run(Unknown Source)
	at java.lang.Thread.run(Thread.java:745)
"Thread-1":
	at Deadlock.lambda$main$1(Deadlock.java:32)
	- waiting to lock <0x00000000d741be68> (a java.lang.Object)
	- locked <0x00000000d741be58> (a java.lang.Object)
	at Deadlock$$Lambda$2/1149319664.run(Unknown Source)
	at java.lang.Thread.run(Thread.java:745)

Found 1 deadlock.
```
