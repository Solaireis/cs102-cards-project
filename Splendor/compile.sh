#!/bin/bash
#from Splendor terminal
#if run here then
#cd ../../ && javac -d classes -cp "src:lib/*" src/Test/Game.java

#run from splendor terminal! alot cleaner
#!/bin/bash
mkdir -p classes
javac -d classes -cp "src:lib/*" src/Test/Game.java
cp config.properties classes/config.properties
cp config.properties classes/Properties/config.properties