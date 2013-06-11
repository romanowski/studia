gnuplot <<\EOF
middle = `echo $middle`
b=1
d=1
p=1
k=1
f(x) = log(p) - b * log(x + d)
g(x) =  k * x


fit f(x) 'data.txt' using 1:(log($2)) via b, d, p
fit g(x) 'data.txt' using 1:2 via k

set arrow 1 from middle,0 to middle,10 nohead

`sleep 1s`

#set logscale x

plot 'data.txt' using 1:(f($1)) with lines, \
     'data.txt' using 1:(g($2)) with lines, \
     'data.txt' using 1:(log($2)) with lines

print "B"
print b

print "D"
print d

print "P"
print p

print "k"
print k



`sleep 50m`