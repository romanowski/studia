data <- as.matrix(read.table("tmp/data@.csv"));
res <- as.matrix(read.table("tmp/res@.csv"));

require(AMORE)

net.start <- newff(n.neurons=c(@,@/2+1,1),      
		     learning.rate.global=1e-3,        
		     momentum.global=0.8,              
		     error.criterium="LMLS",           
		     Stao=NA, hidden.layer="tansig",   
		     output.layer="tansig",           
		     method="ADAPTgdwm") 

result <- train(net.start, data, res, error.criterium="LMS", report=TRUE, show.step=500, n.shows=10)

testData <- as.matrix(read.table("tmp/test-data@.csv"));
testRes <- as.matrix(read.table("tmp/test-res@.csv"));

netOut <-sim(result$net, testData)
error <- abs(netOut- testRes / testRes)
jpeg('plots/plot@.jpg')
plot(error)
dev.off()
