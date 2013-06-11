cat test/mistakes.txt | scala  -J-Xmx2g  -cp classes SpellCheckApp  | test/spellrank/spellrank.py -f  test/correct.txt
wc -l test/correct.txt
