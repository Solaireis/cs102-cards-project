#!/bin/bash
javac -d ../../classes $(find .. -name "*.java")

#from Splendor terminal
javac -d classes -cp "src:lib/*" src/Test/Game.java