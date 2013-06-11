import random
import sys

#12 - 11 + 1
#args = int(sys.argv[1])

lines = open('wines.csv').readlines()

data = map(lambda line: map(lambda x: float(x), line.split(';')), lines)

dataOut = open('tmp/data' + ".csv", "w")
resOut = open('tmp/res' + ".csv", "w")
testDataOut = open('tmp/test-data'  + ".csv", "w")
testResOut = open('tmp/test-res'  + ".csv", "w")

desc = open('tmp/desc', 'w')


args = range(11);

for i in range(11):
	args[i] = {};
	args[i]['min'] = min(data, key=lambda x: x[i])[i]
	args[i]['max'] = max(data, key=lambda x: x[i])[i]
	args[i]['d'] = args[i]['max'] - args[i]['min']
	desc.write(str(i) + ' min= ' + str(args[i]['min']) + " max= " + str(args[i]['max']))

for i in range(len(data)):
	old = data[i]
	for j in range(11):
		data[i][j] = (data[i][j] - args[j]['min']) / args[j]['d']


for i in range(len(data)): 
	dataOut.write(reduce(lambda a,b: str(a) + " " + str(b), data[i][:11]) + "\n")
	resOut.write(str(float(data[i][11] > 5.0)) + "\n")

trainingSize = 100

for ii in range(trainingSize): 
	i = random.randint(0,len(data))
	print(data[i][:11])
	testDataOut.write(reduce(lambda a,b: str(a) + " " + str(b), data[i][:11]) + "\n")
	testResOut.write(str(float(data[i][11] > 5.0)) + "\n")
