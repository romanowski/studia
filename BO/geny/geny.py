from random import random, randint


class GeneticAlgorithm:


    def __init__(self):
        # Tablica wspolzednych miast
        self.cities = [
                       [1,2,3,4,5,6],
                       [1,2,3,4,5,6],
                       [1,2,3,4,5,6],
                       [1,2,3,4,5,6],
                       [1,2,3,4,5,6],
                       [1,2,3,4,5,6]
                       ]
        # Ilosc miast
        self.cities_amount = 6
        # Numer miast w ktorym jest magazyn
        self.magazine = 0
        # Ilosc samochodow do wykorzystania
        self.cars = 3
        # Rozmiar populacji
        self.population_size = 2
        # Ilosc populacji
        self.population_amount = 10
        # Populacja
        self.population = []
        # Oceny
        self.rates = {}


    def create_random_population(self):
        for i in range(self.population_size):
            individual = [[self.magazine] for x in range(self.cars)]
            for j in range(1, self.cities_amount): 
                individual[randint(0,self.cars-1)].append(j)
            self.population.append(individual)
     
    def rate_population(self):
        print "RATE TO DO"
    
    def select_best(self):  
        print "SELECT BEST TO DO"     
        
    def cross_and_mutate(self):  
        print "CROSS AND MUTATE TO DO"
    
    def solve(self):
        self.create_random_population()
        self.rate_population()
        for i in range(self.population_amount):
            self.select_best()
            self.cross_and_mutate()
            self.rate_population()
        return self.population
                 
genetic = GeneticAlgorithm()
print genetic.solve()
