rm -rf output/
hadoop com.sun.tools.javac.Main WordCount.java
jar cf ../wc-v2.jar WordCount*.class
hadoop jar ../wc-v2.jar WordCount -Dwordcount.case.sensitive=false ./input ./output