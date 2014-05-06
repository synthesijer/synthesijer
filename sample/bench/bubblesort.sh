#!/bin/sh

RESULT=bubblesort.result

rm $RESULT
touch $RESULT
a=0
while [ $a -lt 100 ]
do
    a=`expr $a + 1`
    printf $a >> $RESULT
    printf " " >> $RESULT
    java -cp ~/workspace/javarock/bin:. BubbleSortThread $a >> $RESULT
done
