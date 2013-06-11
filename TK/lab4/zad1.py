import re
import sys
 
class Process:
	dot = re.compile("\W*\.\W*")
	eOn = re.compile("\W*ON\W*")
	eOff = re.compile("\W*OFF\W*")
	echo =re.compile("\W*(.*)")

	#choice /C:TNM Idziemy na piwo (Tak, Nie, Moze)?
	choiceOpt = re.compile("\W*/C:(\w+) (.+)")

	ala= "";
	echoOn = 1;
	beg = "";

	def proces_choice(self, data):
	
		m = self.choiceOpt.match(data)
		if m :
			opt = m.group(1)
			ans = m.group(2)
	
			print ans

	


	def procesEcho(self, data):


		if self.dot.match(data):
			return ""

		if self.eOn.match(data):
			self.echoOn = 1
			return 0

		if self.eOff.match(data):

			self.echoOn = 0
			return 0


		data = self.echo.match(data).group(1);
		return data;



	def proces(self, plik):
		
		f = open(plik);
		lines= f.read().split("\n");
		ala =""

		for line in lines:
		  if len(line) > 0:
	
			ala = ""
			m = 1;
			ok = 0;
			if line[0] == '@':
				m = 0
				line = line[1:]

			command = line

			if(self.echoOn and m):
				print  self.beg + "C:\>"+line

			if self.beg == "":
				self.beg = "\n"
	
			if(line.startswith("echo")):
				ala = self.procesEcho(line[4:])
				ok = 1;


			if(line.startswith("rem")):
				ala = 0
				ok = 1;

			if(line.startswith("choice")):
				self.proces_choice(line[6:])
				ala = 0
				ok = 1;
			
			if(line.startswith("exit")):
				return ""

			if(not ok):
				print "Bad command or file name"
				ala =0
			
			if ala != 0:
				print ala
		

Process().proces(sys.argv[1])





