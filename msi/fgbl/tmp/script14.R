data <- as.matrix(read.table("tmp/data14.csv"));
res <- as.matrix(read.table("tmp/res14.csv"));

require(AMORE)

net.start <- newff(n.neurons=c(14,14/2+1,1),      
		     learning.rate.global=1e-3,        
		     momentum.global=0.8,              
		     error.criterium="LMLS",           
		     Stao=NA, hidden.layer="tansig",   
		     output.layer="tansig",           
		     method="ADAPTgdwm") 

result <- train(net.start, data, res, error.criterium="LMS", report=TRUE, show.step=500, n.shows=10)

testData <- as.matrix(read.table("tmp/test-data14.csv"));
testRes <- as.matrix(read.table("tmp/test-res14.csv"));

netOut <-sim(result$net, testData)
error <- abs(netOut- testRes / testRes)
jpeg('plots/plot14.jpg')
plot(error)
dev.off()
