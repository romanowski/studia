 #cat test/mistakes.txt
#echo "\n"
cat test/mistakes.txt | scala  -J-Xmx512m  -cp classes SpellCheckTestApp 
#echo "\n"
#cat test/correct.txt
