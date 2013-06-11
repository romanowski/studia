data <- as.matrix(read.table("tmp/data12.csv"));
res <- as.matrix(read.table("tmp/res12.csv"));

require(AMORE)

net.start <- newff(n.neurons=c(12,12/2+1,1),      
		     learning.rate.global=1e-3,        
		     momentum.global=0.8,              
		     error.criterium="LMLS",           
		     Stao=NA, hidden.layer="tansig",   
		     output.layer="tansig",           
		     method="ADAPTgdwm") 

result <- train(net.start, data, res, error.criterium="LMS", report=TRUE, show.step=500, n.shows=10)

testData <- as.matrix(read.table("tmp/test-data12.csv"));
testRes <- as.matrix(read.table("tmp/test-res12.csv"));

netOut <-sim(result$net, testData)
error <- abs(netOut- testRes / testRes)
jpeg('plots/plot12.jpg')
plot(error)
dev.off()
