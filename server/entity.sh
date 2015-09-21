#!/bin/bash

#the first argument needs to be the picture of the flyer

#remove the extension, and remove the preceding directories to get filename
#filename=`echo $1 | sed 's/\.[^\.]*$//' | awk -F "/" '{print $NF}'`

#do the OCR, store in tmp
#tesseract "$1" "/tmp/$filename" -psm 1

#str="Friday, March 14, 2008 MIT’s Stata Center 32 Vassar St, Cambridge, MA 32-124 10:30am-12pm"
str=`cat $1`

#str=`cat "/tmp/$filename"`

#remove the picture and the OCR text now we have in $str
#rm "/tmp/$filename"
#rm "$1"

#str=`echo -e "$str" | sed "s/[\'\"]//g"`
#str=`echo -e "$str" | iconv -f utf-8 -t iso-8859-1//TRANSLIT | sed "s/[\'\"]//g"`  
str=`echo -e "$str" | iconv -f utf-8 -t iso-8859-1//TRANSLIT` 
echo -e "$str"


curl -H "Content-Type: application/json" -X POST -d "{\"file\":\"$str\",\"port\":\"9191\"}" http://localhost:8008/ner
