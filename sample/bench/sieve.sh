#!/bin/sh

rm sieve.result
touch sieve.result
a=0
while [ $a -lt 100 ]
do
    a=`expr $a + 1`
    printf $a >> sieve.result
    printf " " >> sieve.result
    java -cp ~/workspace/javarock/bin:. SieveThread $a >> sieve.result
done
