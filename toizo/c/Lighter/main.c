/*
 * main.c
 *
 *  Created on: 08-06-2012
 *      Author: jar
 */

#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <string.h>

struct BOARD {
	char** fields;
	int lights;
	int value;
	char** powers;
};

/* kodowanie Oxabcdefg
 *
 * gdzie
 * a - przewod u góry
 * b - przewod po prawej
 * c - na dole
 * d - po lewej
 * e - zarówka
 * f - zasilanie
 */
const char TOP = 64;
const char RIGHT = 32;
const char BOTTOM = 16;
const char LEFT = 8;
const char LIGHT = 4;
const char SOURCE = 2;
const char NEED = 1;

char META;
char ROTE;
char ALL;
char META_LIGHT;

inline char is_source(char c) {
	return c & SOURCE;
}

inline char is_light(char c) {
	return c & LIGHT;
}

inline char is_left(char c) {
	return c & LEFT;
}

inline char is_bottom(char c) {
	return c & BOTTOM;
}
inline char is_right(char c) {
	return c & RIGHT;
}
inline char is_top(char c) {
	return c & TOP;
}

struct mapping {
	char name;
	char index;
	char symbol;

};

struct mapping *mappingString;
int maxMapping = 0;

struct BOARD* board;
int sourceX = 0;
int sourceY = 0;
int w = 0;
int h = 0;
int allLights = 0;

char convert(char* what) {

	int i = 0;
	while (!(mappingString[i].name == what[0]
			&& mappingString[i].index == what[1])) {
		i++;
	}
	return mappingString[i].symbol;
}

char* toStr(char sym) {

	int i = 0;
	while (sym != mappingString[i].symbol && i < maxMapping) {
		i++;
	}
	char* tmp = (char*) malloc(3 * sizeof(char));
	if (i < maxMapping) {
		tmp[0] = mappingString[i].name;
		tmp[1] = mappingString[i].index;
	} else {
		tmp[0] = 'X';
		tmp[1] = 'X';
	}
	tmp[2] = 0;

	return tmp;
}

void fill(int i, char name, char index, char symbol) {
	mappingString[i].name = name;
	mappingString[i].index = index;
	mappingString[i].symbol = symbol;
}

struct BOARD* create() {
	struct BOARD* new = malloc(sizeof(struct BOARD));
	int i;
	new->fields = (char**) malloc((w + 2) * sizeof(char*));
	new->powers = (char**) malloc((w + 2) * sizeof(char*));

	for (i = 0; i <= (w + 1); ++i) {
		new->fields[i] = malloc((h + 2) * sizeof(char));
		new->powers[i] = malloc((h + 2) * sizeof(char));
	}

	for (i = 0; i < (w + 2); i++) {
		new->fields[i][0] = 0;
		new->fields[i][h + 1] = 0;
	}

	for (i = 0; i < (h + 2); i++) {
		new->fields[0][i] = 0;
		new->fields[w + 1][0] = 0;
	}
	return new;
}

void import() {

	srand(time(NULL ));

	mappingString = (struct mapping*) malloc(26 * sizeof(struct mapping));
	ALL = LEFT | RIGHT | TOP | BOTTOM | NEED;

	META = LIGHT | SOURCE | NEED;
	ROTE = TOP | LEFT | RIGHT | BOTTOM;
	META_LIGHT = LIGHT | NEED;

	int i = 0;
	fill(i++, 'A', '1', ALL | NEED);
	fill(i++, 'B', '1', LEFT | RIGHT | NEED);
	fill(i++, 'B', '2', TOP | BOTTOM | NEED);
	fill(i++, 'C', '1', BOTTOM | RIGHT | NEED);
	fill(i++, 'C', '2', BOTTOM | LEFT | NEED);
	fill(i++, 'C', '3', TOP | LEFT | NEED);
	fill(i++, 'C', '4', TOP | RIGHT | NEED);
	fill(i++, 'D', '1', ALL | SOURCE | NEED);
	fill(i++, 'E', '2', SOURCE | TOP | BOTTOM | NEED);
	fill(i++, 'E', '1', LEFT | RIGHT | SOURCE | NEED);
	fill(i++, 'F', '1', BOTTOM | RIGHT | NEED | SOURCE);
	fill(i++, 'F', '2', BOTTOM | LEFT | NEED | SOURCE);
	fill(i++, 'F', '3', TOP | LEFT | NEED | SOURCE);
	fill(i++, 'F', '4', TOP | RIGHT | NEED | SOURCE);
	fill(i++, 'G', '1', (LEFT | RIGHT | BOTTOM | NEED));
	fill(i++, 'G', '2', (LEFT | TOP | BOTTOM | NEED));
	fill(i++, 'G', '3', (LEFT | RIGHT | TOP | NEED));
	fill(i++, 'G', '4', (RIGHT | TOP | BOTTOM | NEED));
	fill(i++, 'H', '1', (LEFT | RIGHT | BOTTOM) | NEED | SOURCE);
	fill(i++, 'H', '2', (LEFT | TOP | BOTTOM) | NEED | SOURCE);
	fill(i++, 'H', '3', (LEFT | RIGHT | TOP) | NEED | SOURCE);
	fill(i++, 'H', '4', (RIGHT | TOP | BOTTOM) | NEED | SOURCE);
	fill(i++, 'I', '1', RIGHT | NEED | LIGHT);
	fill(i++, 'I', '2', BOTTOM | NEED | LIGHT);
	fill(i++, 'I', '3', LEFT | NEED | LIGHT);
	fill(i++, 'I', '4', TOP | NEED | LIGHT);
	maxMapping = i;

	char* tmp = (char*) malloc(124);
	scanf("%s", tmp);
	w = atoi(tmp);

	scanf("%s", tmp);
	h = atoi(tmp);

	i = 0;

	board = create();
	i = 0;
	int j = 0;

	for (i = 1; i <= h; ++i) {
		for (j = 1; j <= w; ++j) {
			scanf("%s", tmp);
			board->fields[j][i] = convert(tmp);
			if (is_source(board->fields[j][i])) {
				sourceX = j;
				sourceY = i;
			}
			if (is_light(board->fields[j][i])) {
				board->lights++;
			}
		}
	}
	allLights = board->lights;

}

void printBoard(struct BOARD* board) {

	int i;
	int j;

	for (j = 1; j < (h + 1); ++j) {
		for (i = 1; i < (w + 1); ++i) {
			printf("%s ", toStr(board->fields[i][j]));
		}
		printf("\n");
	}

}

//for testing
int stack[200];

int stackPointer;

void clean() {
	stackPointer = 0;
}

void push(int x, int y) {
	stack[stackPointer++] = x;
	stack[stackPointer++] = y;

	//printf("pushing %d %d\n", x, y);

}

struct point {
	int x;
	int y;
};

struct point pop() {
	struct point p;
	p.y = stack[--stackPointer];
	p.x = stack[--stackPointer];
	return p;
}

char isEmpty() {
	return stackPointer <= 0;
}

struct BOARD* corrent;
char done = 0;

int connected(struct BOARD* board) {

	int pi, pj;

	for (pi = 1; pi < w + 2; ++pi) {
		for (pj = 1; pj < h + 2; ++pj) {
			board->powers[pi][pj] = 0;
		}
	}

	int lighter = 0;

	int loops = 0;

	clean();
	push(sourceX, sourceY);

	while (!isEmpty()) {

		loops++;

		struct point p = pop();

		int y = p.y;
		int x = p.x;

		char my = board->fields[x][y];
		//printf("mam %d %d %s\n", x, y, toStr(my));

		//power on
		board->powers[x][y] = 1;

		//	printf("0\n");

		int dx = x + 1;
		int dy = y;
		char toCheck = board->fields[dx][dy];

		if (is_right(my) && !board->powers[dx][dy]) {
			if (is_light(toCheck)) {
				board->fields[dx][dy] = META_LIGHT | LEFT;
				board->powers[dx][dy] = 1;
				lighter++;

			} else if (is_left(toCheck)) {
				push(dx, dy);
			}

		}

		dx = x - 1;
		dy = y;
		toCheck = board->fields[dx][dy];

		if (is_left(my) && !board->powers[dx][dy]) {
			if (is_light(toCheck)) {
				board->fields[dx][dy] = META_LIGHT | RIGHT;
				board->powers[dx][dy] = 1;
				lighter++;

			} else if (is_right(toCheck)) {
				push(dx, dy);
			}
		}

		dx = x;
		dy = y - 1;
		toCheck = board->fields[dx][dy];

		if (is_top(my) && !board->powers[dx][dy]) {
			if (is_light(toCheck)) {
				board->fields[dx][dy] = META_LIGHT | BOTTOM;
				board->powers[dx][dy] = 1;
				lighter++;
			} else if (is_bottom(toCheck)) {
				push(dx, dy);
			}
		}

		dx = x;
		dy = y + 1;
		toCheck = board->fields[dx][dy];

		if (is_bottom(my) && !board->powers[dx][dy]) {
			if (is_light(toCheck)) {
				board->fields[dx][dy] = META_LIGHT | TOP;
				board->powers[dx][dy] = 1;
				lighter++;

			} else if (is_top(toCheck)) {
				push(dx, dy);
			}
		}
	}

	//printf("mam: %d req: %d \n", lighter, allLights);
	if (allLights == lighter) {
		corrent = board;
		done = 1;
	}

	board->value = loops + 2 * lighter;
	board->lights = lighter;

	return lighter;
}
//################ rotations

char rotate90(char c) {
	char ls = c & (META);
	char toRote = (c & ROTE);
	char rotate = (toRote << 1 | toRote >> 3) & ROTE;
	return ls | rotate;
}
char rotate180(char c) {
	char ls = c & (META);
	char toRote = (c & ROTE);
	char rotate = (toRote << 2 | toRote >> 2) & ROTE;
	return ls | rotate;
}

char rotate270(char c) {
	char ls = c & (META);
	char toRote = (c & ROTE);
	char rotate = (toRote << 3 | toRote >> 1) & ROTE;
	return ls | rotate;
}

char mute(char base, int prop) {
	switch (rand() % prop) {
	case 0:
		return (rotate90(base));
	case 1:
		return (rotate180(base));
	case 2:
		return (rotate270(base));
	default:
		return base;
	}
}

void cross(struct BOARD* a, struct BOARD* b, struct BOARD* child, int mutation) {

	int i = 0;
	int j = 0;

	for (i = 1; i < w; i++) {
		for (j = 1; j < h; ++j) {
			if (!is_light(a->fields[i][j])) {
				if (rand() % 2) {
					child->fields[i][j] = mute(a->fields[i][j], mutation);
				} else {
					child->fields[i][j] = mute(b->fields[i][j], mutation);
				}
			} else {
				child->fields[i][j] = a->fields[i][j];
			}
		}

	}

	child->lights = connected(child);
}

void shake(struct BOARD* a) {
	int i = 0;
	int j = 0;

	for (i = 0; i <= w; i++) {
		for (j = 1; j < h; ++j) {
			if (!a->powers[i][j]) {
				mute(a->fields[i][j], 8);
			}
		}
	}
}

int populationSize = 50;
int oldPopultionSize = 20;
int newPopultionSize = 0;

struct BOARD* createStartPopulation() {
	newPopultionSize = populationSize - oldPopultionSize;
	struct BOARD* newPop = (struct BOARD*) malloc(
			populationSize * sizeof(struct BOARD));

	int i = 0;
	int x, y;

	for (i = 0; i < populationSize; ++i) {
		newPop[i] = *create();
		for (x = 1; x < w + 1; ++x) {
			for (y = 0; y < h + 1; ++y) {
				newPop[i].fields[x][y] = mute(board->fields[x][y], 4);
			}
		}
		newPop[i].lights = connected(newPop + i);

	}

	return newPop;
}

int compare_boards(const struct BOARD *a, const struct BOARD *b) {
	return (int) (b->value - a->value);
}

void calculate() {
	int popCount = 0;
	while (!done) {

		struct BOARD* population = createStartPopulation();

		int maxRes = 0;
		int delta = 0;
		int lastRes = 0;
		int maxDelta = 200;

		while (!done && delta < maxDelta) {
			qsort(population, populationSize, sizeof(struct BOARD),
					compare_boards);

			int ii;
			lastRes = 0;
			for (ii = 0; ii < oldPopultionSize; ++ii) {
				lastRes += population[ii].value;
				shake(population + ii);
				connected(population + ii);

			}
			/*if (delta % 64 == 0) {
			 for (ii = 0; ii < oldPopultionSize; ++ii)
			 population[ii].value = population[ii].value - 1;
			 }*/

			int mutation = 40 - ((maxDelta - delta) * 30) / maxDelta;
			/*
			 printf("old %d:", lastRes);

			 for (ii = 0; ii < oldPopultionSize; ++ii) {
			 printf("(%d %d)", population[ii].lights, population[ii].value);
			 //printBoard(population + ii);
			 }
			 printf("\n");*/

			int done = 0;
			int nextPopSize = newPopultionSize;
			int parentIndex = 0;

			while (done < newPopultionSize) {

				nextPopSize = nextPopSize / 2 + 1;
				int i = 0;

				struct BOARD* parentA = population + parentIndex;

				for (i = 0; i < nextPopSize && done < newPopultionSize; ++i) {

					int next_offset = parentIndex
							+ rand() % (oldPopultionSize - parentIndex);

					struct BOARD* parentB = population + next_offset;

					cross(parentA, parentB,
							population + oldPopultionSize + done, mutation);

					done++;

				}

				parentIndex++;
			}

			if (maxRes < lastRes) {
				maxRes = lastRes;
				delta = 0;
			} else {
				delta++;
			}
			popCount++;
		}

		//qsort(population, populationSize, sizeof(struct BOARD), compare_boards);
/*		printf("population end! score: %d / %d in %d\n", population->lights,
				allLights, popCount);
		printBoard(&population[0]);*/
		popCount = 0;
	}
}
int main(int argc, char **argv) {

	import();

	calculate();

	printf("%d %d\n", w, h);

	printBoard(corrent);

	return 0;

}
