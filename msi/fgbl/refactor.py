import random
import sys

args = int(sys.argv[1])

lines = open('fgbl.csv').readlines()[1:]

data = map(lambda l: float(l[13]), 
	filter(lambda l: len(l) > 13,
		map(lambda line: line.split(';'), lines)))

dataOut = open('tmp/data' + str(args) + ".csv", "w")
resOut = open('tmp/res' + str(args) + ".csv", "w")
testDataOut = open('tmp/test-data' + str(args) + ".csv", "w")
testResOut = open('tmp/test-res' + str(args) + ".csv", "w")
Rscript = open('tmp/script' + str(args) + ".R", "w")

desc = open('tmp/desc' + str(args), 'w')

_min = min(data)
_max = max(data)


d = _max - _min

desc.write('min= ' + str(_min) + " max= " + str(_max))
desc.close()

Rscript.write(open('script.R').read().replace('@', str(args)));

data = map(lambda x: (x - _min) /d, data) 

dataSize = 10000 + args;
trainingSize = 100;

traingSet = data[:dataSize+args]

for i in range(args,len(traingSet)): 
	dataOut.write(reduce(lambda a,b: str(a) + " " + str(b), data[i - args: i]) + "\n")
	resOut.write(str(data[i]) + "\n")

for ii in range(trainingSize): 
	i = args + random.randint(0, dataSize)
	testDataOut.write(reduce(lambda a,b: str(a) + " " + str(b), data[i - args: i]) + "\n")
	testResOut.write(str(data[i]) + "\n")
