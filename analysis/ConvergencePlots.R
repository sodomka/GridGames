
base.dir = "~/Downloads/"
fname = "GG1.dat"


file = paste(base.dir, fname, sep="")

plot(0, 0, type='n', xlim=c(0,10), ylim=c(0,320), xlab="Iteration", ylab="Value Function Distance From Last Iteration", main="Value Function Convergence Rates\n(value iteration with coco)")
a = read.table("~/Downloads/GG1.dat", sep="\t", header=TRUE)
lines(a$iteration, a$max_difference, type='o', lwd=2, col=1, lty=1, pch=1)

a = read.table("~/Downloads/GG2.dat", sep="\t", header=TRUE)
lines(a$iteration, a$max_difference, type='o', lwd=2, col=2, lty=2, pch=2)

a = read.table("~/Downloads/GG3.dat", sep="\t", header=TRUE)
lines(a$iteration, a$max_difference, type='o', lwd=2, col=3, lty=3, pch=3)

a = read.table("~/Downloads/GG4.dat", sep="\t", header=TRUE)
lines(a$iteration, a$max_difference, type='o', lwd=2, col=4, lty=4, pch=4)

a = read.table("~/Downloads/GG5.dat", sep="\t", header=TRUE)
lines(a$iteration, a$max_difference, type='o', lwd=2, col=5, lty=5, pch=5)

a = read.table("~/Downloads/GG6.dat", sep="\t", header=TRUE)
lines(a$iteration, a$max_difference, type='o', lwd=2, col=6, lty=6, pch=6)
6
a = read.table("~/Downloads/GG7.dat", sep="\t", header=TRUE)
lines(a$iteration, a$max_difference, type='o', lwd=2, col=7, lty=7, pch=7)

legend("topright", c("GG1", "GG2", "GG3", "GG4", "GG5", "GG6", "GG7"), lwd=2, col=1:7, lty=1:7, pch=1:7)
