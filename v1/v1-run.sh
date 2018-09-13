rm -rf output/
hadoop com.sun.tools.javac.Main WordCount.java
jar cf ../wc-v1.jar WordCount*.class
hadoop jar ../wc-v1.jar WordCount ./input ./output