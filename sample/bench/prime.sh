#!/bin/sh

rm prime.result
touch prime.result
a=0
while [ $a -lt 128 ]
do
    a=`expr $a + 1`
    printf $a >> prime.result
    printf " " >> prime.result
    java -cp ~/workspace/javarock/bin:. PrimeThread $a >> prime.result
done
