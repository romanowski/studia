#
# Projekt TOiZO -- Lato 2012
# Piotr Faliszewski

from sys import *
import Image



tile_type     = ["a","b","c","d","e","f","g","h","i"]
tile_variants = [ 1 , 2 , 4 , 1 , 2 , 4 , 4 , 4 , 4 ]
tile_size     = (40,40)
tile          = {}
board         = []
unknown       = Image.open("x.png")




if( len(argv) != 2 ):
  print "Wywolanie: "
  print "  python %s nazwa" % argv[0]
  exit()


# wczytaj opis planszy
data = stdin.readlines()

# zapamietaj opis planszy
header = data[0].split()
x = int(header[0])
y = int(header[1])
if( len( data ) < y+1 ):
  print "Niepoprawny format werscia: Za malo wierszy."
for i in range(y):
  board += [[ z.lower() for z in data[i+1].split() ]]


#wczytaj bitmapy
for i in range(len(tile_type)):
  for j in range(tile_variants[i]):
    name = tile_type[i] + str(j+1)
    tile[name] = Image.open( name+".png" )
    tile_size = tile[name].size



# stworz obraz planszy
bitmap = Image.new("RGB",(x*tile_size[0],y*tile_size[1]))
for yy in range(y):
  for xx in range(x):
    if( board[yy][xx] in tile ):
      bitmap.paste(tile[board[yy][xx]],(xx*tile_size[0],yy*tile_size[1]))
    else:
      bitmap.paste(unknown,(xx*tile_size[0],yy*tile_size[1]))


bitmap.save( argv[1] )
bitmap.show()

    
