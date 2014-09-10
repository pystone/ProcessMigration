JFLAGS = 
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	./edu/cmu/andrew/ds/main/PMMain.java \
	./edu/cmu/andrew/ds/main/ClusterManager.java \
	./edu/cmu/andrew/ds/io/TransactionalFileInputStream.java \
	./edu/cmu/andrew/ds/io/TransactionalFileOutputStream.java \
	./edu/cmu/andrew/ds/network/ClientManager.java \
	./edu/cmu/andrew/ds/network/NetworkManager.java \
	./edu/cmu/andrew/ds/network/CommunicationManager.java \
	./edu/cmu/andrew/ds/network/MessageStruct.java \
	./edu/cmu/andrew/ds/network/ServerHandler.java \
	./edu/cmu/andrew/ds/network/ServerManager.java \
	./edu/cmu/andrew/ds/ps/IOProcess.java \
	./edu/cmu/andrew/ds/ps/NonIOProcess.java \
	./edu/cmu/andrew/ds/ps/MigratableProcess.java \
	./edu/cmu/andrew/ds/ps/ProcessManager.java \

RMCLASSES = \
	./edu/cmu/andrew/ds/main/PMMain.class \
	./edu/cmu/andrew/ds/main/ClusterManager.class \
	./edu/cmu/andrew/ds/io/TransactionalFileInputStream.class \
	./edu/cmu/andrew/ds/io/TransactionalFileOutputStream.class \
	./edu/cmu/andrew/ds/network/ClientManager.class \
	./edu/cmu/andrew/ds/network/NetworkManager.class \
	./edu/cmu/andrew/ds/network/CommunicationManager.class \
	./edu/cmu/andrew/ds/network/MessageStruct.class \
	./edu/cmu/andrew/ds/network/ServerHandler.class \
	./edu/cmu/andrew/ds/network/ServerManager.class \
	./edu/cmu/andrew/ds/ps/IOProcess.class \
	./edu/cmu/andrew/ds/ps/NonIOProcess.class \
	./edu/cmu/andrew/ds/ps/MigratableProcess.class \
	./edu/cmu/andrew/ds/ps/ProcessManager.class \

MAIN = edu/cmu/andrew/ds/main/PMMain
	
default: classes

classes: $(CLASSES:.java=.class)

run:
	java $(MAIN)

clean:	
	rm $(RMCLASSES)
	
