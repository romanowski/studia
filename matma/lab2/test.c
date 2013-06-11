#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <quantum.h>

int main ()
{
  quantum_reg reg;
  int result;

  srand(time(0));

  reg = quantum_new_qureg(0, 1);

  quantum_hadamard(0, &reg);

  result = quantum_bmeasure(0, &reg);

  printf("The Quantum RNG returned %i!\n", result);

  return 0;
}
