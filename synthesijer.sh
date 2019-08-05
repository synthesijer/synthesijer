#! /bin/sh

SJR_BASE_DIR=`dirname $0`
SYNTHESIJER=${SJR_BASE_DIR}/synthesijer.jar

LC_ALL=en_US.UTF-8 LANG=en_US.UTF-8 java -jar ${SYNTHESIJER} $@
