echo "Removing all class files"
rm -rf *.class

echo "Running DS-Server"
./ds-server -c ds-config01--wk9.xml -v -brief -n &

echo "Running Client"
javac MyClient.java

java MyClient

